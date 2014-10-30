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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * This {@link JPanel} shows a color gradient in the background. You can select the
 * direction and the starting and ending color.
 *
 * @author Richard "Shred" Körber
 */
public class JGradientPanel extends JPanel {
    private static final long serialVersionUID = 4123386540283015480L;
    public static final int VERTICAL = SwingConstants.VERTICAL;
    public static final int HORIZONTAL = SwingConstants.HORIZONTAL;

    private Color cTop = null;
    private Color cBottom = null;
    private int direction = VERTICAL;

    /**
     * Creates a vertical {@link JGradientPanel} with the given top and bottom color. If
     * {@code null} is given as color, the standard background color will be used instead.
     *
     * @param top
     *            Top color or {@code null}
     * @param bottom
     *            Bottom color or {@code null}
     */
    public JGradientPanel(Color top, Color bottom) {
        this(top, bottom, VERTICAL);
    }

    /**
     * Creates a {@link JGradientPanel} with the given top and bottom color in the given
     * direction. If {@code null} is given as color, the standard background color will be
     * used instead.
     *
     * @param top
     *            Top color or {@code null}
     * @param bottom
     *            Bottom color or {@code null}
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
     * Changes the top/left color. {@code null} means to use the background color instead.
     *
     * @param top
     *            New top/left color or {@code null}
     */
    public void setColorTop(Color top) {
        this.cTop = top;
        repaint();
    }

    /**
     * Gets the current top/left color.
     *
     * @return Top/left color or {@code null}
     */
    public Color getColorTop() {
        return cTop;
    }

    /**
     * Changes the bottom/right color. {@code null} means to use the background color
     * instead.
     *
     * @param bottom
     *            New bottom/right color or {@code null}
     */
    public void setColorBottom(Color bottom) {
        this.cBottom = bottom;
        repaint();
    }

    /**
     * Gets the current bottom/right color.
     *
     * @return Bottom/right color or {@code null}.
     */
    public Color getColorBottom() {
        return cBottom;
    }

    /**
     * Sets the gradient direction.
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
     * Gets the gradient direction.
     *
     * @return {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    public int getDirection() {
        return direction;
    }

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
