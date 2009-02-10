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

import javax.swing.text.*;

/**
 * This {@link PlainDocument} will limit the input to a certain length.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: LimitedDocument.java 256 2009-02-10 22:56:35Z shred $
 */
public class LimitedDocument extends PlainDocument {
    private static final long serialVersionUID = 3258131358084904248L;
    private int maxLength = -1; // Maximum length

    /**
     * Creates a new {@link Document} with the given maximum length.
     * 
     * @param len
     *            Maximum length in characters
     */
    public LimitedDocument(int len) {
        super();
        maxLength = len;
    }

    /**
     * Overwrites {@link PlainDocument#insertString(int, String, AttributeSet)}
     * to check the input.
     * 
     * @param offs
     *            Offset
     * @param str
     *            String
     * @param a
     *            AttributeSet
     */
    @Override
    public void insertString(int offs, String str, AttributeSet a)
    throws BadLocationException {
        if (str == null)
            return;

        if ((maxLength == -1) || ((getLength() + str.length()) <= maxLength)) {
            // --- Maximum length was not reached ---
            super.insertString(offs, str, a);
        } else {
            // --- Maximum length was reached, truncate! ---
            super.insertString(offs, str.substring(0, maxLength - getLength()), a);
        }
    }
}
