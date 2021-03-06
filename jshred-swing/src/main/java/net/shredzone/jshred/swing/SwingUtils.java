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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * This is a collection of static methods for your convenience.
 *
 * @author Richard "Shred" Körber
 */
public final class SwingUtils {

    private SwingUtils() {
        // Utility class without constructor
    }

    /**
     * Sets the width of a {@link JComponent} to its minimum. Use this to stack multiple
     * {@link JTextField} and {@link JComboBox} in a {@link BoxLayout} along the x axis.
     *
     * @param comp
     *            {@link JComponent} to be minimized.
     */
    public static void setMinimumWidth(JComponent comp) {
        int width = comp.getMinimumSize().width;
        comp.setMaximumSize(new Dimension(width, comp.getMaximumSize().height));
        comp.setPreferredSize(new Dimension(width, comp.getPreferredSize().height));
    }

    /**
     * Sets the height of a {@link JComponent} to its minimum. Use this to stack multiple
     * {@link JTextField} and {@link JComboBox} in a {@link BoxLayout} along the y axis.
     *
     * @param comp
     *            {@link JComponent} to be minimized.
     */
    public static void setMinimumHeight(JComponent comp) {
        int height = comp.getMinimumSize().height;
        comp.setMaximumSize(new Dimension(comp.getMaximumSize().width, height));
        comp.setPreferredSize(new Dimension(comp.getPreferredSize().width, height));
    }

    /**
     * Sets the {@code NAME} and {@code MNEMONIC_KEY} of an {@link Action} to the
     * menu string. The menu string (with stripped underscore) will be the
     * {@code NAME}, and the first character after the underscore will be the
     * {@code MNEMONIC_KEY}.
     * <p>
     * Example: <tt>"_Undo"</tt> results to a {@code NAME} <tt>"Undo"</tt> and a
     * {@code MNEMONIC_KEY} of <tt>"U"</tt>.
     * <p>
     * If no underscore was found, {@code MNEMONIC_KEY} is unchanged.
     *
     * @param action
     *            Action to set {@code NAME} and {@code MNEMONIC_KEY} for
     * @param menu
     *            Menu name
     */
    public static void setMenuKey(Action action, String menu) {
        String name = getMenuName(menu);
        Character stroke = getMenuShortcut(menu);

        action.putValue(Action.NAME, name);
        if (stroke != null) {
            char ch = Character.toUpperCase(stroke.charValue());
            if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'Z')) {
                action.putValue(Action.MNEMONIC_KEY, new Integer(ch));
            }
        }
    }

    /**
     * Gets the name of a menu. The result is the given menu name, with the first
     * underscore being stripped. Subsequent underscores will be ignored. You can escape
     * underscores by doubling them.
     *
     * @param menu
     *            Menu name
     * @return Menu name without shortcut underscore
     */
    public static String getMenuName(String menu) {
        int i = menu.indexOf("_");
        while (i >= 0) {
            // Last char cannot be a shortcut
            if (i == menu.length() - 1) {
                return menu;
            }

            // No double underscore -> found!
            if (menu.charAt(i + 1) != '_') {
                // Remove underscore
                return menu.substring(0, i) + menu.substring(i + 1);
            }

            // Also skip the second underscore
            i = menu.indexOf("_", i + 2);
        }
        return menu; // Nothing was found
    }

    /**
     * Gets the shortcut of a menu. The first char after an underscore will be returned.
     * Underscores can be doubled to escape them.
     *
     * @param menu
     *            Menu name
     * @return Shortcut character or {@code null} if none was found
     */
    public static Character getMenuShortcut(String menu) {
        int i = menu.indexOf("_");
        char mnemo;
        while (i >= 0) {
            // Last char cannot be a shortcut
            if (i == menu.length() - 1) {
                break;
            }

            // Key was found
            mnemo = menu.charAt(i + 1);
            if (mnemo != '_') {
                return new Character(mnemo);
            }

            // Also skip the second underscore
            i = menu.indexOf("_", i + 2);
        }
        return null;
    }

    /**
     * Creates a {@link JMenu} from a title. An underscore marks the menu shortcut.
     *
     * @param title
     *            Menu title
     * @return Created {@link JMenu} having this title and shortcut set
     */
    public static JMenu createJMenu(String title) {
        JMenu menu = new JMenu(getMenuName(title));
        Character mnemo = getMenuShortcut(title);
        if (mnemo != null) {
            menu.setMnemonic(mnemo.charValue());
        }
        return menu;
    }

    /**
     * Gets the {@link Frame} of a {@link Component}. If the {@link Component} was not
     * shown in a frame, {@code null} will be returned.
     *
     * @param comp
     *            {@link Component}
     * @return {@link Frame} this component belongs to, or <code>null</code>
     */
    public static Frame getComponentFrame(Component comp) {
        while (comp != null) {
            if (comp instanceof JPopupMenu) {
                // popup menus do not have a frame, so follow the invoker
                comp = ((JPopupMenu) comp).getInvoker();
                continue;
            }
            if (comp instanceof Frame) {
                return (Frame) comp;
            }
            comp = comp.getParent();
        }
        return null;
    }

    /**
     * Adjusts each column of a {@link JTable} to show its entire content. There is no
     * maximum cell width given, which could result in unreadable tables on very long
     * content.
     *
     * @param table
     *            {@link JTable} to be adjusted
     */
    public static void spreadColumns(JTable table) {
        spreadColumns(table, Integer.MAX_VALUE);
    }

    /**
     * Adjusts each column of a {@link JTable} to show its entire content. The cell width
     * is limited to the given maximum width, though.
     * <p>
     * It is suggested to apply {@code setAutoResizeMode(AUTO_RESIZE_OFF)} to the
     * appropriate table before invoking this method.
     * <p>
     * <b>WARNING:</b> In order to find out the maximum cell widths, this method will scan
     * the entire table model! This will result in a major performance penalty for large
     * models, and especial for dynamic models which will get their content from external
     * sources.
     *
     * @param table
     *            {@link JTable} to be adjusted
     * @param maxwidth
     *            Maximum width of each table cell, in pixels.
     */
    public static void spreadColumns(JTable table, int maxwidth) {
        spreadColumns(table, 0, maxwidth);
    }

    /**
     * Adjusts each column of a {@link JTable} to show its entire content. The cell width
     * is limited to the given maximum width, though. The minimum cell width is either the
     * size of the title, or the given minimum width, whatever is bigger.
     * <p>
     * It is suggested to apply {@code setAutoResizeMode(AUTO_RESIZE_OFF)} to the
     * appropriate table before invoking this method.
     * <p>
     * <b>WARNING:</b> In order to find out the maximum cell widths, this method will scan
     * the entire table model! This will result in a major performance penalty for large
     * models, and especial for dynamic models which will get their content from external
     * sources.
     *
     * @param table
     *            {@link JTable} to be adjusted
     * @param minwidth
     *            Minimum width of each table cell, in pixels.
     * @param maxwidth
     *            Maximum width of each table cell, in pixels.
     * @since R4
     */
    public static void spreadColumns(JTable table, int minwidth, int maxwidth) {
        JTableHeader header = table.getTableHeader();
        TableCellRenderer defaultHeaderRenderer = null;

        if (header != null) defaultHeaderRenderer = header.getDefaultRenderer();

        TableColumnModel columns = table.getColumnModel();
        TableModel data = table.getModel();
        int margin = columns.getColumnMargin();
        int rowCount = data.getRowCount();
        for (int i = columns.getColumnCount() - 1; i >= 0; i--) {
            boolean cbOnly = table.getColumnClass(i).equals(Boolean.class);

            TableColumn column = columns.getColumn(i);
            int columnIndex = column.getModelIndex();
            int width = -1;

            TableCellRenderer h = column.getHeaderRenderer();
            if (h == null) h = defaultHeaderRenderer;

            if (h != null) {
                Component c = h.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, -1, i);
                if (cbOnly) {
                    width = c.getPreferredSize().width;
                } else {
                    width = Math.max(minwidth, c.getPreferredSize().width);
                }
            }

            for (int row = rowCount - 1; row >= 0; row--) {
                TableCellRenderer r = table.getCellRenderer(row, i);
                Component c = r.getTableCellRendererComponent(table, data.getValueAt(row, columnIndex), false, false, row, i);
                width = Math.max(width, c.getPreferredSize().width);
            }

            if (width >= 0) {
                width += margin + 5;
                if (maxwidth > 0 && width > maxwidth) width = maxwidth;
                column.setPreferredWidth(width);
            }
        }
    }

    /**
     * Sets the confirmation button for a {@link RootPaneContainer}. Usually the
     * confirmation button has a broader frame, so the user can identify the default
     * confirmation option of a dialog. Pressing the return key will usually result in
     * triggering that button.
     *
     * @param dialog
     *            {@link RootPaneContainer} to set the confirmation button for
     * @param confirm
     *            Confirmation {@link JButton}.
     * @since R7
     */
    public static void setConfirmKey(RootPaneContainer dialog, JButton confirm) {
        dialog.getRootPane().setDefaultButton(confirm);
    }

    /**
     * Sets the cancel button for a {@link RootPaneContainer}. Pressing the escape key
     * will also trigger that {@link JButton}.
     *
     * @param dialog
     *            {@link RootPaneContainer} to set the cancel button for
     * @param cancel
     *            Cancel {@link JButton}.
     * @since R7
     */
    public static void setCancelKey(RootPaneContainer dialog, JButton cancel) {
        final JButton fCancel = cancel;
        final String name = "CancelAction";

        JLayeredPane lp = dialog.getLayeredPane();
        lp.getActionMap().put(name, new AbstractAction(name) {
            private static final long serialVersionUID = 3760844579897030200L;

            @Override
            public void actionPerformed(ActionEvent e) {
                fCancel.doClick();
            }
        });

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        lp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(stroke, name);
    }

    /**
     * Copies the given String content to the system's default clipboard.
     *
     * @param content
     *            String content to be copied to the clipboard.
     * @since R8
     */
    public static void copyToClipboard(String content) {
        StringSelection selection = new StringSelection(content);
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        clip.setContents(selection, selection);
    }

    /**
     * Gets a String content from the system's default clipboard. If the clipboard is
     * empty, or if it does not contain a String, then {@code null} will be returned. On
     * some systems {@code null} will also be returned if the clipboard is currently
     * accessed by another application.
     * <p>
     * The requestor parameter is required by the JDK, but currently unused. Anyhow please
     * pass a reference to the invoking class (usually {@code this}).
     *
     * @param requestor
     *            Requestor, usually {@code this}.
     * @return The String currently found in the clipboard, or {@code null}.
     * @since R8
     */
    public static String pasteFromClipboard(Object requestor) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = clip.getContents(requestor);
        String result = null;
        try {
            result = (String) trans.getTransferData(DataFlavor.stringFlavor);
        } catch (Exception e) {}
        return result;
    }

    /**
     * Compute a {@link Dimension} that has the same aspect ratio as the first
     * {@link Dimension}, but does not exceed any part of the second {@link Dimension}.
     * The returned {@link Dimension} will also never be larger than the aspect
     * {@link Dimension}.
     * <p>
     * This method can be used to scale down images that are bigger than the displaying
     * pane. Smaller images are not scaled up, though, but are keeping their original
     * size.
     *
     * @param aspect
     *            {@link Dimension} to keep the aspect ratio
     * @param max
     *            Maximum {@link Dimension}
     * @return The scaled {@link Dimension}
     * @since R9
     */
    public static Dimension scaleAspect(Dimension aspect, Dimension max) {
        final Dimension dim = new Dimension(0, 0);

        if (aspect.width > 0 && aspect.height > 0 && max.width > 0 && max.height > 0) {
            dim.width = aspect.width;
            dim.height = aspect.height;

            if (dim.width > max.width) {
                dim.height = (dim.height * max.width) / dim.width;
                dim.width = max.width;
            }

            if (dim.height > max.height) {
                dim.width = (dim.width * max.height) / dim.height;
                dim.height = max.height;
            }
        }

        return dim;
    }

    /**
     * Compute a {@link Dimension} that has the same aspect ratio as the first
     * {@link Dimension}, but uses as much of the maximum {@link Dimension} as possible,
     * without exceeding it. The returned {@link Dimension} may be larger than the aspect
     * {@link Dimension}.
     * <p>
     * This method can be used to scale images to fit into the maximum dimensions at the
     * best.
     *
     * @param aspect
     *            {@link Dimension} to keep the aspect ratio
     * @param max
     *            Maximum {@link Dimension}
     * @return The scaled {@link Dimension}
     * @since R9
     */
    public static Dimension scaleAspectMax(Dimension aspect, Dimension max) {
        Dimension maxCopy = new Dimension(0, 0);

        if (aspect.width > 0 && aspect.height > 0 && max.width > 0 && max.height > 0) {
            if (aspect.width > aspect.height) {
                // Landscape

                maxCopy.width = max.width;
                maxCopy.height = max.width * aspect.height / aspect.width;
            } else {
                // Portrait

                maxCopy.height = max.height;
                maxCopy.width = max.height * aspect.width / aspect.height;
            }

            maxCopy = scaleAspect(maxCopy, max);
        }

        return maxCopy;
    }

    /**
     * Recursively get a {@link Collection} of all {@link Component} in a
     * {@link Component}.
     *
     * @param comp
     *            Root {@link Component}
     * @return {@link Collection} of the {@link Component} and all its sub
     *         {@link Component}s.
     * @since R14
     */
    public static Collection<Component> getComponentsRecursive(Component comp) {
        List<Component> result = new ArrayList<Component>();
        getComponentsRecursiveHelper(result, comp);
        return result;
    }

    private static void getComponentsRecursiveHelper(List<Component> result, Component comp) {
        if (comp instanceof Container) {
            for (Component element : ((Container) comp).getComponents()) {
                getComponentsRecursiveHelper(result, element);
            }
        }
        result.add(comp);
    }

    /**
     * Recursively enables a {@link Component} and all its subcomponents. Usually if you
     * disable a {@link Container}, only the container itself is disabled, but not the
     * children {@link Component}s.
     *
     * @param comp
     *            Root {@link Component}
     * @param enable
     *            enable flag
     * @since R14
     */
    public static void enableRecursive(Component comp, boolean enable) {
        if (comp instanceof Container) {
            for (Component element : ((Container) comp).getComponents()) {
                enableRecursive(element, enable);
            }
        }
        comp.setEnabled(enable);
    }

}
