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

    private static final Map<String, Class<? extends ParseObject>> objectTypes
            = new HashMap<String, Class<? extends ParseObject>>();

    public static void registerDefaultSubClasses() {
        registerSubclass(ParseUser.class, "_User");
        registerSubclass(ParseRole.class, "_Role");
    }

    public static void unregisterSubclass(String className) {
        objectTypes.remove(className);
    }

    public static void registerSubclass(Class<? extends ParseObject> subclass, 
            final String className) {

//        String className = getClassName(subclass);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registering subclass '" + className +  "'");
        }
        if (className == null) {
            throw new IllegalArgumentException(
                    "No ParseClassName annoation provided on " + subclass);
        }

//        if (subclass.getDeclaredConstructors().length > 0) {
//            try {
//                if (!isAccessible(subclass.getDeclaredConstructor(new Class[0]))) {
//                    throw new IllegalArgumentException(
//                            "Default constructor for " + subclass
//                            + " is not accessible.");
//                }
//            } catch (NoSuchMethodException e) {
//                throw new IllegalArgumentException(
//                        "No default constructor provided for " + subclass);
//            }
//        }
//
//        Class<? extends ParseObject> oldValue = (Class<? extends ParseObject>) objectTypes.get(className);
//        if ((oldValue != null) && (subclass.isAssignableFrom(oldValue))) {
//            return;
//        }

        objectTypes.put(className, subclass);

    }

//    private static boolean isAccessible(Member m) {
//        return (Modifier.isPublic(m.getModifiers()))
//                || ((m.getDeclaringClass().getPackage().getName()
//                .equals("com.parse"))
//                && (!Modifier.isPrivate(m.getModifiers())) && (!Modifier
//                .isProtected(m.getModifiers())));
//    }
//
    public static String getClassName(Class<? extends ParseObject> clazz) {
        String name = (String) classNames.get(clazz);
//        if (name == null) {
//            ParseClassName info = (ParseClassName) clazz.getAnnotation(ParseClassName.class);
//            if (info == null) {
//                return null;
//            }
//            name = info.value();
//            classNames.put(clazz, name);
//        }
        return name;
    }

    public static Class<? extends ParseObject> getParseClass(String className) {
        Class<? extends ParseObject> value = (Class<? extends ParseObject>) objectTypes.get(className);
        return value;
    }

}
