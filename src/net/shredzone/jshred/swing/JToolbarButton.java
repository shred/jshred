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
import java.awt.*;

/**
 * This JButton is to be used in toolbars. It takes care that the button never
 * has a text, never gets the focus and has no borders.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JToolbarButton.java,v 1.5 2004/11/29 14:45:46 shred Exp $
 */
public class JToolbarButton extends JButton {

  /**
   * Creates a new basic JToolbarButton.
   * 
   * @since   R8
   */
  public JToolbarButton() {
    super();
    init();
  }

  /**
   * Creates a new JToolbarButton with an icon.
   * 
   * @param   icon      Icon to be used.
   * @since   R8
   */
  public JToolbarButton( Icon icon ) {
    super( icon );
    init();
  }

  /**
   * Creates a new JToolbarButton for a certain action.
   *
   * @param   a     Action
   */
  public JToolbarButton( Action a ) {
    super( a );
    init();
  }
  
  /**
   * Initialize all parameters for a JToolbarButton.
   */
  private void init() {
    setText( "" );
    setRequestFocusEnabled( false );
    setFocusable( false );
    setMargin( new Insets( 0,0,0,0 ) );
    setOpaque( false );
  }

}
