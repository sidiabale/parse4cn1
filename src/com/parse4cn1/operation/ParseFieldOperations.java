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
import com.parse4cn1.ParseObject;
import com.parse4cn1.util.Logger;
import com.parse4cn1.util.ParseDecoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ParseFieldOperations {

    private static final Logger LOGGER = Logger.getInstance();

    static Map<String, ParseFieldOperationFactory> opDecoderMap
            = new HashMap<String, ParseFieldOperationFactory>();

    private static void registerDecoder(String opName, ParseFieldOperationFactory factory) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Registering '" + opName + "' decoder");
        }
        opDecoderMap.put(opName, factory);
    }

    private static abstract interface ParseFieldOperationFactory {

        public abstract ParseFieldOperation decode(JSONObject paramJSONObject) throws JSONException;
    }

    public static void registerDefaultDecoders() {
        // TODO: Review this originally commented out code
        /*
         registerDecoder("Batch", new ParseFieldOperationFactory() {
         public ParseFieldOperation decode(JSONObject object) throws JSONException {
         ParseFieldOperation op = null;
         JSONArray ops = object.getJSONArray("ops");
         for (int i = 0; i < ops.length(); i++) {
         ParseFieldOperation nextOp = ParseFieldOperations.decode(
         ops.getJSONObject(i));
         op = nextOp.mergeWithPrevious(op);
         }
         return op;
         }
         });
         */

        registerDecoder("Delete", new ParseFieldOperationFactory() {
            public ParseFieldOperation decode(JSONObject object) throws JSONException {
                return new DeleteFieldOperation();
            }
        });
        registerDecoder("Increment", new ParseFieldOperationFactory() {
            public ParseFieldOperation decode(JSONObject object) throws JSONException {
                return new IncrementFieldOperation(object.opt("amount"));
            }
        });
        registerDecoder("Add", new ParseFieldOperationFactory() {
            public ParseFieldOperation decode(JSONObject object) throws JSONException {
                return new AddOperation(object.opt("objects"));
            }
        });
        registerDecoder("AddUnique", new ParseFieldOperationFactory() {
            public ParseFieldOperation decode(JSONObject object) throws JSONException {
                return new AddUniqueOperation(object.opt("objects"));
            }
        });
        registerDecoder("Remove", new ParseFieldOperationFactory() {
            public ParseFieldOperation decode(JSONObject object) throws JSONException {
                return new RemoveFieldOperation(object.opt("objects"));
            }
        });
        registerDecoder("AddRelation", new ParseFieldOperationFactory() {
            public ParseFieldOperation decode(JSONObject object) throws JSONException {
                JSONArray objectsArray = object.optJSONArray("objects");
                List objectsList = (List) ParseDecoder.decode(objectsArray);
                return new RelationOperation(new HashSet(objectsList),
                        null);
            }
        });
        registerDecoder("RemoveRelation", new ParseFieldOperationFactory() {
            public ParseFieldOperation decode(JSONObject object) throws JSONException {
                JSONArray objectsArray = object.optJSONArray("objects");
                List objectsList = (List) ParseDecoder.decode(objectsArray);
                return new RelationOperation(null,
                        new HashSet(objectsList));
            }
        });
    }

    public static ParseFieldOperation decode(JSONObject encoded) throws JSONException {
        String op = encoded.optString("__op");
        ParseFieldOperationFactory factory = (ParseFieldOperationFactory) opDecoderMap.get(op);
        if (factory == null) {
            throw new RuntimeException("Unable to decode operation of type " + op);
        }
        return factory.decode(encoded);
    }
}
