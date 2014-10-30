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
package net.shredzone.jshred.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * A jUnit test case for {@link SortedList}.
 *
 * @author Richard "Shred" Körber
 * @since R7
 */
public class SortedListTest {

    @Test
    public void testAddObject() {
        SortedList<String> list = new SortedList<String>();
        list.add("Mike");
        list.add("Charlie");
        list.add("Alpha");
        list.add("Tango");
        list.add("Foxtrot");
        list.add("Zulu");
        list.add("Charlie"); // <-- a duplicate!

        Assert.assertEquals(list.size(), 6);

        Assert.assertEquals("Alpha", list.get(0));
        Assert.assertEquals("Charlie", list.get(1));
        Assert.assertEquals("Foxtrot", list.get(2));
        Assert.assertEquals("Mike", list.get(3));
        Assert.assertEquals("Tango", list.get(4));
        Assert.assertEquals("Zulu", list.get(5));
    }

    @Test
    public void testContainsObject() {
        SortedList<String> list = new SortedList<String>();
        list.add("Mike");
        list.add("Charlie");
        list.add("Alpha");
        list.add("Tango");

        Assert.assertTrue(list.contains("Charlie"));
        Assert.assertFalse(list.contains("Bravo"));
    }

    @Test
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
