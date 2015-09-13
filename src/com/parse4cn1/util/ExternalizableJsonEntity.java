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
 */
package com.parse4cn1.util;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Externalizable;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.encode.ParseDecoder;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A wrapper around relevant JSON classes to make them
 * externalizable.
 * @see ca.weblite.codename1.json.JSONArray
 * @see ca.weblite.codename1.json.JSONObject
 * @see ca.weblite.codename1.json.JSONObject#NULL
 */
public class ExternalizableJsonEntity<T> implements Externalizable {

    private enum EJsonEntityType {
        JSON_ARRAY("JSONArray"),
        JSON_OBJECT("JSONObject"),
        JSON_NULL("JSONNull");

        @Override
        public String toString() {
            return value;
        }
        
        public static EJsonEntityType fromString(String value) {
            if (value != null) {
                for (EJsonEntityType type : EJsonEntityType.values()) {
                    if (value.equalsIgnoreCase(type.value)) {
                        return type;
                    }
                }
            }
            throw new IllegalArgumentException(
                    "Failed to create enum literal from string: " + value);
        }
        
        private EJsonEntityType(final String value) {
            this.value = value;
        };
        
        private final String value;
    }
    
    private T object;
    private EJsonEntityType type;
    
    static public boolean isExternalizableJsonEntity(final Object obj) {
        return (obj instanceof JSONArray) || (obj instanceof JSONObject)
                || (obj == JSONObject.NULL);
    }
    
    public ExternalizableJsonEntity() { 
    }
    
    public ExternalizableJsonEntity(final T jsonEntity) {
        if (jsonEntity == null) {
            throw new NullPointerException("Null JSON entity cannot be serialized");
        }
        
        if (jsonEntity instanceof JSONArray) {
           type = EJsonEntityType.JSON_ARRAY; 
        } else if (jsonEntity instanceof JSONObject) {
            type = EJsonEntityType.JSON_OBJECT;
        } else if (jsonEntity == JSONObject.NULL) {
            type = EJsonEntityType.JSON_NULL;
        } else {
            throw new IllegalArgumentException("Serialization not (yet) supported for the provided JSON entity type");
        }
        object = (T) jsonEntity;
    }
    
    /**
     * @return A unique class name.
     */
    public static String getClassName() {
        return "ExternalizableJsonEntity";
    }
    
    /**
     * @return The {@link java.util.Date} wrapped by this object.
     */
    public T getJsonEntity() {
        return object;
    }
    
    /**
     * @see com.codename1.io.Externalizable
     */
    public int getVersion() {
        return Parse.getSerializationVersion();
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void externalize(DataOutputStream out) throws IOException {
        Util.writeUTF(type.toString(), out);
        if (type == EJsonEntityType.JSON_ARRAY) {
           Util.writeObject(ParseDecoder.convertJSONArrayToList((JSONArray)object), out); 
        } else if (type == EJsonEntityType.JSON_OBJECT) {
           Util.writeObject(ParseDecoder.convertJSONObjectToMap((JSONObject)object), out);
        }
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void internalize(int version, DataInputStream in) throws IOException {
        type = EJsonEntityType.fromString(Util.readUTF(in));
        if (type == EJsonEntityType.JSON_ARRAY) {
           object = (T) ParseDecoder.convertListToJSONArray((ArrayList) Util.readObject(in));
        } else if (type == EJsonEntityType.JSON_OBJECT) {
           object = (T) ParseDecoder.convertMapToJSONObject((HashMap) Util.readObject(in));
        } else if (type == EJsonEntityType.JSON_NULL) {
           object = (T) JSONObject.NULL;
        }
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    // Note: This is a unique identifier for this class not individual objects serialized by it!
    public String getObjectId() {
        return getClassName();
    }
}
