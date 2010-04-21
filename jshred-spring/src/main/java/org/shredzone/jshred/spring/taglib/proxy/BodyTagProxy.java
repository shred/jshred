/**
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
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

/**
 * A proxy that implements {@link BodyTag} and allows the target implementation to use
 * dependency injection.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 430 $
 */
public abstract class BodyTagProxy<T extends BodyTag> extends IterationTagProxy<T> implements BodyTag {

    @Override
    public void doInitBody() throws JspException {
        getTargetBean().doInitBody();
    }

    @Override
    public void setBodyContent(BodyContent bodyContent) {
        getTargetBean().setBodyContent(bodyContent);
    }

}
