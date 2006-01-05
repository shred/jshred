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

import javax.swing.table.*;

/**
 * The SortableTableModel extends a TableModel by methods that are
 * required to order the model by a certain column.
 * <p>
 * A simple way to use the SortableTableModel is to use a classic
 * TableModel and pass it to a SortableTableModelProxy, which will
 * take care for the sorting.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SortableTableModel.java,v 1.3 2004/08/23 23:49:15 shred Exp $
 */
public interface SortableTableModel extends TableModel {

  /**
   * Sort by a certain column in the given order.
   *
   * @param     columnIndex     Column to sort at.
   * @param     desc            true: descending, false: ascending
   */
  public void sortByColumn( int columnIndex, boolean desc );

  /**
   * Get the index of the column that is currently sorted.
   *
   * @return    Currently sorted column.
   */
  public int getSortedColumn();

  /**
   * Get the current sort order.
   *
   * @return    true: descending, false: ascending
   */
  public boolean isDescending();

}
