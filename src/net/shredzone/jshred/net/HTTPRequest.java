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
 * GET requests are limited in their length. This class will not take
 * care to keep within the allowed length. If you are in doubt, you
 * should use POST request.
 * <p>
 * The result is not parsed in any way. You will get HTTP headers and
 * content, and have to split it up yourself.
 * <p>
 * It is possible to pass a ProgressMonitor which will be used to show how
 * many data has been transfered to the server. This is especially useful
 * for file uploads.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: HTTPRequest.java,v 1.3 2004/06/22 21:57:45 shred Exp $
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
   * Note that a parameter can only be passed once. If you add it again,
   * it will replace the previous parameter with the same name. Also take
   * care not to exceed the limits of a GET request. Use POST if you
   * are in doubt.
   * <p>
   * All parameter names and values are urlencoded automatically. You
   * don't need to encode it yourself.
   *
   * @param   name          Parameter name
   * @param   value         Parameter value
   */
  public void addParameter( String name, Object value ) {
    if(name==null || value==null)
      throw new NullPointerException("You must provide name and value");
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
   * If the boolean value was true, a "1" is transferred to the server.
   * This method simulates a HTML checkbox somehow.
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
   * HTTPRequest is also able to upload large files.
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
   * After transmission, max will be the current value of the
   * ProgressMonitor.
   * <p>
   * You can use min and max if the HTTPRequest is just one step in
   * a chain of several steps to be shown in the ProgressMonitor.
   * <p>
   * Note that currently the response transfer will not be shown
   * in the ProgressMonitor. This is because the HTTPRequest class
   * does not know the size of the response in advance.
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
        connect.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
      }else {
        connect.setRequestProperty( "Content-Type", "multipart/form-data, boundary="+boundary );
      }

      // Send parameters
      OutputStream out = connect.getOutputStream();
      PrintStream ps = new PrintStream( out );
      if( hmStream.isEmpty() ) {
        StringBuffer data = new StringBuffer();
        createParamString( data );
        ps.print( data.toString() );
      }else {
        createMultipart( ps );
      }
      ps.flush();

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
   * @param   current       Current counter (0 bis max)
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
   *
   * @param   ps        PrintStream, which will receive the output
   */
  private void createMultipart( PrintStream ps ) throws IOException {
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

      ps.print( "--" );
      ps.println( boundary );
      ps.print( "Content-Disposition: form-data; name=\"" );
      ps.print( URLencode( key ) );
      ps.println( '"');
      ps.println();
      ps.println( hmParam.get( key ).toString() );
    }

    //--- Now send the streams ---
    it = hmStream.keySet().iterator();
    while( it.hasNext() ) {
      monitorSetRelation( counter++, paramCnt );    // Set monitor

      key = it.next().toString();
      DataProvider provider = (DataProvider) hmStream.get( key );

      ps.print( "--" );
      ps.println( boundary );
      ps.print( "Content-Disposition: form-data; name=\"" );
      ps.print( URLencode( key ) );
      ps.print( "\"; filename=\"" );
      ps.print( URLencode( provider.getFileName() ) );
      ps.println( '"' );
      ps.print( "Content-Type: " );
      ps.println( provider.getMimeType() );
      ps.println();
      provider.sendFile( ps );
      ps.println();
    }

    //--- Close the container ---
    monitorSetRelation( counter, paramCnt );        // Finish monitor
    if( !( hmParam.isEmpty() && hmStream.isEmpty() ) ) {
      ps.print( "--" );
      ps.print( boundary );
      ps.println( "--" );
    }

    ps.flush();
  }

  /**
   * Create a parameter string with the given parameters. The string
   * will have the format "name1=val1&name2=val2&name3=val3", where
   * names and values are URLEncoded. The provided StringBuffer will
   * be filled with the parameters.
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
   * URLEncode the provided String. Since JDK1.4, the URLEncoder.encode()
   * method is deprecated. The replacement might throw an exception which
   * does not come in quite handy here. This method will invoke the JDK1.4
   * method with "UTF-8" as encoding (which should always work), and will
   * use the deprecated method if this fails.
   *
   * @param     msg       String to be encoded
   * @return    Encoded string
   */
  private String URLencode( String msg ) {
    try {
      return URLEncoder.encode( msg, "UTF-8" );
    }catch( Throwable t ) {
      return URLEncoder.encode( msg );    // Fallback to JDK1.3
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
   *
   * @return    Reader or null
   */
  public Reader getReader() {
    try {
      return new InputStreamReader( connect.getInputStream() );
    }catch( IOException e ) {
      return null;
    }
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
