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

import javax.swing.*;

/**
 * This {@link DefaultListSelectionModel} also allows that no item can be
 * selected at all. Use this selection model if you want to disable a
 * {@link JList}, but keep the content readable. Besides allowing a fourth
 * selection mode (<code>NO_SELECTION</code>), it completely behaves like the
 * {@link DefaultListSelectionModel}.
 * 
 * @author Richard Körber &lt;dev@shredzone.de&gt;
 * @version $Id: NoListSelectionModel.java 243 2009-01-18 15:05:21Z shred $
 * @since R6
 * @see DefaultListSelectionModel
 */
public class NoListSelectionModel extends DefaultListSelectionModel {
    private static final long serialVersionUID = 3976735869820483376L;

    /**
     * A value for the selectionMode property: no list index can be selected at
     * all.
     */
    public static final int NO_SELECTION = 9181;

    private boolean noselect = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (!noselect) {
            super.setSelectionInterval(index0, index1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSelectionInterval(int index0, int index1) {
        if (!noselect) {
            super.addSelectionInterval(index0, index1);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertIndexInterval(int index, int length, boolean before) {
        if (!noselect) {
            super.insertIndexInterval(index, length, before);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionMode(int selectionMode) {
        if (selectionMode == NO_SELECTION) {
            super.setSelectionMode(SINGLE_SELECTION);
            clearSelection();
            noselect = true;
        } else {
            noselect = false;
            super.setSelectionMode(selectionMode);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectionMode() {
        if (noselect) {
            return NO_SELECTION;
        } else {
            return super.getSelectionMode();
        }
    }

}
