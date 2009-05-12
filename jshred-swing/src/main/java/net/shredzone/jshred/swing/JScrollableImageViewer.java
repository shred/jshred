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
 * A ScrollableImageViewer is an {@link JImageViewer} that also implements the
 * {@link Scrollable} interface.
 * <p>
 * The advantage of this class is that e.g. mouse wheel scrolling will feel more familiar
 * to the user.
 * <p>
 * The disadvantage is that the image will always be shown in the upper left corner,
 * instead of being centered like in the {@link JImageViewer} class.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JScrollableImageViewer.java 302 2009-05-12 22:19:11Z shred $
 * @since R9
 */
public class JScrollableImageViewer extends JImageViewer implements Scrollable {
    private static final long serialVersionUID = 3760844579779262261L;

    private static final int SCROLL_UNITS = 20; // Number of pixels for a scroll

    // unit

    /**
     * Create an empty JScrollableImageViewer.
     */
    public JScrollableImageViewer() {
        super();
    }

    /**
     * Create a JScrollableImageViewer showing the given {@link Image}.
     * 
     * @param image
     *            {@link Image} to be shown.
     */
    public JScrollableImageViewer(Image image) {
        super(image);
    }

    /**
     * Create a JScrollableImageViewer showing the given {@link ImageIcon}.
     * 
     * @param icon
     *            {@link ImageIcon} to be shown.
     */
    public JScrollableImageViewer(ImageIcon icon) {
        super(icon);
    }

    /**
     * Create a JScrollableImageViewer showing an image that is read from the given
     * {@link InputStream}.
     * 
     * @param is
     *            {@link InputStream} to read the image data from.
     * @throws IOException
     *             if the stream could not be read.
     */
    public JScrollableImageViewer(InputStream is) throws IOException {
        super(is);
    }

    /**
     * Create a JScrollableImageViewer showing an image that is read from the given
     * {@link URL}.
     * 
     * @param url
     *            {@link URL} to read the image data from.
     */
    public JScrollableImageViewer(URL url) {
        super(url);
    }

    /**
     * Get the preferred viewport size, which is equal to the minimum size of this
     * component.
     * 
     * @return Preferred scrollable viewport dimension
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    /**
     * How many pixels of the image to be scrolled by an unit increment. This is when the
     * user pressed the arrow button or did one mouse wheel movement.
     * 
     * @return A small number of pixels to be scrolled.
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return SCROLL_UNITS;
    }

    /**
     * How many pixels of the image to be scrolled by a block increment. This is when the
     * user pressed at the area next to the scrollbar slider.
     * 
     * @return A large number of pixels to be scrolled. The default is the visible
     *         width/height (respectively) minus one unit increment.
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        int unit = getScrollableUnitIncrement(visibleRect, orientation, direction);
        int result;
        if (orientation == SwingConstants.HORIZONTAL) {
            result = visibleRect.width;
        } else {
            result = visibleRect.height;
        }
        if (result > (unit + unit)) result -= unit;
        return result;
    }

    /**
     * Adjust own width to viewport width. This is not desired for an image.
     * 
     * @return always false
     */
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }

    /**
     * Adjust own height to viewport height. This is not desired for an image.
     * 
     * @return always false
     */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

}
