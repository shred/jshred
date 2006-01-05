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
import javax.swing.border.*;

/**
 * A JLabelGroup is a JLabel set left to a Component.
 * <p>
 * Multiple JLabelGroup can be connected together. On the last instance of this
 * chain, <code>rearrange()</code> will be invoked, to rearrange all JLabels
 * within this chain to the same, maximum width. This will result into a nicely
 * aligned layout.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JLabelGroup.java,v 1.2 2004/06/22 21:57:45 shred Exp $
 */
public class JLabelGroup extends JPanel {
  private Component   comp;
  private JComponent  label;
  private JLabelGroup pred;

  /**
   * Creates the first JLabelGroup of a chain.
   *
   * @param   c         Component to be labelled
   * @param   text      Label text
   */
  public JLabelGroup( Component c, String text ) {
    this( c, text, null );
  }

  /**
   * Creates a new JLabelGroup element.
   *
   * @param   c         Component to be labelled
   * @param   text      Label text
   * @param   pred      Predecessor JLabelGroup instance, or null
   */
  public JLabelGroup( Component c, String text, JLabelGroup pred ) {
    this( c, new JLabel( SwingUtils.getMenuName( text ) ), pred );
    Character keycode = SwingUtils.getMenuShortcut( text );
    if( keycode != null ) setMnemonic( keycode.charValue() );
  }

  /**
   * Creates a new JLabelGroup label with icon.
   *
   * @param   c         Component to be labelled
   * @param   text      Label text
   * @param   icon      Icon
   * @param   pred      Predecessor JLabelGroup instance, or null
   */
  public JLabelGroup( Component c, String text, Icon icon, JLabelGroup pred ) {
    this( c, new JLabel( text, icon, JLabel.WEST ), pred );
  }

  /**
   * Creates a new JLabelGroup label with a given label component. Use
   * this if you want to use a different label.
   *
   * @param   c         Component to be labelled
   * @param   label     Label component
   * @param   pred      Predecessor JLabelGroup instance, or null
   */
  public JLabelGroup( Component c, JComponent label, JLabelGroup pred ) {
    this.comp  = c;
    this.label = label;
    this.pred  = pred;

    setVerticalAlignment( SwingConstants.CENTER );
    if( label instanceof JLabel ) {
      ((JLabel) label).setLabelFor( comp );
    }

    setLayout( new BorderLayout() );
    add( label, BorderLayout.WEST );
    add( comp , BorderLayout.CENTER );
  }

  /**
   * Calculates the maximum label with of a JLabelGroup chain.
   * <code>rearrange()<code> will use this method to compute the width.
   *
   * @return    Maximum width
   */
  protected int getMaximumWidth() {
    if( pred != null ) {
      return Math.max( label.getMinimumSize().width, pred.getMaximumWidth() );
    }else {
      return label.getMinimumSize().width;
    }
  }

  /**
   * Set the vertical alignment of the label, using SwingConstants. Default
   * is CENTER.
   *
   * @param   pos     Alignment: TOP, CENTER or BOTTOM.
   */
  public void setVerticalAlignment( int pos ) {
    Border border;

    switch( pos ) {
      case SwingConstants.TOP:
        border = new EmptyBorder( 1,0,0,5 );
        break;

      case SwingConstants.BOTTOM:
        border = new EmptyBorder( 0,0,1,5 );
        break;

      default:
        border = new EmptyBorder( 0,0,0,5 );
    }

    label.setBorder( border );
    if( label instanceof JLabel ) {
      ((JLabel) label).setVerticalAlignment( pos );
    }
  }

  /**
   * Recursively set the minimum width of this JLabelGroup chain. This method
   * must be invoked on the <em>last</em> JLabelGroup of the chain. It is used
   * by <code>rearrange()</code>.
   *
   * @param   width       New minimum width
   */
  protected void setMinimumWidth( int width ) {
    Dimension dim = new Dimension( width, label.getMinimumSize().height );
    label.setMinimumSize( dim );
    label.setPreferredSize( dim );
    if( pred != null) pred.setMinimumWidth( width );
    invalidate();
  }

  /**
   * Rearrange the JLabelGroup chain. All labels in this chain are set to the
   * width of the broadest label. This method must be invoked on the <em>last</em>
   * JLabelGroup of a chain!
   * <p>
   * If further JLabelGroup objects are added later, this method must be invoked
   * again, on the new last element of the chain.
   * <p>
   * All JLabelGroups are regarded, even if they are currently invisible.
   */
  public void rearrange() {
    setMinimumWidth( getMaximumWidth() );
  }

  /**
   * Set a mnemonic key for this label.
   *
   * @param   key       Key to be used
   */
  public void setMnemonic( char key ) {
    if( label instanceof JLabel ) {
      ((JLabel) label).setDisplayedMnemonic( key );
    }
  }

  /**
   * Set a mnemonic code for this label.
   *
   * @param   code      Keycode to be used
   */
  public void setMnemonic( int code ) {
    if( label instanceof JLabel ) {
      ((JLabel) label).setDisplayedMnemonic( code );
    }
  }

}

/* jedit :mode=java:tabSize=2:noTabs=true:folding=java:maxLineLen=72: */
