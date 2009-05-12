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
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @since R7
 * @version $Id: EmptyTreeModel.java 302 2009-05-12 22:19:11Z shred $
 */
public final class EmptyTreeModel implements TreeModel {

    /**
     * Get the root node. It will return null since the tree has no nodes.
     * 
     * @return Root node, which is null.
     * @see javax.swing.tree.TreeModel#getRoot()
     */
    public Object getRoot() {
        return null;
    }

    /**
     * Get the child count, which is always 0.
     * 
     * @param parent
     *            Parent object
     * @return Child count.
     * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
     */
    public int getChildCount(Object parent) {
        return 0;
    }

    /**
     * Check if the node is a leaf. Should never be invoked since there are no nodes. Will
     * always return true.
     * 
     * @param node
     *            Node to check
     * @return Always true.
     * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
     */
    public boolean isLeaf(Object node) {
        return true;
    }

    /**
     * Add a {@link TreeModelListener}. Since the tree will never change, the listener is
     * just ignored.
     * 
     * @param l
     *            {@link TreeModelListener} to be ignored.
     * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void addTreeModelListener(TreeModelListener l) {}

    /**
     * Remove a {@link TreeModelListener}. Since the tree will never change, the listener
     * is just ignored.
     * 
     * @param l
     *            {@link TreeModelListener} to be ignored.
     * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
     */
    public void removeTreeModelListener(TreeModelListener l) {}

    /**
     * Get a child. Should never be invoked since there are no childs. It will always
     * return null.
     * 
     * @param parent
     *            Parent node
     * @param index
     *            Child index
     * @return Always null.
     * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
     */
    public Object getChild(Object parent, int index) {
        return null;
    }

    /**
     * Get the index of a child. Should never be invoked since there are no childs. It
     * will always return -1.
     * 
     * @param parent
     *            Parent node
     * @param child
     *            Child node
     * @return Index, always -1.
     * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
     */
    public int getIndexOfChild(Object parent, Object child) {
        return -1;
    }

    /**
     * Value for path changed. Should never be invoked for an empty tree. It just does
     * nothing.
     * 
     * @param path
     *            Path to the changed node.
     * @param newValue
     *            New value of the node.
     * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath,
     *      java.lang.Object)
     */
    public void valueForPathChanged(TreePath path, Object newValue) {}

}
