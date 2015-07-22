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

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import java.util.Map;

import com.parse4cn1.command.ParsePostCommand;
import com.parse4cn1.command.ParseResponse;
import com.parse4cn1.util.Logger;
import java.util.HashMap;

/**
 * The ParseCloud class defines provides methods for interacting with Parse
 * Cloud Functions. and triggering Cloud Jobs.
 * <p>
 * <b>Note:</b> Since triggering Cloud Jobs requires the Master Key which is
 * deliberately not exposed via this client-side library for security
 * considerations, the only way to trigger jobs using this library is to write a
 * wrapper Cloud Function, that does the necessary authentication and forwards
 * the request to the Job API.
 */
public class ParseCloud {

    private static final Logger LOGGER = Logger.getInstance();

    /**
     * Calls a cloud function.
     * 
     * @param <T> The type of result expected by this function call.
     * @param name The name of the function to call.
     * @param params The parameters to pass to the function. These parameters 
     * are sent as JSON data in the body of the resulting POST request.
     * @return The result returned by the function call.
     * @throws ParseException if anything goes wrong, for example with JSON parsing.
     */
    public static <T> T callFunction(String name, Map<String, ?> params)
            throws ParseException {

        T result = null;
        ParsePostCommand command = new ParsePostCommand("functions", name);
        if (params != null) {
            command.setMessageBody(new JSONObject((HashMap) params));
        }
        ParseResponse response = command.perform();

        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            try {
                result = (T) jsonResponse.get("result");
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PROCESSING_RESPONSE, ex);
            }
            return result;
        } else {
            LOGGER.debug("Request failed.");
            throw response.getException();
        }
    }
}
