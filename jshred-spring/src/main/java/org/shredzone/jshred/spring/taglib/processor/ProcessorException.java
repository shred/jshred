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
 * This exception is thrown when the tablib processor failed.
 * 
 * @author Richard Körber {@literal <dev@shredzone.de>}
 * @version $Id: ProcessorException.java 430 2010-04-21 21:58:20Z shred $
 */
public class ProcessorException extends RuntimeException {
    private static final long serialVersionUID = -5858868690095180313L;

    public ProcessorException(String message) {
        super(message);
    }

}
