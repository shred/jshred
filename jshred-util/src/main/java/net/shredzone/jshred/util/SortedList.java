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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A SortedList is a {@link List} which sorts its elements according to a certain order.
 * The natural sorting order of the elements is used unless a {@link Comparator} was given
 * in the constructor.
 * <p>
 * The SortedList does not allow {@code null} elements or duplicate elements. An
 * element is considered equal if {@link Object#equals(Object)} returns {@code true}.
 * <p>
 * Note that a SortedList is not synchronized! Use
 * {@link Collections#synchronizedList(java.util.List)} if you need synchronized access.
 *
 * @author Richard "Shred" Körber
 * @since R7
 */
public class SortedList<T> extends ArrayList<T> {
    private static final long serialVersionUID = 3257003263499972657L;
    private Comparator<T> comparator = null; // Comparator to be used

    /**
     * Creates an empty SortedList with natural order.
     */
    public SortedList() {}

    /**
     * Creates a new SortedList and initialize it with the given {@link Collection}. The
     * collection entries are inserted as they are found in the {@link Collection}, and
     * will be properly sorted in this list even if the {@link Collection} was unsorted.
     *
     * @param col
     *            {@link Collection} to initialize the SortedList with.
     */
    public SortedList(Collection<? extends T> col) {
        addAll(col);
    }

    /**
     * Creates a SortedList that uses the given {@link Comparator} instead of the
     * element's natural order. Note that once set, a {@link Comparator} cannot be changed
     * or removed.
     *
     * @param c
     *            Comparator to be used
     */
    public SortedList(Comparator<T> c) {
        comparator = c;
    }

    /**
     * Gets the {@link Comparator} used for comparison, or {@code null} if natural order
     * is to be used.
     *
     * @return Comparator or {@code null}
     */
    public Comparator<T> comparator() {
        return comparator;
    }

    /**
     * Adds an element to a certain index of this list. This method is not permitted in a
     * SortedList and thus throws an exception.
     *
     * @param index
     *            Index to add an element to
     * @param element
     *            Element to be added
     */
    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("not supported");
    }

    /**
     * Adds an element to the SortedList. The element will be sorted to its proper
     * position. If an equal element has already been added to this SortedList, it will be
     * replaced by this element. A {@code null} element is not permitted in a SortedList,
     * and will throw a {@link NullPointerException}.
     *
     * @param elem
     *            Element to be added.
     * @return Always true.
     * @throws ClassCastException
     * @see java.util.Collection#add(java.lang.Object)
     */
    @Override
    public boolean add(T elem) {
        // --- Assert an element ---
        if (elem == null) throw new NullPointerException("null is not permitted");

        // --- Find its position in the list ---
        int ix = approximate(elem);

        if (ix == size()) {
            // --- Add a new maximum element ---
            super.add(elem);

        } else if (compare(get(ix), elem) == 0) {
            // --- Replace an existing element ---
            super.set(ix, elem);

        } else {
            // --- Insert an element ---
            super.add(ix, elem);
        }

        return true;
    }

    /**
     * Adds all elements of a {@link Collection} to the SortedList, starting at a certain
     * index. This method is not permitted in a SortedList and thus throws an exception.
     *
     * @param index
     *            Starting index
     * @param c
     *            Collection to be added
     * @return true if this list was changed
     */
    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("not supported");
    }

    /**
     * Adds all elements of a {@link Collection} to the SortedList. The entries are
     * inserted as they are found in the {@link Collection}, and sorted to their proper
     * position.
     *
     * @param c
     *            Collection to be added.
     * @return true if this list was changed
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        for (T elem : c) {
            add(elem);
        }
        return true;
    }

    /**
     * This method is not supported by a SortedList.
     */
    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException("not supported");
    }

    /**
     * Checks if a SortedList contains an element. The list contains an element if the
     * given element's equals(Object) returns {@code true} to one of the elements in this
     * list.
     *
     * @param elem
     *            Element to be checked
     * @return {@code true} if the list contains the element
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object elem) {
        if (elem == null) throw new NullPointerException("null is not permitted");

        // ClassCastException is intentional
        int ix = approximate((T) elem);
        return (ix < size() && compare(get(ix), (T) elem) == 0);
    }

    /**
     * Gets the index of an element.
     *
     * @param elem
     *            Element to be found
     * @return index of that element, or -1 if it was not found
     */
    @Override
    @SuppressWarnings("unchecked")
    public int indexOf(Object elem) {
        if (elem == null) throw new NullPointerException("null is not permitted");

        // ClassCastException is intentional
        int ix = approximate((T) elem);
        if (ix < size() && compare(get(ix), (T) elem) == 0) return ix;
        else return -1;
    }

    /**
     * Gets the index of the last occurance of an element. Since there are no duplicate
     * entries in a SortedList, the result is the same as from {@link #indexOf(Object)}.
     *
     * @param elem
     *            Element to be found
     * @return index of that element, or -1 if it was not found
     */
    @Override
    public int lastIndexOf(Object elem) {
        return indexOf(elem);
    }

    /**
     * Approximates the position of the given element in the list. The element at the
     * returned index is either equal to the given element, or it is the next higher
     * element. This would be the insert index for the element passed in.
     * <p>
     * NOTE: The returned index will be size() if the given element is higher than all
     * current elements in the list.
     *
     * @param elem
     *            Element to approximate
     * @return Index of the approximation.
     * @throws ClassCastException
     */
    private int approximate(T elem) {
        // --- Empty list will always return 0 ---
        if (isEmpty()) return 0;

        // --- Check if o is higher than the last object ---
        int size = size();
        if (compare(get(size - 1), elem) < 0) return size;

        // --- Find the position ---
        int min = 0;
        int max = size - 1;
        int current = 0;
        int oldcurrent = -1;

        while (oldcurrent != current) {
            oldcurrent = current;
            current = (min + max) / 2;
            T cmp = get(current);

            int result = compare(cmp, elem);
            if (result < 0) {
                // cmp is less than o
                min = current;

            } else if (result > 0) {
                // cmp is greater than o
                max = current;

            } else {
                // o equals cmp
                break;
            }
        }

        if (compare(get(current), elem) < 0) current += 1;

        return current;
    }

    /**
     * Compares two elements. If a comparator was set, it is used for comparison. If none
     * was set, the natural order of the objects will be compared.
     *
     * @param cmp
     *            Object 1
     * @param cmp2
     *            Object 2
     * @return negative if cmp is less than cmp2, positive is cmp is greater than cmp2,
     *         zero if cmp is equal to cmp2.
     * @throws ClassCastException
     *             if the two objects were not comparable.
     */
    @SuppressWarnings("unchecked")
    private int compare(T cmp, T cmp2) {
        if (comparator != null) {
            return comparator.compare(cmp, cmp2);
        } else {
            // A ClassCastException is intentional
            Comparable<T> co = (Comparable<T>) cmp; // provoke a ClassCastException
            return co.compareTo(cmp2); // if cmp is not a Comparable
        }
    }

}
