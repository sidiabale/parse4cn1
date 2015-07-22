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
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseRelation;
import com.parse4cn1.encode.IParseObjectEncodingStrategy;
import static com.parse4cn1.operation.RelationOperation.ERelationType.AddRelation;
import com.parse4cn1.encode.ParseEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class defines an operation to create or remove a relation between ParseObjects.
 * @param <T> The type of ParseObject for which the relation operation is to be defined.
 */
public class RelationOperation<T extends ParseObject> implements ParseOperation {

    public enum ERelationType {
        AddRelation,
        RemoveRelation;
    }
    
    private String targetClass;
    private Set<ParseObject> relations;
    private ERelationType relationType;

    public RelationOperation(final Set<T> relations, final ERelationType relationType) {
        
        if (relationType == null) {
            throw new IllegalArgumentException("Null relation type");
        }
        
        HashMap<String, ParseObject> uniqueRelations = new HashMap<String, ParseObject>();
        this.targetClass = null;
                
        if (relations != null) {
            for (ParseObject object : relations) {
                if (this.targetClass == null) {
                    this.targetClass = object.getClassName();
                } else if (!this.targetClass.equals(object.getClassName())) {
                    throw new IllegalArgumentException(
                        "All objects in a relation must be of the same class.");
                }

                if (object.getObjectId() == null || 
                        uniqueRelations.containsKey(object.getObjectId())) {
                    throw new IllegalArgumentException(
                        "All objects in a relation must have a unique non-null objectId");
                }
                uniqueRelations.put(object.getObjectId(), object);
            }
        }

        if (this.targetClass == null) {
            throw new IllegalArgumentException(
                    "Cannot create a ParseRelationOperation with no objects.");
        }
        
        this.relationType = relationType;
        this.relations = new HashSet<ParseObject>(uniqueRelations.values());
    }

    public String getTargetClass() {
        return this.targetClass;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object apply(Object oldValue, ParseObject parseObject, String key) {
        ParseRelation<T> relation = null;

        if (oldValue == null) {
            relation = new ParseRelation<T>(parseObject, key, this.targetClass);
        } else if ((oldValue instanceof ParseRelation)) {
            relation = (ParseRelation<T>) oldValue;
            if ((this.targetClass != null) && (relation.getTargetClass() != null)) {
                if (!relation.getTargetClass().equals(this.targetClass)) {
                    throw new IllegalArgumentException(
                            "Related object object must be of class "
                            + relation.getTargetClass() + ", but "
                            + this.targetClass + " was passed in.");
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "Operation is invalid after previous operation.");
        }
        return relation;
    }

    @Override
    public JSONObject encode(IParseObjectEncodingStrategy objectEncoder) throws ParseException {

        JSONObject newRelations = null;

        if (this.relations.size() > 0) {
            newRelations = new JSONObject();
            try {
                newRelations.put(ParseConstants.KEYWORD_OP, 
                        (relationType == AddRelation) ? "AddRelation" : "RemoveRelation");
                newRelations.put("objects", convertSetToArray(this.relations, objectEncoder));
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
            }
        }

        if (newRelations == null) {
            throw new IllegalArgumentException(
                "A ParseRelationOperation was created without any data.");   
        }

        return newRelations;
    }
    
    
    private JSONArray convertSetToArray(Set<ParseObject> set, 
            IParseObjectEncodingStrategy objectEncoder) throws ParseException {
        JSONArray array = new JSONArray();
        for (ParseObject obj : set) {
            array.put(ParseEncoder.encode(obj, objectEncoder));
        }
        return array;
    }
}
