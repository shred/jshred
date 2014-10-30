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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.Action;
import javax.swing.ImageIcon;

/**
 * The {@link MenuActionProxy} proxies {@link Action}s to be used in menus. It takes care
 * about a proper scaling of the {@link Action} icon to a nice menu size.
 *
 * @author Richard "Shred" Körber
 */
public class MenuActionProxy implements Action, Serializable {
    private static final long serialVersionUID = 3257285850856699190L;

    private final Action master;
    private Dimension dim;

    /**
     * Creates a new {@link MenuActionProxy}. The default icon dimensions (16x16) are
     * used.
     *
     * @param a
     *            {@link Action}
     */
    public MenuActionProxy(Action a) {
        this(a, new Dimension(16, 16));
    }

    /**
     * Creates a new MenuActionProxy with given Icon {@link Dimension}.
     *
     * @param a
     *            {@link Action}
     * @param dim
     *            Icon {@link Dimension}
     */
    public MenuActionProxy(Action a, Dimension dim) {
        master = a;
        this.dim = dim;
    }

    /**
     * Sets new Icon dimensions for the menu icon. Default is 16x16 pixels.
     *
     * @param dim
     *            New icon {@link Dimension}
     */
    public void setIconDimension(Dimension dim) {
        this.dim = dim;
    }

    /**
     * Gets the current dimension of the menu icon.
     *
     * @return Icon {@link Dimension}
     */
    public Dimension getIconDimension() {
        return dim;
    }

    @Override
    public Object getValue(String key) {
        Object val = master.getValue(key);

        // --- Scale icon ---
        if (key.equals(SMALL_ICON) && (val instanceof ImageIcon)) {
            ImageIcon icon = (ImageIcon) val;
            if (icon.getIconWidth() != dim.width || icon.getIconHeight() != dim.height) {
                Image img = icon.getImage();
                img = img.getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH);
                val = new ImageIcon(img);
            }
        }

        return val;
    }

    @Override
    public void putValue(String key, Object value) {
        master.putValue(key, value);
    }

    @Override
    public void setEnabled(boolean b) {
        master.setEnabled(b);
    }

    @Override
    public boolean isEnabled() {
        return master.isEnabled();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        master.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        master.removePropertyChangeListener(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        master.actionPerformed(e);
    }

}
