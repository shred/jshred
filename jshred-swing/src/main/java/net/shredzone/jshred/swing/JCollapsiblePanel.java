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
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This {@link JPanel} shows a Component with a headline above it. The user can click on
 * the headline in order to collapse or exand the component. If the component is
 * collapsed, it will be hidden and only the headline is shown. If the component is
 * expanded, everything is shown.
 * <p>
 * {@link JCollapsiblePanel} can be used to allow the user to hide unimportant parts of
 * the GUI if there is only little space available.
 * <p>
 * Due to a bug this component was not really functional until R12.
 *
 * @author Richard "Shred" Körber
 * @since R9
 */
public class JCollapsiblePanel extends JPanel {
    private static final long serialVersionUID = 3546645386727994681L;
    private final static Preferences prefs = Preferences.userNodeForPackage(JCollapsiblePanel.class);

    protected Component content;
    private JToggleButton jbToggle;
    private Icon iconCollapsed;
    private Icon iconExpanded;
    private String id; // unique id for remembering the collapse state
    private final ListenerManager<ChangeListener> listener = new ListenerManager<>();

    /**
     * Creates an empty {@link JCollapsiblePanel} with no title.
     */
    public JCollapsiblePanel() {
        this("");
    }

    /**
     * Creates an empty {@link JCollapsiblePanel} with the given title.
     */
    public JCollapsiblePanel(String title) {
        this(title, null);
    }

    /**
     * Creates a {@link JCollapsiblePanel} with the given title and {@link Component}. The
     * panel is initially expanded.
     *
     * @param title
     *            Title
     * @param comp
     *            {@link Component} to be used as content
     */
    public JCollapsiblePanel(String title, Component comp) {
        this(title, comp, true);
    }

    /**
     * Creates a {@link JCollapsiblePanel} with the given title and {@link Component},
     * using the given expanded state initially.
     *
     * @param title
     *            Title
     * @param comp
     *            {@link Component} to be used as content
     * @param expanded
     *            Initial state, {@code true}: expanded, {@code false}: collapsed
     */
    public JCollapsiblePanel(String title, Component comp, boolean expanded) {
        this(title, comp, expanded, null);
    }

    /**
     * Creates a {@link JCollapsiblePanel} with the given title and {@link Component},
     * using the given expanded state initially.
     * <p>
     * With the given id, the collapse state is remembered for the next time the
     * application is started. If there is no state remembered, the given default
     * "expanded" state will be used instead.
     *
     * @param title
     *            Title
     * @param comp
     *            {@link Component} to be used as content
     * @param expanded
     *            Initial state, {@code true}: expanded, {@code false}: collapsed
     * @param id
     *            Unique identifier to remember the collapse state. Pass {@code null} if
     *            the state shall not be remembered.
     * @since R12
     */
    public JCollapsiblePanel(String title, Component comp, boolean expanded, String id) {
        content = comp;
        this.id = id;

        // --- Check the preferences ---
        if (id != null) {
            expanded = prefs.getBoolean("state." + id, expanded);
        }

        // --- Create the toggle button ---
        jbToggle = new JToggleButton(title);
        jbToggle.setSelected(true);
        jbToggle.setBorderPainted(false);
        jbToggle.setBackground(getBackground().darker());
        jbToggle.setRequestFocusEnabled(false);
        jbToggle.setMargin(new Insets(0, 0, 0, 0));
        jbToggle.setHorizontalTextPosition(SwingConstants.RIGHT);
        jbToggle.setHorizontalAlignment(SwingConstants.LEFT);
        jbToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jbToggle.addActionListener(new Listener());

        // --- Initialize the button ---
        setCollapsedIcon(new ArrowIcon(7, 7, SwingConstants.EAST));
        setExpandedIcon(new ArrowIcon(7, 7, SwingConstants.SOUTH));

        // --- Assemble the GUI ---
        setLayout(new BorderLayout());
        if (content != null) {
            add(content, BorderLayout.CENTER);
        }
        add(jbToggle, BorderLayout.NORTH);

        // --- Set the expanded state ---
        setExpanded(expanded);
    }

    /**
     * Sets the icon to be used in the title if the component is collapsed. This is an
     * arrow pointing to the right by default.
     *
     * @param icon
     *            New icon to be used
     */
    public void setCollapsedIcon(Icon icon) {
        jbToggle.setIcon(icon);
        firePropertyChange("collapsedicon", iconCollapsed, icon);
        iconCollapsed = icon;
    }

    /**
     * Gets the current icon to be used if the component is collapsed.
     *
     * @return Collapsed icon
     */
    public Icon getCollapsedIcon() {
        return iconCollapsed;
    }

    /**
     * Sets the icon to be used in the title if the component is expanded. This is an
     * arrow pointing down by default.
     *
     * @param icon
     *            New icon to be used
     */
    public void setExpandedIcon(Icon icon) {
        jbToggle.setSelectedIcon(icon);
        firePropertyChange("expandedicon", iconExpanded, icon);
        iconExpanded = icon;
    }

    /**
     * Gets the current icon to be used if the component is expanded.
     *
     * @return Expanded icon
     */
    public Icon getExpandedIcon() {
        return iconExpanded;
    }

    /**
     * Sets the enabled state. Disabling the {@link JCollapsiblePanel} will only disable
     * the title button, but not the content. I.e. the user cannot collapse the component,
     * but can still use it.
     * <p>
     * <em>NOTE:</em> if {@link #setEnabled(boolean)} is set to {@code false} while the
     * panel is collapsed, then the user will be unable to expand and use the component.
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        jbToggle.setEnabled(b);
    }

    /**
     * Sets the expanded state. If {@code true}, the {@link Component} will be shown. If
     * {@code false}, the Component will be hidden.
     */
    public void setExpanded(boolean b) {
        jbToggle.setSelected(b);
        doExpand(b);
    }

    /**
     * Internal method that does the actual collapsing and expanding of the content.
     *
     * @param b
     *            {@code true}: expand, {@code false}: collapse
     */
    private void doExpand(boolean b) {
        if (content != null) {
            content.setVisible(b);
            revalidate();
        }
        if (id != null) {
            prefs.putBoolean("state." + id, b);
        }
    }

    /**
     * Gets the current expanded state.
     */
    public boolean isExpanded() {
        return jbToggle.isSelected();
    }

    /**
     * Sets the title above the component.
     */
    public void setTitle(String title) {
        firePropertyChange("title", jbToggle.getText(), title);
        jbToggle.setText(title);
    }

    /**
     * Gets the current title above the component.
     */
    public String getTitle() {
        return jbToggle.getText();
    }

    /**
     * Sets a new content {@link Component}. It will replace the current content.
     */
    public void setContent(Component comp) {
        if (content != null) {
            remove(content);
        }
        firePropertyChange("content", content, comp);

        if (comp != null) {
            add(comp, BorderLayout.CENTER);
            doExpand(isExpanded());
        }
        content = comp;
    }

    /**
     * Gets the current content {@link Component}.
     *
     * @return Content {@link Component}, or {@code null} if none was set.
     */
    public Component getContent() {
        return content;
    }

    /**
     * Adds a {@link ChangeListener}. It will be invoked when the collapsed/expanded state
     * was changed.
     *
     * @param l
     *            {@link ChangeListener} to be added
     */
    public void addChangeListener(ChangeListener l) {
        listener.addListener(l);
    }

    /**
     * Removes a {@link ChangeListener}.
     *
     * @param l
     *            {@link ChangeListener} to be removed
     */
    public void removeChangeListener(ChangeListener l) {
        listener.removeListener(l);
    }

    /**
     * Private {@link ActionListener} implementation. It will be invoked when the title
     * {@link JToggleButton} was pressed.
     */
    private class Listener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (content != null) {
                // --- Change visibility ---
                doExpand(jbToggle.isSelected());

                // --- Notify everyone ---
                ChangeEvent event = new ChangeEvent(JCollapsiblePanel.this);
                for (ChangeListener l : listener.getListeners()) {
                    l.stateChanged(event);
                }
            }
        }
    }

}
