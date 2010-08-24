/**
 * jshred - Shred's Toolbox
 *
 * Copyright (C) 2010 Richard "Shred" Körber
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
package org.shredzone.jshred.spring.taglib;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import org.shredzone.jshred.spring.taglib.proxy.ProxiedTag;

/**
 * Utility class for taglib beans.
 * 
 * @author Richard Körber {@literal <dev@shredzone.de>}
 * @version $Id: TaglibUtils.java 437 2010-08-24 21:23:35Z shred $
 */
public final class TaglibUtils {

    private TaglibUtils() {}

    /**
     * Finds an ancestor tag of the given type. Starting from the given Tag, it traverses
     * up the parent tags until a tag of the given type is found.
     * <p>
     * Use this method instead of {@link TagSupport#findAncestorWithClass(Tag, Class)}, as
     * it is aware of proxied tag classes, while findAncestorWithClass only sees the
     * proxy instances instead of the tag classes behind it. Furthermore, this method is
     * also able to locate interfaces.
     * 
     * @param <T>
     *            Type to find and return
     * @param from
     *            Tag to start from
     * @param type
     *            Type to find
     * @return Ancestor of that type, or {@code null} if none was found.
     */
    @SuppressWarnings("unchecked")
    public static <T> T findAncestorWithType(Tag from, Class<T> type) {
        Tag parent = from.getParent();
        while (parent != null) {
            if (parent instanceof ProxiedTag) {
                parent = ((ProxiedTag<Tag>) parent).getTargetBean();
            }
            if (type.isAssignableFrom(parent.getClass())) {
                return (T) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

}
