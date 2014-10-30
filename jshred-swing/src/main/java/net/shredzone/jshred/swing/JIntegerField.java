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

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

/**
 * A {@link JTextField} that only allows to enter digits. I wonder why Sun didn't supply
 * this obligatory input field.
 *
 * @author Richard "Shred" Körber
 * @since R2
 */
public class JIntegerField extends JTextField {
    private static final long serialVersionUID = 3834875767225332529L;
    private static final IntegerFilter filter = new IntegerFilter();

    /**
     * Creates a new {@link JIntegerField}.
     */
    public JIntegerField() {
        PlainDocument doc = new PlainDocument();
        doc.setDocumentFilter(filter);
        setDocument(doc);
    }

    /**
     * Creates a new {@link JIntegerField} and set an integer.
     *
     * @param value
     *            Value to be set
     */
    public JIntegerField(int value) {
        this();
        setInteger(value);
    }

    /**
     * Sets an integer
     *
     * @param value
     *            Value to be set
     */
    public void setInteger(int value) {
        setText(String.valueOf(value));
    }

    /**
     * Gets the current value, as integer.
     *
     * @return integer
     */
    public int getInteger() {
        return Integer.parseInt(getText());
    }

    /**
     * A {@link DocumentFilter} that only allows digits.
     */
    public static class IntegerFilter extends DocumentFilter {

        /**
         * String to be inserted into the Document. If it does not contain digits only,
         * the insertion will be refused and a beep will sound. Everything matching
         * <code>Character.isDigit()</code> will be accepted as a valid digit.
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
        public void insertString(DocumentFilter.FilterBypass fb, int offset, String string,
            AttributeSet attr)
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
         * contain digits only, the insertion will be refused and a beep will sound.
         * Everything matching {@link Character#isDigit(char)} will be accepted as a valid
         * digit.
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
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
            String string, AttributeSet attrs)
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
