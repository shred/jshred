/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2004 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *
 *-----------------------------------------------------------------------
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is JSHRED.
 *
 * The Initial Developer of the Original Code is
 * Richard "Shred" Körber.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

package net.shredzone.jshred.swing;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;

/**
 * The SortTableModelProxy wraps another TableModel, and allows to sort
 * it by one of its columns. Other TableModel calls are forwarded to it.
 * <p>
 * The sorting of the rows takes place in this proxy only. The master
 * TableModel is not changed in any way by the sorting procedure. You
 * should keep in mind though, that the rows will be re-sorted after
 * every single manipulation to the master TableModel.
 * <p>
 * The liaison between the SortTableModel and its master TableModel
 * is for a lifetime. If you want to change the master TableModel,
 * you will have to generate a new SortTableModel and feed it to the
 * JTable. This TableModel is best suited for the JSortedTable, but not
 * limited to it. You can also use SortTableModelProxy instances for
 * classic JTable, if you want to keep control of the column sorting.
 * <p>
 * Note that this is just a proxy, not a TableModel itself. You should
 * not extend this class in order to create a sorted TableModel, but
 * inherit from standard TableModels, and then pass your TableModel to
 * the SortTableModelProxy constructor.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SortableTableModelProxy.java,v 1.1.1.1 2004/06/21 11:51:44 shred Exp $
 */
public class SortableTableModelProxy implements SortableTableModel, TableModelListener {
  private final TableModel master;
  private int currentColumn = 0;
  private boolean currentDesc = false;
  private Integer[] indexMap;
  private Set sListener = new HashSet();
  
  /**
   * Create a new SortableTableModelProxy for a master TableModel. The
   * object will give a modified view to the master TableModel that allows
   * to sort it by a certain column.
   *
   * @param   master      Master TableModel
   */
  public SortableTableModelProxy( TableModel master ) {
    if( master==null )
      throw new IllegalArgumentException( "Cannot set a null TableModel" );
    this.master = master;
    rebuildIndexMap();
    master.addTableModelListener( this );
  }
  
  /**
   * Get the master table that is connected to this SortableTableModelProxy.
   *
   * @return    The master TableModel
   */
  public TableModel getMasterTable() {
    return master;
  }
  
  /**
   * Get the number of rows. This call is forwarded to the master
   * TableModel.
   *
   * @return    Number of rows
   */
  public int getRowCount() {
    return master.getRowCount();
  }
  
  /**
   * Get the number of columns. This call is forwarded to the master
   * TableModel.
   *
   * @return    Number of columns
   */
  public int getColumnCount() {
    return master.getColumnCount();
  }
  
  /**
   * Get the column name of a certain column. This call is forwarded
   * to the master TableModel.
   *
   * @param     columnIndex     Index
   * @return    Column name
   */
  public String getColumnName( int columnIndex ) {
    return master.getColumnName( columnIndex );
  }
  
  /**
   * Get the column name of a certain column. This call is forwarded
   * to the master TableModel.
   *
   * @param     columnIndex     Index
   * @return    Column name
   */
  public Class getColumnClass( int columnIndex ) {
    return master.getColumnClass( columnIndex );
  }
  
  /**
   * Check if a cell is editable. This call is forwarded to the
   * master TableModel after the row has been mapped according to the
   * sort order.
   *
   * @param     rowIndex        Row
   * @param     columnIndex     Column
   * @return    true: is editable
   */
  public boolean isCellEditable( int rowIndex, int columnIndex ) {
    return master.isCellEditable( indexMap[rowIndex].intValue(), columnIndex );
  }
  
  /**
   * Get a cell value. This call is forwarded to the master TableModel
   * after the row has been mapped according to the sort order.
   *
   * @param     rowIndex        Row
   * @param     columnIndex     Column
   * @return    The value of this cell.
   */
  public Object getValueAt( int rowIndex, int columnIndex ) {
    return master.getValueAt( indexMap[rowIndex].intValue(), columnIndex );
  }
  
  /**
   * Set a cell value. This call is forwarded to the master TableModel
   * after the row has been mapped according to the sort order. If the
   * current sort column was modified, the table will be sorted again
   * after this call.
   *
   * @param     aValue          New value
   * @param     rowIndex        Row
   * @param     columnIndex     Column
   */
  public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
    master.setValueAt( aValue, indexMap[rowIndex].intValue(), columnIndex );
    if( columnIndex==currentColumn ) {
      resort();
      fireTableDataChanged();
    }
  }
  
  /**
   * Sort by a certain column, ascending.
   *
   * @param     columnIndex     Column to sort at.
   */
  public void sortByColumn( int columnIndex ) {
    sortByColumn( columnIndex, false );
  }
  
  /**
   * Sort by a certain column, descending.
   *
   * @param     columnIndex     Column to sort at.
   */
  public void sortByColumnDesc( int columnIndex ) {
    sortByColumn( columnIndex, true );
  }
  
  /**
   * Sort by a certain column in the given order.
   *
   * @param     columnIndex     Column to sort at.
   * @param     desc            true: descending, false: ascending
   */
  public void sortByColumn( int columnIndex, boolean desc ) {
    currentColumn = columnIndex;
    currentDesc   = desc;
    resort();
		fireTableDataChanged();
  }
  
  /**
   * Get the index of the column that is currently sorted.
   *
   * @return    Currently sorted column.
   */
  public int getSortedColumn() {
    return currentColumn;
  }
  
  /**
   * Get the current sort order.
   *
   * @return    true: descending, false: ascending
   */
  public boolean isDescending() {
    return currentDesc;
  }
  
  /**
   * Sort the entire TableModel again.
   */
  protected void resort() {
    ColumnComparator comp = new ColumnComparator( currentColumn, currentDesc );
    Arrays.sort( indexMap, comp );
  }
  
  /**
   * Rebuild the internal index map array. This is always required when
   * the master TableModel changed its number of rows.
   */
  protected void rebuildIndexMap() {
    int cnt = master.getRowCount();
    indexMap = new Integer[cnt];
    for( int ix=0; ix<cnt; ix++ ) {
      indexMap[ix] = new Integer(ix);
    }
  }
  
  /**
   * Internal TableModelListener implementation, do not use!
   *
   * @param   e       TableModelEvent
   */
  public void tableChanged( TableModelEvent e ) {
    rebuildIndexMap();
    resort();
  }
  
  /**
   * Add a TableModelListener. It will be added to the master TableModel
   * as well as to this SortTableModelProxy. The listener will be notified
   * whenever the master TableModel, or the column to be sorted, was
   * changed.
   *
   * @param   l       TableModelListener
   */
  public void addTableModelListener( TableModelListener l ) {
    master.addTableModelListener( l );
    sListener.add( l );
  }
  
  /**
   * Remove a TableModelListener. If it was not added, nothing will
   * happen. Make sure that you use the proxy <code>removeTableModelListener()</code>
   * when you have used the proxy <code>addTableModelListener()</code>.
   *
   * @param   l       TableModelListener
   */
  public void removeTableModelListener( TableModelListener l ) {
    master.removeTableModelListener( l );
    sListener.remove( l );
  }
  
  /**
   * Notify all TableModelListeners that the sort order was changed.
   */
  protected void fireTableDataChanged() {
    final TableModelEvent e = new TableModelEvent( this );
    Iterator it = sListener.iterator();
    while( it.hasNext() ) {
      TableModelListener l = (TableModelListener) it.next();
      l.tableChanged( e );
    }
  }

/*--------------------------------------------------------------------*/

  /**
   * This inner class compares integers according to the sequence of
   * a column in the master TableModel.
   */
	private class ColumnComparator implements Comparator {
    private final int column;       // Column to sort at
    private final boolean desc;     // true: descending
    
    /**
     * Create a new ColumnComparator for a certain column.
     *
     * @param   column        Column to be sorted
     * @param   desc          Order, true: descending, false: ascending
     */
    public ColumnComparator( int column, boolean desc ) {
      this.column = column;
      this.desc   = desc;
    }
    
    /**
     * Compare two objects. This are Integers containing the row numbers
     * to be sorted.
     *
     * @param   o1          Object 1
     * @param   o2          Object 2
     * @return  Sequence: positive, 0 or negative
     */
		public int compare( Object o1, Object o2 ) {
      //--- Get the row numbers ---
			final Integer i1 = (Integer) o1;
			final Integer i2 = (Integer) o2;
      
      //--- Get the table cell objects ---
      final Object d1 = master.getValueAt( (desc ? i2 : i1).intValue(), column );
      final Object d2 = master.getValueAt( (desc ? i1 : i2).intValue(), column );

      //--- Handle all null pointer cases ---
      if( d1==null && d2==null )  return 0;
      if( d1==null )              return 1;
      if( d2==null )              return -1;
      
      try {
        //--- Try to compare Comparables ---
        Comparable c1 = (Comparable) d1;
        return c1.compareTo( d2 );
      }catch( ClassCastException e ) {
        //--- Compare the toString representation ---
        return d1.toString().compareTo( d2.toString() );
      }
		}
	}

}

/* jedit :mode=java:tabSize=2:noTabs=true:folding=java:maxLineLen=72: */
