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
import java.io.UnsupportedEncodingException;

/**
 * This class encapsulates a response to a ParseCommand issued to the Parse
 * server.
 */
public class ParseResponse {

    static final String RESPONSE_CODE_JSON_KEY = "code";
    static final String RESPONSE_ERROR_JSON_KEY = "error";
    private static final Logger LOGGER = Logger.getInstance();

    private ParseException error;
    private byte[] responseBody;
    private int statusCode;

    /**
     * Creates a ParseException that indicates connection failure. This
     * exception will have error code {@link ParseException#CONNECTION_FAILED}.
     *
     * @param e The throwable from which the exception message is to be
     * retrieved.
     * @return The created ParseException.
     */
    static ParseException createConnectionFailedException(Throwable e) {
        return new ParseException(ParseException.CONNECTION_FAILED,
                "Connection to backend failed.", e);
    }

    /**
     * If {@code true}, the associated ParseCommand failed.
     *
     * @return
     */
    public boolean isFailed() {
        return hasConnectionFailed() || hasError();
    }

    /**
     * @return An exception that encapsulates the failure associated with this
     * ParseResponse.
     */
    public ParseException getException() {

        if (hasConnectionFailed()) {
            return new ParseException(ParseException.CONNECTION_FAILED,
                    "Connection to backend failed.");
        }

        if (!hasErrorCode()) {
            return new ParseException(ParseException.OPERATION_FORBIDDEN,
                    "Request to backend completed successfully (no errors).");
        }

        JSONObject response = null;
        try {
            response = getJsonObject();
        } catch (ParseException ex) {
            return ex;
        }

        if (response == null) {
            return new ParseException(ParseException.INVALID_JSON,
                    ParseException.ERR_INVALID_RESPONSE);
        }

        ParseException ex = getParseError(response);
        
        if (ex == null) {
            // Give lowest priority to the http error
            ex = error;
        }
        return ex;
    }

    /**
     * Converts the provided raw response into the corresponding ParseException.
     *
     * @param response The response received from the Parse server.
     * @return The exception corresponding to {@code response}.
     */
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
            
            if (errorMsg.length() > 0) {
                // Capitalize first character for consistency with other error messages in this library
                errorMsg = String.valueOf(errorMsg.charAt(0)).toUpperCase()
                        + (errorMsg.length() > 1 ? errorMsg.substring(1) : "");
            }
            
        } catch (JSONException e) {
            ex = e;
            errorMsg = "An unexpected error occurred.";
        }

        return (ex != null)
                ? new ParseException(code, errorMsg, ex)
                : new ParseException(code, errorMsg);
    }

    /**
     * @return The raw response data received from the Parse server.
     */
    public byte[] getResponseData() {
        return responseBody;
    }

    /**
     * @return The response received from the Parse server encoded in a
     * JSONObject.
     * @throws ParseException if anything goes wrong with converting the
     * response to JSON.
     */
    public JSONObject getJsonObject() throws ParseException {
        try {
            return new JSONObject(new String(responseBody, "UTF-8"));
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON,
                    "Unable to parse the response received from Parse", ex);
        } catch(UnsupportedEncodingException err) {
            // silly and will never happen....
            err.printStackTrace();
            return null;
        }
    }

    /**
     * Extracts the response from the provided {@code request}.
     *
     * @param request The (executed) network request.
     */
    protected void extractResponseData(final ConnectionRequest request) {
        if (request.getResponseData() != null) {
            responseBody = request.getResponseData();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Raw response (as string): " + new String(responseBody));
            }
            setStatusCode(request.getResponseCode());
        }
    }

    /**
     * @return The HTTP status code. This should not be confused with the "code"
     * field returned by the Parse server when a request fails. The latter can
     * be retrieved via the {@link ParseException#getCode()} of the exception
     * returned by {@link #getException()}.
     */
    protected int getStatusCode() {
        return statusCode;
    }
    
    private void setStatusCode(int responseCode) {
        statusCode = responseCode;
    }

    /**
     * Sets the backend error.
     *
     * @param statusCode The HTTP status code.
     * @param message The HTTP error message.
     */
    protected void setConnectionError(int statusCode, String message) {
        setStatusCode(statusCode);
        error = new ParseException(ParseException.CONNECTION_FAILED, "Backend request returned with error.",
            new Exception(message));
    }

    protected void setConnectionError(ParseException error) {
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
