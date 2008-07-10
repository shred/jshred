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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * This is a collection of static methods for your convenience.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: SwingUtils.java 167 2008-07-10 14:59:00Z shred $
 */
public class SwingUtils {

  /**
   * Set the width of a JComponent to its minimum. Use this to stack multiple
   * JTextField and JComboBox in a BoxLayout along the x axis.
   *
   * @param   comp      JComponent to be minimized.
   */
  public static void setMinimumWidth( JComponent comp ) {
    int width = comp.getMinimumSize().width;
    comp.setMaximumSize(   new Dimension( width, comp.getMaximumSize().height   ) );
    comp.setPreferredSize( new Dimension( width, comp.getPreferredSize().height ) );
  }

  /**
   * Set the height of a JComponent to its minimum. Use this to stack multiple
   * JTextField and JComboBox in a BoxLayout along the y axis.
   *
   * @param   comp      JComponent to be minimized.
   */
  public static void setMinimumHeight( JComponent comp ) {
    int height = comp.getMinimumSize().height;
    comp.setMaximumSize(   new Dimension( comp.getMaximumSize().width, height   ) );
    comp.setPreferredSize( new Dimension( comp.getPreferredSize().width, height ) );
  }

  /**
   * Set the NAME and MNEMONIC_KEY of an Action to the menu string. The
   * menu string (with stripped underscore) will be the NAME, and the
   * first character after the underscore will be the MNEMONIC_KEY.
   * <p>
   * Example: <code>"_Undo"</code> results to a NAME "Undo" and a
   * MNEMONIC_KEY of "U".
   * <p>
   * If no underscore was found, MNEMONIC_KEY is unchanged.
   *
   * @param   action      Action to set NAME and MNEMONIC_KEY for
   * @param   menu        Menu name
   */
  public static void setMenuKey( Action action, String menu ) {
    String    name   = getMenuName( menu );
    Character stroke = getMenuShortcut( menu );

    action.putValue( Action.NAME, name );
    if( stroke!=null ) {
      char ch = Character.toUpperCase( stroke.charValue() );
      if( (ch>='0' && ch<='9') || (ch>='A' && ch<='Z') ) {
        action.putValue( Action.MNEMONIC_KEY, new Integer( ch ) );
      }
    }
  }

  /**
   * Get the name of a menu. The result is the given menu name, with
   * the first underscore being stripped. Subsequent underscores will
   * be ignored. You can excape underscores by doubling them.
   *
   * @param   menu        Menu name
   * @return  Menu name without shortcut underscore
   */
  public static String getMenuName( String menu ) {
    int i = menu.indexOf("_");
    while( i >= 0 ) {
      if( i == menu.length()-1 ) return menu;       // Letztes Zeichen kann kein Shortcut sein
      if( menu.charAt( i+1 ) != '_' )               // Kein doppelter Unterstrich = gefunden
        return menu.substring( 0,i ) + menu.substring( i+1 );   // Unterstrich entfernen
      i = menu.indexOf( "_", i+2 );                 // Den zweiten Unterstrich auch überspringen!
    }
    return menu;                                    // Nichts wurde gefunden
  }

  /**
   * Get the shortcut of a menu. The first char after an underscore will
   * be returned. Underscores can be doubled to escape them.
   *
   * @param   menu        Menu name
   * @return  Shortcut character or null if none was found
   */
  public static Character getMenuShortcut( String menu ) {
    int i = menu.indexOf("_");
    char mnemo;
    while( i >= 0 ) {
      if( i == menu.length()-1 ) break;             // Letztes Zeichen kann kein Shortcut sein
      mnemo = menu.charAt( i+1 );
      if( mnemo != '_' )
        return new Character( mnemo );              // Taste gefunden
      i = menu.indexOf( "_", i+2 );                 // Den zweiten Unterstrich auch überspringen!
    }
    return null;
  }

  /**
   * Create a JMenu from a title. An underscore marks the menu shortcut.
   *
   * @param   title       Menu title
   * @return  Created JMenu having this title and shortcut set
   */
  public static JMenu createJMenu( String title ) {
    JMenu menu = new JMenu( getMenuName( title ) );
    Character mnemo = getMenuShortcut( title );
    if( mnemo != null ) {
      menu.setMnemonic( mnemo.charValue() );
    }
    return menu;
  }

  /**
   * Get the Frame of a component. If the component was not shown in
   * a frame, null will be returned.
   *
   * @param   comp       Component
   * @return  Frame this component belongs to, or null
   */
  public static Frame getComponentFrame( Component comp ) {
    while( comp != null ) {
      if( comp instanceof Frame ) return (Frame) comp;
      comp = comp.getParent();
    }
    return null;
  }

  /**
   * Adjust each column of a JTable to show its entire content. There
   * is no maximum cell width given, which could result in unreadable
   * tables on very long content.
   *
   * @param   table       JTable to be adjusted
   */
  public static void spreadColumns( JTable table ) {
    spreadColumns( table, Integer.MAX_VALUE );
  }

  /**
   * Adjust each column of a JTable to show its entire content. The
   * cell width is limited to the given maximum width, though.
   * <p>
   * It is suggested to apply <code>setAutoResizeMode(AUTO_RESIZE_OFF)</code>
   * to the appropriate table before invoking this method.
   * <p>
   * <b>WARNING:</b> In order to find out the maximum cell widths, this
   * method will scan the entire table model! This will result in a major
   * performance penalty for large models, and especial for dynamic models
   * which will get their content from external sources.
   *
   * @param   table       JTable to be adjusted
   * @param   maxwidth    Maximum width of each table cell, in pixels.
   */
  public static void spreadColumns( JTable table, int maxwidth ) {
    spreadColumns( table, 0, maxwidth );
  }

  /**
   * Adjust each column of a JTable to show its entire content. The
   * cell width is limited to the given maximum width, though. The
   * minimum cell width is either the size of the title, or the given
   * minimum width, whatever is bigger.
   * <p>
   * It is suggested to apply <code>setAutoResizeMode(AUTO_RESIZE_OFF)</code>
   * to the appropriate table before invoking this method.
   * <p>
   * <b>WARNING:</b> In order to find out the maximum cell widths, this
   * method will scan the entire table model! This will result in a major
   * performance penalty for large models, and especial for dynamic models
   * which will get their content from external sources.
   *
   * @param   table       JTable to be adjusted
   * @param   minwidth    Minimum width of each table cell, in pixels.
   * @param   maxwidth    Maximum width of each table cell, in pixels.
   * @since   R4
   */
  public static void spreadColumns( JTable table, int minwidth, int maxwidth ) {
    JTableHeader header = table.getTableHeader();
    TableCellRenderer defaultHeaderRenderer = null;

    if( header!=null )
        defaultHeaderRenderer = header.getDefaultRenderer();

    TableColumnModel columns = table.getColumnModel();
    TableModel data = table.getModel();
    int margin = columns.getColumnMargin();
    int rowCount = data.getRowCount();
    for( int i=columns.getColumnCount()-1; i>=0; i-- ) {
      boolean cbOnly = table.getColumnClass( i ).equals( Boolean.class );
      
      TableColumn column = columns.getColumn(i);
      int columnIndex = column.getModelIndex();
      int width = -1;

      TableCellRenderer h = column.getHeaderRenderer();
      if( h==null ) h = defaultHeaderRenderer;

      if( h!=null) {
        Component c = h.getTableCellRendererComponent(
          table,
          column.getHeaderValue(),
          false, false, -1, i
        );
        if( cbOnly ) {
          width = c.getPreferredSize().width;
        }else {
          width = Math.max(minwidth, c.getPreferredSize().width);
        }
      }

      for( int row=rowCount-1; row>=0; row-- ) {
        TableCellRenderer r = table.getCellRenderer( row, i );
        Component c = r.getTableCellRendererComponent(
          table,
          data.getValueAt( row, columnIndex ),
          false, false, row, i
        );
        width = Math.max(width, c.getPreferredSize().width);
      }

      if( width>=0 ) {
        width += margin + 5;
        if( maxwidth>0 && width>maxwidth )
          width = maxwidth;
        column.setPreferredWidth( width );
      }
    }
  }

  /**
   * Set the confirmation button for a JDialog.
   * 
   * @param   dialog        JDialog to set the confirmation button for
   * @param   confirm       Confirmation JButton.
   * @since   R7
   */
  public static void setDialogConfirmKey( JDialog dialog, JButton confirm ) {
    dialog.getRootPane().setDefaultButton( confirm );
  }
  
  /**
   * Set the cancel button for a JDialog. Pressing the Escape key will
   * then also trigger that JButton.
   * 
   * @param   dialog        JDialog to set the cancel button for
   * @param   cancel        Cancel JButton.
   * @since   R7
   */
  public static void setDialogCancelKey( JDialog dialog, JButton cancel ) {
    final JButton fCancel = cancel;
    final String name = "CancelAction";
    
    JLayeredPane lp = dialog.getLayeredPane();
    lp.getActionMap().put( name, new AbstractAction( name ) {
      private static final long serialVersionUID = 3760844579897030200L;
      public void actionPerformed( ActionEvent e ) {
        fCancel.doClick();
      }
    });
    
    KeyStroke stroke = KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 );
    lp.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( stroke, name );
  }
  
  /**
   * Copy the given String content to the system's default clipboard.
   * 
   * @param   content       String content to be copied to the clipboard.
   * @since   R8
   */
  public static void copyToClipboard( String content ) {
    StringSelection selection = new StringSelection( content );
    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
    clip.setContents( selection, selection );
  }

  /**
   * Get a String content from the system's default clipboard. If the
   * clipboard is empty, or if it does not contain a String, then null
   * will be returned. On some systems null will also be returned if the
   * clipboard is currently accessed by another application.
   * <p>
   * The requestor parameter is currently unused (up to JDK1.5.0). Anyhow
   * please pass a reference to the invoking class (usually <code>this</code>).
   * 
   * @param   requestor       Requestor, usually <code>this</code>.
   * @return  The String currently found in the clipboard, or null.
   * @since   R8
   */
  public static String pasteFromClipboard( Object requestor ) {
    Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
    Transferable trans = clip.getContents( requestor );
    String result = null;
    try {
      result = (String) trans.getTransferData( DataFlavor.stringFlavor );
    }catch( Exception e ) {}
    return result;
  }

  /**
   * Compute a Dimension that has the same aspect ratio as the first Dimension,
   * but does not exceed any part of the second Dimension. The returned
   * Dimension will also never be larger than the aspect Dimension.
   * 
   * @param   aspect      Dimension to keep the aspect ratio
   * @param   max         Maximum dimensions
   * @return  The scaled dimension
   * @since   R9
   */
  public static Dimension scaleAspect( Dimension aspect, Dimension max ) {
    final Dimension dim = new Dimension(0,0);

    if( aspect.width>0 && aspect.height>0 && max.width>0 && max.height>0 ) {
      dim.width  = aspect.width;
      dim.height = aspect.height;
      
      if( dim.width > max.width ) {
        dim.height = ( dim.height * max.width ) / dim.width;
        dim.width  = max.width;
      }
      
      if( dim.height > max.height ) {
        dim.width  = ( dim.width * max.height ) / dim.height;
        dim.height = max.height;
      }
    }
    
    return dim;
  }

  /**
   * Compute a Dimension that has the same aspect ratio as the first Dimension,
   * but uses as much of the Maximum dimensions as possible, without exceeding
   * it. The returned Dimension may be larger than the aspect Dimension.
   * 
   * @param   aspect      Dimension to keep the aspect ratio
   * @param   max         Maximum dimensions
   * @return  The scaled dimension
   * @since   R9
   */
  public static Dimension scaleAspectMax( Dimension aspect, Dimension max ) {
    Dimension maxCopy = new Dimension(0,0);
    
    if( aspect.width>0 && aspect.height>0 && max.width>0 && max.height>0 ) {
      if( aspect.width > aspect.height ) {
        // Landscape
        
        maxCopy.width  = max.width;
        maxCopy.height = max.width * aspect.height / aspect.width;
      }else {
        // Portrait
        
        maxCopy.height = max.height;
        maxCopy.width  = max.height * aspect.width / aspect.height;
      }
      
      maxCopy = scaleAspect( maxCopy, max );
    }
   
    return maxCopy;
  }
  
  /**
   * Recursively get a Collection of all Components in a Component.
   * 
   * @param comp    Root component
   * @return  Collection of all Components and sub Components.
   * @since R14
   */
  public static Collection<Component> getComponentsRecursive(Component comp) {
    List<Component> result = new ArrayList<Component>();
    getComponentsRecursiveHelper(result, comp);
    return result;
  }
  
  private static void getComponentsRecursiveHelper(List<Component> result, Component comp) {
    if (comp instanceof Container) {
      Component[] children = ((Container) comp).getComponents();
      for (int ix = 0; ix < children.length; ix++) {
        getComponentsRecursiveHelper(result, children[ix]);
      }
    }
    result.add(comp);
  }
  
  /**
   * Recursively enables a Component and all its subcomponents. Usually if
   * you disable a Container, only the container itself is disabled, but not
   * the children Components.
   * 
   * @param comp    Root component
   * @param enable  enable flag
   * @since R14
   */
  public static void enableRecursive(Component comp, boolean enable) {
    if (comp instanceof Container) {
      Component[] children = ((Container) comp).getComponents();
      for (int ix = 0; ix < children.length; ix++) {
        enableRecursive(children[ix], enable);
      }
    }
    comp.setEnabled(enable);
  }
  
}
