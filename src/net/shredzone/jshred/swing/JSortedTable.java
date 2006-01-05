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
import javax.swing.*;
import javax.swing.table.*;

/**
 * This is a JTable which allows the user to sort each column in
 * ascending or descending order. Everything you have to do to is just
 * to use the <code>JSortedTable</code> instead of a <code>JTable</code>,
 * and pass a <code>SortableTableModel</code> to it.
 * <p>
 * If you want to use a classic TableModel, you can wrap it using the
 * <code>SortableTableModelProxy</code> object, and then pass the
 * <code>SortableTableModelProxy</code> to this class.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JSortedTable.java,v 1.5 2004/07/20 14:00:47 shred Exp $
 */
public class JSortedTable extends JTable {

  /**
   * Create a new, empty JSortedTable.
   */
  public JSortedTable() {
    super();
  }

  /**
   * Create a new JSortedTable with the given SortableTableModel.
   *
   * @param   model       SortableTableModel to be used.
   */
  public JSortedTable( SortableTableModel model ) {
    super( model );
  }

  /**
   * Set the TableModel to be used. You must pass a SortableTableModel
   * here, otherwise you'll get an InvalidArgumentException.
   *
   * @param     model     TableModel
   */
  public void setModel( TableModel model ) {
    if( model instanceof SortableTableModel ) {
      super.setModel( model );
    }else {
      throw new IllegalArgumentException( "You must provide a SortableTableModel" );
    }
  }
  
  /**
   * Sort by a certain column. If this is the currently sorted column,
   * the sort order will be toggled. Otherwise the given column will
   * be sorted ascendingly. This method simulates a mouse click on the
   * appropriate column header.
   *
   * @param   columnIndex     Column to be sorted.
   * @since   R4
   */
  public void sortByColumn( int columnIndex ) {
    SortableTableModel model = (SortableTableModel) getModel();
    if( model.getSortedColumn() != columnIndex ) {
      model.sortByColumn( columnIndex, false );
    }else {
      model.sortByColumn( columnIndex, !model.isDescending() );
    }
  }
  
  /**
   * Create the default JTableHeader instance.
   *
   * @return    Default JTableHeader
   */
  protected JTableHeader createDefaultTableHeader() {
    return new SortTableHeader( columnModel );
  }

  /**
   * Get the default TableModel. It is always a SortableTableModel!
   *
   * @return    SortableTableModel
   */
  protected TableModel createDefaultDataModel() {
    return new SortableTableModelProxy( super.createDefaultDataModel() );
  }

}

/* jedit :mode=java:tabSize=2:noTabs=true:folding=java:maxLineLen=72: */
