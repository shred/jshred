/*
 * jshred -- Shred's Toolbox
 *
 * Copyright (c) 2004 Richard "Shred" Körber
 *   http://www.shredzone.net/go/jshred
 *
 *-----------------------------------------------------------------------
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is JSHRED.
 *
 * The Initial Developer of the Original Code is
 * Richard "Shred" Körber.
 * Portions created by the Initial Developer are Copyright (C) 2004
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK *****
 */

package net.shredzone.jshred.util;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * A jUnit test case for SortedList.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @since R7
 * @version $Id: SortedListTest.java 167 2008-07-10 14:59:00Z shred $
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
