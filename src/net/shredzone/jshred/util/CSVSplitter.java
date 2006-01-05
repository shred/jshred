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
 * This class splits up a String into several columns which are
 * delimited by the delim character.
 * <p>
 * If one column contains the delimiter, it must be quoted in double
 * quotes, and quotes within the column must be doubled.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: CSVSplitter.java,v 1.1.1.1 2004/06/21 11:51:44 shred Exp $
 */
public class CSVSplitter implements Enumeration {
  private final String text;
  private int current, next;
  private final char delim;

  /**
   * Create a new CSVSplitter, with ';' as default delimiter
   *
   * @param   text      Text to be split up
   */
  public CSVSplitter( String text ) {
    this( text, ';' );
  }

  /**
   * Create a new CSVSplitter
   *
   * @param   text      Text to be split up
   * @param   delim     Delimiter character
   */
  public CSVSplitter( String text, char delim ) {
    this.text = text;
    this.delim = delim;
    this.current = 0;
    this.next = text.indexOf( String.valueOf( delim ) );
  }

  /**
   * Check if there are more tokens
   *
   * @return    true: yes, false: no
   */
  public boolean hasMoreElements() {
    return current >= 0;
  }

  /**
   * Get the next token. The same as <code>nextToken()</code> but
   * returns an Object. This was required for the Enumeration interface.
   *
   * @return    Next piece of String
   */
  public Object nextElement() {
    return nextToken();
  }

  /**
   * Get the next token. Will throw a <code>NoSuchElementException</code>
   * if there were no more tokens to read.
   *
   * @return    Next piece of String
   */
  public String nextToken() {
    if( current < 0 )
      throw new NoSuchElementException( "no more elements" );

    String part;

    //--- Extract the next column ---
    if( next < 0 ) {
      part = text.substring( current );
      current = -1;
    }else {
      part = text.substring( current, next );
      current = next+1;
      next = text.indexOf( String.valueOf( delim ), current );
    }

    //--- Handle quotation marks ---
    if( part.length()>=2 && part.startsWith("\"") && part.endsWith("\"") ) {
      part = part.substring(1, part.length()-1);
      int ix;
      while( (ix = part.indexOf("\"\"")) >= 0 ) {
        part = part.substring( 0, ix ) + part.substring( ix+1 );
      }
    }

    return part;
  }
}

/* jedit :mode=java:tabSize=2:noTabs=true:folding=java:maxLineLen=72: */