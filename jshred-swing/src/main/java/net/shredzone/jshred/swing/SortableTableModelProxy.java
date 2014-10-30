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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * The SortTableModelProxy wraps another {@link TableModel}, and allows to sort it by one
 * of its columns. Other {@link TableModel} calls are forwarded to it.
 * <p>
 * The sorting of the rows takes place in this proxy only. The master {@link TableModel}
 * is not changed in any way by the sorting procedure. You should keep in mind though,
 * that the rows will be re-sorted after every single manipulation to the master
 * {@link TableModel}.
 * <p>
 * The liaison between the {@link SortableTableModel} and its master {@link TableModel} is
 * for a lifetime. If you want to change the master {@link TableModel}, you will have to
 * generate a new SortTableModel and feed it to the {@link JTable}. This
 * {@link TableModel} is best suited for the {@link JSortedTable}, but not limited to it.
 * You can also use {@link SortableTableModelProxy} instances for classic {@link JTable},
 * if you want to keep control of the column sorting.
 * <p>
 * Note that this is just a proxy, not a {@link TableModel} itself. You should not extend
 * this class in order to create a sorted {@link TableModel}, but inherit from standard
 * {@link TableModel}s, and then pass your {@link TableModel} to the SortTableModelProxy
 * constructor.
 *
 * @author Richard "Shred" Körber
 */
public class SortableTableModelProxy implements ExtendedSortableTableModel, TableModelListener, Serializable {
    static final long serialVersionUID = -668922708936078948L;

    private final TableModel master;
    private int currentColumn = 0;
    private boolean currentDesc = false;
    private Integer[] indexMap;
    private final ListenerManager<TableModelListener> listener = new ListenerManager<>();

    /**
     * Creates a new SortableTableModelProxy for a master {@link TableModel}. The object
     * will give a modified view to the master {@link TableModel} that allows to sort it
     * by a certain column.
     *
     * @param master
     *            Master {@link TableModel}
     */
    public SortableTableModelProxy(TableModel master) {
        if (master == null) throw new IllegalArgumentException("Cannot set a null TableModel");

        this.master = master;
        rebuildIndexMap();
        master.addTableModelListener(this);
    }

    /**
     * Gets the master table that is connected to this SortableTableModelProxy.
     *
     * @return The master {@link TableModel}
     */
    public TableModel getMasterTable() {
        return master;
    }

    @Override
    public int getRowCount() {
        return master.getRowCount();
    }

    @Override
    public int getColumnCount() {
        return master.getColumnCount();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return master.getColumnName(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return master.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return master.isCellEditable(mapRow(rowIndex), columnIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return master.getValueAt(mapRow(rowIndex), columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        master.setValueAt(aValue, mapRow(rowIndex), columnIndex);
        if (columnIndex == currentColumn) {
            resort();
            fireTableDataChanged();
        }
    }

    /**
     * Maps a row number to the appropriate row number in the master {@link TableModel}.
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
     * Maps a row number of the master {@link TableModel} to the current row number of the
     * SortableTableModel.
     *
     * @param row
     *            Row number of the master {@link TableModel}
     * @return Appropriate current row number of this model.
     * @since R3
     */
    public int unmapRow(int row) {
        for (int ix = 0; ix < indexMap.length; ix++) {
            if (indexMap[ix].intValue() == row) return ix;
        }
        throw new IndexOutOfBoundsException("row is not in table model");
    }

    /**
     * Sorts by a certain column, ascending.
     *
     * @param columnIndex
     *            Column to sort at.
     */
    public void sortByColumn(int columnIndex) {
        sortByColumn(columnIndex, false);
    }

    /**
     * Sorts by a certain column, descending.
     *
     * @param columnIndex
     *            Column to sort at.
     */
    public void sortByColumnDesc(int columnIndex) {
        sortByColumn(columnIndex, true);
    }

    /**
     * Sorts by a certain column in the given order.
     *
     * @param columnIndex
     *            Column to sort at.
     * @param desc
     *            true: descending, false: ascending
     */
    @Override
    public void sortByColumn(int columnIndex, boolean desc) {
        currentColumn = columnIndex;
        currentDesc = desc;
        resort();
        fireTableDataChanged();
    }

    /**
     * Gets the index of the column that is currently sorted.
     *
     * @return Currently sorted column.
     */
    @Override
    public int getSortedColumn() {
        return currentColumn;
    }

    /**
     * Gets the current sort order.
     *
     * @return true: descending, false: ascending
     */
    @Override
    public boolean isDescending() {
        return currentDesc;
    }

    /**
     * Sorts the entire TableModel again.
     */
    protected void resort() {
        ColumnComparator comp = new ColumnComparator(currentColumn, currentDesc);
        Arrays.sort(indexMap, comp);
    }

    /**
     * Rebuilds the internal index map array. This is always required when the master
     * TableModel changed its number of rows.
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
     */
    @Override
    public void tableChanged(TableModelEvent e) {
        rebuildIndexMap();
        resort();
    }

    /**
     * Adds a {@link TableModelListener}. It will be added to the master
     * {@link TableModel} as well as to this SortTableModelProxy. The listener will be
     * notified whenever the master {@link TableModel}, or the column to be sorted, was
     * changed.
     *
     * @param l
     *            {@link TableModelListener}
     */
    @Override
    public void addTableModelListener(TableModelListener l) {
        master.addTableModelListener(l);
        listener.addListener(l);
    }

    /**
     * Removes a {@link TableModelListener}. If it was not added, nothing will happen.
     * Make sure that you use the proxy
     * {@link #removeTableModelListener(TableModelListener)} when you have used the proxy
     * {@link #addTableModelListener(TableModelListener)}.
     *
     * @param l
     *            {@link TableModelListener}
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {
        master.removeTableModelListener(l);
        listener.removeListener(l);
    }

    /**
     * Notifies all {@link TableModelListener} that the sort order was changed.
     */
    protected void fireTableDataChanged() {
        TableModelEvent e = new TableModelEvent(this);
        for (TableModelListener l : listener.getListeners()) {
            l.tableChanged(e);
        }
    }

    /**
     * This inner class compares integers according to the sequence of a column in the
     * master TableModel.
     */
    private class ColumnComparator implements Comparator<Integer> {
        private final int column; // Column to sort at
        private final boolean desc; // true: descending

        /**
         * Creates a new ColumnComparator for a certain column.
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
         * Compares two Integers containing the row numbers to be sorted.
         *
         * @param i1
         *            Integer 1
         * @param i2
         *            Integer 2
         * @return Sequence: positive, 0 or negative
         */
        @Override
        @SuppressWarnings("unchecked")
        public int compare(Integer i1, Integer i2) {
            // --- Get the table cell objects ---
            Object d1 = master.getValueAt((desc ? i2 : i1).intValue(), column);
            Object d2 = master.getValueAt((desc ? i1 : i2).intValue(), column);

            // --- Handle all null pointer cases ---
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;

            try {
                // --- Try to compare Comparables ---
                Comparable<Object> c1 = (Comparable<Object>) d1;
                return c1.compareTo(d2);
            } catch (ClassCastException e) {
                // --- Compare the toString representation ---
                return d1.toString().compareTo(d2.toString());
            }
        }
    }

    /**
     * Returns {@code true} if a column is sortable. This method always returns
     * {@code true}. It can be overridden to avoid sorting of different columns.
     *
     * @param columnIndex
     *            Column index to check
     * @return Always {@code true}
     * @since R15
     */
    @Override
    public boolean isColumnSortable(int columnIndex) {
        return true;
    }

}
