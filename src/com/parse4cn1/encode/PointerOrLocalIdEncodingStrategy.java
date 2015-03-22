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

import java.util.Random;

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;

// TODO: Document
// TODO: Test?
public class PointerOrLocalIdEncodingStrategy implements
        ParseObjectEncodingStrategy {

    @Override
    public JSONObject encodeRelatedObject(ParseObject parseObject)  throws ParseException {
        JSONObject json = new JSONObject();
        try {
            if (parseObject.getObjectId() != null) {
                json.put(ParseConstants.KEYWORD_TYPE, "Pointer");
                json.put(ParseConstants.FIELD_CLASSNAME, parseObject.getClassName());
                json.put(ParseConstants.FIELD_OBJECT_ID, parseObject.getObjectId());
            } else {
                json.put(ParseConstants.KEYWORD_TYPE, "Pointer");
                json.put(ParseConstants.FIELD_CLASSNAME, parseObject.getClassName());
                json.put("localId", createTempId());
            }
        } catch (JSONException e) {
            throw new ParseException(e);
        }
        return json;
    }

    private static String createTempId() {
        Random random = new Random();
        long localIdNumber = random.nextLong();
        String localId = "local_" + Long.toString(localIdNumber, 16);

        if (!isLocalId(localId)) {
            throw new IllegalStateException(
                    "Generated an invalid local id: \""
                    + localId
                    + "\". "
                    + "This should never happen. Contact us at https://parse.com/help");
        }

        return localId;
    }

    private static boolean isLocalId(String localId) {
        if (!localId.startsWith("local_")) {
            return false;
        }
        for (int i = 6; i < localId.length(); i++) {
            char c = localId.charAt(i);
            if (((c < '0') || (c > '9')) && ((c < 'a') || (c > 'f'))) {
                return false;
            }
        }
        return true;
    }
}
