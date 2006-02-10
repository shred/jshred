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

import javax.swing.Icon;
import javax.swing.event.ChangeListener;

/**
 * This interface represents a single step within the wizard.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id$
 * @since   R13
 */
public interface Step {

  /**
   * Gets the title of this step. The title should describe the action that
   * is taken by this steps, in as less words as possible.
   *  
   * @return    This Step's title. Must not be null.
   */
  public String getTitle();

  /**
   * Gets an Icon that is associated with this Step. If null is returned,
   * a standard Icon will be used by the Wizard instead.
   * 
   * @return    This Step's icon. May be null.
   */
  public Icon getIcon();

  /**
   * Gets the Component that is used for the Step's GUI.
   * <p>
   * The background of the wizard pane may contain a wallpaper. Please
   * check the opaque settings of your component.
   * 
   * @return    This Step's component. Must not be null.
   */
  public Component getComponent();

  /**
   * Checks if this Step has a valid input.
   * <p>
   * If this is the current step, the user will not be able to proceed to
   * the next step unless true is returned here.
   * <p>
   * The user will also only be able to finish the wizard if all registered
   * Step return true here.
   * <p>
   * This method may be invoked frequently. Make sure it returns quickly.
   * 
   * @return    true: Step has valid input. false: Step has invalid input.
   */
  public boolean isValid();

  /**
   * Adds a ChangeListener to this Step. Whenever the Step's content changed
   * in a manner that may influence the isValid() result, the ChangeListeners
   * will be notified.
   * <p>
   * This means that you will notify the ChangeListeners whenever an input
   * element of the GUI was changed.
   * 
   * @param l   ChangeListener to be added
   */
  public void addChangeListener( ChangeListener l );

  /**
   * Removes a previously added ChangeListener from this Step. If the
   * ChangeListener was not added, nothing will happen.
   * 
   * @param l   ChangeListener to be removed
   */
  public void removeChangeListener( ChangeListener l );
  
}
