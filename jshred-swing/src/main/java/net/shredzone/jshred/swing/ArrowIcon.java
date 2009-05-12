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

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * This is an icon of a little arrow pointing in one of the four directions, either solid
 * or outlined.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: ArrowIcon.java 302 2009-05-12 22:19:11Z shred $
 * @since R12
 */
public class ArrowIcon implements Icon, Serializable {
    private static final long serialVersionUID = 2798342355992018833L;
    private final int direction;
    private final boolean solid;
    private final int width;
    private final int height;

    /**
     * Create a new, solid ArrowIcon for the given direction.
     * 
     * @param w
     *            Arrow width
     * @param h
     *            Arrow height
     * @param direction
     *            Direction pointing at: one of {@link SwingConstants}' NORTH, SOUTH, EAST
     *            or WEST.
     */
    public ArrowIcon(int w, int h, int direction) {
        this(w, h, direction, true);
    }

    /**
     * Create a new, ArrowIcon for the given direction.
     * 
     * @param w
     *            Arrow width
     * @param h
     *            Arrow height
     * @param direction
     *            Direction pointing at: one of {@link SwingConstants}' NORTH, SOUTH, EAST
     *            or WEST.
     * @param solid
     *            true: Solid, false: Outline
     */
    public ArrowIcon(int w, int h, int direction, boolean solid) {
        if (direction != SwingConstants.NORTH
            && direction != SwingConstants.SOUTH
            && direction != SwingConstants.EAST
            && direction != SwingConstants.WEST) {
            throw new IllegalArgumentException("Unknown direction");
        }

        this.width = w;
        this.height = h;
        this.direction = direction;
        this.solid = solid;
    }

    /**
     * Paint this icon
     * 
     * @param c
     *            {@link Component} (for reference)
     * @param g
     *            {@link Graphics} context
     * @param x
     *            X position
     * @param y
     *            Y position
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        final int w  = width - 1;
        final int h  = height - 1;
        final int wh = w / 2;
        final int hh = h / 2;

        // --- Create an arrow polygon ---
        int pX[] = new int[3];
        int pY[] = new int[3];
        if (direction == SwingConstants.EAST) {
            pX[0] = x;
            pY[0] = y;
            pX[1] = x;
            pY[1] = y + h;
            pX[2] = x + wh;
            pY[2] = y + hh;
        } else if (direction == SwingConstants.WEST) {
            pX[0] = x + wh;
            pY[0] = y;
            pX[1] = x + wh;
            pY[1] = y + h;
            pX[2] = x;
            pY[2] = y + hh;
        } else if (direction == SwingConstants.NORTH) {
            pX[0] = x;
            pY[0] = y + h;
            pX[1] = x + wh;
            pY[1] = y + hh;
            pX[2] = x + w;
            pY[2] = y + h;
        } else { // SOUTH
            pX[0] = x;
            pY[0] = y + hh;
            pX[1] = x + wh;
            pY[1] = y + h;
            pX[2] = x + w;
            pY[2] = y + hh;
        }

        // --- Draw it ---
        g.setColor(c.getForeground());
        g.drawPolygon(pX, pY, pX.length);
        if (solid) {
            g.fillPolygon(pX, pY, pX.length);
        }
    }

    /**
     * Get the icon width.
     * 
     * @return Icon width
     */
    public int getIconWidth() {
        return width;
    }

    /**
     * Get the icon height.
     * 
     * @return Icon height
     */
    public int getIconHeight() {
        return height;
    }
}
