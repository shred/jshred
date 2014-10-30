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

/**
 * This is an extension of the {@link SortableTableModel}. It provides a method to
 * selectively exclude columns from sorting.
 *
 * @author Richard "Shred" Körber
 * @since R15
 */
public interface ExtendedSortableTableModel extends SortableTableModel {

    /**
     * Returns {@code true} if the given column index is sortable.
     *
     * @param columnIndex
     *            Column to be sorted
     * @return {@code true} if this column can be sorted, false otherwise.
     */
    public boolean isColumnSortable(int columnIndex);

}
