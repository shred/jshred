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
package org.shredzone.jshred.spring.taglib.proxy;

import javax.servlet.jsp.tagext.JspTag;

/**
 * This interface marks the Tag as proxied tag, and gives access to the
 * remote tag implementation.
 *
 * @author Richard Körber {@literal <dev@shredzone.de>}
 * @version $Id: ProxiedTag.java 436 2010-08-24 16:35:25Z shred $
 */
public interface ProxiedTag<T extends JspTag> {

    /**
     * Returns a reference to the implementing target bean.
     *
     * @return Target bean
     */
    T getTargetBean();

}
