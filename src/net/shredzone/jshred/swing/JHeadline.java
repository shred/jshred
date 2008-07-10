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

import javax.swing.*;

import java.awt.*;

/**
 * This is a headline to be used in dialogs etc. It shows a big title to the
 * left, and optionally a nice icon to the right and a description below the
 * title. The headline is colored in a gradient, starting in a certain color to
 * the left, and going to the current background color to the right.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JHeadline.java 169 2008-07-10 22:01:03Z shred $
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
     * Create a new JHeadline with the given title. {@link Color#GRAY} is used
     * as default color.
     * 
     * @param title
     *            Title to be used
     */
    public JHeadline(String title) {
        this(title, null);
    }

    /**
     * Create a new JHeadline with the given title and icon. {@link Color#GRAY}
     * is used as default color.
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
     * Create a new JHeadline with the given title, description, icon.
     * {@link Color#GRAY} is used as default color.
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
        if (title == null)
            throw new NullPointerException("title must not be null");
        
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
        if (jlDesc.isVisible())
            return jlDesc.getText();
        else
            return null;
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
        if (jlIcon.isVisible())
            return jlIcon.getIcon();
        else
            return null;
    }

    /**
     * Set the text color. The title will be shown in this color. The
     * description will be shown in the same color, but with an alpha value of
     * 200, so the background gradient will shine through a little.
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
