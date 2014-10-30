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

import java.awt.Insets;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * This {@link JButton} is to be used in toolbars. It takes care that the button never has
 * a text, never gets the focus and has no borders.
 *
 * @author Richard "Shred" Körber
 */
public class JToolbarButton extends JButton {
    private static final long serialVersionUID = 3905246710308417843L;

    /**
     * Creates a new basic {@link JToolbarButton}.
     *
     * @since R8
     */
    public JToolbarButton() {
        super();
        init();
    }

    /**
     * Creates a new {@link JToolbarButton} with a title.
     *
     * @param title
     *            Title to be used
     * @since R14
     */
    public JToolbarButton(String title) {
        super(title);
        init();
    }

    /**
     * Creates a new {@link JToolbarButton} with a title and an icon.
     *
     * @param title
     *            Title to be used
     * @param icon
     *            Icon to be used.
     * @since R14
     */
    public JToolbarButton(String title, Icon icon) {
        super(title, icon);
        init();
    }

    /**
     * Creates a new {@link JToolbarButton} with an icon.
     *
     * @param icon
     *            Icon to be used.
     * @since R8
     */
    public JToolbarButton(Icon icon) {
        super(icon);
        init();
        setText("");
    }

    /**
     * Creates a new {@link JToolbarButton} for a certain {@link Action}. The
     * {@link Action}'s text is not displayed.
     *
     * @param a
     *            {@link Action}
     */
    public JToolbarButton(Action a) {
        this(a, false);
    }

    /**
     * Creates a new {@link JToolbarButton} for a certain {@link Action}.
     *
     * @param a
     *            {@link Action}
     * @param keep
     *            true: Keep the action text.
     * @since R14
     */
    public JToolbarButton(Action a, boolean keep) {
        super(a);
        init();
        if (!keep) setText("");
    }

    private void init() {
        setRequestFocusEnabled(false);
        setFocusable(false);
        setMargin(new Insets(0, 0, 0, 0));
        setContentAreaFilled(false);
    }

}
