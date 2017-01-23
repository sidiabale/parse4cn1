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

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.util.Base64;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseFile;
import com.parse4cn1.ParseGeoPoint;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import com.parse4cn1.ParseRelation;
import com.parse4cn1.util.Logger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class encodes data to be sent to the Parse server.
 */
public class ParseEncoder {

    private static final Logger LOGGER = Logger.getInstance();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object encode(Object value, IParseObjectEncodingStrategy objectEncoder) throws ParseException {

        if (value instanceof ParseObject) {
            return objectEncoder.encodeRelatedObject((ParseObject) value);
        }

        if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            JSONObject output = new JSONObject();
            try {
                output.put(ParseConstants.KEYWORD_TYPE, "Bytes");
                output.put("base64", Base64.encode(bytes));
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
            }
            return output;
        }

        if (value instanceof Date) {
            Date dt = (Date) value;
            JSONObject output = new JSONObject();
            try {
                output.put(ParseConstants.KEYWORD_TYPE, "Date");
                output.put("iso", Parse.encodeDate(dt));
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
            }
            return output;
        }

        if (value instanceof List) {
            JSONArray array = new JSONArray();
            List list = (List) value;
            Iterator i = list.iterator();
            while (i.hasNext()) {
                array.put(encode(i.next(), objectEncoder));
            }
            return array;
        }

        if (value instanceof JSONObject) {
            JSONObject map = (JSONObject) value;
            JSONObject json = new JSONObject();
            Iterator keys = map.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                try {
                    json.put(key, encode(map.opt(key), objectEncoder));
                } catch (JSONException ex) {
                    throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
                }
            }
            return json;
        }

        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            JSONArray json = new JSONArray();
            for (int i = 0; i < array.length(); i++) {
                json.put(encode(array.opt(i), objectEncoder));
            }
            return json;
        }

        if (value instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) value;
            JSONObject json = new JSONObject();
            for (String key : map.keySet()) {
                try {
                    json.put(key, encode(map.get(key), objectEncoder));
                } catch (JSONException ex) {
                    throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
                }
            }
            return json;
        }

        if ((value instanceof ParseRelation)) {
            ParseRelation relation = (ParseRelation) value;
            JSONObject json = null;
            try {
                json = relation.encode(objectEncoder);
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
            }
            return json;
        }
        
        if (value instanceof ParseQuery) {
            return ((ParseQuery) value).encode();
        }

        if ((value instanceof ParseQuery.RelationConstraint)) {
            return ((ParseQuery.RelationConstraint) value).encode(objectEncoder);
        }

        if (value instanceof ParseFile) {
            ParseFile file = (ParseFile) value;
            JSONObject output = new JSONObject();
            try {
                output.put(ParseConstants.KEYWORD_TYPE, "File");
                output.put("name", file.getName());
                output.put("url", file.getUrl());
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
            }
            return output;
        }

        if (value instanceof ParseGeoPoint) {
            ParseGeoPoint gp = (ParseGeoPoint) value;
            JSONObject output = new JSONObject();
            try {
                output.put(ParseConstants.KEYWORD_TYPE, "GeoPoint");
                output.put("latitude", gp.getLatitude());
                output.put("longitude", gp.getLongitude());
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
            }
            return output;
        }

        if (Parse.isValidType(value)) {
            return value;
        }

        LOGGER.error("Object type not decoded: " + value.getClass().toString());
        throw new IllegalArgumentException("Invalid type for ParseObject: " + value.getClass().toString());
    }
}
