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
 * @version $Id: JGradientPanel.java 75 2006-02-10 08:17:27Z shred $
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
