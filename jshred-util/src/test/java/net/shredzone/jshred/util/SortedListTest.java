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

package net.shredzone.jshred.util;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * A jUnit test case for SortedList.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @since R7
 * @version $Id: SortedListTest.java 243 2009-01-18 15:05:21Z shred $
 */
public class SortedListTest extends TestCase {

    /*
     * Class under test for boolean add(Object)
     */
    public void testAddObject() {
        SortedList<String> list = new SortedList<String>();
        list.add("Mike");
        list.add("Charlie");
        list.add("Alpha");
        list.add("Tango");
        list.add("Foxtrot");
        list.add("Zulu");
        list.add("Charlie");    // <-- a duplicate!

        Assert.assertEquals(list.size(), 6);

        Assert.assertEquals("Alpha", list.get(0));
        Assert.assertEquals("Charlie", list.get(1));
        Assert.assertEquals("Foxtrot", list.get(2));
        Assert.assertEquals("Mike", list.get(3));
        Assert.assertEquals("Tango", list.get(4));
        Assert.assertEquals("Zulu", list.get(5));
    }

    /*
     * Class under test for boolean contains(Object)
     */
    public void testContainsObject() {
        SortedList<String> list = new SortedList<String>();
        list.add("Mike");
        list.add("Charlie");
        list.add("Alpha");
        list.add("Tango");

        Assert.assertTrue(list.contains("Charlie"));
        Assert.assertFalse(list.contains("Bravo"));
    }

    /*
     * Class under test for int indexOf(Object)
     */
    public void testIndexOfObject() {
        SortedList<String> list = new SortedList<String>();
        list.add("Alpha");      // 0
        list.add("Charlie");    // 1
        list.add("Mike");       // 2
        list.add("Tango");      // 3

        Assert.assertEquals(list.indexOf("Charlie"), 1);
        Assert.assertEquals(list.indexOf("Tango"), 3);
        Assert.assertEquals(list.indexOf("Bravo"), -1);
    }

}
