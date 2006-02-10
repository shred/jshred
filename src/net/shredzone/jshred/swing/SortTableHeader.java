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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.Serializable;

/**
 * This is a JTableHeader that shows the currently sorted column of a
 * SortableTableModel and allows to select columns to be sorted.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SortTableHeader.java 75 2006-02-10 08:17:27Z shred $
 */
public class SortTableHeader extends JTableHeader implements MouseListener, MouseMotionListener {
  private static final long serialVersionUID = 3256728372658124082L;
  private boolean pressed;        // Mouse is pressed
  private boolean dragged;        // Mouse is being dragged
  private boolean sortingAllowed = true;  // Sorting allowed?
  private int     pressedIndex;   // Index of the currently clicked column
  private Icon    iconAsc;        // Icon for ascending sort order
  private Icon    iconDesc;       // Icon for descending sort order

  /**
   * Create a new SortTableHeader.
   */
  public SortTableHeader() {
    super(null);
  }

  /**
   * Create a new SortTableHeader with the given TableColumnModel
   *
   * @param   cm      TableColumnModel to be used
   */
  public SortTableHeader( TableColumnModel cm ) {
    super( cm );

    //--- Initialize the default iconset ---
    iconAsc = new ArrowIcon( 7,6, SwingConstants.SOUTH );
    iconDesc = new ArrowIcon( 7,6, SwingConstants.NORTH );

    //--- Set the default renderer ---
    setDefaultRenderer( new SortTableCellRenderer( createDefaultRenderer() ) );

    //--- Listen to the mouse ---
    addMouseListener( this );
    addMouseMotionListener( this );
  }

  /**
   * Set if the user is allowed to sort columns by clicking on their
   * headline.
   *
   * @param   b       Sorting allowed? true=Yes (default), false=No
   */
  public void setSortingAllowed( boolean b ) {
    sortingAllowed = b;
  }

  /**
   * Find out if sorting is allowed.
   *
   * @return  true: sorting is allowed, false: not allowed
   */
  public boolean isSortingAllowed() {
    return sortingAllowed;
  }

  /**
   * Set the icon for ascending order.
   *
   * @param   icon      Ascending icon, or null for none
   */
  public void setIconAsc( Icon icon ) {
    iconAsc = icon;
  }

  /**
   * Get the current icon for ascending order.
   *
   * @return  Ascending icon, may be null
   */
  public Icon getIconAsc() {
    return iconAsc;
  }

  /**
   * Set the icon for descending order.
   *
   * @param   icon      Descending icon, or null for none
   */
  public void setIconDesc( Icon icon ) {
    iconDesc = icon;
  }

  /**
   * Get the current icon for descending order.
   *
   * @return  Descending icon, may be null
   */
  public Icon getIconDesc() {
    return iconDesc;
  }

  /**
   * Internal MouseListener implementation, do not use!
   *
   * @param   e         MouseEvent
   */
  public void mousePressed( MouseEvent e ) {
    pressed = true;
    pressedIndex = columnAtPoint( e.getPoint() );
    repaint();
  }

  /**
   * Internal MouseListener implementation, do not use!
   *
   * @param   e         MouseEvent
   */
  public void mouseReleased( MouseEvent e ) {
    if( !dragged ) {
      //--- Find the column ---
      TableModel model = table.getModel();
      if( model instanceof SortableTableModel ) {
        SortableTableModel sortmodel = (SortableTableModel) model;
        int column = table.convertColumnIndexToModel( pressedIndex );

        //--- Change sort order ---
        if( sortingAllowed && column>=0 && column<columnModel.getColumnCount() ) {
          ((JSortedTable) table).sortByColumn( column );
        }
      }
    }
    pressed = false;
    dragged = false;
    repaint();
  }

  /**
   * Internal MouseMotionListener implementation, do not use!
   *
   * @param   e         MouseEvent
   */
  public void mouseDragged( MouseEvent e ) {
    dragged = true;
    pressed = false;
    repaint();
  }

  /**
   * Internal MouseMotionListener implementation, do not use!
   *
   * @param   e         MouseEvent
   */
  public void mouseMoved( MouseEvent e ) {
    dragged = false;
  }

  /**
   * Internal MouseListener implementation, do not use!
   *
   * @param   e         MouseEvent
   */
  public void mouseClicked( MouseEvent e ) {}

  /**
   * Internal MouseListener implementation, do not use!
   *
   * @param   e         MouseEvent
   */
  public void mouseEntered( MouseEvent e ) {}

  /**
   * Internal MouseListener implementation, do not use!
   *
   * @param   e         MouseEvent
   */
  public void mouseExited( MouseEvent e ) {}

/*--------------------------------------------------------------------*/

  /**
   * This is a TableCellRenderer that shows the selection and sorting
   * state of each column, according to the SortTableHeader.
   */
  private class SortTableCellRenderer implements TableCellRenderer, Serializable {
    private static final long serialVersionUID = 3256437023551468342L;
    private TableCellRenderer parent;

    /**
     * Create a new SortTableCellRenderer. A parent TableCellRenderer
     * is given, its result will be manipulated. Usually you will pass
     * the JTableHeader's default TableCellRenderer here.
     *
     * @param   parent      Parent TableCellRenderer
     */
    public SortTableCellRenderer( TableCellRenderer parent ) {
      this.parent = parent;
    }

    /**
     * Get a Component that renders the table cell.
     *
     * @param   table       Referred JTable
     * @param   value       Value to render
     * @param   isSelected  Selection state, usually ignored
     * @param   hasFocus    Focus state, usually ignored
     * @param   row         Row, usually ignored
     * @param   column      Column
     * @return  A component that renders the table cell
     */
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
      //--- Get the original Component ---
      Component c = parent.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

      //--- It should be a JLabel ---
      if( c instanceof JLabel ) {
        JLabel cl = (JLabel) c;

        //--- Show the depressed state ---
        if( pressed && pressedIndex==column ) {
          cl.setBackground( cl.getBackground().darker() );
        }

        //--- Show the sorting state ---
        TableModel model = table.getModel();
        if( model instanceof SortableTableModel ) {
          SortableTableModel sortmodel = (SortableTableModel) model;
          int modelcolumn = table.convertColumnIndexToModel( column );
          if( sortmodel.getSortedColumn()==modelcolumn ) {
            cl.setHorizontalTextPosition( JLabel.LEADING );
            if( sortmodel.isDescending() ) {
              cl.setIcon( iconDesc );
            }else {
              cl.setIcon( iconAsc );
            }
          }else {
            cl.setIcon( null );
          }
        }
      }

      //--- Return the manipulated Component ---
      return c;
    }
  }

}
