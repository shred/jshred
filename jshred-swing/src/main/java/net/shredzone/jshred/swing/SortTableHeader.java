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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * This is a {@link JTableHeader} that shows the currently sorted column of a
 * {@link SortableTableModel} and allows to select columns to be sorted.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SortTableHeader.java 389 2009-11-11 23:47:30Z shred $
 */
public class SortTableHeader extends JTableHeader implements MouseListener, MouseMotionListener {
    private static final long serialVersionUID = 3256728372658124082L;

    private boolean pressed;                // Mouse is pressed
    private boolean dragged;                // Mouse is being dragged
    private boolean sortingAllowed = true;  // Sorting allowed?
    private int pressedIndex;               // Index of the currently clicked column
    private Icon iconAsc;                   // Icon for ascending sort order
    private Icon iconDesc;                  // Icon for descending sort order

    /**
     * Create a new SortTableHeader.
     */
    public SortTableHeader() {
        super(null);
    }

    /**
     * Create a new SortTableHeader with the given {@link TableColumnModel}
     * 
     * @param cm
     *            {@link TableColumnModel} to be used
     */
    public SortTableHeader(TableColumnModel cm) {
        super(cm);

        // --- Initialize the default iconset ---
        iconAsc = new ArrowIcon(7, 6, SwingConstants.SOUTH);
        iconDesc = new ArrowIcon(7, 6, SwingConstants.NORTH);

        // --- Set the default renderer ---
        setDefaultRenderer(new SortTableCellRenderer(createDefaultRenderer()));

        // --- Listen to the mouse ---
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     * Set if the user is allowed to sort columns by clicking on their headline.
     * 
     * @param b
     *            Sorting allowed? true=Yes (default), false=No
     */
    public void setSortingAllowed(boolean b) {
        sortingAllowed = b;
    }

    /**
     * Find out if sorting is allowed.
     * 
     * @return true: sorting is allowed, false: not allowed
     */
    public boolean isSortingAllowed() {
        return sortingAllowed;
    }

    /**
     * Set the icon for ascending order.
     * 
     * @param icon
     *            Ascending icon, or null for none
     */
    public void setIconAsc(Icon icon) {
        iconAsc = icon;
    }

    /**
     * Get the current icon for ascending order.
     * 
     * @return Ascending icon, may be null
     */
    public Icon getIconAsc() {
        return iconAsc;
    }

    /**
     * Set the icon for descending order.
     * 
     * @param icon
     *            Descending icon, or null for none
     */
    public void setIconDesc(Icon icon) {
        iconDesc = icon;
    }

    /**
     * Get the current icon for descending order.
     * 
     * @return Descending icon, may be null
     */
    public Icon getIconDesc() {
        return iconDesc;
    }

    /**
     * Internal MouseListener implementation, do not use!
     * 
     * @param e
     *            MouseEvent
     */
    public void mousePressed(MouseEvent e) {
        pressed = true;
        pressedIndex = columnAtPoint(e.getPoint());
        repaint();
    }

    /**
     * Internal MouseListener implementation, do not use!
     * 
     * @param e
     *            MouseEvent
     */
    public void mouseReleased(MouseEvent e) {
        if (!dragged) {
            // --- Find the column ---
            TableModel model = table.getModel();
            if (model instanceof SortableTableModel) {
                int column = table.convertColumnIndexToModel(pressedIndex);

                do {
                    if (!sortingAllowed) break;

                    if (column < 0 || column >= columnModel.getColumnCount()) break;

                    if (model instanceof ExtendedSortableTableModel) {
                        if (!((ExtendedSortableTableModel) model).isColumnSortable(column)) {
                            break;
                        }
                    }

                    ((JSortedTable) table).sortByColumn(column);
                } while (false);
            }
        }
        pressed = false;
        dragged = false;
        repaint();
    }

    /**
     * Internal MouseMotionListener implementation, do not use!
     * 
     * @param e
     *            MouseEvent
     */
    public void mouseDragged(MouseEvent e) {
        dragged = true;
        pressed = false;
        repaint();
    }

    /**
     * Internal MouseMotionListener implementation, do not use!
     * 
     * @param e
     *            MouseEvent
     */
    public void mouseMoved(MouseEvent e) {
        dragged = false;
    }

    /**
     * Internal MouseListener implementation, do not use!
     * 
     * @param e
     *            MouseEvent
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Internal MouseListener implementation, do not use!
     * 
     * @param e
     *            MouseEvent
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Internal MouseListener implementation, do not use!
     * 
     * @param e
     *            MouseEvent
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * This is a TableCellRenderer that shows the selection and sorting state of each
     * column, according to the {@link SortTableHeader}.
     */
    private class SortTableCellRenderer implements TableCellRenderer, Serializable {
        private static final long serialVersionUID = 3256437023551468342L;
        private TableCellRenderer parent;

        /**
         * Create a new SortTableCellRenderer. A parent {@link TableCellRenderer} is
         * given, its result will be manipulated. Usually you will pass the
         * {@link JTableHeader}'s default {@link TableCellRenderer} here.
         * 
         * @param parent
         *            Parent {@link TableCellRenderer}
         */
        public SortTableCellRenderer(TableCellRenderer parent) {
            this.parent = parent;
        }

        /**
         * Get a Component that renders the table cell.
         * 
         * @param table
         *            Referred {@link JTable}
         * @param value
         *            Value to render
         * @param isSelected
         *            Selection state, usually ignored
         * @param hasFocus
         *            Focus state, usually ignored
         * @param row
         *            Row, usually ignored
         * @param column
         *            Column
         * @return A component that renders the table cell
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // --- Get the original Component ---
            Component c = parent.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // --- It should be a JLabel ---
            if (c instanceof JLabel) {
                JLabel cl = (JLabel) c;

                // --- Show the depressed state ---
                if (pressed && pressedIndex == column) {
                    cl.setBackground(cl.getBackground().darker());
                }

                // --- Show the sorting state ---
                TableModel model = table.getModel();
                if (model instanceof SortableTableModel) {
                    SortableTableModel sortmodel = (SortableTableModel) model;
                    int modelcolumn = table.convertColumnIndexToModel(column);
                    if (sortmodel.getSortedColumn() == modelcolumn) {
                        cl.setHorizontalTextPosition(SwingConstants.LEADING);
                        if (sortmodel.isDescending()) {
                            cl.setIcon(iconDesc);
                        } else {
                            cl.setIcon(iconAsc);
                        }
                    } else {
                        cl.setIcon(null);
                    }
                }
            }

            // --- Return the manipulated Component ---
            return c;
        }
    }

}
