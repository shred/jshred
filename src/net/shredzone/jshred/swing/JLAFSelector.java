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

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 * This component allows the user to select a Look and Feel.
 * <p>
 * Currently this is just a JComboBox which allows to select a Look and Feel
 * from the system's list of Look and Feels. Future releases will also add
 * another JComboBox for different auxiliary looks, and a preview area. If you
 * only have limited space available in your GUI, you are advised to invoke
 * <code>setSmallArea(true)</code> for future compatibility.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JLAFSelector.java 118 2006-12-20 14:45:37Z shred $
 * @since   R8
 */
public class JLAFSelector extends JPanel {
  private static final long serialVersionUID = 3689916188578691125L;
  private final JComboBox jcbSelector;
  private final static Map mLAFs = new HashMap();
  private boolean small = false;

  static {
    //--- Get all System LAFs ---
    final UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
    for( int ix=0; ix<lafs.length; ix++ ) {
      mLAFs.put( lafs[ix].getClassName(), lafs[ix].getName() );
    }
    
    //--- Add Kunststoff if available ---
    attemptAdd( "com.incors.plaf.kunststoff.KunststoffLookAndFeel", "Kunststoff" );

    //--- Add Napkin if available ---
    attemptAdd( "net.sourceforge.napkinlaf.NapkinLookAndFeel", "Napkin Style" );
    
    //--- Add JGoodies if available ---
    attemptAdd( "com.jgoodies.looks.windows.WindowsLookAndFeel", "JGoodies Windows");
    attemptAdd( "com.jgoodies.looks.plastic.PlasticLookAndFeel", "JGoodies Plastic");
    attemptAdd( "com.jgoodies.looks.plastic.Plastic3DLookAndFeel", "JGoodies Plastic 3D");
    attemptAdd( "com.jgoodies.looks.plastic.PlasticXPLookAndFeel", "JGoodies Plastic XP");
  }

  /**
   * Tries to add a LAF class to the selector. If the class is not available,
   * nothing will happen.
   * 
   * @param classname   LAF class name
   * @param label       Human readable name to be added if the class is available
   * @since R10
   */
  private static void attemptAdd( String classname, String label ) {
    try {
      Class clazz = Class.forName( classname );
      if( clazz!=null ) {
        mLAFs.put( classname, label );
      }
    }catch( ClassNotFoundException e ) {}
  }

  
  
  /**
   * Create a new Look and Feel selector.
   */
  public JLAFSelector() {
    setLayout( new BorderLayout() );
    List lNames = new ArrayList( mLAFs.values() );
    Collections.sort( lNames );
    jcbSelector = new JComboBox( lNames.toArray() );
    jcbSelector.setEditable( true );
    add( jcbSelector, BorderLayout.CENTER );
    setCurrentLAF();
  }
  
  /**
   * Set the currently used Look and Feel.
   */
  public void setCurrentLAF() {
    setSelectedLAF( UIManager.getLookAndFeel() );
  }
  
  /**
   * Set a Look and Feel as current selection, by passing a LookAndFeel object.
   * 
   * @param laf     LookAndFeel object to set.
   */
  public void setSelectedLAF( LookAndFeel laf ) {
    setSelectedLAF( laf.getClass().getName() );
  }
  
  /**
   * Set a Look and Feel as current selection, by passing a LookAndFeelInfo.
   * 
   * @param lafinfo   LookAndFeelInfo object to set.
   */
  public void setSelectedLAF( UIManager.LookAndFeelInfo lafinfo ) {
    setSelectedLAF( lafinfo.getClassName() );
  }
  
  /**
   * Set a Look and Feel as current selection, by passing a class name. The
   * class name does not necessarily need to exist or be a valid LookAndFeel
   * class. Alternatively you can also pass the name of a LookAndFeel.
   * 
   * @param classname   LookAndFeel class name to set.
   */
  public void setSelectedLAF( String classname ) {
    //--- Look and Feel name ---
    // Try to convert a look and feel name into a human readable name.
    final String hrName = (String) mLAFs.get( classname );
    if( hrName!=null ) {
      // Locate the index of this entry
      final int cnt = jcbSelector.getItemCount();
      for( int ix=0; ix<cnt; ix++ ) {
        final String cmpName = (String) jcbSelector.getItemAt( ix );
        if( cmpName.equals(hrName) ) {
          jcbSelector.setSelectedIndex( ix );
          return;
        }
      }
    }
    
    //--- Just set the class name ---
    jcbSelector.setSelectedItem( classname );
  }
  
  /**
   * Get the name of the selected LAF. This may be a class name or a human
   * readable look and feel name.
   * <p>
   * <em>NOTE:</em> the returned value can be any string. There is <em>no</em>
   * guarantee that the returned string
   * <ul>
   *   <li>is a valid LookAndFeel class,</li>
   *   <li>is a LookAndFeel that is available on this system,</li>
   *   <li>is a LookAndFeel that is permitted on this system, or</li>
   *   <li>is a valid class name at all.</li>
   * </ul>
   * 
   * @return    The selected Look and Feel.
   */
  public String getSelectedLAF() {
    return jcbSelector.getSelectedItem().toString();
  }
  
  /**
   * Set if a small view area is to be used by this component. Currently only
   * one JComboBox is used for this component, but it is planned that a future
   * release of the JLAFSelector will consist of two JComboBox and an optional
   * preview area.
   * <p>
   * If you do not have plenty of space in your GUI, you should invoke
   * <code>setSmallView(true)</code>, to hide the preview area (and maybe some
   * other components that are not really necessary).
   * <p>
   * Currently this method does nothing, since there is no preview area yet.
   * 
   * @param small
   */
  public void setSmallView( boolean small ) {
    this.small = small;
  }
  
  /**
   * Check if there is only a small view area available for this component.
   * This is false by default.
   * 
   * @return    true: only a small area is available
   */
  public boolean isSmallView() {
    return small;
  }
  
  /**
   * Try to set a look and feel that was selected by this component. This is a
   * convenience method that tries to set the passed look and feel, which can
   * be either a human readable name or a class name. If this method is unable
   * to set this look and feel, the default look and feel is used.
   * 
   * @param   laf   Look and Feel string to be used. May be null.
   * @return  The class name of the actual Look and Feel that has been set.
   *          If no Look and Feel could be set, null will be returned (which
   *          should never happen -- famous last words).
   */
  public static String setLookAndFeel( String laf ) {
    //--- Convert human readable ---
    // Convert a human readable look and feel name into a class name.
    if( laf!=null && mLAFs.containsValue( laf ) ) {
      Iterator it = mLAFs.keySet().iterator();
      while( it.hasNext() ) {
        final String clazz = (String) it.next();
        if( mLAFs.get( clazz ).equals( laf ) ) {
          laf = clazz;
          break;
        }
      }
    }
      
    //--- Try to set this look and feel ---
    try {
      UIManager.setLookAndFeel( laf );
    }catch( Throwable t ) {
      // We were unable to set the user's desired Look and Feel.
      // Now find out what the System's Look and Feel is.
      laf = UIManager.getSystemLookAndFeelClassName();
      
      // The Motif Look and Feel sucks... (sorry, guys, but yes
      // I really think so). If the System's Look and Feel is Motif,
      // we will use the Cross Platform Look and Feel instead, which is
      // usually the standard Metal look.
      if( laf.equals("com.sun.java.swing.plaf.motif.MotifLookAndFeel") ) {
        laf = UIManager.getCrossPlatformLookAndFeelClassName();
      }
      
      // Now try again to set the look and feel
      try {
        UIManager.setLookAndFeel( laf );
      }catch(Throwable t2) {
        // We were even unable to set the System or CrossPlatform
        // Look and Feel. If we have reached this point, something
        // has really badly gone wrong. Just keep the current LAF
        // (whatever it is) and return null.
        laf = null;
      }
    }
    
    //--- Return the LAF that has been set ---
    return laf;
  }

}
