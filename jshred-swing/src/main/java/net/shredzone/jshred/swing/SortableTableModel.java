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

import javax.swing.table.TableModel;

/**
 * The SortableTableModel extends a {@link TableModel} by methods that are required to
 * order the model by a certain column.
 * <p>
 * A simple way to use the SortableTableModel is to use a classic {@link TableModel} and
 * pass it to a {@link SortableTableModelProxy}, which will take care for the sorting.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SortableTableModel.java 389 2009-11-11 23:47:30Z shred $
 */
public interface SortableTableModel extends TableModel {

    /**
     * Sort by a certain column in the given order.
     * 
     * @param columnIndex
     *            Column to sort at.
     * @param desc
     *            true: descending, false: ascending
     */
    public void sortByColumn(int columnIndex, boolean desc);

    /**
     * Get the index of the column that is currently sorted.
     * 
     * @return Currently sorted column.
     */
    public int getSortedColumn();

    /**
     * Get the current sort order.
     * 
     * @return true: descending, false: ascending
     */
    public boolean isDescending();

}
