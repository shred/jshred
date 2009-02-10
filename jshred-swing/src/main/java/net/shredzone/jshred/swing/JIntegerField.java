/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://jshred.shredzone.org-------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @version $Id: JIntegerField.java 256 2009-02-10 22:56:35Z shred $
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
