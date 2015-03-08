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
    private String responseBody;
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

    public JSONObject getJsonObject() throws ParseException {
        try {
            return new JSONObject(responseBody);
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, 
                    "Unable to parse the response to JSON", ex);
        }
    }
    
    protected void extractResponseData(final ConnectionRequest request) {
        if (request.getResponseData() != null) {
            responseBody = new String(request.getResponseData());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Raw response: " + responseBody);
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
