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

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTag;

/**
 * A proxy that implements {@link SimpleTag} and allows the target implementation to
 * use dependency injection.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 430 $
 */
public abstract class SimpleTagProxy<T extends SimpleTag> extends AbstractTagProxy<T> implements SimpleTag {

    @Override
    public void doTag() throws JspException, IOException {
        getTargetBean().getParent();
    }
    
    @Override
    public JspTag getParent() {
        return getTargetBean().getParent();
    }

    @Override
    public void setJspBody(JspFragment jspBody) {
        getTargetBean().setJspBody(jspBody);
    }
    
    @Override
    public void setJspContext(JspContext pc) {
        initTargetBean(pc);
        getTargetBean().setJspContext(pc);
    }

    @Override
    public void setParent(JspTag parent) {
        getTargetBean().setParent(parent);
    }

}