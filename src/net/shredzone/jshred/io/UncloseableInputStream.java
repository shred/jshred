/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2008 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *-----------------------------------------------------------------------
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is a {@link FilterInputStream} where the close() method is disabled.
 * Use this class to pass an {@link InputStream} to another method, to make sure
 * that the method is unable to close your stream unintentionally.
 *
 * @author  Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: UncloseableInputStream.java 169 2008-07-10 22:01:03Z shred $
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
     * Will not close the {@link InputStream}.
     */
    @Override
	public void close() throws IOException {
    }

}
