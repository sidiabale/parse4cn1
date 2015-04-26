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

public class ParseCloud {

    private static final Logger LOGGER = Logger.getInstance();

    @SuppressWarnings("unchecked")
    public static <T> T callFunction(String name, Map<String, ?> params)
            throws ParseException {

        T result = null;
        ParsePostCommand command = new ParsePostCommand("functions", name);
        if (params != null) {
            command.setData(new JSONObject((HashMap) params));
        }
        ParseResponse response = command.perform();

        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            try {
                result = (T) jsonResponse.get("result");
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ex);
            }
            return result;
        } else {
            LOGGER.debug("Request failed.");
            throw response.getException();
        }
    }
    
// //TODO: Implement background operations
//    public static <T> void callFunctionInBackground(String name,
//            Map<String, ?> params, FunctionCallback<T> callback) {
//
//        CallFunctionInBackgroundThread<T> task = new CallFunctionInBackgroundThread<T>(name, params, callback);
//        ParseExecutor.runInBackground(task);
//    }
//
//    private static class CallFunctionInBackgroundThread<T> extends Thread {
//
//        Map<String, ?> params;
//        FunctionCallback<T> functionCallback;
//        String name;
//
//        public CallFunctionInBackgroundThread(String name, Map<String, ?> params, FunctionCallback<T> functionCallback) {
//            this.functionCallback = functionCallback;
//            this.params = params;
//            this.name = name;
//        }
//
//        public void run() {
//            ParseException exception = null;
//            T result = null;
//            try {
//                result = callFunction(name, params);
//            } catch (ParseException e) {
//                LOGGER.debug("Request failed {}", e.getMessage());
//                exception = e;
//            }
//            if (functionCallback != null) {
//                functionCallback.done(result, exception);
//            }
//        }
//    }
}
