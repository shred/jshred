/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *-----------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * Currently this is just a {@link JComboBox} which allows to select a Look and
 * Feel from the system's list of Look and Feels. Future releases will also add
 * another {@link JComboBox} for different auxiliary looks, and a preview area.
 * If you only have limited space available in your GUI, you are advised to pass
 * <code>true</code> to {@link #setSmallView(boolean)} for future compatibility.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JLAFSelector.java 169 2008-07-10 22:01:03Z shred $
 * @since R8
 */
public class JLAFSelector extends JPanel {
    private static final long serialVersionUID = 3689916188578691125L;
    private static final Map<String, String> mLAFs = new HashMap<String, String>();

    private final JComboBox jcbSelector;
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
     * Tries to add a LAF class to the selector. If the class is not available,
     * nothing will happen.
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
        } catch (ClassNotFoundException e) {
        }
    }

    /**
     * Create a new Look and Feel selector.
     */
    public JLAFSelector() {
        setLayout(new BorderLayout());
        List<String> lNames = new ArrayList<String>(mLAFs.values());
        Collections.sort(lNames);
        jcbSelector = new JComboBox(lNames.toArray());
        jcbSelector.setEditable(true);
        add(jcbSelector, BorderLayout.CENTER);
        setCurrentLAF();
    }

    /**
     * Set the currently used Look and Feel.
     */
    public void setCurrentLAF() {
        setSelectedLAF(UIManager.getLookAndFeel());
    }

    /**
     * Set a Look and Feel as current selection, by passing a
     * {@link LookAndFeel} object.
     * 
     * @param laf
     *            {@link LookAndFeel} object to set.
     */
    public void setSelectedLAF(LookAndFeel laf) {
        setSelectedLAF(laf.getClass().getName());
    }

    /**
     * Set a Look and Feel as current selection, by passing a
     * {@link LookAndFeelInfo}.
     * 
     * @param lafinfo
     *            {@link LookAndFeelInfo} object to set.
     */
    public void setSelectedLAF(UIManager.LookAndFeelInfo lafinfo) {
        setSelectedLAF(lafinfo.getClassName());
    }

    /**
     * Set a Look and Feel as current selection, by passing a class name. The
     * class name does not necessarily need to exist or be a valid
     * {@link LookAndFeel} class. Alternatively you can also pass the name of a
     * LookAndFeel.
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
                final String cmpName = (String) jcbSelector.getItemAt(ix);
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
     * Get the name of the selected LAF. This may be a class name or a human
     * readable look and feel name.
     * <p>
     * <em>NOTE:</em> the returned value can be any string. There is <em>no</em>
     * guarantee that the returned string:
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
     * Set if a small view area is to be used by this component. Currently only
     * one {@link JComboBox} is used for this component, but it is planned that
     * a future release of the JLAFSelector will consist of two
     * {@link JComboBox} and an optional preview area.
     * <p>
     * If you do not have plenty of space in your GUI, you should invoke this
     * method with <code>true</code>, to hide the preview area (and maybe some
     * other components that are not really necessary).
     * <p>
     * Currently this method does nothing, since there is no preview area yet.
     * 
     * @param small
     */
    public void setSmallView(boolean small) {
        this.small = small;
    }

    /**
     * Check if there is only a small view area available for this component.
     * This is <code>false</code> by default.
     * 
     * @return <code>true</code>: only a small area is available
     */
    public boolean isSmallView() {
        return small;
    }

    /**
     * Try to set a look and feel that was selected by this component. This is a
     * convenience method that tries to set the passed look and feel, which can
     * be either a human readable name or a class name. If this method is unable
     * to set this look and feel, the default look and feel is used.
     * 
     * @param laf
     *            Look and Feel string to be used. May be <code>null</code>.
     * @return The class name of the actual Look and Feel that has been set. If
     *         no Look and Feel could be set, <code>null</code> will be returned
     *         (which should never happen -- famous last words).
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
