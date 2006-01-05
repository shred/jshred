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
import java.awt.Component;
import java.awt.HeadlessException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This is a JOptionPane which offers a CheckBox with a text like "Remember
 * this decision". If this CheckBox is checked, the JRememberOptionPane will
 * remember the answer and will give this answer immediately next time it is
 * invoked.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JRememberOptionPane.java,v 1.5 2005/02/01 11:18:29 shred Exp $
 * @since   R7
 */
public class JRememberOptionPane extends JOptionPane {
  private static final long serialVersionUID = 3544392526023898161L;
  private static Preferences prefs = Preferences.userNodeForPackage( JRememberOptionPane.class );

  /**
   * Show a Remember dialog with a remember checkbox.
   * This will be a QUESTION_MESSAGE dialog with OK_CANCEL_OPTION.
   * 
   * @param parentComponent   parent component to block
   * @param message           message to be shown
   * @param key               unique key for remembering
   * @param title             dialog title
   * @param remember          remember message to be shown
   * @return  The value selected (or remembered) by the user.
   * @throws HeadlessException
   */
  public static int showRememberDialog(
      Component parentComponent,
      Object message,
      String key,
      String title,
      String remember
  ) throws HeadlessException {
    return showRememberDialog(
        parentComponent, message, key, title, remember, 
        JOptionPane.OK_CANCEL_OPTION
    );
  }

  /**
   * Show a Remember dialog with a remember checkbox.
   * This will be a QUESTION_MESSAGE dialog.
   * 
   * @param parentComponent   parent component to block
   * @param message           message to be shown
   * @param key               unique key for remembering
   * @param title             dialog title
   * @param remember          remember message to be shown
   * @param optionType        option type
   * @return  The value selected (or remembered) by the user.
   * @throws HeadlessException
   */
  public static int showRememberDialog(
      Component parentComponent,
      Object message,
      String key,
      String title,
      String remember,
      int optionType
  ) throws HeadlessException {
    return showRememberDialog(
        parentComponent, message, key, title, remember, optionType,
        JOptionPane.QUESTION_MESSAGE, null
    );
  }

  /**
   * Show a Remember dialog with a remember checkbox. If the user checked
   * the checkbox before, to remember his decision, it will return immediately
   * that decision, without further user interaction.
   * <p>
   * Note that the key must be unique for all applications. It is
   * recommended to use the java package notation here (i.e. always start
   * the key with your reversed domain).
   * <p>
   * The CLOSED_OPTION and CANCEL_OPTION will never be remembered, so if
   * the user just closes or cancels the dialog, he will be asked again next
   * time, no matter whether he checked the checkbox or not.
   * 
   * @param parentComponent   parent component to block
   * @param message           message to be shown
   * @param key               unique key for remembering
   * @param title             dialog title
   * @param remember          remember message to be shown
   * @param optionType        option type
   * @param messageType       message type
   * @param icon              icon to be shown
   * @return  The value selected (or remembered) by the user.
   * @throws HeadlessException
   */
  public static int showRememberDialog(
      Component parentComponent,
      Object message,
      String key,
      String title,
      String remember,
      int optionType,
      int messageType,
      Icon icon
  ) throws HeadlessException {
    //--- Get the remembered result ---
    int result = prefs.getInt( key, -9999 );
    
    if( result == -9999 ) {
      //--- Nothing was remembered ---
      // Construct the input box
      JCheckBox jcbRemember = new JCheckBox( remember );
      JPanel jPane = new JPanel( new BorderLayout() );
      {
        Component msg;
        if( message instanceof Component ) {
          msg = (Component) message;
        }else if( message instanceof Icon ) {
          msg = new JLabel( (Icon) message );
        }else {
          JLabel jLabel = new JLabel( message.toString() );
          jLabel.setBorder( BorderFactory.createEmptyBorder(0,0,10,0) );
          msg = jLabel;
        }
        jPane.add( msg, BorderLayout.CENTER );
        jPane.add( jcbRemember, BorderLayout.SOUTH );
      }

      //--- Show the dialog ---
      result = showConfirmDialog(
          parentComponent,
          jPane,
          title,
          optionType,
          messageType,
          icon
      );
      
      //--- Remember the choice ---
      // CLOSED_OPTION and CANCEL_OPTION will not be remembered though
      if(    result!=JOptionPane.CLOSED_OPTION
          && result!=JOptionPane.CANCEL_OPTION
          && jcbRemember.isSelected() ) {
        prefs.putInt( key, result );
        
        //--- Flush the prefs ---
        // This seems to be required for some strange reasons... 
        try {
          prefs.flush();
        }catch( BackingStoreException e ) {}
      }
    }
    
    //--- Return the user's choice ---
    return result;
  }

  /**
   * Forget a certain key. The user will be asked again next time the
   * dialog is opened. Nothing will happen if there was no decision stored
   * for the key yet.
   * 
   * @param key       Key to forget.
   */
  public static void forget( String key ) {
    prefs.remove( key );
  }
  
  /**
   * Forget all the keys starting with the given base. The user will be asked
   * again next time the appropriate dialogs are opened. If no matching keys
   * were found, nothing will happen.
   * <p>
   * If you pass null as base, the user will be asked again for all
   * JRememberDialogs in <em>all</em> applications. It is strongly discouraged
   * to pass null!
   * 
   * @param   base      Base of the keys to be forgotten, null will forget all
   *    keys, even for other applications.
   * @since R10
   */
  public static void forgetAll( String base ) {
    try {
      if( base!=null ) {
        final String[] keys = prefs.keys();
        for( int ix=0; ix<keys.length; ix++ ) {
          if( keys[ix].startsWith(base) )
            prefs.remove( keys[ix] );
        }
      }else {
        prefs.clear();
      }
    }catch( BackingStoreException e ) {}
  }
 
}
