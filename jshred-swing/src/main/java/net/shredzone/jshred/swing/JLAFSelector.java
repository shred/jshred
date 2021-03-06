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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * This component allows the user to select a Look and Feel.
 * <p>
 * Currently this is just a {@link JComboBox} which allows to select a Look and Feel from
 * the system's list of Look and Feels. Future releases will also add another
 * {@link JComboBox} for different auxiliary looks, and a preview area. If you only have
 * limited space available in your GUI, you are advised to pass {@code true} to
 * {@link #setSmallView(boolean)} for future compatibility.
 *
 * @author Richard "Shred" Körber
 * @since R8
 */
public class JLAFSelector extends JPanel {
    private static final long serialVersionUID = 3689916188578691125L;
    private static final Map<String, String> mLAFs = new HashMap<>();

    private final JComboBox<String> jcbSelector;
    private boolean small = false;

    static {
        // --- Get all System LAFs ---
        for (UIManager.LookAndFeelInfo lafi : UIManager.getInstalledLookAndFeels()) {
            mLAFs.put(lafi.getClassName(), lafi.getName());
        }

        // --- Add Kunststoff if available ---
        attemptAdd("com.incors.plaf.kunststoff.KunststoffLookAndFeel", "Kunststoff");

        // --- Add Napkin if available ---
        attemptAdd("net.sourceforge.napkinlaf.NapkinLookAndFeel", "Napkin Style");

        // --- Add JGoodies if available ---
        attemptAdd("com.jgoodies.looks.windows.WindowsLookAndFeel", "JGoodies Windows");
        attemptAdd("com.jgoodies.looks.plastic.PlasticLookAndFeel", "JGoodies Plastic");
        attemptAdd("com.jgoodies.looks.plastic.Plastic3DLookAndFeel", "JGoodies Plastic 3D");
        attemptAdd("com.jgoodies.looks.plastic.PlasticXPLookAndFeel", "JGoodies Plastic XP");
    }

    /**
     * Tries to add a LAF class to the selector. If the class is not available, nothing
     * will happen.
     *
     * @param classname
     *            LAF class name
     * @param label
     *            Human readable name to be added if the class is available
     * @since R10
     */
    private static void attemptAdd(String classname, String label) {
        try {
            if (Class.forName(classname) != null) {
                mLAFs.put(classname, label);
            }
        } catch (ClassNotFoundException e) {}
    }

    /**
     * Creates a new Look and Feel selector.
     */
    public JLAFSelector() {
        setLayout(new BorderLayout());
        List<String> lNames = new ArrayList<>(mLAFs.values());
        Collections.sort(lNames);
        jcbSelector = new JComboBox<String>(lNames.toArray(new String[lNames.size()]));
        jcbSelector.setEditable(true);
        add(jcbSelector, BorderLayout.CENTER);
        setCurrentLAF();
    }

    /**
     * Sets the currently used Look and Feel.
     */
    public void setCurrentLAF() {
        setSelectedLAF(UIManager.getLookAndFeel());
    }

    /**
     * Sets a Look and Feel as current selection, by passing a {@link LookAndFeel} object.
     *
     * @param laf
     *            {@link LookAndFeel} object to set.
     */
    public void setSelectedLAF(LookAndFeel laf) {
        setSelectedLAF(laf.getClass().getName());
    }

    /**
     * Sets a Look and Feel as current selection, by passing a {@link LookAndFeelInfo}.
     *
     * @param lafinfo
     *            {@link LookAndFeelInfo} object to set.
     */
    public void setSelectedLAF(UIManager.LookAndFeelInfo lafinfo) {
        setSelectedLAF(lafinfo.getClassName());
    }

    /**
     * Sets a Look and Feel as current selection, by passing a class name. The class name
     * does not necessarily need to exist or be a valid {@link LookAndFeel} class.
     * Alternatively you can also pass the name of a LookAndFeel.
     *
     * @param classname
     *            {@link LookAndFeel} class name to set.
     */
    public void setSelectedLAF(String classname) {
        // --- Look and Feel name ---
        // Try to convert a look and feel name into a human readable name.
        String hrName = mLAFs.get(classname);
        if (hrName != null) {
            // Locate the index of this entry
            final int cnt = jcbSelector.getItemCount();
            for (int ix = 0; ix < cnt; ix++) {
                final String cmpName = jcbSelector.getItemAt(ix);
                if (cmpName.equals(hrName)) {
                    jcbSelector.setSelectedIndex(ix);
                    return;
                }
            }
        }

        // --- Just set the class name ---
        jcbSelector.setSelectedItem(classname);
    }

    /**
     * Gets the name of the selected LAF. This may be a class name or a human readable
     * look and feel name.
     * <p>
     * <em>NOTE:</em> the returned value can be any string. There is <em>no</em> guarantee
     * that the returned string:
     * <ul>
     * <li>is a valid LookAndFeel class name,</li>
     * <li>is a LookAndFeel that is available on this system,</li>
     * <li>is a LookAndFeel that is permitted on this system, or</li>
     * <li>is a valid class name at all.</li>
     * </ul>
     *
     * @return The selected Look and Feel.
     */
    public String getSelectedLAF() {
        return jcbSelector.getSelectedItem().toString();
    }

    /**
     * Sets if a small view area is to be used by this component. Currently only one
     * {@link JComboBox} is used for this component, but it is planned that a future
     * release of the JLAFSelector will consist of two {@link JComboBox} and an optional
     * preview area.
     * <p>
     * If you do not have plenty of space in your GUI, you should invoke this method with
     * {@code true}, to hide the preview area (and maybe some other components that are
     * not really necessary).
     * <p>
     * Currently this method does nothing, since there is no preview area yet.
     */
    public void setSmallView(boolean small) {
        this.small = small;
    }

    /**
     * Checks if there is only a small view area available for this component. This is
     * {@code false} by default.
     *
     * @return {@code true}: only a small area is available
     */
    public boolean isSmallView() {
        return small;
    }

    /**
     * Tries to set a look and feel that was selected by this component. This is a
     * convenience method that tries to set the passed look and feel, which can be either
     * a human readable name or a class name. If this method is unable to set this look
     * and feel, the default look and feel is used.
     *
     * @param laf
     *            Look and Feel string to be used. May be {@code null}.
     * @return The class name of the actual Look and Feel that has been set. If no Look
     *         and Feel could be set, {@code null} will be returned (which should never
     *         happen -- famous last words).
     */
    public static String setLookAndFeel(String laf) {
        // --- Convert human readable ---
        // Convert a human readable look and feel name into a class name.
        if (laf != null && mLAFs.containsValue(laf)) {
            for (String cname : mLAFs.keySet()) {
                if (mLAFs.get(cname).equals(laf)) {
                    laf = cname;
                    break;
                }
            }
        }

        // --- Try to set this look and feel ---
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Throwable t) {
            // We were unable to set the user's desired Look and Feel.
            // Now find out what the System's Look and Feel is.
            laf = UIManager.getSystemLookAndFeelClassName();

            // The Motif Look and Feel sucks... (sorry guys, but yes
            // I really think so). If the System's Look and Feel is Motif,
            // we will use the Cross Platform Look and Feel instead, which is
            // usually the standard Metal look.
            if (laf.equals("com.sun.java.swing.plaf.motif.MotifLookAndFeel")) {
                laf = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            // Now try again to set the look and feel
            try {
                UIManager.setLookAndFeel(laf);
            } catch (Throwable t2) {
                // We were even unable to set the System or CrossPlatform
                // Look and Feel. If we have reached this point, something
                // went wrong really badly. Just keep the current LAF
                // (whatever it is) and return null.
                laf = null;
            }
        }

        // --- Return the LAF that has been set ---
        return laf;
    }

}
