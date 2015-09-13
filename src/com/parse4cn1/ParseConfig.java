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
package com.parse4cn1;

import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.command.ParseGetCommand;
import com.parse4cn1.command.ParseResponse;
import java.util.Date;
import java.util.List;

/**
 * ParseConfig is a way to configure your applications remotely by storing a 
 * single configuration object on Parse. It enables you to add things like 
 * feature gating or a simple "Message of the day". 
 * <p>
 * Parse configuration items are read-only
 *
 * @author sidiabale
 */
public class ParseConfig {
    
    private static final String ENDPOINT_CONFIG = "config";
    private static ParseConfig instance;
    private final ParseObject config;
    
    /**
     * Retrieves the parse configuration singleton object.
     * 
     * @return The singleton ParseConfig instance.
     * @throws ParseException if the ParseConfig object creation fails.
     */
    public static ParseConfig getInstance() throws ParseException {
        if (instance == null) {
            instance = new ParseConfig();
        }
        return instance;
    }
    
    /**
     * Forces fresh retrieval of the Parse configuration from the server.
     * This is only useful if configuration items can change at runtime.
     * 
     * @return The new ParseConfig instance.
     * @throws ParseException if the ParseConfig data retrieval fails.
     */
    public ParseConfig refresh() throws ParseException {
        instance = null;
        return getInstance();
    }
    
    /**
     * @see ParseObject#getParseFile(java.lang.String) 
     */
    public ParseFile getParseFile(String key) {
        return config.getParseFile(key);
    }

    /**
     * @see ParseObject#getParseGeoPoint(java.lang.String) 
     */
    public ParseGeoPoint getParseGeoPoint(String key) {
        return config.getParseGeoPoint(key);
    }

    /**
     * @see ParseObject#getDate(java.lang.String) 
     */
    public Date getDate(String key) {
        return config.getDate(key);
    }

    /**
     * @see ParseObject#getBoolean(java.lang.String) 
     */
    public Boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    /**
     * @see ParseObject#getInt(java.lang.String) 
     */
    public Integer getInt(String key) {
        return config.getInt(key);
    }

    /**
     * @see ParseObject#getDouble(java.lang.String) 
     */
    public Double getDouble(String key) {
        return config.getDouble(key);
    }

    /**
     * @see ParseObject#getLong(java.lang.String) 
     */
    public Long getLong(String key) {
        return config.getLong(key);
    }

    /**
     * @see ParseObject#getString(java.lang.String) 
     */
    public String getString(String key) {
        return config.getString(key);
    }

    /**
     * @see ParseObject#getList(java.lang.String) 
     */
    public <T> List<T> getList(String key) {
        return config.getList(key);
    }

    /**
     * @see ParseObject#ParseObject(java.lang.String) 
     */
    public ParseObject getParseObject(String key) {
        return config.getParseObject(key);
    }

    /**
     * @see ParseObject#get(java.lang.String) 
     */
    public Object get(String key) {
        return config.get(key);
    }
    
    private ParseConfig() throws ParseException {
        config = ParseObject.create(ENDPOINT_CONFIG);
        config.setEndPoint(ENDPOINT_CONFIG);
        
        ParseGetCommand command = new ParseGetCommand(ENDPOINT_CONFIG);
        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                throw response.getException();
            }

            if (!jsonResponse.has("params")) {
                throw new ParseException(ParseException.INVALID_JSON, 
                    "Expected 'params' field but got " + jsonResponse);
            }
            config.setData(jsonResponse.optJSONObject("params"));
        } else {
            throw response.getException();
        }
    }
}
