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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * A recessed 1px border, like often used in status bars.
 *
 * @author Richard "Shred" Körber
 */
public class TinyBorder extends AbstractBorder {
    private static final long serialVersionUID = 3761685710469805881L;

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

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(2, 2, 2, 2);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.top = insets.right = insets.bottom = 2;
        return insets;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

}
