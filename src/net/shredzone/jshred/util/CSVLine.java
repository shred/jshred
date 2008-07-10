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

package net.shredzone.jshred.util;

import java.util.*;

/**
 * This class handles one CSV line.
 * <p>
 * If you pass a String, it will be split into several columns which are
 * delimited by the delim character. It will take care for proper
 * unquoting if a column was quoted.
 * <p>
 * You can also add and remove columns at will. The {@link #toString()}
 * method will result in a valid CSV line that can be written out.
 * <p>
 * The CSV format is: Each row ends with a newline. A row consists of
 * one or more columns, each separated by a delimiter character. If
 * one column contains the delimiter character, the entire column must
 * be quoted in double quotes. If the column also contains the quote
 * character, then the quote characters within the column must be doubled.
 * <p>
 * CSVLine inherits the List interface for easier access.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: CSVLine.java 169 2008-07-10 22:01:03Z shred $
 */
public class CSVLine extends ArrayList<String> {
    private static final long serialVersionUID = 3544948840219359284L;
    private char delim;

    /**
     * Create a new, empty CSVLine with ';' as delimiter.
     */
    public CSVLine() {
        this(';');
    }

    /**
     * Create a new, empty CSVLine with the given delimiter.
     * 
     * @param delim
     *            Delimiter
     */
    public CSVLine(char delim) {
        setDelimiter(delim);
    }

    /**
     * Create a new CSVLine, with the given text and ';' as default delimiter.
     * 
     * @param text
     *            Text to be split up
     */
    public CSVLine(String text) {
        this();
        addLine(text);
    }

    /**
     * Create a new CSVLine with the given text and the given delimiter.
     * 
     * @param text
     *            Text to be split up
     * @param delim
     *            Delimiter character
     */
    public CSVLine(String text, char delim) {
        this(delim);
        addLine(text);
    }

    /**
     * Add a CSV line to the current content. All columns are added to the end
     * of the current column list. This is most probably not what you wanted to
     * do!
     * <p>
     * If you want to use this class with a new line, use <code>clear()</code>
     * before:
     * <pre>
     * csvline.clear();
     * csvline.addLine(line);
     * </pre>
     * 
     * @param text
     *            Line to be added
     */
    public void addLine(String text) {
        // --- Split the row ---
        int max = text.length();
        int start = 0;
        int end;

        while (start < max) {
            if (text.charAt(start) == '"') {
                // --- Quoted Column ---
                end = start - 1;
                do {
                    end = text.indexOf("\"", end + 2);
                } while (end > 0 && end < (max - 1) && text.charAt(end + 1) == '"');
                if (end < 0)
                    throw new IllegalArgumentException("ending quote char missing");
                if (end < (max - 1) && text.charAt(end + 1) != delim)
                    throw new IllegalArgumentException("bad quoting");

                String content = text.substring(start + 1, end);
                content = content.replaceAll("\\\"\\\"", "\"");
                add(content);
                start = end + 1;
                if (start < max && text.charAt(start) == delim) {
                    start++;
                    if (start >= max) add("");
                }

            } else {
                // --- Unquoted Column ---
                end = text.indexOf(String.valueOf(delim), start);
                if (end >= 0) {
                    add(text.substring(start, end));
                    start = end + 1;
                    if (start >= max) add("");
                } else {
                    add(text.substring(start));
                    start = max;
                }
            }
        }
    }

    /**
     * Set the delimiter character. The quote character '"' is not allowed as a
     * delimiter, and will throw an IllegalArgumentException.
     * 
     * @param delim
     *            Delimiter
     */
    public void setDelimiter(char delim) {
        if (delim == '"')
            throw new IllegalArgumentException("quote char is not a valid delimiter");
        
        this.delim = delim;
    }

    /**
     * Get the delimiter character.
     * 
     * @return Delimiter
     */
    public char getDelimiter() {
        return delim;
    }

    /**
     * Get a valid CSV line of the current content, using the current delimiter
     * char.
     * <p>
     * <em>Note:</em> There is no newline character at the end of the line!
     * 
     * @return CSV line, without newline character
     */
    @Override
	public String toString() {
        StringBuilder buffer = new StringBuilder();
        boolean first = true;
        for (String col : this) {
            if (!first) {
                buffer.append(delim);
            }
            
            if (col == null) col = "";
            if (col.indexOf('"') >= 0) {
                // --- Escaping needed ---
                col = col.replaceAll("\\\"", "\"\"");
                buffer.append('"');
                buffer.append(col);
                buffer.append('"');
            } else {
                // --- No escaping needed ---
                buffer.append(col);
            }
            first = false;
        }
        return buffer.toString();
    }

}
