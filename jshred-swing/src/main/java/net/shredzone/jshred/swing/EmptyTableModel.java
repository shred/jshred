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

import javax.swing.event.TableModelListener;

/**
 * This is just a mere empty {@link TableModel} which will never have any
 * entries. It also implements the {@link SortableTableModel}, so it can be used
 * for {@link JSortedTable} as well.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @since R14
 * @version $Id: EmptyTableModel.java 243 2009-01-18 15:05:21Z shred $
 */
public final class EmptyTableModel implements SortableTableModel {

    /**
     * Add a listener. Since this list will always be empty, the listeners are
     * just ignored.
     * 
     * @param l
     *            {@link TableModelListener} to be ignored.
     * @see javax.swing.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    public void addTableModelListener(TableModelListener l) {
    }

    /**
     * Get the column class. This is always Object, just to return anything.
     * 
     * @return Column class
     */
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    /**
     * Get the column count. The table is empty, so there are 0 columns.
     * 
     * @return Column count
     */
    public int getColumnCount() {
        return 0;
    }

    /**
     * Get the column name. This is an empty string, but not null.
     * 
     * @return Column name
     */
    public String getColumnName(int columnIndex) {
        return "";
    }

    /**
     * Get the row count. This is always 0 since the table is empty.
     * 
     * @return Row count
     */
    public int getRowCount() {
        return 0;
    }

    /**
     * Get the value at a certain position. This is always null.
     * 
     * @param rowIndex
     *            Row
     * @param columnIndex
     *            Column
     * @return Value of a cell
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    /**
     * Check if a cell is editable. For this model, no cell is editable.
     * 
     * @param rowIndex
     *            Row
     * @param columnIndex
     *            Column
     * @return false: Cell is not editable
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Remove a listener. Since this list will always be empty, the listeners
     * are just ignored.
     * 
     * @param l
     *            {@link TableModelListener} to be ignored.
     * @see javax.swing.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    public void removeTableModelListener(TableModelListener l) {
    }

    /**
     * Set a value of a cell. Changing an empty table is always ignored.
     * 
     * @param aValue
     *            New value
     * @param rowIndex
     *            Row
     * @param columnIndex
     *            Column
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    /**
     * Get the colum that is sorted. Always returns 0.
     * 
     * @return Sorted column.
     */
    public int getSortedColumn() {
        return 0;
    }

    /**
     * Check if the sort order is descending. Always returns false.
     * 
     * @return Sort order.
     */
    public boolean isDescending() {
        return false;
    }

    /**
     * Sort by a column. This is ignored for empty tables.
     * 
     * @param columnIndex
     *            Column index
     * @param desc
     *            Descending?
     */
    public void sortByColumn(int columnIndex, boolean desc) {
    }

}
