/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://jshred.shredzone.org-------------------------------------------------------------------
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

package net.shredzone.jshred.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.swing.ProgressMonitor;

import net.shredzone.jshred.io.UncloseableOutputStream;

/**
 * This class sends a HTTP request to a server, and returns an
 * {@link InputStream} to the returned data.
 * <p>
 * All kind of parameters can be added to the request using
 * {@link #addParameter(String, Object)}.
 * <p>
 * Example:
 * <pre>
 *   HTTPRequest req = new HTTPRequest(url, HTTPRequest.POST);
 *   req.addParameter("user", username);
 *   req.addParameter("email", email);
 *   req.doRequest();
 *   Reader input = req.getContentReader();
 * </pre>
 * <p>
 * All parameters will be sent UTF-8 encoded, as proposed by W3C.
 * <p>
 * GET requests are limited in their length. This class will not take
 * care to keep within the allowed length. If you are in doubt, you
 * should use POST request.
 * <p>
 * It is possible to pass a {@link ProgressMonitor} which will be used to show how
 * many data has been transfered to the server. This is especially useful
 * for file uploads.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: HTTPRequest.java 256 2009-02-10 22:56:35Z shred $
 */
public class HTTPRequest {

    public static enum Method { GET, POST }
    
    private HashMap<String,Object> hmParam;         // Parameter Hashmap
    private HashMap<String,DataProvider> hmStream;  // Stream Hashmap
    private URL getUrl;                             // Target URL
    private final Method method;                    // Method (GET or POST)
    private HttpURLConnection connect;              // Connection
    private final String boundary;                  // Boundary for multiparts
    private ProgressMonitor monitor;                // ProgressMonitor to be used
    private String transferNote;                    // String to show while transmission
    private int monitorMin;                         // Minimum Monitor value
    private int monitorMax;                         // Maximum Monitor value

    /**
     * Creates a new HTTPRequest using method GET.
     * 
     * @param url
     *            Target {@link URL} for the request.
     */
    public HTTPRequest(URL url) {
        this(url, Method.GET);
    }

    /**
     * Creates a new HTTPRequest.
     * 
     * @param url
     *            Target {@link URL} for the request.
     * @param method
     *            {@link Method}: GET or POST
     */
    public HTTPRequest(URL url, Method method) {
        this.getUrl = url;
        this.method = method;

        // --- Initialize parameter hashes ---
        hmParam = new HashMap<String,Object>();
        hmStream = new HashMap<String,DataProvider>();
        connect = null;
        monitor = null;

        // --- Create a boundary string ---
        String bcomp = "---------------------bd";
        bcomp += String.valueOf(Math.floor(Math.random() * 10000000));
        boundary = bcomp;
    }

    /**
     * Adds a parameter to the HTTPRequest. The {@link Object#toString()} result
     * of the value is transferred to the server. The value is evaluated when
     * {@link #doRequest()} is invoked, NOT at the time you pass the parameter
     * here. Keep this in mind!
     * <p>
     * If a <code>null</code> was passed as value, the parameter will not be
     * sent at all.
     * <p>
     * Note that a parameter can only be passed once. If you add it again, it
     * will replace the previous parameter with the same name. Also take care
     * not to exceed the limits of a GET request. Use POST if you are in doubt.
     * <p>
     * HTTPRequest will take care for proper encoding of the parameter names and
     * values. You won't need to urlencode them.
     * 
     * @param name
     *            Parameter name
     * @param value
     *            Parameter value, may be null
     */
    public void addParameter(String name, Object value) {
        if (name == null)
            throw new NullPointerException("You must provide a name");
        
        if (value != null) {
            hmParam.put(name, value);
        }
    }

    /**
     * Convenience call to add a boolean parameter to the HTTPRequest. If the
     * boolean value is false, no parameter will be sent at all. If the boolean
     * value is true, a "1" is transferred as parameter value to the server.
     * This method somehow simulates a HTML checkbox.
     * 
     * @param name
     *            Parameter name
     * @param value
     *            Parameter value
     */
    public void addParameter(String name, boolean value) {
        if (value == true) {
            addParameter(name, 1);
        }
    }

    /**
     * Adds a {@link File} to the HTTPRequest. You must use POST requests in
     * order to upload files to the server. The file will be sent using the
     * mime type "application/octet-stream". The file is not kept in memory,
     * but sent using a data stream during transmission, so HTTPRequest is
     * also able to upload large files with a small memory print.
     * 
     * @param name
     *            Parameter name
     * @param file
     *            {@link File} to be sent
     */
    public void addFile(String name, File file)
    throws FileNotFoundException {
        addDataProvider(name, new FileProvider(file));
    }

    /**
     * Adds a {@link File} to the HTTPRequest. You must use POST requests in
     * order to upload files to the server. The file is not kept in memory,
     * but sent using a data stream during transmission, so HTTPRequest is
     * also able to upload large files.
     * 
     * @param name
     *            Parameter name
     * @param file
     *            {@link File} to be sent
     * @param mimetype
     *            Mime type of the file to be transmitted
     */
    public void addFile(String name, File file, String mimetype)
    throws FileNotFoundException {
        addDataProvider(name, new FileProvider(file, mimetype));
    }

    /**
     * Adds a {@link DataProvider} to the HTTPRequest. You must use POST
     * requests in order to upload files to the server. The file is not kept
     * in memory, but sent using a data stream during transmission, so
     * HTTPRequest is also able to upload large files.
     * 
     * @param name
     *            Parameter name
     * @param provider
     *            {@link DataProvider} of the data to be uploaded
     */
    public void addDataProvider(String name, DataProvider provider) {
        if (!isPost())
            throw new IllegalArgumentException("POST method is required for sending files");

        if (name == null || provider == null)
            throw new NullPointerException("No name or provider given");

        hmStream.put(name, provider);
    }

    /**
     * Returns the method this request is sent with.
     * 
     * @return true:POST, false:GET
     */
    public Method getMethod() {
        return method;
    }
    
    /**
     * Returns true if this request uses the POST method.
     * 
     * @return  true: uses POST method
     */
    public boolean isPost() {
        return Method.POST.equals(method);
    }

    /**
     * Defines a {@link ProgressMonitor} for the transmission process. After
     * {@link #doRequest()} connected to the server, the transfer string is
     * shown as a note.
     * <p>
     * The {@link ProgressMonitor} will start at the left corner during start of
     * transmission, and will end in the right corner of the gauge.
     * 
     * @param monitor
     *            {@link ProgressMonitor} to be used.
     * @param transfer
     *            Message to be shown after connection.
     */
    public void setProgressMonitor(ProgressMonitor monitor, String transfer) {
        setProgressMonitor(monitor, transfer, 0, 100);
    }

    /**
     * Defines a {@link ProgressMonitor} for the transmission process. After
     * {@link #doRequest()} connected to the server, the transfer string is
     * shown as a note. During transmission, a value between min and max is
     * shown according to the process of the transmission. After transmission,
     * max will always be the current value of the {@link ProgressMonitor}.
     * <p>
     * You can use min and max if the HTTPRequest is just one step in a chain of
     * several steps to be shown in the {@link ProgressMonitor}.
     * <p>
     * Note that the response transfer is not included in the {@link ProgressMonitor}.
     * This is because there is no way to find out the response size from the
     * server before the request has been entirely sent to it.
     * 
     * @param monitor
     *            {@link ProgressMonitor} to be used.
     * @param transfer
     *            Message to be shown after connection.
     * @param min
     *            Minimum value during transmission
     * @param max
     *            Maximum value at the end of transmission
     */
    public void setProgressMonitor(ProgressMonitor monitor, String transfer, int min, int max) {
        this.monitor = monitor;
        this.transferNote = transfer;
        this.monitorMin = min;
        this.monitorMax = max;
    }

    /**
     * Sends the request to the server. You cannot reuse the request after it has
     * been sent. If you have previously set a {@link ProgressMonitor}, it will
     * be used.
     * 
     * @return Return code of the server, see {@link HttpURLConnection}
     * @throws IOException
     *             IO exception
     * @throws MalformedURLException
     *             URL was malformed
     */
    public int doRequest() throws IOException, MalformedURLException {
        if (connect != null)
            throw new RuntimeException("already connected");

        if (isPost()) {
            // --- POST ----------------------------------------
            // Create Connection
            URLConnection con = getUrl.openConnection();
            if (!(con instanceof HttpURLConnection)) {
                throw new RuntimeException("No HTTP connection");
            }
            connect = (HttpURLConnection) con;
            connect.setRequestMethod("POST");
            connect.setUseCaches(false); // No caching, since it's most likely a CGI call
            connect.setDoOutput(true);

            // Inform ProgressMonitor
            monitorConnected();

            // Get Content Type
            if (hmStream.isEmpty()) {
                connect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"");
            } else {
                // See RFC 2388 for multipart/form-data
                connect.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            }

            // Send parameters
            OutputStream out = connect.getOutputStream();
            if (hmStream.isEmpty()) {
                StringBuilder data = new StringBuilder();
                createParamString(data);
                out.write(UTF8encode(data.toString()));
            } else {
                createMultipart(out);
            }
            out.flush();

        } else {
            // --- GET -----------------------------------------
            // Compose URL
            int port = getUrl.getPort();
            StringBuilder file = new StringBuilder(getUrl.getFile());
            file.append('?');
            createParamString(file);

            URL url = new URL(
                    getUrl.getProtocol(),
                    getUrl.getHost(),
                    (port >= 0 ? port : 80),
                    file.toString()
            );

            // Create Connection
            URLConnection con = url.openConnection();
            if (!(con instanceof HttpURLConnection)) {
                throw new RuntimeException("No HTTP connection");
            }
            connect = (HttpURLConnection) con;
            connect.setRequestMethod("GET");
            connect.setUseCaches(false);            // CGI call, no caching!

            // Inform ProgressMonitor
            monitorConnected();                     
        }

        // Set ProgressMonitor to the max value
        monitorDone();

        // --- Get HTTP Response ---
        return connect.getResponseCode();
    }

    /**
     * Tell the {@link ProgressMonitor} that a connection has been established.
     */
    private void monitorConnected() {
        if (monitor != null) {
            if (transferNote != null) {
                monitor.setNote(transferNote);  // Set TransferNote if available
            }
            monitor.setProgress(monitorMin);    // Set to minimum value
        }
    }

    /**
     * Make sure the {@link ProgressMonitor} has reached max at the end of the
     * {@link #doRequest()}.
     */
    private void monitorDone() {
        if (monitor != null) {
            if (transferNote != null) {
                monitor.setNote(transferNote); // Set TransferNote
            }
            monitor.setProgress(monitorMax);
        }
    }

    /**
     * Set the {@link ProgressMonitor} between min and max according to the
     * relation between current and max.
     * 
     * @param current
     *            Current counter (0 to max)
     * @param max
     *            Maximum expected counter value
     */
    private void monitorSetRelation(int current, int max) {
        if (monitor == null)
            return; // No monitor was set

        if (max > 0) {
            // Use rule of three. If current==0, monitorMin will be set,
            // for current==max, monitorMax will be set, everything else
            // is in between.
            current = Math.min(current, max);
            monitor.setProgress(monitorMin + (((monitorMax - monitorMin) * current) / max));
        } else {
            // Avoid division by zero
            monitor.setProgress(monitorMax);
        }
    }

    /**
     * Create a "multipart/form-data" request as described in RFC 1867. The data
     * sent to the output stream will be UTF-8 encoded.
     * 
     * @param out
     *            {@link OutputStream}, which will receive the output
     */
    private void createMultipart(OutputStream out) throws IOException {
        // --- Count all parameters ---
        int paramCnt = hmParam.size() + hmStream.size();
        int counter = 0;

        // --- First send all the simple parameters ---
        for (String key : hmParam.keySet()) {
            monitorSetRelation(counter++, paramCnt); // Set monitor

            StringBuilder buff = new StringBuilder();
            buff.append("--");
            buff.append(boundary);
            buff.append("\r\nContent-Disposition: form-data; name=\"");
            buff.append(URLencode(key));
            buff.append("\"\r\nContent-Type: text/plain; charset=\"utf-8\"\r\n\r\n");
            buff.append(hmParam.get(key).toString());
            buff.append("\r\n");
            out.write(UTF8encode(buff.toString()));
        }

        // --- Now send the streams ---
        for (String key : hmStream.keySet()) {
            monitorSetRelation(counter++, paramCnt); // Set monitor

            DataProvider provider = hmStream.get(key);

            StringBuilder buff = new StringBuilder();
            buff.append("--");
            buff.append(boundary);
            buff.append("\r\nContent-Disposition: form-data; name=\"");
            buff.append(URLencode(key));
            buff.append("\"; filename=\"");
            buff.append(URLencode(provider.getFileName()));
            buff.append("\"\r\nContent-Type: ");
            buff.append(provider.getMimeType());
            buff.append("\r\n\r\n");
            out.write(UTF8encode(buff.toString()));

            // Make sure the DataProvider cannot accidentally close the
            // OutputStream. This would invalidate the entire request.
            provider.sendFile(new UncloseableOutputStream(out));

            out.write("\r\n".getBytes());
        }

        // --- Close the container ---
        monitorSetRelation(counter, paramCnt); // Finish monitor
        if (!(hmParam.isEmpty() && hmStream.isEmpty())) {
            StringBuffer buff = new StringBuffer();
            buff.append("--");
            buff.append(boundary);
            buff.append("--\r\n");
            out.write(UTF8encode(buff.toString()));
        }

        out.flush();
    }

    /**
     * Create a parameter string with the given parameters. The string will have
     * the format "name1=val1&name2=val2&name3=val3", where names and values are
     * URLEncoded. The provided {@link StringBuilder} will be filled with the
     * parameters. Note that there is no check whether the parameter string
     * exceeds the maximum size.
     * 
     * @param dest
     *            {@link StringBuilder} to be filled
     */
    private void createParamString(StringBuilder dest) {
        boolean prefix = false;
        for (String key : hmParam.keySet()) {
            if (prefix) dest.append('&');
            dest.append(URLencode(key));
            dest.append('=');
            dest.append(URLencode(hmParam.get(key).toString()));
            prefix = true;
        }
    }

    /**
     * URLEncode the provided String.
     * 
     * @param msg
     *            String to be encoded
     * @return Encoded string
     */
    private String URLencode(String msg) {
        try {
            return URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new InternalError("This VM does not support UTF-8");
        }
    }

    /**
     * Get an UTF8 encoded byte array of the given string.
     * 
     * @param msg
     *            String to be encoded
     * @return UTF8 encoded byte array
     */
    private byte[] UTF8encode(String msg) {
        try {
            return msg.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new InternalError("This VM does not support UTF-8");
        }
    }

    /**
     * Return the {@link HttpURLConnection} object after {@link #doRequest()}
     * has been invoked.
     * 
     * @return {@link HttpURLConnection} or null if {@link #doRequest()} was
     *      not invoked yet.
     */
    public HttpURLConnection getConnection() {
        return connect;
    }

    /**
     * Return the server's response in an {@link InputStream} after a
     * {@link #doRequest()}.
     * 
     * @return {@link InputStream} of the server's response.
     * @throws IOException
     *             if the InputStream could not be opened.
     * @since R13
     */
    public InputStream getResult() throws IOException {
        return connect.getInputStream();
    }

    /**
     * Get the charset of the response. This method will try to evaluate the
     * "ContentType" header. If there was no such header, or if it did
     * not contain a charset, null is returned.
     * 
     * @return Charset of the response, or null
     * @since R4
     */
    public String getCharset() {
        String encoding = null;
        String type = connect.getContentType();
        if (type != null) {
            type = type.toUpperCase();
            int cspos = type.indexOf("CHARSET=");
            int csend = type.indexOf(';', cspos);
            if (cspos >= 0) {
                if (csend >= 0) {
                    encoding = type.substring(cspos + 8, csend).trim();
                } else {
                    encoding = type.substring(cspos + 8).trim();
                }
                int len = encoding.length();
                if ((encoding.charAt(0) == '"' || encoding.charAt(0) == '\'')
                        && (encoding.charAt(len - 1) == '"'
                        || encoding.charAt(len - 1) == '\'')
                   ) {
                    encoding = encoding.substring(1, len - 1).trim();
                }
            }
        }
        return encoding;
    }

    /**
     * Return a {@link Reader} to the response body after a {@link #doRequest()}.
     * The content charset sent by the server is used. If the given charset is
     * not known, an exception will be raised. If no charset was given,
     * "ISO-8859-1" is assumed.
     * 
     * @return {@link Reader} to the response body.
     * @throws UnsupportedEncodingException
     *             if the encoding given by the server, is not known to this
     *             Java platform.
     * @throws IOException
     *             if an I/O exception occured
     * @since R4
     */
    public Reader getContentReader()
    throws UnsupportedEncodingException, IOException {
        String encoding = getCharset();
        if (encoding == null)
            encoding = "ISO-8859-1";
        return new InputStreamReader(connect.getInputStream(), encoding);
    }

/* ------------------------------------------------------------------------ */

    /**
     * A DataProvider provides a data stream to be sent by the
     * {@link HTTPRequest}. The connected server will receive it as a file
     * upload.
     * <p>
     * {@link #sendFile(OutputStream)} will deliver the data. It is invoked
     * just that moment the data is required. The {@link HTTPRequest} does not
     * store the data itself, so this mechanism also allows to send huge files
     * with a small memory print.
     */
    public static interface DataProvider {

        /**
         * Return the MimeType of the file. If you are in doubt, use
         * "application/octet-stream" here.
         * 
         * @return MimeType
         */
        public String getMimeType();

        /**
         * Return the file name of the file.
         * 
         * @return Dateiname
         */
        public String getFileName();

        /**
         * Commands this FileProvider to send its data to the given
         * {@link OutputStream}.
         * <p>
         * <b>IMPORTANT:</b> you must not flush or close the stream!
         * 
         * @param out
         *            {@link OutputStream}
         * @throws IOException
         *             if transmission failed
         */
        public void sendFile(OutputStream out) throws IOException;

    }

/* ------------------------------------------------------------------------ */

    /**
     * This {@link DataProvider} implementation transmits the given
     * {@link InputStream}. It is copied to the {@link OutputStream} later,
     * so you should prefer a way to feed the {@link OutputStream} directly.
     * <p>
     * This is an abstract class, missing the {@link #getMimeType()} and
     * {@link #getFileName()} method.
     */
    public static abstract class InputStreamProvider implements DataProvider {
        protected final InputStream in;

        /**
         * Create a new InputStreamProvider.
         * 
         * @param in
         *            InputStream
         */
        public InputStreamProvider(InputStream in) {
            this.in = in;
        }

        /**
         * Transfers the {@link InputStream} to the {@link OutputStream} by
         * copying it.
         * 
         * @param out
         *            {@link OutputStream} to be filled
         */
        public void sendFile(OutputStream out) throws IOException {
            int data;
            while ((data = in.read()) >= 0) {
                out.write((byte) data);
            }
        }

    }

/* ------------------------------------------------------------------------ */

    /**
     * This {@link DataProvider} implementation uploads a {@link File} to the
     * server.
     */
    public static class FileProvider extends InputStreamProvider {
        protected File file;
        protected String mimetype;

        /**
         * Create a new FileProvider with an "application/octet-stream" mime
         * type.
         * 
         * @param file
         *            {@link File} to be sent
         */
        public FileProvider(File file)
        throws FileNotFoundException {
            this(file, "application/octet-stream");
        }

        /**
         * Create a new FileProvider with the given mime type.
         * 
         * @param file
         *            {@link File} to be sent
         * @param mimetype
         *            Mime type of that file
         */
        public FileProvider(File file, String mimetype)
        throws FileNotFoundException {
            super(new FileInputStream(file));
            this.file = file;
            this.mimetype = mimetype;
        }

        /**
         * Return the MimeType of the file.
         * 
         * @return MimeType
         */
        public String getMimeType() {
            return mimetype;
        }

        /**
         * Return the file name of the file.
         * 
         * @return File name
         */
        public String getFileName() {
            return file.getName();
        }

    }
}
