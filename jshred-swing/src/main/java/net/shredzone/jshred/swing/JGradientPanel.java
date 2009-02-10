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

import javax.swing.*;
import java.awt.*;

/**
 * This {@link JPanel} shows a color gradient in the background. You can select
 * the direction and the starting and ending color.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JGradientPanel.java 256 2009-02-10 22:56:35Z shred $
 */
public class JGradientPanel extends JPanel {
    private static final long serialVersionUID = 4123386540283015480L;
    public static final int VERTICAL = SwingConstants.VERTICAL;
    public static final int HORIZONTAL = SwingConstants.HORIZONTAL;

    private Color cTop = null;
    private Color cBottom = null;
    private int direction = VERTICAL;

    /**
     * Create a vertical JGradientPanel with the given top and bottom color. If
     * null is given as color, the standard background color will be used
     * instead.
     * 
     * @param top
     *            Top color or <code>null</code>
     * @param bottom
     *            Bottom color or <code>null</code>
     */
    public JGradientPanel(Color top, Color bottom) {
        this(top, bottom, VERTICAL);
    }

    /**
     * Create a JGradientPanel with the given top and bottom color in the given
     * direction. If null is given as color, the standard background color will
     * be used instead.
     * 
     * @param top
     *            Top color or <code>null</code>
     * @param bottom
     *            Bottom color or <code>null</code>
     * @param direction
     *            {@link #VERTICAL} or {@link #HORIZONTAL}
     */
    public JGradientPanel(Color top, Color bottom, int direction) {
        if (direction != VERTICAL && direction != HORIZONTAL)
            throw new IllegalArgumentException("illegal direction");
        
        this.cTop = top;
        this.cBottom = bottom;
        this.direction = direction;
        setOpaque(false);
    }

    /**
     * Change the top/left color. null means to use the background color
     * instead.
     * 
     * @param top
     *            New top/left color or <code>null</code>
     */
    public void setColorTop(Color top) {
        this.cTop = top;
        repaint();
    }

    /**
     * Get the current top/left color.
     * 
     * @return Top/left color or <code>null</code>.
     */
    public Color getColorTop() {
        return cTop;
    }

    /**
     * Change the bottom/right color. <code>null</code> means to use the
     * background color instead.
     * 
     * @param bottom
     *            New bottom/right color or <code>null</code>
     */
    public void setColorBottom(Color bottom) {
        this.cBottom = bottom;
        repaint();
    }

    /**
     * Get the current bottom/right color.
     * 
     * @return Bottom/right color or <code>null</code>.
     */
    public Color getColorBottom() {
        return cBottom;
    }

    /**
     * Set the gradient direction.
     * 
     * @param direction
     *            {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public void setDirection(int direction) {
        if (direction != VERTICAL && direction != HORIZONTAL)
            throw new IllegalArgumentException("illegal direction");

        this.direction = direction;
        repaint();
    }

    /**
     * Get the gradient direction.
     * 
     * @return {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Paint the gradient and the components.
     * 
     * @param g
     *            {@link Graphics} context
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // --- Create Gradient Paint ---
        Color top = (cTop != null ? cTop : getBackground());
        Color bottom = (cBottom != null ? cBottom : getBackground());
        GradientPaint gp;
        if (direction == VERTICAL) {
            gp = new GradientPaint(0f, 0f, top, 0f, getHeight(), bottom);
        } else {
            gp = new GradientPaint(0f, 0f, top, getWidth(), 0f, bottom);
        }
        g2.setPaint(gp);

        // --- Fill Background ---
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.dispose();

        // --- Paint Components ---
        super.paint(g);
    }

}
