/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2006 Richard "Shred" Körber
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

package net.shredzone.jshred.swing.wizard;

import java.awt.Component;

import javax.swing.JLabel;

/**
 * This Step implementation only shows an informational text.
 * 
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id$
 * @since   R13
 */
public class InformationStep extends AbstractStep {
  protected String title;
  protected JLabel jlContent;
  
  /**
   * Creates a new InformationStep.
   *  
   * @param title       Title of this step. Must not be null.
   * @param content     Text content. Must not be null. The text may meet
   *    the features of all Swing texts, like simple HTML formatting.
   */
  public InformationStep( String title, String content ) {
    if( title==null ) throw new IllegalArgumentException("title must not be null");
    if( content==null ) throw new IllegalArgumentException("content must not be null");
    
    this.title = title;
    this.jlContent = new JLabel( content );
  }

  /**
   * Gets the title of this step. The title should describe the action that
   * is taken by this steps, in as less words as possible.
   *  
   * @return    This Step's title.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the Component that is used for the Step's GUI.
   * 
   * @return    This Step's component.
   */
  public Component getComponent() {
    return jlContent;
  }

}
