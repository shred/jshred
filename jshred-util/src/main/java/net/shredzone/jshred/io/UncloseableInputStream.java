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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a {@link FilterInputStream} where the close() method is disabled. Use this
 * class to pass an {@link InputStream} to another method, to make sure that the method is
 * unable to close your stream unintentionally.
 *
 * @author Richard "Shred" Körber
 * @since R13
 */
public class UncloseableInputStream extends FilterInputStream {

    /**
     * Creates a new UncloseableInputStream.
     *
     * @param in
     *            {@link InputStream} which shall not be closed.
     */
    public UncloseableInputStream(InputStream in) {
        super(in);
    }

    /**
     * Does not close the {@link InputStream}.
     */
    @Override
    public void close() throws IOException {
        // do nothing...
    }

}
