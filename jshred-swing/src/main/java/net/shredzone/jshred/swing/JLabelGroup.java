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
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * A {@link JLabelGroup} is a {@link JLabel} set left to a {@link Component}. At RTL
 * systems, the label is set to the right.
 * <p>
 * Multiple {@link JLabelGroup} can be connected together. On the last instance of this
 * chain, {@link #rearrange()} will be invoked, to rearrange all {@link JLabel}s within
 * this chain to the same, maximum width. This will result into a nicely aligned layout.
 *
 * @author Richard "Shred" Körber
 */
public class JLabelGroup extends JPanel {
    private static final long serialVersionUID = 4120855451547482167L;
    private Component comp;
    private JComponent label;
    private JLabelGroup pred;

    /**
     * Creates the first {@link JLabelGroup} of a chain.
     *
     * @param c
     *            {@link Component} to be labelled
     * @param text
     *            Label text
     */
    public JLabelGroup(Component c, String text) {
        this(c, text, null);
    }

    /**
     * Creates a new {@link JLabelGroup} element.
     *
     * @param c
     *            {@link Component} to be labelled
     * @param text
     *            Label text
     * @param pred
     *            Predecessor {@link JLabelGroup} instance, or {@code null}
     */
    public JLabelGroup(Component c, String text, JLabelGroup pred) {
        this(c, createDefaultLabel(SwingUtils.getMenuName(text), null), pred);
        Character keycode = SwingUtils.getMenuShortcut(text);
        if (keycode != null) setMnemonic(keycode.charValue());
    }

    /**
     * Creates a new {@link JLabelGroup} label with an {@link Icon}.
     *
     * @param c
     *            {@link Component} to be labelled
     * @param text
     *            Label text
     * @param icon
     *            {@link Icon}
     * @param pred
     *            Predecessor {@link JLabelGroup} instance, or {@code null}
     */
    public JLabelGroup(Component c, String text, Icon icon, JLabelGroup pred) {
        this(c, createDefaultLabel(text, icon), pred);
    }

    /**
     * Creates a new {@link JLabelGroup} label with a given label {@link JComponent}. Use
     * this if you want to use a different label.
     *
     * @param c
     *            {@link Component} to be labelled
     * @param label
     *            Label {@link JComponent}
     * @param pred
     *            Predecessor {@link JLabelGroup} instance, or {@code null}
     */
    public JLabelGroup(Component c, JComponent label, JLabelGroup pred) {
        this.comp = c;
        this.label = label;
        this.pred = pred;

        setVerticalAlignment(SwingConstants.CENTER);
        if (label instanceof JLabel) {
            ((JLabel) label).setLabelFor(comp);
        }

        setLayout(new BorderLayout());
        add(label, BorderLayout.LINE_START);
        add(comp, BorderLayout.CENTER);
    }

    /**
     * Calculates the maximum label with of a {@link JLabelGroup} chain.
     * {@link #rearrange()} will use this method to compute the width.
     *
     * @return Maximum width
     */
    protected int getMaximumWidth() {
        if (pred != null) {
            return Math.max(label.getMinimumSize().width, pred.getMaximumWidth());
        } else {
            return label.getMinimumSize().width;
        }
    }

    /**
     * Sets the vertical alignment of the label, using {@link SwingConstants}. Default is
     * {@link SwingConstants#CENTER}.
     *
     * @param pos
     *            Alignment: TOP, CENTER or BOTTOM.
     */
    public void setVerticalAlignment(int pos) {
        Border border;

        switch (pos) {
            case SwingConstants.TOP:
                border = new EmptyBorder(1, 0, 0, 5);
                break;

            case SwingConstants.BOTTOM:
                border = new EmptyBorder(0, 0, 1, 5);
                break;

            default:
                border = new EmptyBorder(0, 0, 0, 5);
        }

        label.setBorder(border);
        if (label instanceof JLabel) {
            ((JLabel) label).setVerticalAlignment(pos);
        }
    }

    /**
     * Recursively sets the minimum width of this JLabelGroup chain. This method must be
     * invoked on the <em>last</em> JLabelGroup of the chain. It is used by
     * {@link #rearrange()}.
     *
     * @param width
     *            New minimum width
     */
    protected void setMinimumWidth(int width) {
        Dimension dim = new Dimension(width, label.getMinimumSize().height);
        label.setMinimumSize(dim);
        label.setPreferredSize(dim);
        if (pred != null) pred.setMinimumWidth(width);
        invalidate();
    }

    /**
     * Rearranges the {@link JLabelGroup} chain. All labels in this chain are set to the
     * width of the broadest label. This method must be invoked on the <em>last</em>
     * {@link JLabelGroup} of a chain!
     * <p>
     * If further {@link JLabelGroup} objects are added later, this method must be invoked
     * again, on the new last element of the chain.
     * <p>
     * All {@link JLabelGroup} instances are regarded, even if they are currently
     * invisible.
     */
    public void rearrange() {
        setMinimumWidth(getMaximumWidth());
    }

    /**
     * Sets a mnemonic key for this label.
     *
     * @param key
     *            Key to be used
     */
    public void setMnemonic(char key) {
        if (label instanceof JLabel) {
            ((JLabel) label).setDisplayedMnemonic(key);
        }
    }

    /**
     * Sets a mnemonic code for this label.
     *
     * @param code
     *            Keycode to be used
     */
    public void setMnemonic(int code) {
        if (label instanceof JLabel) {
            ((JLabel) label).setDisplayedMnemonic(code);
        }
    }

    /**
     * Gets the {@link JComponent} that is used as a label for this {@link JLabelGroup}.
     * Usually this is a {@link JLabel}, but you should not rely on this!
     *
     * @return {@link JComponent} used as a label.
     * @since R5
     */
    public JComponent getLabel() {
        return label;
    }

    /**
     * Creates the default {@link JComponent} used as a label, if one of the text
     * constructors is used.
     *
     * @param text
     *            Label text
     * @param icon
     *            {@link Icon} to be used, or {@code null}
     * @return {@link JComponent} showing the text and the icon
     * @since R5
     */
    protected static JComponent createDefaultLabel(String text, Icon icon) {
        if (icon != null) {
            return new JLabel(text, icon, SwingConstants.WEST);
        } else {
            return new JLabel(text);
        }
    }

}
