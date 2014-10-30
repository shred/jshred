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
 * @author Richard "Shred" Körber
 * @since R14
 */
public final class EmptyTableModel implements SortableTableModel {

    /**
     * Adds a listener. Since this list will always be empty, the listeners are just
     * ignored.
     */
    @Override
    public void addTableModelListener(TableModelListener l) {}

    /**
     * Gets the column class. This is always Object, just to return anything.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return Object.class;
    }

    /**
     * Gets the column count. The table is empty, so there are 0 columns.
     */
    @Override
    public int getColumnCount() {
        return 0;
    }

    /**
     * Gets the column name. This is an empty string, but not {@code null}.
     */
    @Override
    public String getColumnName(int columnIndex) {
        return "";
    }

    /**
     * Gets the row count. This is always 0 since the table is empty.
     */
    @Override
    public int getRowCount() {
        return 0;
    }

    /**
     * Gets the value at a certain position. This is always {@code null}.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    /**
     * Checks if a cell is editable. For this model, no cell is editable.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Remove sa listener. Since this list will always be empty, the listeners are just
     * ignored.
     */
    @Override
    public void removeTableModelListener(TableModelListener l) {}

    /**
     * Sets a value of a cell. Changing an empty table is always ignored.
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}

    /**
     * Gets the colum that is sorted. Always returns 0.
     */
    @Override
    public int getSortedColumn() {
        return 0;
    }

    /**
     * Checks if the sort order is descending. Always returns {@code false}.
     */
    @Override
    public boolean isDescending() {
        return false;
    }

    /**
     * Sorts by a column. This is ignored for empty tables.
     *
     * @param columnIndex
     *            Column index
     * @param desc
     *            Descending?
     */
    @Override
    public void sortByColumn(int columnIndex, boolean desc) {
        // does nothing
    }

}
