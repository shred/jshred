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

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is a headline to be used in dialogs etc. It shows a big title to the left, and
 * optionally a nice icon to the right and a description below the title. The headline is
 * colored in a gradient, starting in a certain color to the left, and going to the
 * current background color to the right.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JHeadline.java 389 2009-11-11 23:47:30Z shred $
 * @since R8
 */
public class JHeadline extends JGradientPanel {
    private static final long serialVersionUID = 3618137866132074806L;
    private final JLabel jlTitle;
    private final JLabel jlDesc;
    private final JLabel jlIcon;
    private boolean init = false;

    /**
     * Create an empty JHeadline. {@link Color#GRAY} is used as default color.
     */
    public JHeadline() {
        this("");
    }

    /**
     * Create a new JHeadline with the given title. {@link Color#GRAY} is used as default
     * color.
     * 
     * @param title
     *            Title to be used
     */
    public JHeadline(String title) {
        this(title, null);
    }

    /**
     * Create a new JHeadline with the given title and icon. {@link Color#GRAY} is used as
     * default color.
     * 
     * @param title
     *            Title to be used
     * @param icon
     *            {@link Icon} or null
     */
    public JHeadline(String title, Icon icon) {
        this(title, null, icon);
    }

    /**
     * Create a new JHeadline with the given title, description, icon. {@link Color#GRAY}
     * is used as default color.
     * 
     * @param title
     *            Title to be used
     * @param desc
     *            Description or null
     * @param icon
     *            {@link Icon} or null
     */
    public JHeadline(String title, String desc, Icon icon) {
        this(title, desc, icon, Color.GRAY);
    }

    /**
     * Create a new JHeadline with the given title, description, icon and color.
     * 
     * @param title
     *            Title to be used
     * @param desc
     *            Description or null
     * @param icon
     *            {@link Icon} or null
     * @param color
     *            Left gradient {@link Color}
     */
    public JHeadline(String title, String desc, Icon icon, Color color) {
        super(color, null, JGradientPanel.HORIZONTAL);
        setLayout(new BorderLayout());

        jlTitle = new JLabel(title);
        jlTitle.setFont(jlTitle.getFont().deriveFont(20.0f));

        jlDesc = new JLabel("");
        jlDesc.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        setDescription(desc);

        jlIcon = new JLabel("");
        setIcon(icon);

        JPanel jpLeft = new JPanel();
        jpLeft.setOpaque(false);
        jpLeft.setLayout(new BoxLayout(jpLeft, BoxLayout.Y_AXIS));
        {
            jpLeft.add(Box.createVerticalGlue());
            jpLeft.add(jlTitle);
            jpLeft.add(jlDesc);
        }
        add(jpLeft, BorderLayout.WEST);
        add(jlIcon, BorderLayout.EAST);

        init = true;
        setForeground(Color.WHITE);

        setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
    }

    /**
     * Set a new title text.
     * 
     * @param title
     *            new title text, must not be <code>null</code>.
     */
    public void setTitle(String title) {
        if (title == null) throw new NullPointerException("title must not be null");

        jlTitle.setText(title);
    }

    /**
     * Get the current title text.
     * 
     * @return Current title text.
     */
    public String getTitle() {
        return jlTitle.getText();
    }

    /**
     * Set a new description.
     * 
     * @param desc
     *            new description, <code>null</code> if there is none.
     */
    public void setDescription(String desc) {
        if (desc != null) {
            jlDesc.setText(desc);
            jlDesc.setVisible(true);
        } else {
            jlDesc.setVisible(false);
        }
    }

    /**
     * Get the current description.
     * 
     * @return Current description, or <code>null</code> if there is none.
     */
    public String getDescription() {
        if (jlDesc.isVisible()) return jlDesc.getText();
        else return null;
    }

    /**
     * Set a new {@link Icon}.
     * 
     * @param icon
     *            new {@link Icon}, <code>null</code> if there is none
     */
    public void setIcon(Icon icon) {
        if (icon != null) {
            jlIcon.setIcon(icon);
            jlIcon.setVisible(true);
        } else {
            jlIcon.setVisible(false);
        }
    }

    /**
     * Get the current {@link Icon}
     * 
     * @return Current {@link Icon}, or <code>null</code> if there is none.
     */
    public Icon getIcon() {
        if (jlIcon.isVisible()) return jlIcon.getIcon();
        else return null;
    }

    /**
     * Set the text color. The title will be shown in this color. The description will be
     * shown in the same color, but with an alpha value of 200, so the background gradient
     * will shine through a little.
     * 
     * @param fg
     *            New foreground {@link Color}
     */
    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (init) {
            jlTitle.setForeground(fg);
            jlDesc.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 200));
        }
    }

}
