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
package com.parse4cn1;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.operation.OperationUtil;
import com.parse4cn1.operation.ParseOperationDecoder;
import com.parse4cn1.util.ParseRegistry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Parse {

    public interface IPersistable {
        /**
         *  An object is dirty if a change has been made to it that requires it to be persisted.
         * @return {@code true} if the object is dirty; otherwise {@code false}.
         */
        boolean isDirty();
        
        /**
         * Sets the dirty flag.
         * 
         * @param dirty {@code true} if the object should be marked as dirty; otherwise {@code false}. 
         */
        void setDirty(boolean dirty);
        
        /**
         * Saves the object. Calling this method on an object that is not dirty
         * should have no side effects.
         * 
         * @throws ParseException if anything goes wrong during the save operation.
         */
        void save() throws ParseException;
    }
    
    /**
     * A factory for instantiating ParseObjects of various concrete types
     */
    public interface IParseObjectFactory {
        /**
         * Creates a Parse object of the type matching the provided class name.
         * Defaults to the base ParseObject, i.e., call must always return a 
         * non-null object.
         * 
         * @param <T> The type of ParseObject to be instantiated
         * @param className The class name associated with type T
         * @return The newly created Parse object.
         */
        <T extends ParseObject> T create(final String className);
    }
    
    public static class DefaultParseObjectFactory implements IParseObjectFactory {

        public <T extends ParseObject> T create(String className) {
            T obj;

            if (ParseConstants.ENDPOINT_USERS.equals(className)
                    || ParseConstants.CLASS_NAME_USER.equals(className)) {
                obj = (T) new ParseUser();
            } else if (ParseConstants.ENDPOINT_ROLES.equals(className)
                    || ParseConstants.CLASS_NAME_ROLE.equals(className)) {
                obj = (T) new ParseRole();
            } else {
                obj = (T) new ParseObject(className);
            }
        // TODO: Extend with other 'default' parse object subtypes
            // e.g. ParseFile, ParseGeoPoint.

            return obj;
        }
    }
    
    private static String mApplicationId;
    private static String mClientKey;
    private static final DateFormat dateFormat;

    // TODO: Test
    static {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//        format.setTimeZone(TimeZone.getTimeZone("GMT")); // Not supported in CN1; TODO: Check if it's really needed
        dateFormat = format;

        ParseRegistry.registerDefaultSubClasses();
        ParseOperationDecoder.registerDefaultDecoders();
    }

    static public void initialize(String applicationId, String clientKey) {
        mApplicationId = applicationId;
        mClientKey = clientKey;
    }

    static public String getApplicationId() {
        return mApplicationId;
    }

    static public String getClientKey() {
        return mClientKey;
    }

    static public String getParseAPIUrl(String context) {
        return ParseConstants.API_ENDPOINT + "/" + ParseConstants.API_VERSION
                + "/" + ((context != null) ? context : "");
    }
	
    // TODO: Test
    static public synchronized String encodeDate(Date date) {
        return dateFormat.format(date);
    }
        
    // TODO: Test
    public static synchronized Date parseDate(String dateString) {
        try {
            return dateFormat.parse(dateString);
        } catch (java.text.ParseException e) {
            return null;
        }
    }

    public static boolean isReservedKey(String key) {
        return ParseConstants.FIELD_OBJECT_ID.equals(key)
                || ParseConstants.FIELD_CREATED_AT.equals(key)
                || ParseConstants.FIELD_UPDATED_AT.equals(key);
    }

    // TODO: Test
    public static boolean isReservedEndPoint(String endPoint) {
        // Parse-reserved end points and classes
        return ParseConstants.CLASS_NAME_USER.equals(endPoint)
            || ParseConstants.CLASS_NAME_ROLE.equals(endPoint)
            || ParseConstants.ENDPOINT_USERS.equals(endPoint)
            || ParseConstants.ENDPOINT_ROLES.equals(endPoint)
            || ParseConstants.ENDPOINT_SESSIONS.equals(endPoint); 
    }
    
    public static boolean isValidType(Object value) {
        return ((value instanceof JSONObject))
                || ((value instanceof JSONArray))
                || ((value instanceof String))
                || (OperationUtil.isSupportedNumberType(value))
                || ((value instanceof Boolean))
                || (value == JSONObject.NULL)
                || ((value instanceof ParseObject))
                || ((value instanceof ParseFile))
                || ((value instanceof ParseRelation))
                || ((value instanceof ParseGeoPoint))
                || ((value instanceof Date))
                || ((value instanceof byte[]))
                || ((value instanceof List))
                || ((value instanceof Map));
    }
    
    @SuppressWarnings("rawtypes")
    public static String join(Collection<String> items, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = items.iterator();
        if (iter.hasNext()) {
            buffer.append((String) iter.next());
            while (iter.hasNext()) {
                buffer.append(delimiter);
                buffer.append((String) iter.next());
            }
        }
        return buffer.toString();
    }
}
