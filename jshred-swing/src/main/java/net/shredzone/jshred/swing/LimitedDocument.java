/**
 * jshred - Shred's Toolbox
 *
 * Copyright (C) 2009 Richard "Shred" Körber
 *   http://jshred.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License / GNU Lesser
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package net.shredzone.jshred.swing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * This {@link PlainDocument} will limit the input to a certain length.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: LimitedDocument.java 389 2009-11-11 23:47:30Z shred $
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
     * Overwrites {@link PlainDocument#insertString(int, String, AttributeSet)} to check
     * the input.
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
        if (str == null) return;

        if ((maxLength == -1) || ((getLength() + str.length()) <= maxLength)) {
            // --- Maximum length was not reached ---
            super.insertString(offs, str, a);
        } else {
            // --- Maximum length was reached, truncate! ---
            super.insertString(offs, str.substring(0, maxLength - getLength()), a);
        }
    }
}
