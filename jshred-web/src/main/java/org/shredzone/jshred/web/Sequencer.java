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
package org.shredzone.jshred.web;

/**
 * A helper class that returns an endless sequence of the given item set. This is useful
 * in JSPs, e.g. for rendering each row of a table in a different color.
 * <p>
 * Example:
 * 
 * <pre>
 * &lt;% pageContext.setAttribute(&quot;sequence&quot;, new Sequencer(&quot;oddrow&quot;, &quot;evenrow&quot;)); %&gt;
 * &lt;c:forEach var=&quot;entry&quot; items=&quot;${entryList}&quot;&gt;
 *   &lt;tr class=&quot;${sequence.next}&quot;&gt;
 *     &lt;td&gt;&lt;c:out value=&quot;${entry.name}&quot;/&gt;&lt;/td&gt;
 *   &lt;/tr&gt;
 * &lt;/c:forEach&gt;
 * </pre>
 * 
 * @author Richard "Shred" Körber
 * @version $Revision: 302 $
 */
public class Sequencer {
    private int pos = 0;
    private final String[] sequence;

    /**
     * Creates a new Sequencer with the given sequence.
     * 
     * @param sequence
     *            The sequence of strings to be used. Must have at least one entry.
     */
    public Sequencer(String... sequence) {
        if (sequence.length == 0)
            throw new IllegalArgumentException("At least one item is required!");

        this.sequence = sequence;
        this.pos = 0;
    }

    /**
     * Gets the next entry of the sequence. If the last entry was returned, it will start
     * again with the first entry.
     * 
     * @return Sequence entry
     */
    public String getNext() {
        if (pos >= sequence.length) pos = 0;
        return sequence[pos++];
    }

}
