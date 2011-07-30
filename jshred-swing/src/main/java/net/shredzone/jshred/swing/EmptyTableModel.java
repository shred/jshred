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

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * This is just a mere empty {@link TableModel} which will never have any entries. It also
 * implements the {@link SortableTableModel}, so it can be used for {@link JSortedTable}
 * as well.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @since R14
 * @version $Id: EmptyTableModel.java 584 2011-07-30 20:42:48Z shred $
 */
public final class EmptyTableModel implements SortableTableModel {

    /**
     * Add a listener. Since this list will always be empty, the listeners are just
     * ignored.
     * 
     * @param l
     *            {@link TableModelListener} to be ignored.
     * @see javax.swing.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
     */
    @Override
    public void addTableModelListener(TableModelListener l) {}

    /**
     * Get the column class. This is always Object, just to return anything.
     * 
     * @return Column class
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    /**
     * Get the column count. The table is empty, so there are 0 columns.
     * 
     * @return Column count
     */
    @Override
    public int getColumnCount() {
        return 0;
    }

    /**
     * Get the column name. This is an empty string, but not null.
     * 
     * @return Column name
     */
    @Override
    public String getColumnName(int columnIndex) {
        return "";
    }

    /**
     * Get the row count. This is always 0 since the table is empty.
     * 
     * @return Row count
     */
    @Override
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
    @Override
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
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Remove a listener. Since this list will always be empty, the listeners are just
     * ignored.
     * 
     * @param l
     *            {@link TableModelListener} to be ignored.
     * @see javax.swing.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {}

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
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

    /**
     * Get the colum that is sorted. Always returns 0.
     * 
     * @return Sorted column.
     */
    @Override
    public int getSortedColumn() {
        return 0;
    }

    /**
     * Check if the sort order is descending. Always returns false.
     * 
     * @return Sort order.
     */
    @Override
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
    @Override
    public void sortByColumn(int columnIndex, boolean desc) {}

}
