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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * This is some kind of {@link JTextField} with a {@link JComboBox}. Text can be entered
 * freely into the text field. The class keeps a history of recently entered texts, which
 * can be selected in the {@link JComboBox}. The history is optionally stored as
 * {@link Preferences} if a unique name is given to the constructor.
 *
 * @author Richard "Shred" Körber
 * @since R6
 */
public class JHistoryTextField extends JComponent {
    private static final long serialVersionUID = 3688784791113577272L;

    private JComboBox jCombo;
    private int histSize;
    private boolean autoSelect = true;
    private String nodeName = null;
    private final ListenerManager<ActionListener> listeners = new ListenerManager<ActionListener>();

    /**
     * Creates a new {@link JHistoryTextField}. The history will only be stored during the
     * lifetime of this object.
     */
    public JHistoryTextField() {
        this(null);
    }

    /**
     * Creates a new {@link JHistoryTextField}. The history will be persistently stored
     * using {@link Preferences}. You must provide a unique name for the history to be
     * stored. It is strongly recommended to use the Java notation for unique names, as
     * used in package names (i.e. your domain name in reverse notation, following a
     * unique string of your choice).
     *
     * @param name
     *            Name which is used to persist the history.
     */
    public JHistoryTextField(String name) {
        nodeName = name;

        // --- Create the GUI ---
        setLayout(new BorderLayout());
        jCombo = new JComboBox();
        jCombo.setEditable(true);
        add(jCombo, BorderLayout.CENTER);

        // --- Set the default history size ---
        setHistorySize(10); // Default is 10 entries

        // --- Recall the history ---
        if (nodeName != null) {
            try {
                recallHistory(nodeName);
            } catch (BackingStoreException e) {
                // We will lose the history now. Anyhow we shouldn't bother
                // the user because he won't be able to do anything about it
                // anyways. So silently ignore the exception.
            }
            jCombo.setSelectedIndex(-1);
        }

        // --- Add an ActionListener ---
        jCombo.getEditor().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newtext = jCombo.getEditor().getItem().toString();

                // --- Remember the entry ---
                addHistory(newtext);

                // --- Mark all the text ---
                if (autoSelect) jCombo.getEditor().selectAll();

                // --- Notify everyone about a new text ---
                final ActionEvent e2 = new ActionEvent(JHistoryTextField.this,
                                ActionEvent.ACTION_PERFORMED, "textChanged", e.getWhen(),
                                e.getModifiers());
                fireActionEvent(e2);
            }
        });
    }

    /**
     * Sets the maximum history size. If a new entry would exceed the limit, the least
     * recently used entry will be removed from the history.
     *
     * @param size
     *            New history size
     */
    public void setHistorySize(int size) {
        if (histSize < 0) throw new IllegalArgumentException("size must be positive");

        // --- Remove entries to reach the size ---
        synchronized (jCombo) {
            histSize = size;
            if (histSize > jCombo.getItemCount()) {
                for (int ix = jCombo.getItemCount() - 1; ix >= histSize; ix--) {
                    jCombo.removeItemAt(ix);
                }
            }
        }

        // --- Set Maximum ---
        // Take care that the entire history is visible
        // within a reasonable range.
        jCombo.setMaximumRowCount(Math.min(size, 20));
    }

    /**
     * Gets the current history size. Default is 10.
     *
     * @return Current history size
     */
    public int getHistorySize() {
        return histSize;
    }

    /**
     * Sets the auto selection mode. If enabled, the entire text field will be marked
     * after the user pressed return, so a new text entered will automatically replace the
     * old one. This mode is turned on by default.
     *
     * @param as
     *            Auto selection mode.
     */
    public void setAutoSelection(boolean as) {
        autoSelect = as;
    }

    /**
     * Checks if the auto selection mode is enabled.
     *
     * @return Auto selection mode state.
     */
    public boolean isAutoSelection() {
        return autoSelect;
    }

    /**
     * Sets the text to be shown in this component. The text will also be added to the
     * history.
     *
     * @param text
     *            Text to be set.
     */
    public void setText(String text) {
        synchronized (jCombo) {
            jCombo.setSelectedItem(text);
        }
    }

    /**
     * Gets the text that is currently shown in this component.
     *
     * @return Current text.
     */
    public String getText() {
        synchronized (jCombo) {
            Object item = jCombo.getSelectedItem();
            return (item != null ? item.toString() : "");
        }
    }

    /**
     * Adds a text to the top of the history. If the text was already within the history,
     * it is moved to the top. After that the text will be shown in the component. The
     * current history will be persisted if a name was given to the constructor.
     *
     * @param text
     *            Text to be added to the history.
     */
    protected void addHistory(String text) {
        if (text == null || text.equals("")) return;

        synchronized (jCombo) {
            // --- First look if the entry already exists ---
            int cnt = jCombo.getItemCount();
            for (int ix = 0; ix < cnt; ix++) {
                if (jCombo.getItemAt(ix).equals(text)) {
                    // --- YES: move it to the top ---
                    jCombo.removeItemAt(ix);
                    break;
                }
            }

            // --- Add the new item to the top ---
            jCombo.insertItemAt(text, 0);
            jCombo.setSelectedIndex(0);

            // --- Keep within maximum ---
            while (jCombo.getItemCount() > histSize) {
                jCombo.removeItemAt(jCombo.getItemCount() - 1);
            }

            // --- Store history ---
            if (nodeName != null) {
                try {
                    storeHistory(nodeName);
                } catch (BackingStoreException e) {
                    // We will lose the history now. Anyhow we shouldn't bother
                    // the user because he won't be able to do anything about it
                    // anyhow. So silently ignore the exception.
                }
            }
        }
    }

    /**
     * Gets a {@link List} of the current history. The list is unmodifiable and sorted
     * from the most recent to the least recent history entry.
     *
     * @return {@link List} of all history entries
     */
    public List<String> getHistory() {
        synchronized (jCombo) {
            int cnt = jCombo.getItemCount();
            List<String> lResult = new ArrayList<String>(cnt);
            for (int ix = 0; ix < cnt; ix++) {
                lResult.add(jCombo.getItemAt(ix).toString());
            }
            return Collections.unmodifiableList(lResult);
        }
    }

    /**
     * Gets the current number of history entries.
     *
     * @return Current number of history entries.
     */
    public int getCurrentSize() {
        synchronized (jCombo) {
            return jCombo.getItemCount();
        }
    }

    /**
     * Clears the entire history.
     */
    public void clearHistory() {
        synchronized (jCombo) {
            jCombo.removeAllItems();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        jCombo.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * Stores the current history to the preferences, using the given name.
     *
     * @param name
     *            Name to file the history to.
     * @throws BackingStoreException
     *             if it was not possible to store the history.
     */
    protected void storeHistory(String name) throws BackingStoreException {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        prefs = prefs.node(name);

        synchronized (jCombo) {
            // --- Clear all old prefs ---
            prefs.clear();

            // --- Write the current history ---
            int cnt = jCombo.getItemCount();
            for (int ix = 0; ix < cnt; ix++) {
                prefs.put(String.valueOf(ix), jCombo.getItemAt(ix).toString());
            }
        }
    }

    /**
     * Recalls the history from preferences, using the given name. The current history
     * will be replaced.
     *
     * @param name
     *            Name the history was filed to.
     * @throws BackingStoreException
     *             if it was not possible to recall the history.
     */
    protected void recallHistory(String name) throws BackingStoreException {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());
        prefs = prefs.node(name);

        synchronized (jCombo) {
            // --- Clear history ---
            clearHistory();

            // --- Recall the current history ---
            String[] keys = prefs.keys();
            Arrays.sort(keys, new HTFComparator());
            int max = Math.min(keys.length, histSize);
            for (int ix = 0; ix < max; ix++) {
                jCombo.addItem(prefs.get(keys[ix], ""));
            }
        }
    }

    /**
     * Adds an {@link ActionListener}. It will be invoked if a new text has been entered.
     * If it was already added, nothing will happen.
     *
     * @param l
     *            ActionListener to be added.
     */
    public void addActionListener(ActionListener l) {
        listeners.addListener(l);
    }

    /**
     * Removes an ActionListener. If it was not added, nothing will happen.
     *
     * @param l
     *            ActionListener to be removed.
     */
    public void removeActionListener(ActionListener l) {
        listeners.removeListener(l);
    }

    /**
     * Informs all registered ActionListeners about a new ActionEvent.
     *
     * @param e
     *            The ActionEvent to be broadcasted.
     */
    protected void fireActionEvent(ActionEvent e) {
        for (ActionListener l : listeners.getListeners()) {
            l.actionPerformed(e);
        }
    }

    /**
     * This comparator compares two integer values of a string representation.
     */
    private static class HTFComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int i1 = Integer.parseInt(o1);
            int i2 = Integer.parseInt(o2);
            return i1 - i2;
        }
    }

}
