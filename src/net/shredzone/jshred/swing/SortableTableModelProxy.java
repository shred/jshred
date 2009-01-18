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

import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;
import java.io.Serializable;

/**
 * The SortTableModelProxy wraps another {@link TableModel}, and allows to sort
 * it by one of its columns. Other {@link TableModel} calls are forwarded to it.
 * <p>
 * The sorting of the rows takes place in this proxy only. The master
 * {@link TableModel} is not changed in any way by the sorting procedure. You
 * should keep in mind though, that the rows will be re-sorted after every
 * single manipulation to the master {@link TableModel}.
 * <p>
 * The liaison between the SortTableModel and its master {@link TableModel} is
 * for a lifetime. If you want to change the master {@link TableModel}, you will
 * have to generate a new SortTableModel and feed it to the {@link JTable}. This
 * {@link TableModel} is best suited for the {@link JSortedTable}, but not
 * limited to it. You can also use SortTableModelProxy instances for classic
 * {@link JTable}, if you want to keep control of the column sorting.
 * <p>
 * Note that this is just a proxy, not a {@link TableModel} itself. You should
 * not extend this class in order to create a sorted {@link TableModel}, but
 * inherit from standard {@link TableModel}s, and then pass your
 * {@link TableModel} to the SortTableModelProxy constructor.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SortableTableModelProxy.java 239 2009-01-18 14:52:51Z shred $
 */
public class SortableTableModelProxy implements ExtendedSortableTableModel, TableModelListener, Serializable {
    static final long serialVersionUID = -668922708936078948L;

    private final TableModel master;
    private int currentColumn = 0;
    private boolean currentDesc = false;
    private Integer[] indexMap;
    private final ListenerManager<TableModelListener> listener
         = new ListenerManager<TableModelListener>();

    /**
     * Create a new SortableTableModelProxy for a master {@link TableModel}. The
     * object will give a modified view to the master {@link TableModel} that
     * allows to sort it by a certain column.
     * 
     * @param master
     *            Master {@link TableModel}
     */
    public SortableTableModelProxy(TableModel master) {
        if (master == null)
            throw new IllegalArgumentException("Cannot set a null TableModel");
        
        this.master = master;
        rebuildIndexMap();
        master.addTableModelListener(this);
    }

    /**
     * Get the master table that is connected to this SortableTableModelProxy.
     * 
     * @return The master {@link TableModel}
     */
    public TableModel getMasterTable() {
        return master;
    }

    /**
     * Get the number of rows. This call is forwarded to the master
     * {@link TableModel}.
     * 
     * @return Number of rows
     */
    public int getRowCount() {
        return master.getRowCount();
    }

    /**
     * Get the number of columns. This call is forwarded to the master
     * {@link TableModel}.
     * 
     * @return Number of columns
     */
    public int getColumnCount() {
        return master.getColumnCount();
    }

    /**
     * Get the column name of a certain column. This call is forwarded to the
     * master {@link TableModel}.
     * 
     * @param columnIndex
     *            Index
     * @return Column name
     */
    public String getColumnName(int columnIndex) {
        return master.getColumnName(columnIndex);
    }

    /**
     * Get the column name of a certain column. This call is forwarded to the
     * master {@link TableModel}.
     * 
     * @param columnIndex
     *            Index
     * @return Column name
     */
    public Class<?> getColumnClass(int columnIndex) {
        return master.getColumnClass(columnIndex);
    }

    /**
     * Check if a cell is editable. This call is forwarded to the master
     * {@link TableModel} after the row has been mapped according to the
     * sort order.
     * 
     * @param rowIndex
     *            Row
     * @param columnIndex
     *            Column
     * @return true: is editable
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return master.isCellEditable(mapRow(rowIndex), columnIndex);
    }

    /**
     * Get a cell value. This call is forwarded to the master {@link TableModel}
     * after the row has been mapped according to the sort order.
     * 
     * @param rowIndex
     *            Row
     * @param columnIndex
     *            Column
     * @return The value of this cell.
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        return master.getValueAt(mapRow(rowIndex), columnIndex);
    }

    /**
     * Set a cell value. This call is forwarded to the master {@link TableModel}
     * after the row has been mapped according to the sort order. If the
     * current sort column was modified, the table will be sorted again after
     * this call.
     * 
     * @param aValue
     *            New value
     * @param rowIndex
     *            Row
     * @param columnIndex
     *            Column
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        master.setValueAt(aValue, mapRow(rowIndex), columnIndex);
        if (columnIndex == currentColumn) {
            resort();
            fireTableDataChanged();
        }
    }

    /**
     * Map a row number to the appropriate row number in the master
     * {@link TableModel}.
     * 
     * @param row
     *            Row number of this model
     * @return Row number of the row in the master {@link TableModel}.
     * @since R3
     */
    public int mapRow(int row) {
        return indexMap[row].intValue();
    }

    /**
     * Map a row number of the master {@link TableModel} to the current row
     * number of the SortableTableModel.
     * 
     * @param row
     *            Row number of the master {@link TableModel}
     * @return Appropriate current row number of this model.
     * @since R3
     */
    public int unmapRow(int row) {
        for (int ix = 0; ix < indexMap.length; ix++) {
            if (indexMap[ix].intValue() == row)
                return ix;
        }
        throw new IndexOutOfBoundsException("row is not in table model");
    }

    /**
     * Sort by a certain column, ascending.
     * 
     * @param columnIndex
     *            Column to sort at.
     */
    public void sortByColumn(int columnIndex) {
        sortByColumn(columnIndex, false);
    }

    /**
     * Sort by a certain column, descending.
     * 
     * @param columnIndex
     *            Column to sort at.
     */
    public void sortByColumnDesc(int columnIndex) {
        sortByColumn(columnIndex, true);
    }

    /**
     * Sort by a certain column in the given order.
     * 
     * @param columnIndex
     *            Column to sort at.
     * @param desc
     *            true: descending, false: ascending
     */
    public void sortByColumn(int columnIndex, boolean desc) {
        currentColumn = columnIndex;
        currentDesc = desc;
        resort();
        fireTableDataChanged();
    }

    /**
     * Get the index of the column that is currently sorted.
     * 
     * @return Currently sorted column.
     */
    public int getSortedColumn() {
        return currentColumn;
    }

    /**
     * Get the current sort order.
     * 
     * @return true: descending, false: ascending
     */
    public boolean isDescending() {
        return currentDesc;
    }

    /**
     * Sort the entire TableModel again.
     */
    protected void resort() {
        ColumnComparator comp = new ColumnComparator(currentColumn, currentDesc);
        Arrays.sort(indexMap, comp);
    }

    /**
     * Rebuild the internal index map array. This is always required when the
     * master TableModel changed its number of rows.
     */
    protected void rebuildIndexMap() {
        int cnt = master.getRowCount();
        indexMap = new Integer[cnt];
        for (int ix = 0; ix < cnt; ix++) {
            indexMap[ix] = new Integer(ix);
        }
    }

    /**
     * Internal TableModelListener implementation, do not use!
     * 
     * @param e
     *            TableModelEvent
     */
    public void tableChanged(TableModelEvent e) {
        rebuildIndexMap();
        resort();
    }

    /**
     * Add a {@link TableModelListener}. It will be added to the master
     * {@link TableModel} as well as to this SortTableModelProxy. The listener
     * will be notified whenever the master {@link TableModel}, or the column to
     * be sorted, was changed.
     * 
     * @param l
     *            {@link TableModelListener}
     */
    public void addTableModelListener(TableModelListener l) {
        master.addTableModelListener(l);
        listener.addListener(l);
    }

    /**
     * Remove a {@link TableModelListener}. If it was not added, nothing will
     * happen. Make sure that you use the proxy
     * {@link #removeTableModelListener(TableModelListener)} when you have used
     * the proxy {@link #addTableModelListener(TableModelListener)}.
     * 
     * @param l
     *            {@link TableModelListener}
     */
    public void removeTableModelListener(TableModelListener l) {
        master.removeTableModelListener(l);
        listener.removeListener(l);
    }

    /**
     * Notify all {@link TableModelListener} that the sort order was changed.
     */
    protected void fireTableDataChanged() {
        TableModelEvent e = new TableModelEvent(this);
        for (TableModelListener l : listener.getListeners()) {
            l.tableChanged(e);
        }
    }

/* -------------------------------------------------------------------- */

    /**
     * This inner class compares integers according to the sequence of a column
     * in the master TableModel.
     */
    private class ColumnComparator implements Comparator<Integer> {
        private final int column;       // Column to sort at
        private final boolean desc;     // true: descending

        /**
         * Create a new ColumnComparator for a certain column.
         * 
         * @param column
         *            Column to be sorted
         * @param desc
         *            Order, true: descending, false: ascending
         */
        public ColumnComparator(int column, boolean desc) {
            this.column = column;
            this.desc = desc;
        }

        /**
         * Compare two Integers containing the row numbers to be sorted.
         * 
         * @param i1
         *            Integer 1
         * @param i2
         *            Integer 2
         * @return Sequence: positive, 0 or negative
         */
        @SuppressWarnings("unchecked")
        public int compare(Integer i1, Integer i2) {
            // --- Get the table cell objects ---
            Object d1 = master.getValueAt((desc ? i2 : i1).intValue(), column);
            Object d2 = master.getValueAt((desc ? i1 : i2).intValue(), column);

            // --- Handle all null pointer cases ---
            if (d1 == null && d2 == null)
                return 0;
            if (d1 == null)
                return 1;
            if (d2 == null)
                return -1;

            try {
                // --- Try to compare Comparables ---
                Comparable c1 = (Comparable) d1;
                return c1.compareTo(d2);
            } catch (ClassCastException e) {
                // --- Compare the toString representation ---
                return d1.toString().compareTo(d2.toString());
            }
        }
    }

    /**
     * Returns <code>true</code> if a column is sortable. This method always
     * returns <code>true</code>. It can be overridden to avoid sorting of
     * different columns.
     * 
     * @param
     *    columnIndex       Column index to check
     * @return
     *    Always <code>true</code>
     * @since R15
     */
    public boolean isColumnSortable(int columnIndex) {
      return true;
    }

}
