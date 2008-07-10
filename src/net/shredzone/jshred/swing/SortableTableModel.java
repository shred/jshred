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

/**
 * The SortableTableModel extends a {@link TableModel} by methods that are
 * required to order the model by a certain column.
 * <p>
 * A simple way to use the SortableTableModel is to use a classic
 * {@link TableModel} and pass it to a {@link SortableTableModelProxy}, which
 * will take care for the sorting.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SortableTableModel.java 169 2008-07-10 22:01:03Z shred $
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
