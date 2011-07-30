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

import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/**
 * This is a {@link JTable} which allows the user to sort each column in ascending or
 * descending order. Everything you have to do to is just to use the {@link JSortedTable}
 * instead of a {@link JTable}, and pass a {@link SortableTableModel} to it.
 * <p>
 * If you want to use a classic {@link TableModel}, you can wrap it using the
 * {@link SortableTableModelProxy} object, and then pass the
 * {@link SortableTableModelProxy} to this class.
 * <p>
 * Starting with Java 1.6, Swing brings an own implementation for sortable table, which
 * may be preferable to this solution.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JSortedTable.java 584 2011-07-30 20:42:48Z shred $
 */
public class JSortedTable extends JTable {
    private static final long serialVersionUID = 3256728372624110384L;

    /**
     * Create a new, empty JSortedTable.
     */
    public JSortedTable() {
        super();
    }

    /**
     * Create a new JSortedTable with the given {@link SortableTableModel}.
     * 
     * @param model
     *            {@link SortableTableModel} to be used.
     */
    public JSortedTable(SortableTableModel model) {
        super(model);
    }

    /**
     * Set the {@link TableModel} to be used. You must pass a {@link SortableTableModel}
     * here, otherwise you'll get an {@link IllegalArgumentException}.
     * 
     * @param model
     *            A {@link SortableTableModel}
     */
    @Override
    public void setModel(TableModel model) {
        if (!(model instanceof SortableTableModel)) {
            throw new IllegalArgumentException("You must provide a SortableTableModel");
        }

        // --- Remember current column ---
        int column = 0;
        boolean desc = false;
        SortableTableModel current = (SortableTableModel) getModel();
        if (current != null) {
            column = current.getSortedColumn();
            desc = current.isDescending();
        }

        // --- Set new model ---
        super.setModel(model);

        // --- Sort by this column ---
        SortableTableModel newmodel = (SortableTableModel) model;
        if (column >= newmodel.getColumnCount()) {
            // Column does not exist any more. Use first column.
            column = 0;
            desc = false;
        }
        newmodel.sortByColumn(column, desc);
    }

    /**
     * Sort by a certain column. If this is the currently sorted column, the sort order
     * will be reversed. Otherwise the given column will be sorted ascendingly. This
     * method simulates a mouse click on the appropriate column header.
     * <p>
     * Since R11, the selection will be kept if the {@link SortableTableModelProxy} is
     * used.
     * 
     * @param columnIndex
     *            Column to be sorted.
     * @since R4
     */
    public void sortByColumn(int columnIndex) {
        // --- Sorting allowed? ---
        if (getModel() instanceof ExtendedSortableTableModel) {
            ExtendedSortableTableModel ext = (ExtendedSortableTableModel) getModel();
            if (!ext.isColumnSortable(columnIndex)) return;
        }

        // --- Proxy? ---
        final SortableTableModel model = (SortableTableModel) getModel();
        SortableTableModelProxy proxy = null;
        if (model instanceof SortableTableModelProxy) {
            proxy = (SortableTableModelProxy) model;
        }

        // --- Remember all selected indices ---
        int[] rows = null;
        if (proxy != null) {
            rows = getSelectedRows();
            for (int ix = 0; ix < rows.length; ix++) {
                rows[ix] = proxy.mapRow(rows[ix]);
            }
        }

        // --- Change sort order ---
        if (model.getSortedColumn() != columnIndex) {
            model.sortByColumn(columnIndex, false);
        } else {
            model.sortByColumn(columnIndex, !model.isDescending());
        }

        // --- Restore the selection ---
        clearSelection();
        if (proxy != null && rows != null) {
            for (int ix = 0; ix < rows.length; ix++) {
                int row = proxy.unmapRow(rows[ix]);
                addRowSelectionInterval(row, row);
                if (ix == 0) {
                    Rectangle cellRect = getCellRect(row, 0, false);
                    if (cellRect != null) {
                        scrollRectToVisible(cellRect);
                    }
                }
            }
        }
    }

    /**
     * Create the default {@link JTableHeader} instance.
     * 
     * @return Default {@link JTableHeader}
     */
    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new SortTableHeader(columnModel);
    }

    /**
     * Get the default {@link TableModel}. It is always a {@link SortableTableModel}!
     * 
     * @return {@link SortableTableModel}
     */
    @Override
    protected TableModel createDefaultDataModel() {
        return new SortableTableModelProxy(super.createDefaultDataModel());
    }

}
