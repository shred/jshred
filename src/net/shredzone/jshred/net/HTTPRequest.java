/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2004 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *
 *-----------------------------------------------------------------------
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is JSHRED.
 *
 * The Initial Developer of the Original Code is
 * Richard "Shred" Körber.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

package net.shredzone.jshred.net;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.ProgressMonitor;

/**
 * This class sends a HTTP request to a server, and returns an InputStream
 * to the returned data.
 * <p>
 * All kind of parameters can be added to the request using
 * <code>addParameter()</code>.
 * <p>
 * Example:
 * <pre>
 *   HTTPRequest req = new HTTPRequest( url, HTTPRequest.POST );
 *   req.addParameter( "user", username );
 *   req.addParameter( "email", email );
 *   req.doRequest();
 *   Reader input = req.getReader();
 * </pre>
 * <p>
 * All parameters will be sent UTF-8 encoded, as proposed by W3C.
 * <p>
 * GET requests are limited in their length. This class will not take
 * care to keep within the allowed length. If you are in doubt, you
 * should use POST request.
 * <p>
 * It is possible to pass a ProgressMonitor which will be used to show how
 * many data has been transfered to the server. This is especially useful
 * for file uploads.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: HTTPRequest.java,v 1.5 2004/07/15 16:55:42 shred Exp $
 */
public class HTTPRequest {

  /**
   * Use the GET method.
   */
  public static final boolean GET = false;

  /**
   * Use the POST method.
   */
  public static final boolean POST = true;

  private HashMap           hmParam;        // Parameter Hashmap
  private HashMap           hmStream;       // Stream Hashmap
  private URL               getUrl;         // Target URL
  private final boolean     post;           // Mode (GET or POST)
  private HttpURLConnection connect;        // Connection
  private final String      boundary;       // Boundary for multiparts
  private ProgressMonitor   monitor;        // ProgressMonitor to be used
  private String            transferNote;   // String to show while transmission
  private int               monitorMin;     // Minimum Monitor value
  private int               monitorMax;     // Maximum Monitor value

  /**
   * Create a new HTTPRequest using GET.
   *
   * @param   url           Target URL for the request.
   */
  public HTTPRequest( URL url ) {
    this( url, false );
  }

  /**
   * Create a new HTTPRequest.
   *
   * @param   url           Target URL for the request.
   * @param   post          true: use POST, false: use GET
   */
  public HTTPRequest( URL url, boolean post ) {
    this.getUrl = url;
    this.post   = post;

    //--- Initialize parameter hashes ---
    hmParam  = new HashMap();
    hmStream = new HashMap();
    connect  = null;
    monitor  = null;

    //--- Create a boundary string ---
    String bcomp = "---------------------bd";
    bcomp += String.valueOf( Math.floor( Math.random() * 10000000 ) );
    boundary = bcomp;
  }

  /**
   * Add a parameter to the HTTPRequest. The <code>toString()</code>
   * result of the value is transferred to the server. The value will
   * only be evaluated when <code>doRequest()</code> is invoked, NOT
   * at the time you have passed the parameter here. Keep this in mind!
   * <p>
   * If a null was passed as value, the parameter is not sent at all.
   * <p>
   * Note that a parameter can only be passed once. If you add it again,
   * it will replace the previous parameter with the same name. Also take
   * care not to exceed the limits of a GET request. Use POST if you
   * are in doubt.
   * <p>
   * HTTPRequest will take care for proper encoding of the parameter
   * names and values. You won't need to urlencode them.
   *
   * @param   name          Parameter name
   * @param   value         Parameter value, may be null
   */
  public void addParameter( String name, Object value ) {
    if( name==null )
      throw new NullPointerException("You must provide a name");
    if( value!=null )
      hmParam.put( name, value );
  }

  /**
   * Convenience call to add an int parameter to the HTTPRequest.
   *
   * @param   name          Parameter name
   * @param   value         Parameter value
   */
  public void addParameter( String name, int value ) {
    addParameter( name, new Integer( value ) );
  }

  /**
   * Convenience call to add a boolean parameter to the HTTPRequest.
   * If the boolean value is false, no parameter will be sent at all.
   * If the boolean value was true, a "1" is transferred as parameter
   * value to the server. This method somehow simulates a HTML checkbox.
   *
   * @param   name          Parameter name
   * @param   value         Parameter value
   */
  public void addParameter( String name, boolean value ) {
    if( value )
      addParameter( name, 1 );
  }

  /**
   * Add a file to the HTTPRequest. You must use POST requests in order
   * to upload files to the server. The file will be sent using the
   * mime type "application/octet-stream". The file is not kept in
   * memory, but sent using a data stream during transmission, so
   * HTTPRequest is also able to upload large files with a small
   * memory print.
   *
   * @param   name          Parameter name
   * @param   file          File to be sent
   */
  public void addFile( String name, File file )
  throws FileNotFoundException {
    addDataProvider( name, new FileProvider( file ) );
  }

  /**
   * Add a file to the HTTPRequest. You must use POST requests in order
   * to upload files to the server. The file is not kept in
   * memory, but sent using a data stream during transmission, so
   * HTTPRequest is also able to upload large files.
   *
   * @param   name          Parameter name
   * @param   file          File to be sent
   * @param   mimetype      Mime type of the file to be transmitted
   */
  public void addFile( String name, File file, String mimetype )
  throws FileNotFoundException {
    addDataProvider( name, new FileProvider( file, mimetype ) );
  }

  /**
   * Add a DataProvider to the HTTPRequest. You must use POST requests
   * in order to upload files to the server. The file is not kept in
   * memory, but sent using a data stream during transmission, so
   * HTTPRequest is also able to upload large files.
   *
   * @param   name          Parameter name
   * @param   provider      Source for the data to be uploaded
   */
  public void addDataProvider( String name, DataProvider provider ) {
    if( !isPost() )
      throw new IllegalArgumentException( "POST mode required for sending files" );

    if( name==null || provider==null )
      throw new NullPointerException( "No name or provider given" );

    hmStream.put( name, provider );
  }

  /**
   * Return if the request will be sent in POST or GET mode.
   *
   * @return  true:POST, false:GET
   */
  public boolean isPost() {
    return post;
  }

  /**
   * Defines a ProgressMonitor for the transmission process. After
   * <code>doRequest()</code> connected to the server, the transfer
   * string is shown as a note.
   * <p>
   * The ProgressMonitor will start at the left corner during start
   * of transmission, and will end in the right corner of the gauge.
   *
   * @param   monitor       ProgressMonitor to be used.
   * @param   transfer      Message to be shown after connection.
   */
  public void setProgressMonitor( ProgressMonitor monitor, String transfer ) {
    setProgressMonitor( monitor, transfer, 0, 100 );
  }

  /**
   * Defines a ProgressMonitor for the transmission process. After
   * <code>doRequest()</code> connected to the server, the transfer
   * string is shown as a note. During transmission, a value between
   * min and max is shown according to the process of the transmission.
   * After transmission, max will always be the current value of the
   * ProgressMonitor.
   * <p>
   * You can use min and max if the HTTPRequest is just one step in
   * a chain of several steps to be shown in the ProgressMonitor.
   * <p>
   * Note that the response transfer is not included in the
   * ProgressMonitor. This is because there is no way to find out the
   * response size from the server before the request has been entirely
   * sent to it.
   *
   * @param   monitor       ProgressMonitor to be used.
   * @param   transfer      Message to be shown after connection.
   * @param   min           Minimum value during transmission
   * @param   max           Maximum value at the end of transmission
   */
  public void setProgressMonitor( ProgressMonitor monitor, String transfer, int min, int max ) {
    this.monitor      = monitor;
    this.transferNote = transfer;
    this.monitorMin   = min;
    this.monitorMax   = max;
  }

  /**
   * Send the request to the server. You cannot reuse the request after
   * it has been sent. If you have previously set a ProgressMonitor, it
   * will be used.
   *
   * @return    Return code of the server, see HttpURLConnection
   * @throws    IOException               IO exception
   * @throws    MalformedURLException     URL was malformed
   */
  public int doRequest() throws IOException, MalformedURLException {
    if( connect != null )
      throw new RuntimeException( "already connected" );

    if( isPost() ) {
      //--- POST ----------------------------------------
      // Create Connection
      URLConnection con = getUrl.openConnection();
      if( ! (con instanceof HttpURLConnection) ) {
        throw new RuntimeException( "No HTTP connection" );
      }
      connect = (HttpURLConnection)con;
      connect.setUseCaches( false );      // No caching, since it's probably a CGI call
      connect.setDoOutput( true );

      monitorConnected();                 // Inform ProgressMonitor

      // Get Content Type
      if( hmStream.isEmpty() ) {
        connect.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded; charset=\"utf-8\"" );
      }else {
        // See RFC 2388 for multipart/form-data
        connect.setRequestProperty( "Content-Type", "multipart/form-data; boundary="+boundary );
      }

      // Send parameters
      OutputStream out = connect.getOutputStream();
      if( hmStream.isEmpty() ) {
        StringBuffer data = new StringBuffer();
        createParamString( data );
        out.write( UTF8encode( data.toString() ) );
      }else {
        createMultipart( out );
      }
      out.flush();

    }else {
      //--- GET -----------------------------------------
      // Compose URL
      int port = getUrl.getPort();
      StringBuffer file = new StringBuffer( getUrl.getFile() );
      file.append( '?' );
      createParamString( file );

      URL url = new URL(
        getUrl.getProtocol(),
        getUrl.getHost(),
        (port>=0 ? port : 80),
        file.toString()
      );

      // Create Connection
      URLConnection con = url.openConnection();
      if( ! (con instanceof HttpURLConnection) ) {
        throw new RuntimeException( "No HTTP connection" );
      }
      connect = (HttpURLConnection)con;
      connect.setUseCaches( false );      // CGI call, no caching!

      monitorConnected();                 // Inform ProgressMonitor
    }

    monitorDone();                        // Set ProgressMonitor to the max value

    //--- Get HTTP Response ---
    return connect.getResponseCode();
  }

  /**
   * Tell the ProgressMonitor that a connection has been established-
   */
  private void monitorConnected() {
    if( monitor!=null ) {
      if( transferNote!=null ) {
        monitor.setNote( transferNote );    // Set TransferNote if available
      }
      monitor.setProgress( monitorMin );    // Set to minimum value
    }
  }

  /**
   * Make sure the ProgressMonitor has reached max at the end of the
   * doRequest().
   */
  private void monitorDone() {
    if( monitor!=null ) {
      if( transferNote!=null ) {
        monitor.setNote( transferNote );    // Set TransferNote
      }
      monitor.setProgress( monitorMax );
    }
  }

  /**
   * Set the ProgressMonitor between min and max according to the
   * relation between current and max.
   *
   * @param   current       Current counter (0 to max)
   * @param   max           Maximum expected counter value
   */
  private void monitorSetRelation( int current, int max ) {
    if( monitor==null ) return;             // No monitor was set

    if( max>0 ) {
      // Use rule of three. If current==0, monitorMin will be set,
      // for current==max, monitorMax will be set, everything else
      // is in between.
      current = Math.min( current, max );
      monitor.setProgress( monitorMin + ( ( (monitorMax-monitorMin) * current ) / max ) );
    }else {
      // Avoid division by zero
      monitor.setProgress( monitorMax );
    }
  }

  /**
   * Create a "multipart/form-data" request as described in RFC 1867.
   * The data sent to the output stream will be UTF-8 encoded.
   *
   * @param   out       OutputStream, which will receive the output
   */
  private void createMultipart( OutputStream out ) throws IOException {
    String key;
    Iterator it;

    //--- Count all parameters ---
    int paramCnt = hmParam.size() + hmStream.size();
    int counter = 0;

    //--- First send all the simple parameters ---
    it = hmParam.keySet().iterator();
    while( it.hasNext() ) {
      monitorSetRelation( counter++, paramCnt );    // Set monitor

      key = it.next().toString();

      StringBuffer buff = new StringBuffer();
      buff.append( "--" );
      buff.append( boundary );
      buff.append( "\r\nContent-Disposition: form-data; name=\"" );
      buff.append( URLencode( key ) );
      buff.append( "\"\r\nContent-Type: text/plain; charset=\"utf-8\"\r\n\r\n" );
      buff.append( hmParam.get( key ).toString() );
      buff.append( "\r\n" );
      out.write( UTF8encode( buff.toString() ) );
    }

    //--- Now send the streams ---
    it = hmStream.keySet().iterator();
    while( it.hasNext() ) {
      monitorSetRelation( counter++, paramCnt );    // Set monitor

      key = it.next().toString();
      DataProvider provider = (DataProvider) hmStream.get( key );

      StringBuffer buff = new StringBuffer();
      buff.append( "--" );
      buff.append( boundary );
      buff.append( "\r\nContent-Disposition: form-data; name=\"" );
      buff.append( URLencode( key ) );
      buff.append( "\"; filename=\"" );
      buff.append( URLencode( provider.getFileName() ) );
      buff.append( "\"\r\nContent-Type: " );
      buff.append( provider.getMimeType() );
      buff.append( "\r\n\r\n" );
      out.write( UTF8encode( buff.toString() ) );
      provider.sendFile( out );
      out.write( "\r\n".getBytes() );
    }

    //--- Close the container ---
    monitorSetRelation( counter, paramCnt );        // Finish monitor
    if( !( hmParam.isEmpty() && hmStream.isEmpty() ) ) {
      StringBuffer buff = new StringBuffer();
      buff.append( "--" );
      buff.append( boundary );
      buff.append( "--\r\n" );
      out.write( UTF8encode( buff.toString() ) );
    }

    out.flush();
  }

  /**
   * Create a parameter string with the given parameters. The string
   * will have the format "name1=val1&name2=val2&name3=val3", where
   * names and values are URLEncoded. The provided StringBuffer will
   * be filled with the parameters. Note that there is no check whether
   * the parameter string exceeds the maximum size.
   *
   * @param   dest          StringBuffer to be filled
   */
  private void createParamString( StringBuffer dest ) {
    Iterator it = hmParam.keySet().iterator();
    String key;
    while( it.hasNext() ) {
      key = it.next().toString();

      dest.append( URLencode( key ) );
      dest.append( '=' );
      dest.append( URLencode( hmParam.get( key ).toString() ) );

      if( it.hasNext() ) dest.append('&');
    }
  }

  /**
   * URLEncode the provided String.
   *
   * @param     msg       String to be encoded
   * @return    Encoded string
   */
  private String URLencode( String msg ) {
    try {
      return URLEncoder.encode( msg, "UTF-8" );
    }catch( Throwable t ) {
      // According to Sun's java.nio.charset.Charset javadoc, every
      // Java platform must implement "UTF-8", so we should never reach
      // the catch block! Anyhow a (deprecated) fallback is provided here.
      return URLEncoder.encode( msg );
    }
  }
  
  /**
   * Get an UTF8 encoded byte array of the given string.
   *
   * @param     msg       String to be encoded
   * @return    UTF8 encoded byte array
   */
  private byte[] UTF8encode( String msg ) {
    try {
      return msg.getBytes( "UTF-8" );
    }catch( Throwable t ) {
      // According to Sun's java.nio.charset.Charset javadoc, every
      // Java platform must implement "UTF-8", so we should never reach
      // the catch block! Anyhow a fallback is provided here.
      return msg.getBytes();
    }
  }

  /**
   * Return the HttpURLConnection object after <code>doRequest</code>
   * has been invoked.
   *
   * @return    HttpURLConnection or null if doRequest was not invoked
   *            yet.
   */
  public HttpURLConnection getConnection() {
    return connect;
  }

  /**
   * Return the server's response in a Reader after a <code>doRequest</code>.
   * <p>
   * This method ignores the content encoding sent by the server. The
   * local machine's encoding is used instead.
   *
   * @return      Reader or null if an error occured.
   * @deprecated  Use getContentReader() instead.
   */
  public Reader getReader() {
    try {
      return new InputStreamReader( connect.getInputStream() );
    }catch( IOException e ) {
      return null;
    }
  }

  /**
   * Get the charset of the response. This method will try to evaluate
   * the "ContentType" header. If there was no ContentType header, or
   * if it did not contain a charset, null is returned.
   *
   * @return    Charset of the response, or null
   */
  public String getCharset() {
    String encoding = null;
    String type = connect.getContentType();
    if( type!=null ) {
      type = type.toUpperCase();
      int cspos = type.indexOf( "CHARSET=" );
      int csend = type.indexOf( ';', cspos );
      if( cspos>=0 ) {
        if( csend>=0 ) {
          encoding = type.substring( cspos+8, csend ).trim();
        }else {
          encoding = type.substring( cspos+8 ).trim();
        }
        int len = encoding.length();
        if(   ( encoding.charAt(0)=='"' || encoding.charAt(0)=='\'' )
           && ( encoding.charAt(len-1)=='"' || encoding.charAt(len-1)=='\'' ) ) {
          encoding = encoding.substring(1,len-1).trim();
        }
      }
    }
    return encoding;
  }
  
  /**
   * Return a reader to the response body after a <code>doRequest</code>.
   * The content charset sent by the server is used. If the given
   * charset is not known, an exception will be raised. If no charset
   * was given, "ISO-8859-1" is assumed.
   *
   * @return    Reader to the response body.
   * @throws    UnsupportedEncodingException    if the encoding given
   *            by the server, is not known to this Java platform.
   * @throws    IOException     if an I/O exception occured
   */
  public Reader getContentReader()
  throws UnsupportedEncodingException, IOException {
    String encoding = getCharset();
    if( encoding==null ) encoding = "ISO-8859-1";
    return new InputStreamReader( connect.getInputStream(), encoding );
  }

/*------------------------------------------------------------------------*/

  /**
   * A DataProvider provides a data stream to be sent by the HTTPRequest.
   * The connected server will receive it as a file upload.
   * <p>
   * <code>sendFile()</code> will deliver the data. It is invoked just
   * in time when the data is required. The HTTPRequest does not store
   * the data itself, so this mechanism also allows to send huge files
   * with a small memory print.
   */
  public static interface DataProvider {

    /**
     * Return the MimeType of the file. If you are in doubt, use
     * "application/octet-stream" here.
     *
     * @return    MimeType
     */
    public String getMimeType();

    /**
     * Return the file name of the file.
     *
     * @return    Dateiname
     */
    public String getFileName();

    /**
     * Commands this FileProvider to send its data to the given
     * OutputStream.
     * <p>
     * <b>IMPORTANT:</b> you must not flush or close the stream!
     *
     * @param   out       OutputStream
     * @throws  IOException   if transmission failed
     */
    public void sendFile( OutputStream out ) throws IOException;

  }

/*------------------------------------------------------------------------*/

  /**
   * This DataProvider implementation transmits the given InputStream.
   * It is copied to the OutputStream later, so you should prefer a
   * way to feed the OutputStream directly.
   * <p>
   * This is an abstract class, missing the getMimeType() and
   * getFileName() method.
   */
  public static abstract class InputStreamProvider implements DataProvider {

    /**
     * The InputStream
     */
    protected final InputStream in;

    /**
     * Create a new InputStreamProvider.
     *
     * @param   in        InputStream
     */
    public InputStreamProvider( InputStream in ) {
      this.in = in;
    }

    /**
     * Transfers the InputStream to the OutputStream by copying it.
     *
     * @param   out       OutputStream to be filled
     */
    public void sendFile( OutputStream out ) throws IOException {
      int data;
      while( (data = in.read()) >= 0 ) {
        out.write( (byte) data );
      }
    }

  }

/*------------------------------------------------------------------------*/

  /**
   * This DataProvider implementation uploads a File to the server.
   */
  public static class FileProvider extends InputStreamProvider {
    protected File file;
    protected String mimetype;

    /**
     * Create a new FileProvider with an "application/octet-stream"
     * mime type.
     *
     * @param   file      File to be sent
     */
    public FileProvider( File file )
    throws FileNotFoundException {
      this( file, "application/octet-stream" );
    }

    /**
     * Create a new FileProvider with the given mime type.
     *
     * @param   file      File to be sent
     * @param   mimetype  Mime type of that file
     */
    public FileProvider( File file, String mimetype )
    throws FileNotFoundException {
      super( new FileInputStream( file ) );
      this.file = file;
      this.mimetype = mimetype;
    }

    /**
     * Return the MimeType of the file.
     *
     * @return    MimeType
     */
    public String getMimeType() {
      return mimetype;
    }

    /**
     * Return the file name of the file.
     *
     * @return    File name
     */
    public String getFileName() {
      return file.getName();
    }

  }
}

/* jedit :mode=java:tabSize=2:noTabs=true:folding=java:maxLineLen=72: */
