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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * An abstract default implementation of the Step interface.
 * <p>
 * Deriving classes must implement the <code>getTitle()</code> and
 * <code>getComponent()</code> methods.
 * 
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id$
 * @since   R13
 */
public abstract class AbstractStep implements Step {

  protected List lChangeListeners = new ArrayList();
  
  /**
   * Gets an Icon that is associated with this Step. AbstractStep will
   * return null here, so the wizard will show a standard icon. Override
   * this method to set your own icon.
   * 
   * @return    This Step's icon. May be null.
   */
  public Icon getIcon() {
    return null;
  }

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
   * <p>
   * The AbstractStep default implementation will always return true.
   * 
   * @return    true: Step has valid input. false: Step has invalid input.
   */
  public boolean isValid() {
    return true;
  }

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
  public void addChangeListener( ChangeListener l ) {
    if(! lChangeListeners.contains( l ) ) {
      lChangeListeners.add( l );
    }
  }

  /**
   * Removes a previously added ChangeListener from this Step. If the
   * ChangeListener was not added, nothing will happen.
   * 
   * @param l   ChangeListener to be removed
   */
  public void removeChangeListener( ChangeListener l ) {
    lChangeListeners.remove( l );
  }
  
  /**
   * Notifies all ChangeListeners that this Step's content was changed.
   */
  protected void fireStateChanged() {
    Iterator it = lChangeListeners.iterator();
    ChangeEvent evt = null;
    while( it.hasNext() ) {
      if( evt==null ) {
        // Lazily create the ChangeEvent
        evt = new ChangeEvent( this );
      }
      ChangeListener l = (ChangeListener) it.next();
      l.stateChanged( evt );
    }
  }
  
}
