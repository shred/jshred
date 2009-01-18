/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *-----------------------------------------------------------------------
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

import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * This is an icon of a little arrow pointing in one of the four directions,
 * either solid or outlined.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: ArrowIcon.java 243 2009-01-18 15:05:21Z shred $
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
     *            Direction pointing at: one of {@link SwingConstants}' NORTH,
     *            SOUTH, EAST or WEST.
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
     *            Direction pointing at: one of {@link SwingConstants}' NORTH,
     *            SOUTH, EAST or WEST.
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
        final int w = width - 1;
        final int h = height - 1;
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
