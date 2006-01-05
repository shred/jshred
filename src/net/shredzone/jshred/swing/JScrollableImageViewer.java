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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

/**
 * A ScrollableImageViewer is an ImageViewer that also implements the
 * Scrollable interface.
 * <p>
 * The advantage of this class is that e.g. mouse wheel scrolling will feel
 * more familiar to the user.
 * <p>
 * The disadvantage is that the image will always be shown in the upper
 * left corner, instead of being centered like in the JImageViewer class.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JScrollableImageViewer.java,v 1.1 2005/01/24 10:08:08 shred Exp $
 * @since   R9
 */
public class JScrollableImageViewer extends JImageViewer implements Scrollable {
  private static final long serialVersionUID = 3760844579779262261L;

  private static final int SCROLL_UNITS = 20;   // Number of pixels for a scroll unit
  
  /**
   * Create an empty JScrollableImageViewer.
   */
  public JScrollableImageViewer() {
    super();
  }

  /**
   * Create a JScrollableImageViewer showing the given Image.
   * 
   * @param   image       Image to be shown.
   */
  public JScrollableImageViewer( Image image ) {
    super( image );
  }
  
  /**
   * Create a JScrollableImageViewer showing the given ImageIcon.
   * 
   * @param   icon        ImageIcon to be shown.
   */
  public JScrollableImageViewer( ImageIcon icon ) {
    super( icon );
  }
  
  /**
   * Create a JScrollableImageViewer showing an image that is read from the given
   * InputStream.
   * 
   * @param   is          InputStream to read the image data from.
   * @throws IOException    if the stream could not be read.
   */
  public JScrollableImageViewer( InputStream is ) throws IOException {
    super( is );
  }
  
  /**
   * Create a JScrollableImageViewer showing an image that is read from the
   * given URL.
   * 
   * @param   url         URL to read the image data from.
   */
  public JScrollableImageViewer( URL url ) {
    super( url );
  }

  /**
   * Get the preferred viewport size, which is equal to the
   * minimum size of this component.
   * 
   * @return    Preferred Scrollable Viewport Dimension
   */
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  /**
   * How many pixels of the image to be scrolled by an unit
   * increment. This is when the user pressed the arrow button or did one
   * mouse wheel movement.
   * 
   * @return  A small number of pixels to be scrolled.
   */
  public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
    return SCROLL_UNITS;
  }

  /**
   * How many pixels of the image to be scrolled by a block
   * increment. This is when the user pressed at the area next to the
   * scrollbar slider.
   * 
   * @return  A large number of pixels to be scrolled. The default is
   *    the visible width/height (respectively) minus one unit increment.
   */
  public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction ) {
    final int unit = getScrollableUnitIncrement( visibleRect, orientation, direction );
    int result;
    if( orientation==SwingConstants.HORIZONTAL ) {
      result = visibleRect.width;
    }else {
      result = visibleRect.height;
    }
    if( result>(unit+unit) )
      result -= unit;
    return result;
  }

  /**
   * Adjust own width to viewport width. This is not desired
   * for an image.
   *  
   * @return    always false
   */
  public boolean getScrollableTracksViewportWidth() {
    return false;
  }

  /**
   * Adjust own height to viewport height. This is not desired
   * for an image.
   *  
   * @return    always false
   */
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }
  
}
