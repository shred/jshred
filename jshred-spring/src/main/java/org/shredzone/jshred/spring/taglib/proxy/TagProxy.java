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
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 * A proxy that implements {@link Tag} and allows the target implementation to
 * use dependency injection.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 430 $
 */
public abstract class TagProxy<T extends Tag> extends AbstractTagProxy<T> implements Tag {

    @Override
    public void setPageContext(PageContext pageContext) {
        initTargetBean(pageContext);
        getTargetBean().setPageContext(pageContext);
    }
    
    @Override
    public int doEndTag() throws JspException {
        return getTargetBean().doEndTag();
    }

    @Override
    public int doStartTag() throws JspException {
        return getTargetBean().doStartTag();
    }

    @Override
    public Tag getParent() {
        return getTargetBean().getParent();
    }

    @Override
    public void release() {
        getTargetBean().release();
    }

    @Override
    public void setParent(Tag t) {
        getTargetBean().setParent(t);
    }

    /**
     * Handles {@link TryCatchFinally} implementations.
     */
    public void doCatch(Throwable t)
    throws Throwable {
        T target = getTargetBean();
        if (target instanceof TryCatchFinally) {
            TryCatchFinally tcf = (TryCatchFinally) target;
            tcf.doCatch(t);
        } else {
            throw t;
        }
    }
    
    /**
     * Handles {@link TryCatchFinally} implementations.
     */
    public void doFinally() {
        T target = getTargetBean();
        if (target instanceof TryCatchFinally) {
            TryCatchFinally tcf = (TryCatchFinally) target;
            tcf.doFinally();
        }
    }

}
