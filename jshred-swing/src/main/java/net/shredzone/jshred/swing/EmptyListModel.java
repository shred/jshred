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
 * @author Richard "Shred" Körber
 * @since R7
 */
public final class EmptyListModel<E> implements ListModel<E> {

    /**
     * Gets the size. It's always 0.
     */
    @Override
    public int getSize() {
        return 0;
    }

    /**
     * Gets an element. Should never be called since the list is empty. Will always return
     * {@code null}.
     */
    @Override
    public E getElementAt(int index) {
        return null;
    }

    /**
     * Adds a listener. Since this list will always be empty, the listeners are just
     * ignored.
     */
    @Override
    public void addListDataListener(ListDataListener l) {}

    /**
     * Removes a listener. Since this list will always be empty, the listeners are just
     * ignored.
     */
    @Override
    public void removeListDataListener(ListDataListener l) {}

}
