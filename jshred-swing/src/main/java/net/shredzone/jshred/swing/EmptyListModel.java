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

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * This is just a mere empty {@link ListModel} which will never have any entries.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @since R7
 * @version $Id: EmptyListModel.java 584 2011-07-30 20:42:48Z shred $
 */
public final class EmptyListModel implements ListModel {

    /**
     * Get the size. It's always 0.
     * 
     * @return Size
     * @see javax.swing.ListModel#getSize()
     */
    @Override
    public int getSize() {
        return 0;
    }

    /**
     * Get an element. Should never be called since the list is empty. Will always return
     * null.
     * 
     * @param index
     *            Index
     * @return Always null
     * @see javax.swing.ListModel#getElementAt(int)
     */
    @Override
    public Object getElementAt(int index) {
        return null;
    }

    /**
     * Add a listener. Since this list will always be empty, the listeners are just
     * ignored.
     * 
     * @param l
     *            {@link ListDataListener} to be ignored.
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void addListDataListener(ListDataListener l) {}

    /**
     * Remove a listener. Since this list will always be empty, the listeners are just
     * ignored.
     * 
     * @param l
     *            {@link ListDataListener} to be ignored.
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    @Override
    public void removeListDataListener(ListDataListener l) {}

}
