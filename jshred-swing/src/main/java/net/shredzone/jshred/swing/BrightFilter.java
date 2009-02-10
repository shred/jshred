/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://jshred.shredzone.org-------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shredzone.jshred.swing;

import java.awt.*;
import java.awt.image.*;

/**
 * This filter is brighting or darking an image, e.g. for mouse over effects.
 * The brightness factor is multiplied to the image, so black will stay black.
 * Transparency is honored.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: BrightFilter.java 256 2009-02-10 22:56:35Z shred $
 */
public class BrightFilter extends RGBImageFilter {
    private int p;
    private int redMul;
    private int greenMul;
    private int blueMul;

    /**
     * Creates a brightened {@link Image}. A default factor of 40 and the
     * brightening color {@link Color#WHITE} will be used.
     * 
     * @param i
     *            Source {@link Image}
     * @return Brightened {@link Image}
     */
    public static Image createBrightImage(Image i) {
        BrightFilter filter = new BrightFilter(40);
        ImageProducer prod = new FilteredImageSource(i.getSource(), filter);
        Image brightImage = Toolkit.getDefaultToolkit().createImage(prod);
        return brightImage;
    }

    /**
     * Creates a BrightFilter. The given factor and the brightening color
     * {@link Color#WHITE} will be used.
     * 
     * @param p
     *            Brightener factor
     */
    public BrightFilter(int p) {
        this(p, Color.WHITE);
    }

    /**
     * Creates a BrightFilter. The given factor and the given brightening color
     * will be used.
     * 
     * @param p
     *            Brightener factor
     * @param color
     *            {@link Color}
     */
    public BrightFilter(int p, Color color) {
        this.p = p;

        int rgb = color.getRGB();
        redMul = ((rgb >> 16) & 0xFF);
        greenMul = ((rgb >> 8) & 0xFF);
        blueMul = ((rgb) & 0xFF);

        canFilterIndexColorModel = true;
    }

    /**
     * The filter method itself.
     * 
     * @param x
     *            X coordinate
     * @param y
     *            Y coordinate
     * @param rgb
     *            RGB value
     * @return brightened RGB value
     */
    @Override
	public int filterRGB(int x, int y, int rgb) {
        // --- Decompose ---
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = (rgb) & 0xFF;

        // --- Compute the new color values ---
        red = (100 * red - p * red + p * redMul) / 100;
        green = (100 * green - p * green + p * greenMul) / 100;
        blue = (100 * blue - p * blue + p * blueMul) / 100;

        // --- Clipping ---
        red = (red < 0 ? 0 : (red > 255 ? 255 : red));
        green = (green < 0 ? 0 : (green > 255 ? 255 : green));
        blue = (blue < 0 ? 0 : (blue > 255 ? 255 : blue));

        // --- Compose new color ---
        return (rgb & 0xFF000000 | red << 16 | green << 8 | blue);
    }
}
