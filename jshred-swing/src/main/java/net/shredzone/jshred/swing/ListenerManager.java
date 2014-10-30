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
package net.shredzone.jshred.swing;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A {@link ListenerManager} helps you keep track of {@link EventListener} lists. The
 * speciality of this manager is to store the listeners weakly, so you won't need to
 * remove the listeners before you destroy the receiver object.
 *
 * @author Richard "Shred" Körber
 * @since R15
 */
public class ListenerManager<T extends EventListener> {
    private Set<WeakReference<T>> sListener = new HashSet<>();

    /**
     * Adds a listener to this manager. If the listener was already added, nothing will
     * happen.
     *
     * @param listener
     *            Listener to be added
     */
    public void addListener(T listener) {
        for (WeakReference<T> wr : sListener) {
            if (wr.get() == listener) return;
        }
        sListener.add(new WeakReference<T>(listener));
    }

    /**
     * Removes the listener from this manager. If the listener was not added, nothing will
     * happen.
     *
     * @param listener
     *            Listener to be removed
     */
    public void removeListener(T listener) {
        Iterator<WeakReference<T>> it = sListener.iterator();
        while (it.hasNext()) {
            WeakReference<T> wr = it.next();
            T wl = wr.get();
            if (wl == null || wl == listener) {
                it.remove();
            }
        }
    }

    /**
     * Gets a {@link Collection} of all current listeners. This collection is
     * <em>for immediate use only</em> and <em>must not be stored</em>. If a listener is
     * removed after this method was invoked, it will still be present in the result
     * collection. Also, storing the result collection will keep a reference to the
     * listeners and will render the weak reference mechanism useless.
     * <p>
     * Example for proper usage:
     *
     * <pre>
     * for (ActionListener listener : manager.getListeners()) {
     *     listener.actionPerformed(event);
     * }
     * </pre>
     * <p>
     * The listeners are returned in no special order.
     *
     * @return {@link Collection} of all current listeners.
     */
    public Collection<T> getListeners() {
        Set<T> result = new HashSet<>();
        Iterator<WeakReference<T>> it = sListener.iterator();
        while (it.hasNext()) {
            WeakReference<T> wr = it.next();
            T wl = wr.get();
            if (wl != null) {
                result.add(wl);
            } else {
                it.remove();
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    /**
     * Removes all listeners from this manager.
     */
    public void removeAllListeners() {
        sListener.clear();
    }

}
