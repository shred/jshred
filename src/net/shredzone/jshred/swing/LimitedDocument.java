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

package net.shredzone.jshred.swing;

import javax.swing.text.*;

/**
 * This PlainDocument will limit the input to a certain length.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: LimitedDocument.java,v 1.3 2004/08/23 23:49:15 shred Exp $
 */
public class LimitedDocument extends PlainDocument {
  private int maxLength = -1;   // Maximum length

  /**
   * Create a new Document with the given maximum length.
   *
   * @param   len       Maximum length in characters
   */
  public LimitedDocument( int len ) {
    super();
    maxLength = len;
  }

  /**
   * Overwrite insertString to check the input.
   *
   * @param   offs      Offset
   * @param   str       String
   * @param   a         AttributeSet
   */
  public void insertString( int offs, String str, AttributeSet a )
  throws BadLocationException {
    if( str==null ) return;

    if(   (maxLength==-1)
       || ((getLength() + str.length()) <= maxLength)) {
      //--- Maximum length was not reached ---
      super.insertString( offs, str, a );
    }else {
      //--- Maximum length was reached, truncate! ---
      super.insertString( offs, str.substring(0, maxLength - getLength()), a );
    }
  }
}
