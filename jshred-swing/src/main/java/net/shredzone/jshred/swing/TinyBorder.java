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

import java.awt.*;
import javax.swing.border.*;

/**
 * A recessed 1px border, like often used in status bars.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: TinyBorder.java 243 2009-01-18 15:05:21Z shred $
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
     * Reinitialize the insets parameter with this Border's current
     * {@link Insets}.
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
