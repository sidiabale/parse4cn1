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

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Util;
import com.parse4cn1.command.ParseCommand;
import com.parse4cn1.command.ParseDeleteCommand;
import com.parse4cn1.command.ParseGetCommand;
import com.parse4cn1.command.ParsePostCommand;
import com.parse4cn1.command.ParseResponse;
import com.parse4cn1.util.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The ParseUser is a local representation of user data that can be saved and
 * retrieved from the Parse cloud.
 */
public class ParseInstallation extends ParseObject {

    private static final Logger LOGGER = Logger.getInstance();
    private static final String KEY_INSTALLATION_ID = "installationId";
    private static final String KEY_DEVICE_TYPE = "deviceType";
    private static final String KEY_APP_NAME = "appName";
    private static final String KEY_APP_IDENTIFIER = "appIdentifier";
    private static final String KEY_PARSE_VERSION = "parseVersion";
    private static final String KEY_DEVICE_TOKEN = "deviceToken";
    private static final String KEY_PUSH_TYPE = "pushType";
    private static final String KEY_TIME_ZONE = "timeZone";
    private static final String KEY_LOCALE = "localeIdentifier";
    private static final String KEY_APP_VERSION = "appVersion";

    private static ParseInstallation current;
    
    private static final List<String> READ_ONLY_FIELDS = Collections.unmodifiableList(
      Arrays.asList(KEY_DEVICE_TYPE, KEY_INSTALLATION_ID, KEY_DEVICE_TOKEN, KEY_PUSH_TYPE,
          KEY_TIME_ZONE, KEY_LOCALE, KEY_APP_VERSION, KEY_APP_NAME, KEY_PARSE_VERSION,
          KEY_APP_IDENTIFIER));

    public static ParseInstallation getCurrentInstallation() {
        throw new RuntimeException("Unimplemented");
    }
    
    public static ParseInstallation create() throws ParseException {
        /*
            
            {
              "code": 132,
              "error": "Invalid installation ID: 982021"
            }
        
            {
              "code": 135,
              "error": "deviceType must be specified in this operation"
            }
        
            {
              "code": 135,
              "error": "at least one ID field (installationId,deviceToken) must be specified in this operation"
            }
        */
        ParseInstallation installation = new ParseInstallation();
        return installation;
    }

    public static ParseQuery<ParseInstallation> getQuery() {
        throw new RuntimeException("Unimplemented");
    }

    public String getInstallationId() {
        throw new RuntimeException("Unimplemented");
    }

    public List<String> getChannels() {
        throw new RuntimeException("Unimplemented");
    }

    public void subscribe(final String channel) {
        throw new RuntimeException("Unimplemented");
    }

    public void subscribe(final List<String> channels) {
        throw new RuntimeException("Unimplemented");
    }

    public void unsubscribe(final String channel) {
        throw new RuntimeException("Unimplemented");
    }

    public void unsubscribe(final List<String> channels) {
        throw new RuntimeException("Unimplemented");
    }

    public void unsubscribeAll() {
        throw new RuntimeException("Unimplemented");
    }

    protected ParseInstallation() {
        super(ParseConstants.CLASS_NAME_INSTALLATION);
    }
}
