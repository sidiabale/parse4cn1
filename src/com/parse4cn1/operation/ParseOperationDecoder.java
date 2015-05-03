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
package com.parse4cn1.operation;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.util.Logger;
import com.parse4cn1.encode.ParseDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This class defines methods for decoding various Parse operations.
 */
public class ParseOperationDecoder {

    private static abstract interface IParseOperationDecoder {
        public abstract ParseOperation decode(JSONObject paramJSONObject) throws JSONException;
    }

    private static final Logger LOGGER = Logger.getInstance();

    private static final Map<String, IParseOperationDecoder> opDecoderMap
            = new HashMap<String, IParseOperationDecoder>();

    /**
     * Registers a (custom) decoder for the specified Parse operation
     * @param opName The name of the operation
     * @param decoder The decoder to be used to decode {@code opName}
     */
    private static void registerDecoder(String opName, IParseOperationDecoder decoder) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registering '" + opName + "' decoder");
        }
        opDecoderMap.put(opName, decoder);
    }

    /**
     * Registers the 'standard' decoders for all known/supported Parse operations.
     */
    public static void registerDefaultDecoders() {
        registerDecoder("Delete", new IParseOperationDecoder() {
            public ParseOperation decode(JSONObject object) throws JSONException {
                return new DeleteFieldOperation();
            }
        });
        registerDecoder("Increment", new IParseOperationDecoder() {
            public ParseOperation decode(JSONObject object) throws JSONException {
                return new IncrementFieldOperation(object.opt("amount"));
            }
        });
        registerDecoder("Add", new IParseOperationDecoder() {
            public ParseOperation decode(JSONObject object) throws JSONException {
                return new AddToArrayOperation(object.opt("objects"));
            }
        });
        registerDecoder("AddUnique", new IParseOperationDecoder() {
            public ParseOperation decode(JSONObject object) throws JSONException {
                return new AddUniqueToArrayOperation(object.opt("objects"));
            }
        });
        registerDecoder("Remove", new IParseOperationDecoder() {
            public ParseOperation decode(JSONObject object) throws JSONException {
                return new RemoveFromArrayOperation(object.opt("objects"));
            }
        });
        registerDecoder("AddRelation", new IParseOperationDecoder() {
            public ParseOperation decode(JSONObject object) throws JSONException {
                JSONArray objectsArray = object.optJSONArray("objects");
                List objectsList = (List) ParseDecoder.decode(objectsArray);
                return new RelationOperation(new HashSet(objectsList),
                        RelationOperation.ERelationType.AddRelation);
            }
        });
        registerDecoder("RemoveRelation", new IParseOperationDecoder() {
            public ParseOperation decode(JSONObject object) throws JSONException {
                JSONArray objectsArray = object.optJSONArray("objects");
                List objectsList = (List) ParseDecoder.decode(objectsArray);
                return new RelationOperation(new HashSet(objectsList),
                        RelationOperation.ERelationType.RemoveRelation);
            }
        });
    }

    /**
     * Decodes the provided Parse operation data based on the {@value ParseConstants#KEYWORD_OP} 
     * defined in the {@code encoded} object.
     * 
     * @param encoded A JSON object defining a Parse operation.
     * @return The decoded ParseOperation.
     * @throws JSONException if any JSON error occurs.
     */
    public static ParseOperation decode(JSONObject encoded) throws JSONException {
        String op = encoded.optString(ParseConstants.KEYWORD_OP);
        IParseOperationDecoder decoder = (IParseOperationDecoder) opDecoderMap.get(op);
        if (decoder == null) {
            throw new RuntimeException("Unable to decode operation of type " + op);
        }
        return decoder.decode(encoded);
    }
}
