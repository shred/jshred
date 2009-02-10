/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2009 Richard "Shred" Körber
 *   http://jshred.shredzone.org
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

package org.shredzone.jshred.web;

/**
 * A helper class that returns an endless sequence of the given item set.
 * This is useful in JSPs, e.g. for rendering each row of a table in a
 * different color.
 * <p>
 * Example:
 * <pre>
 * &lt;% pageContext.setAttribute("sequence", new Sequencer("oddrow", "evenrow")); %>
 * &lt;c:forEach var="entry" items="${entryList}">
 *   &lt;tr class="${sequence.next}">
 *     &lt;td>&lt;c:out value="${entry.name}"/>&lt;/td>
 *   &lt;/tr>
 * &lt;/c:forEach>
 * </pre>
 * 
 * @author Richard "Shred" Körber
 * @version $Revision: 257 $
 */
public class Sequencer {
    private int pos = 0;
    private final String[] sequence;

    /**
     * Creates a new Sequencer with the given sequence.
     * 
     * @param sequence  The sequence of strings to be used. Must have at least
     *      one entry.
     */
    public Sequencer(String... sequence) {
        if (sequence.length == 0)
            throw new IllegalArgumentException("At least one item is required!");

        this.sequence = sequence;
        this.pos = 0;
    }

    /**
     * Gets the next entry of the sequence. If the last entry was returned,
     * it will start again with the first entry.
     * 
     * @return  Sequence entry
     */
    public String getNext() {
        if (pos >= sequence.length) pos = 0;
        return sequence[pos++];
    }
        
}
