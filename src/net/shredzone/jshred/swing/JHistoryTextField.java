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
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.prefs.*;

/**
 * This is some kind of JTextField with a JComboBox. Text can be entered
 * freely into the text field. The class keeps a history of recently
 * entered texts, which can be selected in the JComboBox. The history
 * is optionally stored as Preferences if a unique name is given to the
 * constructor.
 * <p>
 * This class is thread safe.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JHistoryTextField.java,v 1.6 2005/01/11 19:37:41 shred Exp $
 * @since   R6
 */
public class JHistoryTextField extends JComponent {
  private static final long serialVersionUID = 3688784791113577272L;
  private JComboBox jCombo;
  private int       histSize;
  private boolean   autoSelect = true;
  private String    nodeName   = null;
  private final Set sListener  = new HashSet();
  
  /**
   * Create a new JHistoryTextField. The history will only be stored
   * during the lifetime of this object.
   */
  public JHistoryTextField() {
    this( null );
  }
  
  /**
   * Create a new JHistoryTextField. The history will be persistently
   * stored using Preferences. You must provide a unique name for the
   * history to be stored. It is strongly recommended to use the
   * Java notation for unique names, as used in package names (i.e.
   * your domain name in reverse notation, following a unique string
   * of your choice).
   *
   * @param   name      Name which is used to persist the history.
   */
  public JHistoryTextField( String name ) {
    nodeName = name;
    
    //--- Create the GUI ---
    setLayout( new BorderLayout() );
    jCombo = new JComboBox();
    jCombo.setEditable( true );
    add( jCombo, BorderLayout.CENTER );
    
    //--- Set the default history size ---
    setHistorySize( 10 );       // Default is 10 entries
    
    //--- Recall the history ---
    if( nodeName!=null ) {
      try {
        recallHistory( nodeName );
      }catch( BackingStoreException e ) {
        // We will lose the history now. Anyhow we shouldn't bother
        // the user because he won't be able to do anything about it
        // anyhow. So silently ignore the exception.
      }
      jCombo.setSelectedIndex( -1 );
    }

    //--- Add an ActionListener ---
    jCombo.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        //--- Remember the entry ---
        addHistory( getText() );

        //--- Mark all the text ---
        if( autoSelect )
          jCombo.getEditor().selectAll();
        
        //--- Notify everyone about a new text ---
        if( e.getActionCommand().equals("comboBoxChanged") ) {
          ActionEvent e2 = new ActionEvent(
            JHistoryTextField.this,
            ActionEvent.ACTION_PERFORMED,
            "textChanged",
            e.getWhen(),
            e.getModifiers()
          );
          fireActionEvent( e2 );
        }
      }
    });
  }

  /**
   * Set the maximum history size. If a new entry would exceed the
   * limit, the least recently used entry will be removed from the
   * history.
   *
   * @param   size        New history size
   */
  public void setHistorySize( int size ) {
    if( histSize<0 )
      throw new IllegalArgumentException( "size must be positive" );

    //--- Remove entries to reach the size ---
    synchronized( jCombo ) {
      histSize = size;
      if( histSize>jCombo.getItemCount() ) {
        for( int ix=jCombo.getItemCount()-1; ix>=histSize; ix-- ) {
          jCombo.removeItemAt( ix );
        }
      }
    }

    //--- Set Maximum ---
    // Take care that the entire history is visible
    // within a reasonable range.
    jCombo.setMaximumRowCount( Math.min( size, 20 ) );
  }
  
  /**
   * Get the current history size. Default is 10.
   *
   * @return  Current history size
   */
  public int getHistorySize() {
    return histSize;
  }
  
  /**
   * Set the auto selection mode. If enabled, the entire text field
   * will be marked after the user pressed return, so a new text
   * entered will automatically replace the old one. This mode is
   * turned on by default.
   *
   * @param   as          Auto selection mode.
   */
  public void setAutoSelection( boolean as ) {
    autoSelect = as;
  }
  
  /**
   * Check if the auto selection mode is enabled.
   *
   * @return  Auto selection mode state.
   */
  public boolean isAutoSelection() {
    return autoSelect;
  }

  /**
   * Set the text to be shown in this component. The text will
   * also be added to the history.
   *
   * @param   text        Text to be set.
   */
  public void setText( String text ) {
    synchronized( jCombo ) {
      jCombo.setSelectedItem( text );
    }
  }
  
  /**
   * Get the text that is currently shown in this component.
   *
   * @return  Current text.
   */
  public String getText() {
    synchronized( jCombo ) {
      Object item = jCombo.getSelectedItem();
      return( item!=null ? item.toString() : "" );
    }
  }
  
  /**
   * Add a text to the top of the history. If the text was already
   * within the history, it is moved to the top. After that the text
   * will be shown in the component. The current history will be
   * persisted if a name was given to the constructor.
   *
   * @param   text        Text to be added to the history.
   */
  protected void addHistory( String text ) {
    if( text==null || text.equals("") ) return;
    
    synchronized( jCombo ) {
      //--- First look if the entry already exists ---
      int cnt = jCombo.getItemCount();
      for( int ix=0; ix<cnt; ix++ ) {
        if( jCombo.getItemAt( ix ).equals( text ) ) {
          //--- YES: move it to the top ---
          jCombo.removeItemAt( ix );
          break;
        }
      }
      
      //--- Add the new item to the top ---
      jCombo.insertItemAt( text, 0 );
      jCombo.setSelectedIndex( 0 );
      
      //--- Keep within maximum ---
      while( jCombo.getItemCount() > histSize ) {
        jCombo.removeItemAt( jCombo.getItemCount()-1 );
      }

      //--- Store history ---
      if( nodeName!=null ) {
        try {
          storeHistory( nodeName );
        }catch( BackingStoreException e ) {
          // We will lose the history now. Anyhow we shouldn't bother
          // the user because he won't be able to do anything about it
          // anyhow. So silently ignore the exception.
        }
      }
    }
  }
  
  /**
   * Get a list of the current history. The list is unmodifiable and
   * sorted from the most recent to the least recent history entry.
   *
   * @return    List of all history entries
   */
  public java.util.List getHistory() {
    synchronized( jCombo ) {
      int cnt = jCombo.getItemCount();
      java.util.List lResult = new ArrayList( cnt );
      for( int ix=0; ix<cnt; ix++ ) {
        lResult.add( jCombo.getItemAt(ix) );
      }
      return Collections.unmodifiableList( lResult );
    }
  }
  
  /**
   * Get the current number of history entries.
   *
   * @return  Current number of history entries.
   */
  public int getCurrentSize() {
    synchronized( jCombo ) {
      return jCombo.getItemCount();
    }
  }
  
  /**
   * Clear the entire history.
   */
  public void clearHistory() {
    synchronized( jCombo ) {
      jCombo.removeAllItems();
    }
  }
  
  /**
   * Enable this component.
   *
   * @param   enabled       true: enable the component
   */
  public void setEnabled( boolean enabled ) {
    jCombo.setEnabled( enabled );
    super.setEnabled( enabled );
  }
  
  /**
   * Store the current history to the preferences, using the given
   * name.
   *
   * @param   name        Name to file the history to.
   * @throws  BackingStoreException     if it was not possible to
   *            store the history.
   */
  protected void storeHistory( String name )
  throws BackingStoreException {
    Preferences prefs = Preferences.userNodeForPackage( this.getClass() );
    prefs = prefs.node( name );
    
    synchronized( jCombo ) {
      //--- Clear all old prefs ---
      prefs.clear();
      
      //--- Write the current history ---
      int cnt = jCombo.getItemCount();
      for( int ix=0; ix<cnt; ix++ ) {
        prefs.put( String.valueOf(ix), jCombo.getItemAt(ix).toString() );
      }
    }
  }
  
  /**
   * Recall the history from preferences, using the given name. The
   * current history will be replaced.
   *
   * @param   name        Name the history was filed to.
   * @throws  BackingStoreException     if it was not possible to
   *            recall the history.
   */
  protected void recallHistory( String name )
  throws BackingStoreException {
    Preferences prefs = Preferences.userNodeForPackage( this.getClass() );
    prefs = prefs.node( name );
    
    synchronized( jCombo ) {
      //--- Clear history ---
      clearHistory();
      
      //--- Recall the current history ---
      String[] keys =prefs.keys();
      Arrays.sort( keys, new HTFComparator() );
      int max = Math.min( keys.length, histSize );
      for( int ix=0; ix<max; ix++ ) {
        jCombo.addItem( prefs.get( keys[ix], "" ) );
      }
    }
  }
  
  /**
   * Add an ActionListener. It will be invoked if a new text has
   * been entered. If it was already added, nothing will happen.
   *
   * @param   l       ActionListener to be added.
   */
  public void addActionListener( ActionListener l ) {
    sListener.add( l );
  }
  
  /**
   * Remove an ActionListener. If it was not added, nothing will
   * happen.
   *
   * @param   l       ActionListener to be removed.
   */
  public void removeActionListener( ActionListener l ) {
    sListener.remove( l );
  }
  
  /**
   * Get an array of all ActionListeners currently set.
   *
   * @return  ActionListeners
   */
  public ActionListener[] getActionListeners() {
    return (ActionListener[]) sListener.toArray( new ActionListener[sListener.size()] );
  }
  
  /**
   * Inform all registered ActionListeners about a new ActionEvent.
   *
   * @param   e       The ActionEvent to be broadcasted.
   */
  protected void fireActionEvent( ActionEvent e ) {
    Iterator it = sListener.iterator();
    while( it.hasNext() ) {
      ActionListener l = (ActionListener) it.next();
      l.actionPerformed( e );
    }
  }

/*----------------------------------------------------------------------------*/
  
  /**
   * This comparator compares two integer values of a string representation.
   */
  private static class HTFComparator implements Comparator {
    public int compare( Object o1, Object o2 ) {
      int i1 = Integer.parseInt( o1.toString() );
      int i2 = Integer.parseInt( o2.toString() );
      return i1-i2;
    }
  }
  
}
