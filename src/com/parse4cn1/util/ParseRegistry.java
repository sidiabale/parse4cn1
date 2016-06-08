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

import com.codename1.io.Util;
import com.parse4cn1.Parse.DefaultParseObjectFactory;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseFile;
import com.parse4cn1.ParseGeoPoint;
import com.parse4cn1.ParseInstallation;
import java.util.Map;
import java.util.HashMap;

import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseRelation;
import com.parse4cn1.ParseRole;
import com.parse4cn1.ParseUser;

/**
 * Defines a registry for ParseObject (sub-)classes to facilitate their 
 * instantiation at runtime. 
 * <p>
 * Note that since CN1 does not support Java Reflection, 
 * custom ParseObject sub-classes need to be manually registered here using
 * {@link #registerParseFactory(java.lang.String, com.parse4cn1.Parse.IParseObjectFactory)}
 * and {@link #registerSubclass(java.lang.Class, java.lang.String)}.
 */
public class ParseRegistry {

    private static final Logger LOGGER = Logger.getInstance();
    private static final Parse.IParseObjectFactory DEFAULT_OBJECT_FACTORY = 
       new DefaultParseObjectFactory();     

    private static final Map<Class<? extends ParseObject>, String> classNames
        = new HashMap<Class<? extends ParseObject>, String>();

    private static final Map<String, Parse.IParseObjectFactory> objectFactories
        = new HashMap<String, Parse.IParseObjectFactory>();
    
    /**
     * Resets all registered class mappings.
     * <p>Note that registered externalizable classes are not deregistered as that 
     * is not supported.
     */
    public static void reset() {
        classNames.clear();
        objectFactories.clear();
    }

    /**
     * Registers predefined Parse classes like _User and _Role so that they 
     * can be instantiated when needed.
     */
    public static void registerDefaultSubClasses() {
        registerSubclass(ParseUser.class, ParseConstants.CLASS_NAME_USER);
        registerSubclass(ParseRole.class, ParseConstants.CLASS_NAME_ROLE);
        registerSubclass(ParseInstallation.class, ParseConstants.CLASS_NAME_INSTALLATION);
        // TODO: Register other Parse sub-classes
    }
    
    /**
     * Registers all externalizable classes defined in the parse4cn1 library.
     * <p>
     * <b>Note: User-defined externalizable custom types stored in {@link ParseObject} or
     * any of its sub-classes <em>must</em> be 
     * {@link com.codename1.io.Util#register(java.lang.String, java.lang.Class) registered} 
     * by the user otherwise persistence of any ParseObject containing such custom types will fail.</b>
     */
    public static void registerExternalizableClasses() {
        Util.register(ExternalizableParseObject.getClassName(), ExternalizableParseObject.class);
        Util.register(ExternalizableJsonEntity.getClassName(), ExternalizableJsonEntity.class);
        Util.register(ParseFile.getClassName(), ParseFile.class);
        Util.register(ParseRelation.getClassName(), ParseRelation.class);
        Util.register(ParseGeoPoint.getClassName(), ParseGeoPoint.class);
    }
    
    /**
     * Registers the factory to be used to instantiate object of {@code className}.
     * @param className The name of the ParseObject sub-class to be associated with {@code factory}.
     * @param factory The factory to be used to instantiate objects of class {@code className}.
     */
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

    /**
     * Registers the ParseObject subclass associated with a given class name.
     * @param subclass The ParseObject subclass.
     * @param className The class name associated with {@code subClass}.
     */
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

    /**
     * Retrieves the class name associated with the provided ParseObject sub-class.
     * @param clazz A ParseObject sub-class.
     * @return The class name associated with {@code clazz} or null if none is defined.
     */
    public static String getClassName(Class<? extends ParseObject> clazz) {
        return (String) classNames.get(clazz);
    }

    /**
     * Retrieves the object factory for instantiating objects of the class with 
     * the specified name.
     * 
     * @param className The class name whose object factory is required.
     * @return The object factory for {@code className}. If none has been defined, the 
     * {@link DefaultParseObjectFactory} is returned.
     */
    public static Parse.IParseObjectFactory getObjectFactory(final String className) {
        
        if (!objectFactories.containsKey(className)) {
           return DEFAULT_OBJECT_FACTORY;
        }
        
        return objectFactories.get(className);
    }
}
