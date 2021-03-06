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
import java.awt.HeadlessException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This is a {@link JOptionPane} which offers a CheckBox with a text like "Remember this
 * decision". If this CheckBox is checked, the {@link JRememberOptionPane} will remember
 * the answer and will give this answer immediately next time it is invoked.
 *
 * @author Richard "Shred" Körber
 * @since R7
 */
public class JRememberOptionPane extends JOptionPane {
    private static final long serialVersionUID = 3544392526023898161L;

    private static Preferences prefs = Preferences.userNodeForPackage(JRememberOptionPane.class);

    /**
     * Shows a Remember dialog with a remember checkbox. This will be a
     * {@code QUESTION_MESSAGE} dialog with {@code OK_CANCEL_OPTION}.
     *
     * @param parentComponent
     *            parent component to block
     * @param message
     *            message to be shown
     * @param key
     *            unique key for remembering
     * @param title
     *            dialog title
     * @param remember
     *            remember message to be shown
     * @return The value selected (or remembered) by the user.
     * @throws HeadlessException
     */
    public static int showRememberDialog(Component parentComponent, Object message, String key, String title, String remember)
    throws HeadlessException {
        return showRememberDialog(parentComponent, message, key, title, remember, JOptionPane.OK_CANCEL_OPTION);
    }

    /**
     * Shows a Remember dialog with a remember checkbox. This will be a
     * {@code QUESTION_MESSAGE} dialog.
     *
     * @param parentComponent
     *            parent component to block
     * @param message
     *            message to be shown
     * @param key
     *            unique key for remembering
     * @param title
     *            dialog title
     * @param remember
     *            remember message to be shown
     * @param optionType
     *            option type
     * @return The value selected (or remembered) by the user.
     * @throws HeadlessException
     */
    public static int showRememberDialog(Component parentComponent, Object message, String key, String title, String remember, int optionType)
    throws HeadlessException {
        return showRememberDialog(parentComponent, message, key, title, remember, optionType, JOptionPane.QUESTION_MESSAGE, null);
    }

    /**
     * Shows a Remember dialog with a remember checkbox. If the user checked the checkbox
     * before, to remember his decision, it will return immediately that decision, without
     * further user interaction.
     * <p>
     * Note that the key must be unique for all applications. It is recommended to use the
     * java package notation here (i.e. always start the key with your reversed domain).
     * <p>
     * The {@code CLOSED_OPTION} and {@code CANCEL_OPTION} will never be remembered, so if
     * the user just closes or cancels the dialog, he will be asked again next time, no
     * matter whether he checked the checkbox or not.
     *
     * @param parentComponent
     *            parent component to block
     * @param message
     *            message to be shown
     * @param key
     *            unique key for remembering
     * @param title
     *            dialog title
     * @param remember
     *            remember message to be shown
     * @param optionType
     *            option type
     * @param messageType
     *            message type
     * @param icon
     *            icon to be shown
     * @return The value selected (or remembered) by the user.
     * @throws HeadlessException
     */
    public static int showRememberDialog(Component parentComponent, Object message, String key, String title, String remember, int optionType, int messageType, Icon icon)
    throws HeadlessException {
        // --- Get the remembered result ---
        int result = prefs.getInt(key, -9999);

        if (result == -9999) {
            // --- Nothing was remembered ---
            // Construct the input box
            JCheckBox jcbRemember = new JCheckBox(remember);
            JPanel jPane = new JPanel(new BorderLayout());
            {
                Component msg;
                if (message instanceof Component) {
                    msg = (Component) message;
                } else if (message instanceof Icon) {
                    msg = new JLabel((Icon) message);
                } else {
                    JLabel jLabel = new JLabel(message.toString());
                    jLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                    msg = jLabel;
                }
                jPane.add(msg, BorderLayout.CENTER);
                jPane.add(jcbRemember, BorderLayout.SOUTH);
            }

            // --- Show the dialog ---
            result = showConfirmDialog(parentComponent, jPane, title, optionType, messageType, icon);

            // --- Remember the choice ---
            // CLOSED_OPTION and CANCEL_OPTION will not be remembered though
            if (result != JOptionPane.CLOSED_OPTION
                && result != JOptionPane.CANCEL_OPTION && jcbRemember.isSelected()) {
                prefs.putInt(key, result);

                // --- Flush the prefs ---
                // This seems to be required for some strange reasons...
                try {
                    prefs.flush();
                } catch (BackingStoreException e) {}
            }
        }

        // --- Return the user's choice ---
        return result;
    }

    /**
     * Forgets a certain key. The user will be asked again next time the dialog is opened.
     * Nothing will happen if there was no decision stored for the key yet.
     *
     * @param key
     *            Key to forget.
     */
    public static void forget(String key) {
        prefs.remove(key);
    }

    /**
     * Forgets all the keys starting with the given base. The user will be asked again
     * next time the appropriate dialogs are opened. If no matching keys were found,
     * nothing will happen.
     * <p>
     * If you pass {@code null} as base, the user will be asked again for all
     * {@link JRememberOptionPane}s in <em>all</em> applications. It is strongly
     * discouraged to pass {@code null}!
     *
     * @param base
     *            Base of the keys to be forgotten, {@code null} will forget all keys,
     *            even for other applications.
     * @since R10
     */
    public static void forgetAll(String base) {
        try {
            if (base != null) {
                for (String key : prefs.keys()) {
                    if (key.startsWith(base)) prefs.remove(key);
                }
            } else {
                prefs.clear();
            }
        } catch (BackingStoreException e) {}
    }

}
