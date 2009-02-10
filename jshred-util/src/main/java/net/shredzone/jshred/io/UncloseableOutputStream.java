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

package net.shredzone.jshred.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This is a {@link FilterOutputStream} where the close() method is disabled.
 * Use this class to pass an {@link OutputStream} to another method, to make
 * sure that the method is unable to close your stream unintentionally.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: UncloseableOutputStream.java 256 2009-02-10 22:56:35Z shred $
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
