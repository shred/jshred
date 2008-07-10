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
 * This {@link JButton} is to be used in toolbars. It takes care that the button
 * never has a text, never gets the focus and has no borders.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JToolbarButton.java 169 2008-07-10 22:01:03Z shred $
 */
public class JToolbarButton extends JButton {
    private static final long serialVersionUID = 3905246710308417843L;

    /**
     * Creates a new basic JToolbarButton.
     * 
     * @since R8
     */
    public JToolbarButton() {
        super();
        init();
    }

    /**
     * Creates a new JToolbarButton with a title.
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
     * Creates a new JToolbarButton with a title and an icon.
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
     * Creates a new JToolbarButton with an icon.
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
     * Creates a new JToolbarButton for a certain {@link Action}. The
     * {@link Action}'s text is not displayed.
     * 
     * @param a
     *            {@link Action}
     */
    public JToolbarButton(Action a) {
        this(a, false);
    }

    /**
     * Creates a new JToolbarButton for a certain {@link Action}.
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

    /**
     * Initialize all parameters for a JToolbarButton.
     */
    private void init() {
        setRequestFocusEnabled(false);
        setFocusable(false);
        setMargin(new Insets(0, 0, 0, 0));
        setContentAreaFilled(false);
    }

}
