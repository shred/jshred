/**
 * jshred - Shred's Toolbox
 *
 * Copyright (C) 2009 Richard "Shred" Körber
 *   http://jshred.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License / GNU Lesser
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package net.shredzone.jshred.swing;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

/**
 * This filter is brighting or darking an image, e.g. for mouse over effects. The
 * brightness factor is multiplied to the image, so black will stay black. Transparency is
 * honored.
 *
 * @author Richard "Shred" Körber
 */
public class BrightFilter extends RGBImageFilter {
    private int p;
    private int redMul;
    private int greenMul;
    private int blueMul;

    /**
     * Creates a brightened {@link Image}. A default factor of 40 and the brightening
     * color {@link Color#WHITE} will be used.
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
     * Creates a {@link BrightFilter}. The given factor and the brightening color
     * {@link Color#WHITE} will be used.
     *
     * @param p
     *            Brightness factor
     */
    public BrightFilter(int p) {
        this(p, Color.WHITE);
    }

    /**
     * Creates a {@link BrightFilter}. The given factor and the given brightening color
     * will be used.
     *
     * @param p
     *            Brightness factor
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
