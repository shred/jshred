/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://jshred.shredzone.org-------------------------------------------------------------------
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
 * This Panel shows a Component with a headline above it. The user can click on
 * the headline in order to collapse or exand the component. If the component is
 * collapsed, it will be hidden and only the headline is shown. If the component
 * is expanded, everything is shown.
 * <p>
 * JCollapsiblePanel can be used to allow the user to hide unimportant parts of
 * the GUI if there is only little space available.
 * <p>
 * Due to a bug this component was not really functional until R12.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JCollapsiblePanel.java 256 2009-02-10 22:56:35Z shred $
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
    private final ListenerManager<ChangeListener> listener = new ListenerManager<ChangeListener>();

    /**
     * Creates an empty JCollapsiblePanel with no title.
     */
    public JCollapsiblePanel() {
        this("");
    }

    /**
     * Creates an empty JCollapsiblePanel with the given title.
     * 
     * @param title
     *            Title
     */
    public JCollapsiblePanel(String title) {
        this(title, null);
    }

    /**
     * Creates a JCollapsiblePanel with the given title and {@link Component}.
     * The panel is initially expanded.
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
     * Creates a JCollapsiblePanel with the given title and {@link Component},
     * using the given expanded state initially.
     * 
     * @param title
     *            Title
     * @param comp
     *            {@link Component} to be used as content
     * @param expanded
     *            Initial state, true: expanded, false: collapsed
     */
    public JCollapsiblePanel(String title, Component comp, boolean expanded) {
        this(title, comp, expanded, null);
    }

    /**
     * Creates a JCollapsiblePanel with the given title and {@link Component},
     * using the given expanded state initially.
     * <p>
     * With the given id, the collapse state is remembered for the next time the
     * application is started. If there is no state remembered, the given
     * default "expanded" state will be used instead.
     * 
     * @param title
     *            Title
     * @param comp
     *            {@link Component} to be used as content
     * @param expanded
     *            Initial state, true: expanded, false: collapsed
     * @param id
     *            Unique identifier to remember the collapse state. Pass
     *            <code>null</code> if the state shall not be remembered.
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
        jbToggle.setHorizontalTextPosition(JToggleButton.RIGHT);
        jbToggle.setHorizontalAlignment(JToggleButton.LEFT);
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
     * Set the icon to be used in the title if the component is collapsed. This
     * is an arrow pointing to the right by default.
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
     * Get the current icon to be used if the component is collapsed.
     * 
     * @return Collapsed icon
     */
    public Icon getCollapsedIcon() {
        return iconCollapsed;
    }

    /**
     * Set the icon to be used in the title if the component is expanded. This
     * is an arrow pointing down by default.
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
     * Get the current icon to be used if the component is expanded.
     * 
     * @return Expanded icon
     */
    public Icon getExpandedIcon() {
        return iconExpanded;
    }

    /**
     * Set the enabled state. Disabling the JCollapsiblePane will only disable
     * the title button, but not the content. I.e. the user cannot collapse the
     * component, but can still use it.
     * <p>
     * <em>NOTE:</em> if {@link #setEnabled(boolean)} is set to <code>false</code>
     * while the panel is collapsed, then the user will be unable to expand and
     * use the component.
     * 
     * @param b
     *            true: JCollapsiblePane is enabled, false: disabled.
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        jbToggle.setEnabled(b);
    }

    /**
     * Set the expanded state. If <code>true</code>, the {@link Component} will
     * be shown. If <code>false</code>, the Component will be hidden.
     * 
     * @param b
     *            true: expand, false: collapse
     */
    public void setExpanded(boolean b) {
        jbToggle.setSelected(b);
        doExpand(b);
    }

    /**
     * Internal method that does the actual collapsing and expanding of the
     * content.
     * 
     * @param b
     *            true: expand, false: collapse
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
     * Get the current expanded state.
     * 
     * @return true: expanded, false: collapsed
     */
    public boolean isExpanded() {
        return jbToggle.isSelected();
    }

    /**
     * Set the title above the component.
     * 
     * @param title
     *            New title to be used.
     */
    public void setTitle(String title) {
        firePropertyChange("title", jbToggle.getText(), title);
        jbToggle.setText(title);
    }

    /**
     * Get the current title above the component.
     * 
     * @return Current title.
     */
    public String getTitle() {
        return jbToggle.getText();
    }

    /**
     * Set a new content {@link Component}. It will replace the current content.
     * 
     * @param comp
     *            New content {@link Component} to be used.
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
     * Get the current content {@link Component}.
     * 
     * @return Content {@link Component}, or <code>null</code> if none was set.
     */
    public Component getContent() {
        return content;
    }

    /**
     * Add a {@link ChangeListener}. It will be invoked when the
     * collapsed/expanded state was changed.
     * 
     * @param l
     *            {@link ChangeListener} to be added
     */
    public void addChangeListener(ChangeListener l) {
        listener.addListener(l);
    }

    /**
     * Remove a {@link ChangeListener}.
     * 
     * @param l
     *            {@link ChangeListener} to be removed
     */
    public void removeChangeListener(ChangeListener l) {
        listener.removeListener(l);
    }

/* -------------------------------------------------------------------------- */

    /**
     * Private {@link ActionListener} implementation. It will be invoked when
     * the title {@link JToggleButton} was pressed.
     */
    private class Listener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (content != null) {
                //--- Change visibility ---
                doExpand(jbToggle.isSelected());

                //--- Notify everyone ---
                ChangeEvent event = new ChangeEvent(JCollapsiblePanel.this);
                for (ChangeListener l : listener.getListeners()) {
                    l.stateChanged(event);
                }
            }
        }
    }

}
