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
import javax.swing.text.*;
import java.awt.*;

/**
 * A {@link JTextField} that only allows to enter digits. I wonder why Sun
 * didn't supply this obligatory input field.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: JIntegerField.java 167 2008-07-10 14:59:00Z shred $
 * @since R2
 */
public class JIntegerField extends JTextField {
    private static final long serialVersionUID = 3834875767225332529L;
    private static final IntegerFilter filter = new IntegerFilter();

    /**
     * Create a new JIntegerField.
     */
    public JIntegerField() {
        PlainDocument doc = new PlainDocument();
        doc.setDocumentFilter(filter);
        setDocument(doc);
    }

    /**
     * Create a new JIntegerField and set an integer.
     * 
     * @param value
     *            Value to be set
     */
    public JIntegerField(int value) {
        this();
        setInteger(value);
    }

    /**
     * Set an integer
     * 
     * @param value
     *            Value to be set
     */
    public void setInteger(int value) {
        setText(String.valueOf(value));
    }

    /**
     * Get the current value, as integer.
     * 
     * @return integer
     */
    public int getInteger() {
        return Integer.parseInt(getText());
    }

/* -------------------------------------------------------------------- */
    /**
     * A DocumentFilter that only allows digits.
     */
    public static class IntegerFilter extends DocumentFilter {

        /**
         * String to be inserted into the Document. If it does not contain
         * digits only, the insertion will be refused and a beep will sound.
         * Everything matching <code>Character.isDigit()</code> will be accepted
         * as a valid digit.
         * 
         * @param fb
         *            FilterBypass
         * @param offset
         *            Insert offset
         * @param string
         *            String to be inserted
         * @param attr
         *            Attribute
         * @throws BadLocationException
         *             Bad position
         */
        @Override
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
        throws BadLocationException {
            int len = string.length();
            for (int ix = 0; ix < len; ix++) {
                if (!Character.isDigit(string.charAt(ix))) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            super.insertString(fb, offset, string, attr);
        }

        /**
         * String to be replaced with parts of the {@link Document}. If it does not
         * contain digits only, the insertion will be refused and a beep will
         * sound. Everything matching {@link Character#isDigit(char)} will be
         * accepted as a valid digit.
         * 
         * @param fb
         *            FilterBypass
         * @param offset
         *            Replacement offset
         * @param length
         *            Length to be replaced
         * @param string
         *            String to be replaced
         * @param attrs
         *            Attribute
         * @throws BadLocationException
         *             Bad position
         */
        @Override
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String string, AttributeSet attrs)
        throws BadLocationException {
            int len = string.length();
            for (int ix = 0; ix < len; ix++) {
                if (!Character.isDigit(string.charAt(ix))) {
                    Toolkit.getDefaultToolkit().beep();
                    return;
                }
            }
            super.replace(fb, offset, length, string, attrs);
        }
    }

}
