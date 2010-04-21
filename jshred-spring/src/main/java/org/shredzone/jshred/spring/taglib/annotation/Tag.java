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
package org.shredzone.jshred.spring.taglib.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 * Annotates a Tag implementation class.
 * 
 * @author Richard Körber {@literal <dev@shredzone.de>}
 * @version $Id: Tag.java 430 2010-04-21 21:58:20Z shred $
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Tag {

    /**
     * Class type of the Tag.
     * <p>
     * Currently these types are supported:
     * <ul>
     *   <li>javax.servlet.jsp.tagext.Tag</li>
     *   <li>javax.servlet.jsp.tagext.IterationTag</li>
     *   <li>javax.servlet.jsp.tagext.BodyTag</li>
     *   <li>javax.servlet.jsp.tagext.SimpleTag</li>
     * </ul>
     */
    Class<? extends JspTag> type();

    /**
     * Name of the tag. Will be derived from the class name if none is given.
     */
    String name() default "";

    /**
     * Body content of the tag. Defaults to "JSP".
     */
    String bodycontent() default "JSP";

    /**
     * A custom spring bean name. Optional.
     */
    String bean() default "";
    
    /**
     * Does the tag class implements the {@link TryCatchFinally} implementation?
     */
    boolean tryCatchFinally() default false;

}
