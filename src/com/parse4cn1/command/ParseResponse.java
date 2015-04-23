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

package com.parse4cn1.command;

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.ConnectionRequest;
import com.parse4cn1.ParseException;
import com.parse4cn1.util.Logger;

public class ParseResponse {

    static final String RESPONSE_CODE_JSON_KEY = "code";
    static final String RESPONSE_ERROR_JSON_KEY = "error";
    private static final Logger LOGGER = Logger.getInstance();
    
    private ParseException error;
    private byte[] responseBody;
    private int statusCode;

    static ParseException getConnectionFailedException(String message) {
        return new ParseException(ParseException.CONNECTION_FAILED,
                "Connection failed with Parse servers.  Log: " + message);
    }

    static ParseException getConnectionFailedException(Throwable e) {
        return getConnectionFailedException(e.getMessage());
    }
    
    public boolean isFailed() {
        return hasConnectionFailed() || hasError();
    }

    public ParseException getException() {

        if (error != null) {
            return error;
        }
        
        if (hasConnectionFailed()) {
            return new ParseException(ParseException.CONNECTION_FAILED,
                    "Connection to Parse servers failed.");
        }

        if (!hasErrorCode()) {
            return new ParseException(ParseException.OPERATION_FORBIDDEN,
                    "getException called with successful response");
        }

        JSONObject response = null;
        try {
            response = getJsonObject();
        } catch (ParseException ex) {
            return ex;
        }

        if (response == null) {
            return new ParseException(ParseException.INVALID_JSON,
                    "Invalid response from Parse servers.");
        }

        return getParseError(response);
    }
    
    public static ParseException getParseError(JSONObject response) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getParseError(): " + response);
        }
        int code;
        String errorMsg;
        JSONException ex = null;

        try {
            code = response.getInt(RESPONSE_CODE_JSON_KEY);
        } catch (JSONException e) {
            ex = e;
            code = ParseException.NOT_INITIALIZED;
        }

        try {
            errorMsg = response.getString(RESPONSE_ERROR_JSON_KEY);
        } catch (JSONException e) {
            ex = e;
            errorMsg = "Error undefined by Parse server.";
        }
        
        return (ex != null) 
                ? new ParseException(code, errorMsg, ex) 
                : new ParseException(code, errorMsg);
    }

    public byte[] getResponseData() {
        return responseBody;
    }
    
    public JSONObject getJsonObject() throws ParseException {
        try {
            return new JSONObject(new String(responseBody));
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, 
                    "Unable to parse the response to JSON", ex);
        }
    }
    
    protected void extractResponseData(final ConnectionRequest request) {
        if (request.getResponseData() != null) {
            responseBody = request.getResponseData();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Raw response (as string): " + new String(responseBody));
            }
            setStatusCode(request.getResponseCode());
        }
    }

    protected int getStatusCode() {
        return statusCode;
    }
    
    protected void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    
    protected void setError(ParseException error) {
        this.error = error;
    }
    
    private boolean hasConnectionFailed() {
        return responseBody == null;
    }

    private boolean hasError() {
        return hasErrorCode() || (error != null);
    }
    
    private boolean hasErrorCode() {
        return (statusCode < 200 || statusCode >= 300);
    }
}
