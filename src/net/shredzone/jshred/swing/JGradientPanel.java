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
 * This JPanel shows a color gradient in the background. You can select
 * the direction and the starting and ending color.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JGradientPanel.java,v 1.6 2005/01/19 09:35:09 shred Exp $
 */
public class JGradientPanel extends JPanel {
  private static final long serialVersionUID = 4123386540283015480L;
  public static final boolean VERTICAL   = true;
  public static final boolean HORIZONTAL = false;
  public static final Color   BACKGROUND = null;

  private Color cTop = null;
  private Color cBottom = null;
  private boolean vertical = true;

  /**
   * Create a vertical JGradientPanel with the given top and bottom
   * color. If null is given as color, the standard background color
   * will be used instead.
   *
   * @param   top         Top color or null
   * @param   bottom      Bottom color or null
   */
  public JGradientPanel( Color top, Color bottom ) {
    this( top, bottom, true );
  }

  /**
   * Create a JGradientPanel with the given top and bottom color in
   * the given direction. If null is given as color, the standard
   * background color will be used instead.
   *
   * @param   top         Top color or null
   * @param   bottom      Bottom color or null
   * @param   vertical    true: vertical, false: horizontal
   */
  public JGradientPanel( Color top, Color bottom, boolean vertical ) {
    this.cTop     = top;
    this.cBottom  = bottom;
    this.vertical = vertical;
    setOpaque( false );
  }

  /**
   * Change the top/left color. null means to use the background color
   * instead.
   *
   * @param   top         New top/left color
   */
  public void setColorTop( Color top ) {
    this.cTop = top;
    repaint();
  }

  /**
   * Get the current top/left color.
   *
   * @return  Top/left color or null.
   */
  public Color getColorTop() {
    return cTop;
  }

  /**
   * Change the bottom/right color. null means to use the background color
   * instead.
   *
   * @param   bottom      New bottom/right color
   */
  public void setColorBottom( Color bottom ) {
    this.cBottom = bottom;
    repaint();
  }

  /**
   * Get the current bottom/right color.
   *
   * @return  Bottom/right color or null.
   */
  public Color getColorBottom() {
    return cBottom;
  }

  /**
   * Set the gradient direction.
   *
   * @param   vertical    Direction: true=vertical, false=horizontal
   */
  public void setVertical( boolean vertical ) {
    this.vertical = vertical;
    repaint();
  }

  /**
   * Get the gradient direction.
   *
   * @return  true=vertical, false=horizontal
   */
  public boolean isVertical() {
    return vertical;
  }

  /**
   * Paint the gradient and the components.
   *
   * @param   g       Graphics context
   */
  public void paint( Graphics g ) {
    Graphics2D g2 = (Graphics2D) g.create();

    //--- Create Gradient Paint ---
    Color top    = ( cTop!=BACKGROUND    ? cTop    : getBackground());
    Color bottom = ( cBottom!=BACKGROUND ? cBottom : getBackground());
    GradientPaint gp;
    if( vertical ) {
      gp = new GradientPaint( 0f,0f,top, 0f,getHeight(),bottom );
    }else {
      gp = new GradientPaint( 0f,0f,top, getWidth(),0f,bottom );
    }
    g2.setPaint(gp);

    //--- Fill Background ---
    g2.fillRect(0,0, getWidth(), getHeight());

    g2.dispose();

    //--- Paint Components ---
    super.paint(g);
  }

}
