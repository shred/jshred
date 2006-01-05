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

/**
 * This is a headline to be used in dialogs etc. It shows a big title to the
 * left, and optionally a nice icon to the right and a description below the
 * title. The headline is colored in a gradient, starting in a certain color
 * to the left, and going to the current background color to the right.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JHeadline.java,v 1.2 2005/01/11 19:37:41 shred Exp $
 * @since   R8
 */
public class JHeadline extends JGradientPanel {
  private static final long serialVersionUID = 3618137866132074806L;
  private final JLabel jlTitle;
  private final JLabel jlDesc;
  private final JLabel jlIcon;
  private boolean init = false;

  /**
   * Create an empty JHeadline.
   * Color.GRAY is used as default color. 
   */
  public JHeadline() {
    this( "" );
  }

  /**
   * Create a new JHeadline with the given title.
   * Color.GRAY is used as default color. 
   *
   * @param   title       Title to be used
   */
  public JHeadline( String title ) {
    this( title, null );
  }

  /**
   * Create a new JHeadline with the given title and icon.
   * Color.GRAY is used as default color. 
   *
   * @param   title       Title to be used
   * @param   icon        Icon or null
   */
  public JHeadline( String title, Icon icon ) {
    this( title, null, icon );
  }

  /**
   * Create a new JHeadline with the given title, description, icon.
   * Color.GRAY is used as default color. 
   *
   * @param   title       Title to be used
   * @param   desc        Description or null
   * @param   icon        Icon or null
   */
  public JHeadline( String title, String desc, Icon icon ) {
    this( title, desc, icon, Color.GRAY );
  }

  /**
   * Create a new JHeadline with the given title, description, icon and color.
   *
   * @param   title       Title to be used
   * @param   desc        Description or null
   * @param   icon        Icon or null
   * @param   color       Left gradient color
   */
  public JHeadline( String title, String desc, Icon icon, Color color ) {
    super( color, null, JGradientPanel.HORIZONTAL );
    setLayout( new BorderLayout() );

    jlTitle = new JLabel( title );
    jlTitle.setFont( jlTitle.getFont().deriveFont( 20.0f ) );
    
    jlDesc = new JLabel( "" );
    jlDesc.setBorder( BorderFactory.createEmptyBorder(0,25,0,0) );
    setDescription( desc );
    
    jlIcon = new JLabel( "" );
    setIcon( icon );
    
    JPanel jpLeft = new JPanel();
    jpLeft.setOpaque( false );
    jpLeft.setLayout( new BoxLayout( jpLeft, BoxLayout.Y_AXIS ) );
    {
      jpLeft.add( Box.createVerticalGlue() );
      jpLeft.add( jlTitle );
      jpLeft.add( jlDesc );
    }
    add( jpLeft, BorderLayout.WEST );
    add( jlIcon, BorderLayout.EAST );

    init = true;
    setForeground( Color.WHITE );
    
    setBorder( BorderFactory.createEmptyBorder(2,4,2,4) );
  }
  
  /**
   * Set a new title text.
   * 
   * @param title   new title text, must not be null.
   */
  public void setTitle( String title ) {
    jlTitle.setText( title );
  }
  
  /**
   * Get the current title text.
   * 
   * @return    Current title text.
   */
  public String getTitle() {
    return jlTitle.getText();
  }
  
  /**
   * Set a new description.
   * 
   * @param desc    new description, null if there is none.
   */
  public void setDescription( String desc ) {
    if( desc!=null ) {
      jlDesc.setText( desc );
      jlDesc.setVisible( true );
    }else {
      jlDesc.setVisible( false );
    }
  }
  
  /**
   * Get the current description.
   * 
   * @return  Current description, or null if there is none.
   */
  public String getDescription() {
    if( jlDesc.isVisible() )
      return jlDesc.getText();
    else
      return null;
  }

  /**
   * Set a new icon.
   * 
   * @param icon    new icon, null if there is none
   */
  public void setIcon( Icon icon ) {
    if( icon!=null ) {
      jlIcon.setIcon( icon );
      jlIcon.setVisible( true );
    }else {
      jlIcon.setVisible( false );
    }
  }

  /**
   * Get the current icon
   * 
   * @return  Current icon, or null if there is none.
   */
  public Icon getIcon() {
    if( jlIcon.isVisible() )
      return jlIcon.getIcon();
    else
      return null;
  }
  
  /**
   * Set the text color. The title will be shown in this color. The description
   * will be shown in the same color, but with an alpha value of 200, so the
   * background gradient will shine through a little.
   * 
   * @param   fg        New foreground color
   */
  public void setForeground( Color fg ) {
    super.setForeground( fg );
    if( init ) {
      jlTitle.setForeground( fg );
      jlDesc.setForeground( new Color( fg.getRed(), fg.getGreen(), fg.getBlue(), 200 ) );
    }
  }
  
}
