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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Parse {

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
        } catch (ParseException e) {
            return null;
        }
    }

    public static boolean isReservedKey(String key) {
        return ParseConstants.FIELD_OBJECT_ID.equalsIgnoreCase(key)
                || ParseConstants.FIELD_CREATED_AT.equalsIgnoreCase(key)
                || ParseConstants.FIELD_UPDATED_AT.equalsIgnoreCase(key);
    }

    // TODO: Test
    public static boolean isValidType(Object value) {
        return ((value instanceof JSONObject))
                || ((value instanceof JSONArray))
                || ((value instanceof String))
                || (OperationUtil.isSupportedNumberType(value))
                || ((value instanceof Boolean))
                || (value == JSONObject.NULL)
                || ((value instanceof ParseObject))
                // || ((value instanceof ParseACL))
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
