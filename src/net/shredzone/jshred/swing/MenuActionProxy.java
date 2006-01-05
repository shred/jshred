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
import java.awt.event.*;
import java.beans.*;
import java.io.Serializable;

/**
 * The MenuActionProxy proxies Actions to be used in menus. It takes
 * care about a proper scaling of the Action icon to a nice menu size.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: MenuActionProxy.java,v 1.4 2004/07/20 14:00:48 shred Exp $
 */
public class MenuActionProxy implements Action, Serializable {
  private final Action master;
  private Dimension dim;

  /**
   * Creates a new MenuActionProxy. The default icon dimensions
   * (16x16) are used.
   *
   * @param   a       Action
   */
  public MenuActionProxy( Action a ) {
    this( a, new Dimension( 16,16 ) );
  }

  /**
   * Creates a new MenuActionProxy with given Icon dimensions.
   *
   * @param   a       Action
   * @param   dim     Icon dimensions
   */
  public MenuActionProxy( Action a, Dimension dim ) {
    master = a;
    this.dim = dim;
  }

  /**
   * Set new Icon dimensions for the menu icon. Default is 16x16 pixels.
   *
   * @param   dim       New icon dimensions
   */
  public void setIconDimension( Dimension dim ) {
    this.dim = dim;
  }

  /**
   * Get the current dimension of the menu icon.
   *
   * @return    Icon dimension
   */
   public Dimension getIconDimension() {
     return dim;
   }

   public Object getValue( String key ) {
    Object val = master.getValue( key );

    //--- Scale icon ---
    if( key.equals( SMALL_ICON ) && ( val instanceof ImageIcon ) ) {
      ImageIcon icon = (ImageIcon) val;
      if( icon.getIconWidth()!=dim.width || icon.getIconHeight()!=dim.height ) {
        Image img = icon.getImage();
        img = img.getScaledInstance( dim.width, dim.height, Image.SCALE_SMOOTH );
        val = new ImageIcon( img );
      }
    }

    return val;
  }

  public void putValue( String key, Object value ) {
    master.putValue( key, value );
  }

  public void setEnabled( boolean b ) {
    master.setEnabled( b );
  }

  public boolean isEnabled() {
    return master.isEnabled();
  }

  public void addPropertyChangeListener( PropertyChangeListener listener ) {
    master.addPropertyChangeListener( listener );
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    master.removePropertyChangeListener( listener );
  }

  public void actionPerformed( ActionEvent e ) {
    master.actionPerformed( e );
  }
}

/* jedit :mode=java:tabSize=2:noTabs=true:folding=java:maxLineLen=72: */
