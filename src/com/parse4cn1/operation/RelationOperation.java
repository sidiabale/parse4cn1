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

import java.util.HashSet;
import java.util.Set;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseRelation;
import com.parse4cn1.encode.ParseObjectEncodingStrategy;
import com.parse4cn1.util.ParseEncoder;

public class RelationOperation<T extends ParseObject> implements ParseFieldOperation {

    private String targetClass;
    private Set<ParseObject> relationsToAdd;
    private Set<ParseObject> relationsToRemove;

    public RelationOperation(Set<T> newRelationsToAdd, Set<T> newRelationsToRemove) {
        this.targetClass = null;
        this.relationsToAdd = new HashSet<ParseObject>();
        this.relationsToRemove = new HashSet<ParseObject>();

        if (newRelationsToAdd != null) {
            for (ParseObject object : newRelationsToAdd) {
                addParseObjectToSet(object, this.relationsToAdd);

                if (this.targetClass == null) {
                    this.targetClass = object.getClassName();
                } else if (!this.targetClass.equals(object.getClassName())) {
                    throw new IllegalArgumentException(
                            "All objects in a relation must be of the same class.");
                }

            }

        }

        if (newRelationsToRemove != null) {
            for (ParseObject object : newRelationsToRemove) {
                addParseObjectToSet(object, this.relationsToRemove);

                if (this.targetClass == null) {
                    this.targetClass = object.getClassName();
                } else if (!this.targetClass.equals(object.getClassName())) {
                    throw new IllegalArgumentException(
                            "All objects in a relation must be of the same class.");
                }

            }

        }

        if (this.targetClass == null) {
            throw new IllegalArgumentException(
                    "Cannot create a ParseRelationOperation with no objects.");
        }
    }

    private void addParseObjectToSet(ParseObject obj, Set<ParseObject> set) {

        if (obj.getObjectId() == null) {
            set.add(obj);
            return;
        }

        for (ParseObject existingObject : set) {
            if (obj.getObjectId().equals(existingObject.getObjectId())) {
                set.remove(existingObject);
            }
        }
        set.add(obj);
    }

    public String getTargetClass() {
        return this.targetClass;
    }

    public JSONArray convertSetToArray(Set<ParseObject> set, ParseObjectEncodingStrategy objectEncoder) throws ParseException {
        JSONArray array = new JSONArray();
        for (ParseObject obj : set) {
            array.put(ParseEncoder.encode(obj, objectEncoder));
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object apply(Object oldValue, ParseObject parseObject, String key) {
        ParseRelation<T> relation = null;

        if (oldValue == null) {
            relation = new ParseRelation<T>(parseObject, key);
            relation.setTargetClass(this.targetClass);
        } else if ((oldValue instanceof ParseRelation)) {
            relation = (ParseRelation<T>) oldValue;
            if ((this.targetClass != null) && (relation.getTargetClass() != null)) {
                if (!relation.getTargetClass().equals(this.targetClass)) {
                    throw new IllegalArgumentException(
                            "Related object object must be of class "
                            + relation.getTargetClass() + ", but "
                            + this.targetClass + " was passed in.");
                }

                relation.setTargetClass(this.targetClass);
            }

        } else {
            throw new IllegalArgumentException(
                    "Operation is invalid after previous operation.");
        }
        return relation;
    }

    @Override
    public JSONObject encode(ParseObjectEncodingStrategy objectEncoder) throws ParseException {

        JSONObject adds = null;
        JSONObject removes = null;

        if (this.relationsToAdd.size() > 0) {
            adds = new JSONObject();
            try {
                adds.put("__op", "AddRelation");
                adds.put("objects", convertSetToArray(this.relationsToAdd, objectEncoder));
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ex);
            }
        }

        if (this.relationsToRemove.size() > 0) {
            removes = new JSONObject();
            try {
                removes.put("__op", "RemoveRelation");
                removes.put("objects", convertSetToArray(this.relationsToRemove, objectEncoder));
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ex);
            }
        }

        if ((adds != null) && (removes != null)) {
            JSONObject result = new JSONObject();
            try {
                result.put("__op", "Batch");
                JSONArray ops = new JSONArray();
                ops.put(adds);
                ops.put(removes);
                result.put("ops", ops);
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ex);
            }
            return result;
        }

        if (adds != null) {
            return adds;
        }

        if (removes != null) {
            return removes;
        }

        throw new IllegalArgumentException(
                "A ParseRelationOperation was created without any data.");
    }
}
