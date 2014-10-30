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

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * This is just a mere empty {@link TreeModel} which will never have any entries.
 *
 * @author Richard "Shred" Körber
 * @since R7
 */
public final class EmptyTreeModel implements TreeModel {

    /**
     * Gets the root node. It will return {@code null} since the tree has no nodes.
     */
    @Override
    public Object getRoot() {
        return null;
    }

    /**
     * Gets the child count, which is always 0.
     */
    @Override
    public int getChildCount(Object parent) {
        return 0;
    }

    /**
     * Checks if the node is a leaf. Should never be invoked since there are no nodes.
     * Will always return {@code true}.
     */
    @Override
    public boolean isLeaf(Object node) {
        return true;
    }

    /**
     * Adds a {@link TreeModelListener}. Since the tree will never change, the listener is
     * just ignored.
     */
    @Override
    public void addTreeModelListener(TreeModelListener l) {}

    /**
     * Removes a {@link TreeModelListener}. Since the tree will never change, the listener
     * is just ignored.
     */
    @Override
    public void removeTreeModelListener(TreeModelListener l) {}

    /**
     * Gets a child. Should never be invoked since there are no childs. It will always
     * return {@code null}.
     */
    @Override
    public Object getChild(Object parent, int index) {
        return null;
    }

    /**
     * Gets the index of a child. Should never be invoked since there are no childs. It
     * will always return -1.
     */
    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return -1;
    }

    /**
     * Value for path changed. Should never be invoked for an empty tree. It just does
     * nothing.
     */
    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // does nothing
    }

}
