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

package net.shredzone.jshred.util;

import java.util.*;

/**
 * This class handles one CSV line.
 * <p>
 * If you pass a String, it will be split into several columns which are
 * delimited by the delim character. It will take care for proper
 * unquoting if a column was quoted.
 * <p>
 * You can also add and remove columns at will. The toString() method
 * will result in a valid CSV line that can be written out.
 * <p>
 * The CSV format is: Each row ends with a newline. A row consists of
 * one ore more columns, each separated by a delimiter character. If
 * one column contains the delimiter character, the entire column must
 * be quoted in double quotes. If the column also contains the quote
 * character, then the quote characters within the column must be doubled.
 * <p>
 * CSVLine inherits the List interface for easier access.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: CSVLine.java,v 1.4 2005/01/11 19:37:41 shred Exp $
 */
public class CSVLine extends ArrayList {
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
   * @param   delim     Delimiter
   */
  public CSVLine( char delim ) {
    setDelimiter( delim );
  }

  /**
   * Create a new CSVLine, with the given text and ';' as default
   * delimiter.
   *
   * @param   text      Text to be split up
   */
  public CSVLine( String text ) {
    this();
    addLine( text );
  }

  /**
   * Create a new CSVLine with the given text and the given delimiter.
   *
   * @param   text      Text to be split up
   * @param   delim     Delimiter character
   */
  public CSVLine( String text, char delim ) {
    this( delim );
    addLine( text );
  }

  /**
   * Add a CSV line to the current content. All columns are added to
   * the end of the current column list. This is most probably not
   * what you wanted to do!
   * <p>
   * If you want to use this class with a new line, use <code>clear()</code>
   * before:
   * <pre>
   *   csvline.clear();
   *   csvline.addLine( line );
   * </pre>
   *
   * @param   text      Line to be added
   */
  public void addLine( String text ) {
    //--- Split the row ---
    final int max = text.length();
    int start = 0;
    int end;

    while( start < max ) {
      if( text.charAt(start)=='"' ) {
        //--- Quoted Column ---
        end = start-1;
        do {
          end = text.indexOf( "\"", end+2 );
        }while( end>0 && end<(max-1) && text.charAt(end+1)=='"' );
        if( end<0 )
          throw new IllegalArgumentException( "ending quote char missing" );
        if( end<(max-1) && text.charAt(end+1)!=delim )
          throw new IllegalArgumentException( "bad quoting" );

        String content = text.substring( start+1, end );
        content = content.replaceAll( "\\\"\\\"", "\"" );
        add( content );
        start = end+1;
        if( start<max && text.charAt(start)==delim ) {
          start++;
          if( start>=max )
            add( "" );
        }

      }else {
        //--- Unquoted Column ---
        end = text.indexOf( String.valueOf( delim ), start );
        if( end>=0 ) {
          add( text.substring( start, end ) );
          start = end+1;
          if( start>=max )
            add( "" );
        }else {
          add( text.substring( start ) );
          start = max;
        }
      }
    }
  }

  /**
   * Set the delimiter character. The quote character '"' is not allowed
   * as a delimiter, and will throw an IllegalArgumentException.
   *
   * @param     delim       Delimiter
   */
  public void setDelimiter( char delim ) {
    if( delim=='"' )
      throw new IllegalArgumentException( "quote char is not a valid delimiter" );
    this.delim = delim;
  }

  /**
   * Get the delimiter character.
   *
   * @return    Delimiter
   */
  public char getDelimiter() {
    return delim;
  }

  /**
   * Get a valid CSV line of the current content, using the current
   * delimiter char.
   * <p>
   * <em>Note:</em> There is no newline character at the end of the
   * line!
   *
   * @return    CSV line, without newline character
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    boolean first = true;
    Iterator it = iterator();
    while( it.hasNext() ) {
      if( !first ) {
        buffer.append( delim );
      }
      Object colobj = it.next();
      String col = ( colobj!=null ? colobj.toString() : "" );
      if( col.indexOf('"') >= 0 ) {
        //--- Escaping needed ---
        col = col.replaceAll( "\\\"", "\"\"" );
        buffer.append( '"' );
        buffer.append( col );
        buffer.append( '"' );
      }else {
        //--- No escaping needed ---
        buffer.append( col );
      }
      first = false;
    }
    return buffer.toString();
  }

}
