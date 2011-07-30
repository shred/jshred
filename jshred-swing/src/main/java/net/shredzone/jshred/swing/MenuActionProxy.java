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
 * The MenuActionProxy proxies {@link Action}s to be used in menus. It takes care about a
 * proper scaling of the {@link Action} icon to a nice menu size.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: MenuActionProxy.java 584 2011-07-30 20:42:48Z shred $
 */
public class MenuActionProxy implements Action, Serializable {
    private static final long serialVersionUID = 3257285850856699190L;

    private final Action master;
    private Dimension dim;

    /**
     * Creates a new MenuActionProxy. The default icon dimensions (16x16) are used.
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
     * Set new Icon dimensions for the menu icon. Default is 16x16 pixels.
     * 
     * @param dim
     *            New icon {@link Dimension}
     */
    public void setIconDimension(Dimension dim) {
        this.dim = dim;
    }

    /**
     * Get the current dimension of the menu icon.
     * 
     * @return Icon {@link Dimension}
     */
    public Dimension getIconDimension() {
        return dim;
    }

    /**
     * Delegates the invocation to the Action's {@link Action#getValue(String)} method.
     */
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

    /**
     * Delegates the invocation to the Action's {@link Action#putValue(String, Object)}
     * method.
     */
    @Override
    public void putValue(String key, Object value) {
        master.putValue(key, value);
    }

    /**
     * Delegates the invocation to the Action's {@link Action#setEnabled(boolean)} method.
     */
    @Override
    public void setEnabled(boolean b) {
        master.setEnabled(b);
    }

    /**
     * Delegates the invocation to the Action's {@link Action#isEnabled()} method.
     */
    @Override
    public boolean isEnabled() {
        return master.isEnabled();
    }

    /**
     * Delegates the invocation to the Action's
     * {@link Action#addPropertyChangeListener(PropertyChangeListener)} method.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        master.addPropertyChangeListener(listener);
    }

    /**
     * Delegates the invocation to the Action's
     * {@link Action#removePropertyChangeListener(PropertyChangeListener)} method.
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        master.removePropertyChangeListener(listener);
    }

    /**
     * Delegates the invocation to the Action's
     * {@link Action#actionPerformed(ActionEvent)} method.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        master.actionPerformed(e);
    }
}
