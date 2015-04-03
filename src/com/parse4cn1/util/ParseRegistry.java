/*
 * Copyright 2015 Chidiebere Okwudire.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Original implementation adapted from Thiago Locatelli's Parse4J project
 * (see https://github.com/thiagolocatelli/parse4j)
 */

package com.parse4cn1.util;

import com.parse4cn1.DefaultParseObjectFactory;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import java.util.Map;
import java.util.HashMap;

import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseRole;
import com.parse4cn1.ParseUser;

public class ParseRegistry {

    private static final Logger LOGGER = Logger.getInstance();

    // TODO: Check impact of ConcurrentHashMap --> HashMap
    private static final Map<Class<? extends ParseObject>, String> classNames
        = new HashMap<Class<? extends ParseObject>, String>();

    private static final Map<String, Parse.IParseObjectFactory> objectFactories
        = new HashMap<String, Parse.IParseObjectFactory>();

    public static void registerDefaultSubClasses() {
        registerSubclass(ParseUser.class, ParseConstants.CLASS_NAME_USER);
        registerSubclass(ParseRole.class, ParseConstants.CLASS_NAME_ROLE);
    }
    
    public static void registerDefaultObjectFactories() {
        final Parse.IParseObjectFactory factory = new DefaultParseObjectFactory();
        registerParseFactory(ParseConstants.ENDPOINT_USERS, factory);
        registerParseFactory(ParseConstants.CLASS_NAME_USER, factory);
        // TODO: Register other classes
    }
    
    public static void registerParseFactory(final String className, 
            final Parse.IParseObjectFactory factory) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registering object factory for class '" + className +  "'");
        }
        
        if (className == null || factory == null) {
            throw new IllegalArgumentException("Null class name and/or factory");
        }
             
        objectFactories.put(className, factory);
    }

    public static void registerSubclass(Class<? extends ParseObject> subclass, 
            final String className) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registering subclass '" + className +  "'");
        }
        if (className == null) {
            throw new IllegalArgumentException("Null subclass");
        }

        classNames.put(subclass, className);
    }

    public static String getClassName(Class<? extends ParseObject> clazz) {
        String name = (String) classNames.get(clazz);
        return name;
    }

    public static Parse.IParseObjectFactory getObjectFactory(final String className) {
        
        if (!objectFactories.containsKey(className)) {
           throw new IllegalArgumentException(
                   "No factory registered for class '" + className + "'");
        }
        
        return objectFactories.get(className);
    }
}
