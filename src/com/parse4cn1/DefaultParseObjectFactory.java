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

/**
 *
 * @author sidiabale
 */
public class DefaultParseObjectFactory implements Parse.IParseObjectFactory {

    public <T extends ParseObject> T create(String className) {
        T obj;
        
        if (ParseConstants.ENDPOINT_USERS.equalsIgnoreCase(className)
                || ParseConstants.CLASS_NAME_USER.equalsIgnoreCase(className)) {
            obj = (T) new ParseUser();
        } else {
            obj = (T) new ParseObject(className);
        }
        // TODO: Extend with other 'default' parse object subtypes
        // e.g. ParseFile, ParseGeoPoint.
        
        return obj;
    }
}
