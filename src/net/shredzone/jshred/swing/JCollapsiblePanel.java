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
import javax.swing.event.ListDataListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This Panel shows a Component with a headline above it. The user can click
 * on the headline in order to collapse or exand the component. If the component
 * is collapsed, it will be hidden and only the headline is shown. If the
 * component is expanded, everything is shown.
 * <p>
 * JCollapsiblePanel can be used to allow the user to hide unimportant parts
 * of the GUI if there is only little space available.
 * <p>
 * Due to a bug this component was not really functional until R12.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JCollapsiblePanel.java,v 1.6 2005/12/27 14:18:59 shred Exp $
 * @since   R9
 */
public class JCollapsiblePanel extends JPanel {
  private static final long serialVersionUID = 3546645386727994681L;
  private final static java.util.prefs.Preferences prefs;
  
  static {
    java.util.prefs.Preferences created = null;
    try {
      created = java.util.prefs.Preferences.userNodeForPackage( JCollapsiblePanel.class );
    }catch( Throwable t ) {
      // Fallback for JDK 1.3. We don't have preferences there.
    }
    prefs = created;
  }

  protected Component content;
  private JToggleButton jbToggle;
  private Icon iconCollapsed;
  private Icon iconExpanded;
  private String id;            // unique id for remembering the collapse state
  private final Set sListener = new HashSet();
  
  /**
   * Creates an empty JCollapsiblePanel with no title.
   */
  public JCollapsiblePanel() {
    this( "" );
  }
  
  /**
   * Creates an empty JCollapsiblePanel with the given title.
   * 
   * @param title     Title
   */
  public JCollapsiblePanel( String title ) {
    this( title, null );
  }

  /**
   * Creates a JCollapsiblePanel with the given title and Component. The
   * panel is initially expanded.
   * 
   * @param title     Title
   * @param comp      Component to be used as content
   */
  public JCollapsiblePanel( String title, Component comp ) {
    this( title, comp, true );
  }

  /**
   * Creates a JCollapsiblePanel with the given title and Component, using
   * the given expanded state initially.
   * 
   * @param title     Title
   * @param comp      Component to be used as content
   * @param expanded  Initial state, true: expanded, false: collapsed
   */
  public JCollapsiblePanel( String title, Component comp, boolean expanded ) {
    this( title, comp, expanded, null );
  }
  
  /**
   * Creates a JCollapsiblePanel with the given title and Component, using
   * the given expanded state initially.
   * <p>
   * With the given id, the collapse state is remembered for the next time
   * the application is started. If there is no state remembered, the given
   * default "expanded" state will be used instead. Note that this feature
   * requires at least JDK 1.4.
   * 
   * @param title     Title
   * @param comp      Component to be used as content
   * @param expanded  Initial state, true: expanded, false: collapsed
   * @param id        Unique identifier to remember the collapse state.
   *                  Pass null if the state shall not be remembered.
   * @since R12
   */
  public JCollapsiblePanel( String title, Component comp, boolean expanded, String id ) {
    content = comp;
    this.id = id;

    //--- Check the preferences ---
    if( id!=null && prefs!=null ) {
      expanded = prefs.getBoolean( "state."+id, expanded );
    }
    
    //--- Create the toggle button ---
    jbToggle = new JToggleButton( title );
    jbToggle.setSelected( true );
    jbToggle.setBorderPainted( false );
    jbToggle.setBackground( getBackground().darker() );
    jbToggle.setRequestFocusEnabled( false );
    jbToggle.setMargin( new Insets(0,0,0,0) );
    jbToggle.setHorizontalTextPosition( JToggleButton.RIGHT );
    jbToggle.setHorizontalAlignment( JToggleButton.LEFT );
    jbToggle.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    jbToggle.addActionListener( new Listener() );
    
    //--- Initialize the button ---
    setCollapsedIcon( new ArrowIcon( 7,7, SwingConstants.EAST ) );
    setExpandedIcon( new ArrowIcon( 7,7, SwingConstants.SOUTH ) );
    
    //--- Assemble the GUI ---
    setLayout( new BorderLayout() );
    if( content!=null ) {
      add( content, BorderLayout.CENTER );
    }
    add( jbToggle,  BorderLayout.NORTH );
    
    //--- Set the expanded state ---
    setExpanded( expanded );
  }

  /**
   * Set the icon to be used in the title if the component is collapsed.
   * This is an arrow pointing to the right by default.
   * 
   * @param icon    New icon to be used
   */
  public void setCollapsedIcon( Icon icon ) {
    jbToggle.setIcon( icon );
    firePropertyChange( "collapsedicon", iconCollapsed, icon );
    iconCollapsed = icon;
  }
  
  /**
   * Get the current icon to be used if the component is collapsed.
   * 
   * @return    Collapsed icon
   */
  public Icon getCollapsedIcon() {
    return iconCollapsed;
  }
  
  /**
   * Set the icon to be used in the title if the component is expanded.
   * This is an arrow pointing down by default.
   * 
   * @param icon    New icon to be used
   */
  public void setExpandedIcon( Icon icon ) {
    jbToggle.setSelectedIcon( icon );
    firePropertyChange( "expandedicon", iconExpanded, icon );
    iconExpanded = icon;
  }
  
  /**
   * Get the current icon to be used if the component is expanded.
   * 
   * @return    Expanded icon
   */
  public Icon getExpandedIcon() {
    return iconExpanded;
  }
  
  /**
   * Set the enabled state. Disabling the JCollapsiblePane will only disable
   * the title button, but not the content. I.e. the user cannot collapse the
   * component, but can still use it.
   * <p>
   * <em>NOTE:</em> if <code>setEnabled(false)</code> is invoked while this
   * panel is collapsed, then the user will be unable to expand and use the
   * component.
   * 
   * @param   b       true: JCollapsiblePane is enabled, false: disabled.
   */
  public void setEnabled( boolean b ) {
    super.setEnabled( b );
    jbToggle.setEnabled( b );
  }
  
  /**
   * Set the expanded state. If true, the Component will be shown. If false,
   * the Component will be hidden.
   * 
   * @param b     true: expand, false: collapse
   */
  public void setExpanded( boolean b ) {
    jbToggle.setSelected( b );
    doExpand( b );
  }
  
  /**
   * Internal method that does the actual collapsing and expanding of the
   * content.
   * 
   * @param b       true: expand, false: collapse
   */
  private void doExpand( boolean b ) {
    if( content!=null ) {
      content.setVisible( b );
      revalidate();
    }
    if( id!=null && prefs!=null ) {
      prefs.putBoolean( "state."+id, b );
    }
  }
  
  /**
   * Get the current expanded state.
   * 
   * @return    true: expanded, false: collapsed
   */
  public boolean isExpanded() {
    return jbToggle.isSelected();
  }
  
  /**
   * Set the title above the component.
   * 
   * @param title   New title to be used.
   */
  public void setTitle( String title ) {
    firePropertyChange( "title", jbToggle.getText(), title );
    jbToggle.setText( title );
  }

  /**
   * Get the current title above the component.
   * 
   * @return    Current title.
   */
  public String getTitle() {
    return jbToggle.getText();
  }
  
  /**
   * Set a new content Component. It will replace the current content.
   * 
   * @param comp    New content Component to be used.
   */
  public void setContent( Component comp ) {
    if( content!=null ) remove( content );
    firePropertyChange( "content", content, comp );
    if( comp!=null ) {
      add( comp, BorderLayout.CENTER );
      doExpand( isExpanded() );
    }
    content = comp;
  }
  
  /**
   * Get the current content Component.
   * 
   * @return  Content Component, or null if none was set.
   */
  public Component getContent() {
    return content;
  }
  
  /**
   * Add an ActionListener. It will be invoked when the collapsed/expanded
   * state was changed.
   * 
   * @param l     ActionListener to be added
   */
  public void addActionListener( ActionListener l ) {
    addActionListener( l, false );
  }

  /**
   * Add an ActionListener. It will be invoked when the collapsed/expanded
   * state was changed.
   * 
   * @param l       ActionListener to be added
   * @param weakly  Add the listener by WeakReference
   * @since R12
   */
  public void addActionListener( ActionListener l, boolean weakly ) {
    //--- Check if the Listener is already added weakly ---
    for (
        Iterator it = sListener.iterator();
        it.hasNext();
        ) {
      final Object next = it.next();
      if( next instanceof WeakReference ) {
        final WeakReference wr = (WeakReference) next;
        if (wr.get() == l) {
          return;
        }
      }
    }
    
    //--- Add the Listener ---
    if( weakly ) {
      sListener.add( new WeakReference( l ) );
    }else {
      sListener.add( l );
    }
  }

  /**
   * Remove an ActionListener.
   * 
   * @param l     ActionListener to be removed
   */
  public void removeActionListener( ActionListener l ) {
    //--- Remove weak reference ---
    for (
        Iterator it = sListener.iterator();
        it.hasNext();
        ) {
      final Object next = it.next();
      if( next instanceof WeakReference ) {
        final WeakReference wr = (WeakReference) next;
        if (wr.get() == l) {
          it.remove();
          break;
        }
      }
    }
    
    //--- Remove standard reference ---
    sListener.remove( l );
  }
  
/*----------------------------------------------------------------------------*/
  
  /**
   * Private ActionListener implementation. It will be invoked when the
   * title JToggleButton was pressed.
   */
  private class Listener implements ActionListener {
    public void actionPerformed( ActionEvent e ) {
      if( content!=null ) {
        //--- Change visibility ---
        doExpand( jbToggle.isSelected() );
        
        //--- Copy the event ---
        ActionEvent event = new ActionEvent(
            JCollapsiblePanel.this,
            e.getID(),
            "expand",
            e.getWhen(),
            e.getModifiers()
        );
        
        //--- Notify everyone ---
        for (
            Iterator it = sListener.iterator();
            it.hasNext();
            ) {
          final Object next = it.next();
          final ActionListener l = (ActionListener) (
              next instanceof WeakReference ?
              ((WeakReference) next).get():
              next
          );
          if (l != null) {
            l.actionPerformed( e );
          }else {
            it.remove();
          }
        }

      }
    }
  }

}
