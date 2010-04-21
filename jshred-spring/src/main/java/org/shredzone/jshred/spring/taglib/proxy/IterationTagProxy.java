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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.IterationTag;

/**
 * A proxy that implements {@link IterationTag} and allows the target implementation to
 * use dependency injection.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 430 $
 */
public abstract class IterationTagProxy<T extends IterationTag> extends TagProxy<T> implements IterationTag {

    @Override
    public int doAfterBody() throws JspException {
        return getTargetBean().doAfterBody();
    }

}
