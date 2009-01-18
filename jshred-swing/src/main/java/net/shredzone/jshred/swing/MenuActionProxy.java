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
import java.awt.event.*;
import java.beans.*;
import java.io.Serializable;

/**
 * The MenuActionProxy proxies {@link Action}s to be used in menus. It takes
 * care about a proper scaling of the {@link Action} icon to a nice menu size.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: MenuActionProxy.java 243 2009-01-18 15:05:21Z shred $
 */
public class MenuActionProxy implements Action, Serializable {
    private static final long serialVersionUID = 3257285850856699190L;
    
    private final Action master;
    private Dimension dim;

    /**
     * Creates a new MenuActionProxy. The default icon dimensions (16x16) are
     * used.
     * 
     * @param a
     *            {@link Action}
     */
    public MenuActionProxy(Action a) {
        this(a, new Dimension(16, 16));
    }

    /**
     * Creates a new MenuActionProxy with given Icon {@link Dimension}.
     * 
     * @param a
     *            {@link Action}
     * @param dim
     *            Icon {@link Dimension}
     */
    public MenuActionProxy(Action a, Dimension dim) {
        master = a;
        this.dim = dim;
    }

    /**
     * Set new Icon dimensions for the menu icon. Default is 16x16 pixels.
     * 
     * @param dim
     *            New icon {@link Dimension}
     */
    public void setIconDimension(Dimension dim) {
        this.dim = dim;
    }

    /**
     * Get the current dimension of the menu icon.
     * 
     * @return Icon {@link Dimension}
     */
    public Dimension getIconDimension() {
        return dim;
    }

    /**
     * Delegates the invocation to the Action's {@link Action#getValue(String)}
     * method.
     */
    public Object getValue(String key) {
        Object val = master.getValue(key);

        // --- Scale icon ---
        if (key.equals(SMALL_ICON) && (val instanceof ImageIcon)) {
            ImageIcon icon = (ImageIcon) val;
            if (icon.getIconWidth() != dim.width
                    || icon.getIconHeight() != dim.height) {
                Image img = icon.getImage();
                img = img.getScaledInstance(dim.width, dim.height, Image.SCALE_SMOOTH);
                val = new ImageIcon(img);
            }
        }

        return val;
    }

    /**
     * Delegates the invocation to the Action's
     * {@link Action#putValue(String, Object)} method.
     */
    public void putValue(String key, Object value) {
        master.putValue(key, value);
    }

    /**
     * Delegates the invocation to the Action's
     * {@link Action#setEnabled(boolean)} method.
     */
    public void setEnabled(boolean b) {
        master.setEnabled(b);
    }

    /**
     * Delegates the invocation to the Action's {@link Action#isEnabled()}
     * method.
     */
    public boolean isEnabled() {
        return master.isEnabled();
    }

    /**
     * Delegates the invocation to the Action's
     * {@link Action#addPropertyChangeListener(PropertyChangeListener)} method.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        master.addPropertyChangeListener(listener);
    }

    /**
     * Delegates the invocation to the Action's
     * {@link Action#removePropertyChangeListener(PropertyChangeListener)}
     * method.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        master.removePropertyChangeListener(listener);
    }

    /**
     * Delegates the invocation to the Action's
     * {@link Action#actionPerformed(ActionEvent)} method.
     */
    public void actionPerformed(ActionEvent e) {
        master.actionPerformed(e);
    }
}
