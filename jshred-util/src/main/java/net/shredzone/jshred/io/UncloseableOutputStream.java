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
package net.shredzone.jshred.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This is a {@link FilterOutputStream} where the close() method is disabled. Use this
 * class to pass an {@link OutputStream} to another method, to make sure that the method
 * is unable to close your stream unintentionally.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: UncloseableOutputStream.java 302 2009-05-12 22:19:11Z shred $
 * @since R13
 */
public class UncloseableOutputStream extends FilterOutputStream {

    /**
     * Creates a new UncloseableOutputStream.
     * 
     * @param out
     *            {@link OutputStream} to make sure it will not be closed.
     */
    public UncloseableOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Will not close the {@link OutputStream}. It will only be flushed.
     */
    @Override
    public void close() throws IOException {
        super.flush();
    }

}
