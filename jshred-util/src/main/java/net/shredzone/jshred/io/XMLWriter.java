/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *-----------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shredzone.jshred.io;

import java.io.*;
import java.util.*;
import org.xml.sax.*;

/**
 * A very simple writer for cleanly formatted XML output.
 * <p>
 * This writer will do for creating simple XML files, but please note
 * that it is not perfect at all. It does not support namespace, DTDs
 * and it is unable to read DOM structures as in the <code>org.w3c.dom</code>
 * package. And last but not least it does not validate your XML against
 * a DTD.
 * <p>
 * What it <em>does</em> is indention of the text, proper escaping
 * of XML special chars and correct charset encoding.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: XMLWriter.java 243 2009-01-18 15:05:21Z shred $
 */
public class XMLWriter extends BufferedWriter {
    private String indent = "  ";
    private String charset = "UTF-8";
    private int level = 0;
    private Stack<String> sElements = new Stack<String>();
    private StringBuilder bTag = new StringBuilder();
    private String store = null;

    /**
     * Create a new XMLWriter basing on a {@link Writer}.
     * 
     * @param out
     *            {@link Writer} to send the XML data to
     */
    public XMLWriter(Writer out) {
        super(out);
        if (out instanceof OutputStreamWriter) {
            charset = ((OutputStreamWriter) out).getEncoding();
        }
    }

    /**
     * Create a new XMLWriter basing on an {@link OutputStream}. The
     * {@link OutputStream} will receive UTF-8 encoded data.
     * 
     * @param out
     *            {@link OutputStream} to send the XML data to
     * @throws UnsupportedEncodingException
     *             if this VM does not support UTF-8, which should never
     *             happen...
     */
    public XMLWriter(OutputStream out) throws UnsupportedEncodingException {
        this(new OutputStreamWriter(out, "UTF-8"));
    }

    /**
     * Set the indention string. This string is used to indent lines according
     * to their nesting. Defaults to two spaces, but you can also set several
     * spaces, tabs or an empty string.
     * <p>
     * If you want to change the indention string, you must do so before
     * invoking {@link #startDocument()}.
     * 
     * @param indent
     *            New Indention String
     */
    public void setIndent(String indent) {
        this.indent = indent;
    }

    /**
     * Set the encoding used by this writer. The XMLWriter tries to find out the
     * proper encoding itself. If an @{link OutputStream} is used, encoding
     * will always be UTF-8. If an @{link OutputStreamWriter} was passed in,
     * its current encoding will be used. In any other case UTF-8 is assumed
     * for encoding, which can be changed by using this method.
     * <p>
     * If you want to change the encoding string, you must do so before
     * invoking {@link #startDocument()}.
     * 
     * @param encoding
     *            New encoding
     */
    public void setEncoding(String encoding) {
        this.charset = encoding;
    }

    /**
     * Indent by one level
     */
    protected void writeIndent() throws IOException {
        for (int cnt = 0; cnt < level; cnt++) {
            write(indent);
        }
    }

    /**
     * Start an XML document. The XML header will be written.
     */
    public void startDocument() throws IOException {
        if (charset.equalsIgnoreCase("UTF8")) charset = "UTF-8";
        write("<?xml version=\"1.0\" encoding=\"" + charset + "\"?>");
        newLine();
        newLine();
    }

    /**
     * Finish an XML document.
     */
    public void endDocument() throws IOException {
        flushTag();
        if (!sElements.isEmpty()) {
            throw new IOException("Still open elements");
        }
    }

    /**
     * Start a new XML element. There are no attributes added to this element.
     * 
     * @param element
     *            Element name
     */
    public void startElement(String element) throws IOException {
        startElement(element, (Attributes) null);
    }

    /**
     * Start a new XML element with attributes. This method will take proper
     * care for excaping all chars within the attribute values.
     * 
     * @param element
     *            Element name
     * @param attr
     *            Attribute name/value pairs
     * @since R6
     */
    public void startElement(String element, String... attr)
    throws IOException {
        if (attr.length % 2 == 1)
            throw new IllegalArgumentException("Number of attr/value pairs must be even!");
        
        org.xml.sax.helpers.AttributesImpl xmla = new org.xml.sax.helpers.AttributesImpl();
        for (int ix = 0; ix < attr.length; ix += 2) {
            xmla.addAttribute("", "", attr[ix], "CDATA", attr[ix+1]);
        }
        startElement(element, xmla);
    }

    /**
     * Start a new XML element with attributes. This method will take proper
     * care for excaping all chars within the attribute values.
     * <p>
     * Note that since R15, the map is required to have {@link String} keys
     * and values.
     * 
     * @param element
     *            Element name
     * @param attrs
     *            Map with attributes and corresponding values
     * @since R6
     */
    public void startElement(String element, Map<String,String> attrs)
    throws IOException {
        org.xml.sax.helpers.AttributesImpl xmla = new org.xml.sax.helpers.AttributesImpl();
        for (String key : attrs.keySet()) {
            xmla.addAttribute("", "", key, "CDATA", attrs.get(key));
        }
        startElement(element, xmla);
    }

    /**
     * Start a new XML element with attributes. This method will take proper
     * care for excaping all chars within the attribute values.
     * 
     * @param element
     *            Element name
     * @param attr
     *            Attributes
     */
    public void startElement(String element, Attributes attr)
    throws IOException {
        flushTag();
        sElements.push(element);
        writeIndent();
        bTag.append(element);

        // --- Append attributes ---
        if (attr != null && attr.getLength() > 0) {
            int cnt = attr.getLength();
            for (int ix = 0; ix < cnt; ix++) {
                bTag.append(' ');
                bTag.append(attr.getQName(ix));
                bTag.append("=\"");
                bTag.append(escape(attr.getValue(ix)));
                bTag.append('"');
            }
        }
        level++;
    }

    /**
     * Close the most recent XML element. The XMLWriter recognizes empty
     * elements and will send a shortcut to the output.
     */
    public void endElement() throws IOException {
        String element = sElements.pop();
        if (element == null) {
            throw new IOException("Too many elements closed");
        }
        
        if (store != null && bTag.length() > 0) {
            level--;
            write('<');
            write(bTag.toString());
            write('>');
            write(escape(store));
            write("</");
            write(element);
            write('>');
            store = null;
            bTag = new StringBuilder();
        } else {
            if (store != null) {
                writeIndent();
                write(escape(store));
                newLine();
                store = null;
            }
            level--;
            if (bTag.length() > 0) {
                write('<');
                write(bTag.toString());
                write("/>");
                bTag = new StringBuilder();
            } else {
                writeIndent();
                write("</" + element + '>');
            }
        }
        newLine();
    }

    /**
     * Write the content of an XML container. Leading and trailing spaces will
     * be trimmed. Empty contents will be ignored. You can invoke this method
     * several times for a container. Each content will then be written into a
     * separate line. Special chars will automatically be escaped.
     * 
     * @param content
     *            Content of the current XML container
     */
    public void writeContent(String content) throws IOException {
        // --- Ignore empty container ---
        content = content.trim();
        if (content.equals(""))
            return;

        if (store == null) {
            // --- Temporary store it ---
            store = content;
        } else {
            // --- Write it ---
            flushTag();
            writeIndent();
            write(escape(content));
            newLine();
        }
    }

    /**
     * Write a comment. Content will be escaped properly.
     * 
     * @param comment
     *            The comment's content.
     */
    public void writeComment(String comment) throws IOException {
        // --- Ignore comment ---
        comment = comment.trim();
        if (comment.equals(""))
            return;

        // --- Write it ---
        flushTag();
        writeIndent();
        write("<!-- " + escape(comment) + " -->");
        newLine();
    }

    /**
     * Escape a String so it can be used in XML context. All &amp;, &lt;, &gt;
     * and &quot; will be converted into their respective entity.
     * 
     * @param text
     *            Text to be escaped
     * @return Escapted text
     */
    protected String escape(String text) {
        text = text.replaceAll("\\&", "&amp;");
        text = text.replaceAll("\\<", "&lt;");
        text = text.replaceAll("\\>", "&gt;");
        text = text.replaceAll("\\\"", "&quot;");
        return text;
    }

    /**
     * Flush the Tag buffer, which is used to write empty containers in their
     * short form.
     */
    protected void flushTag() throws IOException {
        if (bTag.length() > 0) {
            write("<");
            write(bTag.toString());
            write(">");
            newLine();
            bTag = new StringBuilder();
        }
        if (store != null) {
            writeIndent();
            write(escape(store));
            newLine();
            store = null;
        }
    }
}
