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
package org.shredzone.jshred.spring.taglib.processor;

/**
 * A bean that stores the parameters of a tag attribute. This bean is immutable once
 * it is created.
 * 
 * @author Richard Körber {@literal <dev@shredzone.de>}
 * @version $Id: AttributeBean.java 430 2010-04-21 21:58:20Z shred $
 */
public class AttributeBean implements Comparable<AttributeBean> {
    
    private final String name;
    private final String type;
    private final boolean required;
    private final boolean rtexprvalue;

    /**
     * Creates and initializes a new {@link AttributeBean}.
     * 
     * @param name
     *            Attribute name
     * @param type
     *            Attribute type
     * @param required
     *            {@code true}: The attribute is required
     * @param rtexprvalue
     *            {@code true} The attribute is a rtexpression value
     */
    public AttributeBean(String name, String type, boolean required, boolean rtexprvalue) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.rtexprvalue = rtexprvalue;
    }
    
    public String getName()                 { return name; }
    
    public String getType()                 { return type; }
    
    public boolean isRequired()             { return required; }
    
    public boolean isRtexprvalue()          { return rtexprvalue; }

    /**
     * {@inheritDoc}
     * <p>
     * Two {@link AttributeBean} are considered equal if they have an equal name.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AttributeBean)) return false;
        return ((AttributeBean) obj).getName().equals(name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public int compareTo(AttributeBean o) {
        return name.compareTo(o.name);
    }

}
