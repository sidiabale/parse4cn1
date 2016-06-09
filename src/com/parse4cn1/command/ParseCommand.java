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
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseUser;
import com.parse4cn1.callback.ProgressCallback;
import com.parse4cn1.util.Logger;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * This class encapsulates a network request to be made to the Parse REST API 
 * using any of the supported HTTP verbs.
 */
public abstract class ParseCommand {

    private static final Logger LOGGER = Logger.getInstance();
    private static final String REQUEST_BODY_KEY = "data";

    private final JSONObject data = new JSONObject();
    private final JSONObject headers = new JSONObject();
    private ProgressCallback progressCallback;

    /**
     * Sets up the network connection request that will be issued when performing 
     * this operation. Typically, that involves specifying the HTTP verb,
     * headers, url, content type, etc.
     * <p>
     * This method is invoked by {@link #perform()}.
     * 
     * @param request The request to be initialized.
     * @throws ParseException if anything goes wrong.
     */
    abstract void setUpRequest(final ConnectionRequest request) throws ParseException;

    /**
     * Performs this ParseCommand by issuing a synchronous network request.
     * @return The response received if the request was successful.
     * 
     * @throws ParseException if anything goes wrong.
     */
    public ParseResponse perform() throws ParseException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Data to be sent: " + data.toString());
        }

        long commandStart = System.currentTimeMillis();
        final ParseResponse response = new ParseResponse();
        final ConnectionRequest request = createConnectionRequest(response);
        setUpRequest(request);
        
        if (progressCallback != null) {
            NetworkManager.getInstance().addProgressListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    if (evt instanceof NetworkEvent) {
                        final NetworkEvent networkEvent = (NetworkEvent) evt;
                        if (request.equals(networkEvent.getConnectionRequest())) {
                            int progressPercentage = networkEvent.getProgressPercentage();
                            if (progressPercentage >= 0) {
                                progressCallback.done(progressPercentage);
                            }
                        }
                    }
                }
            });
        }

        Iterator keys = headers.keys();
        while (keys.hasNext()) {
            final String key = (String) keys.next();
           
             try {
                request.addRequestHeader(key, (String) headers.get(key));
            } catch (JSONException ex) {
                Logger.getInstance().error("Error parsing header '" + key + "' + Error: " + ex);
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
            }
        }
        
        keys = data.keys();
        while (keys.hasNext()) {
            final String key = (String) keys.next();
            if (!REQUEST_BODY_KEY.equals(key)) {
                try {
                    request.addArgument(key, data.get(key).toString());
                } catch (JSONException ex) {
                    LOGGER.error("Error parsing key '" + key + "' in command data. Error: " + ex);
                    throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
                }
            }
        }
        NetworkManager.getInstance().addToQueueAndWait(request);
        response.extractResponseData(request);
        long commandReceived = System.currentTimeMillis();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parse " + request.getHttpMethod() + " Command took " 
                    + (commandReceived - commandStart) + " milliseconds\n");
        }

        return response;
    }
    
    /**
     * Add the HTTP header field associated with the provided key and value.
     * 
     * @param key The header's key.
     * @param value The header's value.
     * @throws ParseException if anything goes wrong.
     */
    public void addHeader(final String key, final String value) throws ParseException {
        try {
            headers.put(key, value);
        } catch (JSONException ex) {
            LOGGER.error("Unable to add header. Error: " + ex);
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
        }
    }

    /**
     * Creates and initializes a network connection request.
     * 
     * @param response The response associated with the request. This response 
     * object will be updated with the error information if the request fails.
     * 
     * @return The created connection request.
     */
    protected ConnectionRequest createConnectionRequest(final ParseResponse response) {
        ConnectionRequest request = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                response.setConnectionError(code, message);
            }

            @Override
            protected void handleException(Exception err) {
                response.setConnectionError(new ParseException(ParseException.CONNECTION_FAILED, 
                    ParseException.ERR_NETWORK, err));
            }

            @Override
            protected void buildRequestBody(OutputStream os) throws IOException {
                if (data.has(REQUEST_BODY_KEY)) {
                    try {
                        os.write(data.get(REQUEST_BODY_KEY).toString().getBytes("UTF-8"));
                    } catch (JSONException ex) {
                        throw new IllegalArgumentException("Unable to read request body from json object. Error:"
                                + ex.getMessage());
                    }
                } else {
                    super.buildRequestBody(os);
                }
            }
        };
        request.setReadResponseForErrors(true);
        request.setDuplicateSupported(true);
        return request;
    }

    /**
     * Adds the default headers (e.g., {@link ParseConstants#HEADER_APPLICATION_ID}
     * and {@link ParseConstants#HEADER_CLIENT_KEY}) associated with Parse REST API calls.
     * The content type is also set to {@link ParseConstants#CONTENT_TYPE_JSON} by default
     * and can be overruled in {@link #setUpRequest(com.codename1.io.ConnectionRequest)}.
     * @throws ParseException if anything goes wrong.
     */
    protected void setupDefaultHeaders() throws ParseException {
        try {
            headers.put(ParseConstants.HEADER_APPLICATION_ID, Parse.getApplicationId());
            headers.put(ParseConstants.HEADER_CLIENT_KEY, Parse.getClientKey());
            // Although doc. states that json content type is only needed for PUT and POST
            // requests, it turns out that other commands that didn't require 
            // an explicit json content type in Parse.com now require it in the open source Parse server.
            // Hence, it is set here in the base command class by default.
            headers.put(ParseConstants.HEADER_CONTENT_TYPE, ParseConstants.CONTENT_TYPE_JSON);
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
        }
    }

    /**
     * Create a Parse API URL using the provided data.
     * 
     * @param endPoint The end point
     * @param objectId The optional objectId
     * @return The Parse API URL of the format {@code https://api.parse.com/<endpoint>[/<objectId>]}.
     */
    static protected String getUrl(final String endPoint, final String objectId) {
        String url = Parse.getParseAPIUrl(endPoint) + (!Parse.isEmpty(objectId) ? "/" + objectId : "");

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Request URL: " + url);
        }

        return url;
    }

    /**
     * Sets the message body data for the HTTP request.
     * 
     * @param data The message body to be set.
     * @throws ParseException if anything goes wrong.
     */
    public void setMessageBody(JSONObject data) throws ParseException {
        try {
            this.data.put(REQUEST_BODY_KEY, data);
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
        }
    }

    /**
     * Adds the specified key-value pair as an argument to the HTTP request.
     * 
     * @param key The key of the argument.
     * @param value The value for {@code key}.
     * @throws ParseException if anything goes wrong.
     */
    public void addArgument(final String key, final String value) throws ParseException {
        try {
            this.data.put(key, value);
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
        }
    }
    
    /**
     * Sets a callback to be notified of the progress of this command when it 
     * is performed.
     * 
     * @param progressCallback The callback to be set. It will replace any previously 
     * set callback.
     */
    public void setProgressCallback(final ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }
}
