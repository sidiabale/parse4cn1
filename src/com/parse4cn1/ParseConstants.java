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

/**
 * This class defines various constants used in the library.
 */
public class ParseConstants {

    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_APPLICATION_ID = "X-Parse-Application-Id";
    public static final String HEADER_CLIENT_KEY = "X-Parse-Client-Key";
    public static final String HEADER_SESSION_TOKEN = "X-Parse-Session-Token";

    public static final String CONTENT_TYPE_JSON = "application/json";

    public static final String FIELD_OBJECT_ID = "objectId";
    public static final String FIELD_CREATED_AT = "createdAt";
    public static final String FIELD_UPDATED_AT = "updatedAt";
    public static final String FIELD_SESSION_TOKEN = "sessionToken";
    public static final String FIELD_CLASSNAME = "className";
    
    public static final String CLASS_NAME_USER = "_User";
    public static final String CLASS_NAME_ROLE = "_Role";
    public static final String CLASS_NAME_SESSION = "_Session";
    public static final String CLASS_NAME_INSTALLATION = "_Installation";
    public static final String ENDPOINT_USERS = "users";
    public static final String ENDPOINT_ROLES = "roles";
    public static final String ENDPOINT_SESSIONS = "sessions";
    public static final String CLASSES_PATH = "classes/";
    public static final String FILES_PATH = "files/";
    
    public static final String KEYWORD_OP = "__op";
    public static final String KEYWORD_TYPE = "__type";
}
