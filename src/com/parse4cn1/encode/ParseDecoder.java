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
package com.parse4cn1.encode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.codename1.util.Base64;
import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseFile;
import com.parse4cn1.ParseGeoPoint;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseRelation;
import com.parse4cn1.operation.ParseOperationDecoder;
import com.parse4cn1.util.ParseRegistry;

/**
 * This class decodes data retrieved from the Parse server.
 */
public class ParseDecoder {

    @SuppressWarnings("rawtypes")
    public static Object decode(Object object) {

        if ((object instanceof JSONArray)) {
            return convertJSONArrayToList((JSONArray) object);
        }

        if (!(object instanceof JSONObject)) {
            return object;
        }

        JSONObject jsonObject = (JSONObject) object;

        String typeString = jsonObject.optString(ParseConstants.KEYWORD_TYPE, null);
        if (typeString == null) {
            return convertJSONObjectToMap(jsonObject);
        } else if ("Date".equals(typeString)) {
            String iso = jsonObject.optString("iso");
            return Parse.parseDate(iso);
        } else if ("Bytes".equals(typeString)) {
            String base64 = jsonObject.optString("base64");
            return Base64.decode(base64.getBytes());
        } else if ("Pointer".equals(typeString)) {
            return decodePointer(jsonObject.optString(ParseConstants.FIELD_CLASSNAME), 
                    jsonObject.optString("objectId"));
        } else if ("Object".equals(typeString)) {
            return decodeObject(jsonObject);
        } else if ("File".equals(typeString)) {
            final ParseFile file = new ParseFile(jsonObject.optString("name"),
                    jsonObject.optString("url"));
            file.setDirty(false);
            return file;
        } else if ("GeoPoint".equals(typeString)) {
            double latitude, longitude;
            try {
                latitude = jsonObject.getDouble("latitude");
                longitude = jsonObject.getDouble("longitude");
            } catch (JSONException e) {
                throw new RuntimeException(e.getMessage());
            }
            return new ParseGeoPoint(latitude, longitude);
        } else if ("Relation".equals(typeString)) {
            return new ParseRelation(jsonObject);
        }

        String opString = jsonObject.optString(ParseConstants.KEYWORD_OP, null);
        if (opString != null) {
            try {
                return ParseOperationDecoder.decode(jsonObject);
            } catch (JSONException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return null;
    }

    public static List<Object> convertJSONArrayToList(JSONArray array) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            list.add(decode(array.opt(i)));
        }
        return list;
    }
    
    public static JSONArray convertListToJSONArray(ArrayList<Object> list) {
        return new JSONArray(list);
    }

    public static Map<String, Object> convertJSONObjectToMap(JSONObject object) {
        Map<String, Object> outputMap = new HashMap<String, Object>();
        Iterator<?> it = object.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object value = object.opt(key);
            outputMap.put(key, decode(value));
        }
        return outputMap;
    }
    
    public static JSONObject convertMapToJSONObject(HashMap<String, Object> map) {
        return new JSONObject(map);
    }

    private static ParseObject decodePointer(String className, String objectId) {
        ParseObject obj = ParseRegistry.getObjectFactory(className).create(className);
        obj.setObjectId(objectId);
        
        return obj;
    }

    private static ParseObject decodeObject(JSONObject jsonObject) {
        final String className = jsonObject.optString(ParseConstants.FIELD_CLASSNAME);
        ParseObject obj = ParseRegistry.getObjectFactory(className).create(className);
        jsonObject.remove(ParseConstants.FIELD_CLASSNAME);
        jsonObject.remove(ParseConstants.KEYWORD_TYPE);
        obj.setData(jsonObject);
        return obj;
    }
}
