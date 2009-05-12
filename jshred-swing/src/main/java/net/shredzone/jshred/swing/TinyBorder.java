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

import java.awt.*;
import javax.swing.border.*;

/**
 * A recessed 1px border, like often used in status bars.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: TinyBorder.java 302 2009-05-12 22:19:11Z shred $
 */
public class TinyBorder extends AbstractBorder {
    private static final long serialVersionUID = 3761685710469805881L;

    /**
     * Paint the border.
     * 
     * @param c
     *            {@link Component} to paint the border around
     * @param g
     *            {@link Graphics} context
     * @param x
     *            Left coordinate
     * @param y
     *            Top coordinate
     * @param width
     *            Width
     * @param height
     *            Height
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Color oldColor = g.getColor();
        Color bgColor = c.getBackground();

        g.translate(x, y);

        g.setColor(bgColor.brighter());
        g.drawLine(width - 1, height - 1, width - 1, 0);
        g.drawLine(width - 1, height - 1, 0, height - 1);

        g.setColor(bgColor.darker());
        g.drawLine(0, 0, width - 2, 0);
        g.drawLine(0, 0, 0, height - 2);

        g.translate(-x, -y);
        g.setColor(oldColor);
    }

    /**
     * Return the {@link Insets} of the border.
     * 
     * @param c
     *            {@link Component} for this border
     */
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }

    /**
     * Reinitialize the insets parameter with this Border's current {@link Insets}.
     * 
     * @param c
     *            {@link Component} for this border
     * @param insets
     *            The {@link Insets} to be reinitialized
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 2;
        return insets;
    }

    /**
     * Return whether or not the border is opaque.
     * 
     * @return opaque state
     */
    @Override
    public boolean isBorderOpaque() {
        return true;
    }

}
