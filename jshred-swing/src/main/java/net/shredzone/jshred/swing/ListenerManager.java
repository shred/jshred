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

package net.shredzone.jshred.swing;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A ListenerManager helps you keep track of {@link EventListener} lists.
 * The speciality of this manager is to store the listeners weakly, so you
 * won't need to remove the listeners before you destroy the receiver object.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: ListenerManager.java 243 2009-01-18 15:05:21Z shred $
 * @since R15
 */
public class ListenerManager<T extends EventListener> {
    private Set<WeakReference<T>> sListener = new HashSet<WeakReference<T>>();
    
    /**
     * Adds a listener to this manager. If the listener was already added,
     * nothing will happen.
     * 
     * @param listener
     *          Listener to be added
     */
    public void addListener(T listener) {
        for (WeakReference<T> wr : sListener) {
            if (wr.get() == listener) return;
        }
        sListener.add(new WeakReference<T>(listener));
    }

    /**
     * Removes the listener from this manager. If the listener was not added,
     * nothing will happen.
     * 
     * @param listener
     *          Listener to be removed
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
     * Gets a {@link Collection} of all current listeners. This collection
     * is <em>for immediate use only</em> and <em>must not be stored</em>. If
     * a listener is removed after this method was invoked, it will still be
     * present in the result collection. Also, storing the result collection
     * will keep a reference to the listeners and will render the weak
     * reference mechanism useless.
     * <p>
     * Example for proper usage:
     * <pre>
     *   for (ActionListener listener : manager.getListeners()) {
     *     listener.actionPerformed(event);
     *   }
     * </pre>
     * <p>
     * The listeners are returned in no special order.
     * 
     * @return  {@link Collection} of all current listeners.
     */
    public Collection<T> getListeners() {
        Set<T> result = new HashSet<T>();
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
