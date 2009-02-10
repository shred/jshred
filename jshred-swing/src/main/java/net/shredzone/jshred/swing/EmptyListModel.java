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

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * This is just a mere empty {@link ListModel} which will never have any
 * entries.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @since R7
 * @version $Id: EmptyListModel.java 256 2009-02-10 22:56:35Z shred $
 */
public final class EmptyListModel implements ListModel {

    /**
     * Get the size. It's always 0.
     * 
     * @return Size
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return 0;
    }

    /**
     * Get an element. Should never be called since the list is empty. Will
     * always return null.
     * 
     * @param index
     *            Index
     * @return Always null
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        return null;
    }

    /**
     * Add a listener. Since this list will always be empty, the listeners are
     * just ignored.
     * 
     * @param l
     *            {@link ListDataListener} to be ignored.
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener l) {
    }

    /**
     * Remove a listener. Since this list will always be empty, the listeners
     * are just ignored.
     * 
     * @param l
     *            {@link ListDataListener} to be ignored.
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    public void removeListDataListener(ListDataListener l) {
    }

}
