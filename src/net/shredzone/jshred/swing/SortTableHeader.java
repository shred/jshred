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
import javax.swing.event.*;

/**
 * This is a JTableHeader that shows the currently sorted column of a
 * SortableTableModel and allows to select columns to be sorted.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SortTableHeader.java,v 1.2 2004/06/22 21:57:45 shred Exp $
 */
public class SortTableHeader extends JTableHeader implements MouseListener, MouseMotionListener {
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
    iconAsc = new ArrowIcon( false );
    iconDesc = new ArrowIcon( true );

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
          if( column==sortmodel.getSortedColumn() ) {
            sortmodel.sortByColumn( column, !sortmodel.isDescending() );
          }else {
            sortmodel.sortByColumn( column, false );
          }
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
  private class SortTableCellRenderer implements TableCellRenderer {
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

/*--------------------------------------------------------------------*/

  /**
   * Draws an arrow icon, either up or down.
   */
  private static class ArrowIcon implements Icon {
    private boolean up;

    /**
     * Create a new ArrowIcon.
     *
     * @param   up      true: Array is up, false: Array is down
     */
    public ArrowIcon( boolean up ) {
      this.up = up;
    }

    /**
     * Paint this icon
     *
     * @param   c       Component (for reference)
     * @param   g       Graphics context
     * @param   x       X position
     * @param   y       Y position
     */
    public void paintIcon( Component c, Graphics g, int x, int y ) {
      //--- Create an arrow polygon ---
      int pX[] = new int[3];
      int pY[] = new int[3];
      if( up ) {
        pX[0] = x  ; pY[0] = y+5;
        pX[1] = x+3; pY[1] = y+2;
        pX[2] = x+6; pY[2] = y+5;
      }else {
        pX[0] = x  ; pY[0] = y+2;
        pX[1] = x+3; pY[1] = y+5;
        pX[2] = x+6; pY[2] = y+2;
      }

      //--- Draw it ---
      g.setColor( c.getForeground() );
      g.drawPolygon( pX, pY, pX.length );
      g.fillPolygon( pX, pY, pX.length );
    }

    /**
     * Width is fixed to 7 pixel.
     *
     * @return    Icon width
     */
    public int getIconWidth() {
      return 7;
    }

    /**
     * Width is fixed to 6 pixel.
     *
     * @return    Icon height
     */
    public int getIconHeight() {
      return 6;
    }
  }

}

/* jedit :mode=java:tabSize=2:noTabs=true:folding=java:maxLineLen=72: */
