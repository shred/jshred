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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
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
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JCollapsiblePanel.java,v 1.2 2005/01/11 19:37:40 shred Exp $
 * @since   R9
 */
public class JCollapsiblePanel extends JPanel {
  private static final long serialVersionUID = 3546645386727994681L;
  
  protected Component content;
  private JToggleButton jbToggle;
  private Icon iconCollapsed;
  private Icon iconExpanded;
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
    content = comp;
    
    //--- Create the toggle button ---
    jbToggle = new JToggleButton( title );
    jbToggle.setSelected( true );
    jbToggle.setBorderPainted( false );
    jbToggle.setBackground( getBackground().darker() );
    jbToggle.setRequestFocusEnabled( false );
    jbToggle.setMargin( new Insets(0,0,0,0) );
    jbToggle.setHorizontalTextPosition( JToggleButton.RIGHT );
    jbToggle.setHorizontalAlignment( JToggleButton.LEFT );
    jbToggle.addActionListener( new Listener() );
    
    //--- Initialize the button ---
    setCollapsedIcon( new ArrowIcon( true ) );
    setExpandedIcon( new ArrowIcon( false ) );
    
    //--- Assemble the GUI ---
    setLayout( new BorderLayout() );
    if( content!=null )
      add( content, BorderLayout.CENTER );
    add( jbToggle,  BorderLayout.NORTH );
    
    //--- Set a border around it ---
    setBorder( BorderFactory.createLoweredBevelBorder() );
    
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
    if( content!=null )
      content.setVisible( b );
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
      comp.setVisible( isExpanded() );
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
    sListener.add( l );
  }

  /**
   * Remove an ActionListener.
   * 
   * @param l     ActionListener to be removed
   */
  public void removeActionListener( ActionListener l ) {
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
        content.setVisible( jbToggle.isSelected() );
        
        //--- Copy the event ---
        ActionEvent event = new ActionEvent(
            JCollapsiblePanel.this,
            e.getID(),
            "expand",
            e.getWhen(),
            e.getModifiers()
        );
        
        //--- Notify everyone ---
        Iterator it = sListener.iterator();
        while( it.hasNext() ) {
          ActionListener l = (ActionListener) it.next();
          l.actionPerformed( event );
        }
      }
    }
  }
  
/*----------------------------------------------------------------------------*/

  /**
   * Draws a collapse handle. This is a triangle either pointing down or to
   * the right.
   */
  private static class ArrowIcon implements Icon, Serializable {
    private static final long serialVersionUID = 3760566403421712949L;
    private boolean collapsed;

    /**
     * Create a new ArrowIcon.
     *
     * @param   collapsed    true: Array is right, false: Array is down
     */
    public ArrowIcon( boolean collapsed ) {
      this.collapsed = collapsed;
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
      if( collapsed ) {
        pX[0] = x  ; pY[0] = y  ;
        pX[1] = x  ; pY[1] = y+6;
        pX[2] = x+3; pY[2] = y+3;
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
     * Width is fixed to 7 pixel.
     *
     * @return    Icon height
     */
    public int getIconHeight() {
      return 7;
    }
  }

}
