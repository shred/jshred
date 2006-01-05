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
import java.awt.image.*;

/**
 * This filter is brighting or darking an image, e.g. for mouse over
 * effects. The brightness factor is multiplied to the image, so black
 * will stay black. Transparency is honored.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: BrightFilter.java,v 1.4 2004/08/23 23:49:15 shred Exp $
 */
public class BrightFilter extends RGBImageFilter {
  private int p;
  private int redMul;
  private int greenMul;
  private int blueMul;

  /**
   * Creates a brightened image. A default factor of 40 and the
   * brightening color <code>Color.WHITE</code> will be used.
   *
   * @param   i       Source image
   * @return  Brightened Image
   */
  public static Image createBrightImage ( Image i ) {
    BrightFilter filter = new BrightFilter( 40 );
    ImageProducer prod = new FilteredImageSource( i.getSource(), filter );
    Image brightImage = Toolkit.getDefaultToolkit().createImage(prod);
    return brightImage;
  }

  /**
   * Creates a BrightFilter. The given factor and the brightening color
   * <code>Color.WHITE</code> will be used.
   *
   * @param   p       Brightener factor
   */
  public BrightFilter( int p ) {
    this( p, Color.WHITE );
  }

  /**
   * Creates a BrightFilter. The given factor and the given brightening
   * color will be used.
   *
   * @param   p       Brightener factor
   * @param   color   Color
   */
  public BrightFilter( int p, Color color ) {
    this.p = p;

    int rgb  = color.getRGB();
    redMul   = ( (rgb>>16) & 0xFF );
    greenMul = ( (rgb>> 8) & 0xFF );
    blueMul  = ( (rgb    ) & 0xFF );

    canFilterIndexColorModel = true;
  }

  /**
   * The filter method itself.
   *
   * @param   x       X coordinate
   * @param   y       Y coordinate
   * @param   rgb     RGB value
   * @return  brightened RGB value
   */
  public int filterRGB( int x, int y, int rgb ) {
    //--- Decompose ---
    int red   = (rgb>>16)&0xFF;
    int green = (rgb>> 8)&0xFF;
    int blue  = (rgb    )&0xFF;

    //--- Compute the new color values ---
    red   = (100*red   - p*red   + p*redMul  ) / 100;
    green = (100*green - p*green + p*greenMul) / 100;
    blue  = (100*blue  - p*blue  + p*blueMul ) / 100;

    //--- Clipping ---
    red   = ( red<0   ? 0 : ( red>255   ? 255 : red   ) );
    green = ( green<0 ? 0 : ( green>255 ? 255 : green ) );
    blue  = ( blue<0  ? 0 : ( blue>255  ? 255 : blue  ) );

    //--- Compose new color ---
    return( rgb&0xFF000000 | red<<16 | green<<8 | blue );
  }
}
