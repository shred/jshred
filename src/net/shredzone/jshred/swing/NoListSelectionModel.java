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

import javax.swing.*;

/**
 * This DefaultListSelectionModel also allows that no item can be
 * selected at all. Use this selection model if you want to disable a
 * JList, but keep the content readable. Besides allowing a fourth
 * selection mode (<code>NO_SELECTION</code>), it completely behaves
 * like the DefaultListSelectionModel.
 * <p>
 * For the documentation, also see the DefaultListSelectionModel JavaDoc.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: NoListSelectionModel.java,v 1.2 2004/08/23 23:49:15 shred Exp $
 * @since   R6
 */
public class NoListSelectionModel extends DefaultListSelectionModel {

  /**
   * A value for the selectionMode property: no list index can be
   * selected at all.
   */
  public static final int NO_SELECTION = 9181;
  
  private boolean noselect = false;
    
  public void setSelectionInterval( int index0, int index1 ) {
    if( !noselect ) {
      super.setSelectionInterval( index0, index1 );
    }
  }

  public void addSelectionInterval( int index0, int index1 ) {
    if( !noselect ) {
      super.addSelectionInterval( index0, index1 );
    }
  }

  public void insertIndexInterval( int index, int length, boolean before ) {
    if( !noselect ) {
      super.insertIndexInterval( index, length, before );
    }
  }
  
  public void setSelectionMode( int selectionMode ) {
    if( selectionMode==NO_SELECTION ) {
      super.setSelectionMode( SINGLE_SELECTION );
      clearSelection();
      noselect = true;
    }else {
      noselect = false;
      super.setSelectionMode( selectionMode );
    }
  }
  
  public int getSelectionMode() {
    if( noselect ) {
      return NO_SELECTION;
    }else {
      return super.getSelectionMode();
    }
  }

}
