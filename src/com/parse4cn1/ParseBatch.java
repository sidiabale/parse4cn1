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

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Util;
import com.codename1.util.StringUtil;
import com.parse4cn1.command.ParseCommand;
import com.parse4cn1.command.ParsePostCommand;
import com.parse4cn1.command.ParseResponse;
import com.parse4cn1.util.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class enables issuing of batch requests.
 * <p>
 * Batching of requests helps reduce the amount of network round trips
 * performed.
 */
public class ParseBatch {

    private static final Logger LOGGER = Logger.getInstance();
    private final List<ParseObject> parseObjects;
    private final JSONArray data;
    private List<ParseException> results;
    private boolean succeeded = false;

    /**
     * An enumeration of batch operation types.
     */
    public enum EBatchOpType {

        CREATE,
        UPDATE,
        DELETE
    }

    /**
     * Creates a new ParseBatch instance.
     *
     * @return The newly created object.
     */
    public static ParseBatch create() {
        return new ParseBatch();
    }

    /**
     * Adds an object to the batch to be {@link #execute() executed}.
     *
     * @param object The object to be added to the batch.
     * @param opType The type of operation to be performed on {@code object}.
     * @return {@code this} to enable chaining.
     * @throws ParseException if the object does not meet the constraints for
     * {@code opType}, for example, an objectId is required for
     * {@link EBatchOpType#UPDATE} and {@link EBatchOpType#DELETE} but should
     * not exist for {@link EBatchOpType#CREATE}. because it already has an
     * objectId.
     */
    public ParseBatch addObject(final ParseObject object,
            final EBatchOpType opType) throws ParseException {
        return addObjects(Arrays.asList(object), opType);
    }

    /**
     * Adds multiple objects to the batch to be {@link #execute() executed}.
     *
     * @param objects The objects to be added to the batch.
     * @param opType The type of operation to be performed on ALL
     * {@code objects}.
     * @return {@code this} to enable chaining.
     * @throws ParseException if any of the objects does not meet the
     * constraints for {@code opType}, for example, an objectId is required for
     * {@link EBatchOpType#UPDATE} and {@link EBatchOpType#DELETE} but should
     * not exist for {@link EBatchOpType#CREATE}. because it already has an
     * objectId.
     */
    public ParseBatch addObjects(final Collection<? extends ParseObject> objects,
            final EBatchOpType opType) throws ParseException {

        final String urlPath =  StringUtil.replaceAll(Util.getURLPath(Parse.getApiEndpoint()), "/", "");
        final String pathPrefix = "/" + (!Parse.isEmpty(urlPath) ? urlPath + "/" : "");

        final String method = opTypeToHttpMethod(opType);
        
        for (ParseObject object : objects) {
            validate(object, opType);
            final JSONObject objData = new JSONObject();
            try {
                objData.put("method", method);
                objData.put("path", pathPrefix + getObjectPath(object, opType));
                objData.put("body", object.getParseData());
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, 
                        ParseException.ERR_PREPARING_REQUEST, ex);
            }
            data.put(objData);
            parseObjects.add(object);
        }
        return this;
    }

    /**
     * Executes the batch operation. 
     * <p>
     * All ParseObjects in the batch for which the 
     * requested operation was successful will also be updated with the response 
     * received from the server.
     *
     * @return {@code true} if the all the operations in the batch are
     * successfully executed. This is the same value returned by
     * {@link #isSucceeded()}.
     * @throws ParseException if executing the batch operation itself fails, for
     * example due to batch size exceeding limit.
     */
    public boolean execute() throws ParseException {
        
        final ParseCommand command = new ParsePostCommand("batch");
        final JSONObject payload = new JSONObject();
        try {
            payload.put("requests", data);
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, 
                    ParseException.ERR_PREPARING_REQUEST, ex);
        }
        
        command.setMessageBody(payload);
        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            processParseResponse(response);
        } else {
            succeeded = false;
            LOGGER.error("Request failed.");
            throw response.getException();
        }
        return isSucceeded();
    }

    /**
     * Indicates the status of the batch operation.
     *
     * @return {@code true} if the requested command is successfully performed
     * on each object in the batch; otherwise returns {@code false}.
     */
    public boolean isSucceeded() {
        return succeeded;
    }

    /**
     * Retrieves the error returned per ParseObject by the Parse server when the
     * batch was executed.
     *
     * @return A read-only map of all ParseObjects in the batch to result returned 
     * by the server. A null value indicates that the operation was successful 
     * for that key (ParseObject).
     * @throws ParseException if this method is invoked before {@link #execute()}.
     */
    public Map<ParseObject, ParseException> getErrors() throws ParseException {
        
        if (results == null) {
            throw new ParseException(ParseException.OTHER_CAUSE,
                    "The batch must first be executed");
        }
        
        Map<ParseObject, ParseException> map = new HashMap<ParseObject, ParseException>();
        for (int i = 0; i < parseObjects.size(); ++i) {
            map.put(parseObjects.get(i), results.get(i));
        }
        return Collections.unmodifiableMap(map);
    }

    private static String opTypeToHttpMethod(final EBatchOpType opType)
        throws ParseException {
        
        final String method;
        switch (opType) {
            case DELETE:
                method = "DELETE";
                break;
            case UPDATE:
                method = "PUT";
                break;
            case CREATE:
                method = "POST";
                break;
            default:
                throw new ParseException(ParseException.OPERATION_FORBIDDEN,
                        "Unknown/unsupported opType: " + opType);
        }
        return method;
    }
    
    private static String getObjectPath(final ParseObject object, final EBatchOpType opType) {
        String endpoint = object.getEndPoint();
        if (opType != EBatchOpType.CREATE) {
            endpoint += "/" + object.getObjectId();
        }

        return endpoint;
    }
    
    private ParseBatch() {
        parseObjects = new ArrayList<ParseObject>();
        data = new JSONArray();
    }

    /**
     * Checks if the provided object meets the constraints for the requested operation.
     * @param object The object to be validated.
     * @param opType The kind of operation to be performed on {@code object}.
     * @throws ParseException if the validation fails.
     */
    private static void validate(final ParseObject object, final EBatchOpType opType)
            throws ParseException {
        
        switch (opType) {
            case DELETE: // Deliberate fallthrough
            case UPDATE:
                if (object.getObjectId() == null) {
                    throw new ParseException(ParseException.OPERATION_FORBIDDEN,
                            "Cannot update or delete an object without an objectId.");
                }
                break;
            case CREATE:
                if (object.getObjectId() != null) {
                    throw new ParseException(ParseException.OPERATION_FORBIDDEN,
                            "Cannot create an object already having an objectId.");
                }
                break;
            default:
                throw new ParseException(ParseException.OPERATION_FORBIDDEN,
                        "Unknown/unsupported opType: " + opType);
        }
    }
    
    /**
     * Processes the response received from the server after executing the batch.
     * @param response The response to be processed.
     * @throws ParseException if anything goes wrong.
     */
    private void processParseResponse(final ParseResponse response) throws ParseException {
        succeeded = false;
        results = null;
        
        JSONArray json;
        try {
            json = new JSONArray(new String(response.getResponseData()));
        } catch (JSONException ex) {
            throw new ParseException(
                    "Response could not be converted to a JSONArray", ex); 
        }
        
        if (json.length() != parseObjects.size()) {
            throw new ParseException(ParseException.OTHER_CAUSE,
                 ParseException.ERR_PROCESSING_RESPONSE, new IllegalStateException(
                "Incorrect batch result count. Expected " + parseObjects.size()
                + " results but found " + json.length()));
        }
        
        results = new ArrayList<ParseException>();
        for (int i = 0; i < json.length(); ++i) {
            try {
                JSONObject result = json.getJSONObject(i);
                if (result.has("success")) {
                    
                    results.add(null);
                    final ParseObject parseObject = parseObjects.get(i);
                    final JSONObject resultData = result.getJSONObject("success");
                    parseObject.setData(resultData);
                    
                    if (parseObject.getUpdatedAt() == null) {
                        parseObject.setUpdatedAt(parseObject.getCreatedAt());
                    }
                    
                    if (resultData.length() == 0) {
                        parseObject.reset();;
                    }
                    
                } else if (result.has("error")) {
                    results.add(ParseResponse.getParseError(result.getJSONObject("error")));
                } else {
                  throw new ParseException(ParseException.INVALID_JSON, 
                    "Result '" + result + "' at index " + i 
                            + " neither has a success nor error field");  
                }
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, 
                        ParseException.ERR_PROCESSING_RESPONSE, ex);
            }
        }
        
        succeeded = true;
        for (ParseException ex: results) {
            if (ex != null) {
                succeeded = false;
                break;
            }
        }
    }
}
