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

import java.awt.*;
import javax.swing.border.*;

/**
 * A recessed 1px border, like often used in status bars.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: TinyBorder.java,v 1.1.1.1 2004/06/21 11:51:44 shred Exp $
 */
public class TinyBorder extends AbstractBorder {

  /**
   * Paint the border.
   *
   * @param   c         Component to paint the border around
   * @param   g         Graphics context
   * @param   x         Left coordinate
   * @param   y         Top coordinate
   * @param   width     Width
   * @param   height    Height
   */
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Color oldColor = g.getColor();
    Color bgColor  = c.getBackground();

    g.translate(x,y);

    g.setColor(bgColor.brighter());
    g.drawLine(width-1,height-1, width-1,0);
    g.drawLine(width-1,height-1, 0,height-1);

    g.setColor(bgColor.darker());
    g.drawLine(0,0, width-2,0);
    g.drawLine(0,0, 0,height-2);

    g.translate(-x,-y);
    g.setColor(oldColor);
  }

  /**
   * Return the insets of the border.
   *
   * @param   c         Component for this border
   */
  public Insets getBorderInsets(Component c) {
    return new Insets(2,2,2,2);
  }

  /**
   * Reinitialize the insets parameter with this Border's current
   * Insets.
   *
   * @param   c         Component for this border
   * @param   insets    The insets to be reinitialized
   */
  public Insets getBorderInsets(Component c, Insets insets) {
    insets.left = insets.top = insets.right = insets.bottom = 2;
    return insets;
  }

  /**
   * Return whether or not the border is opaque.
   *
   * @return  opaque state
   */
  public boolean isBorderOpaque() {
    return true;
  }

}

/* jedit :mode=java:tabSize=2:noTabs=true:folding=java:maxLineLen=72: */