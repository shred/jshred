/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://jshred.shredzone.org-------------------------------------------------------------------
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

/**
 * This is an extension of the {@link SortableTableModel}. It provides a
 * method to selectively exclude columns from sorting.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @since R15
 * @version $Id: SortableTableModel.java 169 2008-07-10 22:01:03Z shred $
 */
public interface ExtendedSortableTableModel extends SortableTableModel {

    /**
     * Returns <code>true</code> if the given column index is sortable.
     * 
     * @param columnIndex
     *    Column to be sorted
     * @return  <code>true</code> if this column can be sorted, false otherwise.
     */
    public boolean isColumnSortable(int columnIndex);
  
}
