/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2004 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *
 *-----------------------------------------------------------------------
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is JSHRED.
 *
 * The Initial Developer of the Original Code is
 * Richard "Shred" Körber.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

package net.shredzone.jshred.swing;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * This is just a mere empty TreeModel which will never have any entries.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @since   R7
 * @version $Id: EmptyTreeModel.java,v 1.1 2004/09/21 07:20:37 shred Exp $
 */
public final class EmptyTreeModel implements TreeModel {

  /**
   * Get the root node. It will return null since the tree has no nodes.
   * 
   * @return  Root node, which is null.
   * @see javax.swing.tree.TreeModel#getRoot()
   */
  public Object getRoot() {
    return null;
  }

  /**
   * Get the child count, which is always 0.
   * 
   * @param   parent      Parent object
   * @return  Child count.
   * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
   */
  public int getChildCount( Object parent ) {
    return 0;
  }

  /**
   * Check if the node is a leaf. Should never be invoked since there are
   * no nodes. Will always return true.
   * 
   * @param   node        Node to check
   * @return  Always true.
   * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
   */
  public boolean isLeaf( Object node ) {
    return true;
  }

  /**
   * Add a TreeModelListener. Since the tree will never change, the listener
   * is just ignored.
   * 
   * @param   l       TreeModelListener to be ignored.
   * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
   */
  public void addTreeModelListener( TreeModelListener l ) {
  }

  /**
   * Remove a TreeModelListener. Since the tree will never change, the listener
   * is just ignored.
   * 
   * @param   l       TreeModelListener to be ignored.
   * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
   */
  public void removeTreeModelListener( TreeModelListener l ) {
  }

  /**
   * Get a child. Should never be invoked since there are no childs. It
   * will always return null.
   * 
   * @param   parent      Parent node
   * @param   index       Child index
   * @return  Always null.
   * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
   */
  public Object getChild( Object parent, int index ) {
    return null;
  }

  /**
   * Get the index of a child. Should never be invoked since there are no
   * childs. It will always return -1.
   * 
   * @param   parent      Parent node
   * @param   child       Child node
   * @return  Index, always -1.
   * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
   */
  public int getIndexOfChild( Object parent, Object child ) {
    return -1;
  }

  /**
   * Value for path changed. Should never be invoked for an empty tree. It
   * just does nothing.
   * 
   * @param   path        Path to the changed node.
   * @param   newValue    New value of the node.
   * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
   */
  public void valueForPathChanged( TreePath path, Object newValue ) {
  }

}
