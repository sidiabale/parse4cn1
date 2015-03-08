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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.encode.ParseObjectEncodingStrategy;
import com.parse4cn1.operation.RelationOperation;
import com.parse4cn1.util.ParseDecoder;

public class ParseRelation<T extends ParseObject> {

    private ParseObject parent;
    private String key;
    private String targetClass;
    private Set<T> knownObjects = new HashSet<T>();

    @SuppressWarnings("unchecked")
    public ParseRelation(JSONObject jsonObject) {
        this.parent = null;
        this.key = null;
        this.targetClass = jsonObject.optString("className", null);
        JSONArray objectsArray = jsonObject.optJSONArray("objects");
        if (objectsArray != null) {
            for (int i = 0; i < objectsArray.length(); i++) {
                this.knownObjects.add((T) ParseDecoder.decode(objectsArray
                        .optJSONObject(i)));
            }
        }
    }

    public ParseRelation(String targetClass) {
        this.parent = null;
        this.key = null;
        this.targetClass = targetClass;
    }

    public ParseRelation(ParseObject parent, String key) {
        this.parent = parent;
        this.key = key;
        this.targetClass = null;
    }

    public String getTargetClass() {
        return this.targetClass;
    }

    public void setTargetClass(String className) {
        this.targetClass = className;
    }

    void ensureParentAndKey(ParseObject someParent, String someKey) {

        if (this.parent == null) {
            this.parent = someParent;
        }

        if (this.key == null) {
            this.key = someKey;
        }

        if (this.parent != someParent) {
            throw new IllegalStateException(
                    "Internal error. One ParseRelation retrieved from two different ParseObjects.");
        }

        if (!this.key.equals(someKey)) {
            throw new IllegalStateException(
                    "Internal error. One ParseRelation retrieved from two different keys.");
        }

    }

    public void add(T object) {

        this.knownObjects.add(object);

        /*
         RelationOperation<T> operation = new RelationOperation<T>(
         Collections.singleton(object), null);
         */
        RelationOperation<T> operation = new RelationOperation<T>(
                Collections.unmodifiableSet(this.knownObjects), null);

        this.targetClass = operation.getTargetClass();
        this.parent.performOperation(this.key, operation);

    }

    public void remove(T object) {

        this.knownObjects.remove(object);

        RelationOperation<T> operation = new RelationOperation<T>(null,
                Collections.singleton(object));

        this.targetClass = operation.getTargetClass();
        this.parent.performOperation(this.key, operation);
    }

    public ParseQuery<T> getQuery() {

        ParseQuery<T> query;
        if (this.targetClass == null) {
            query = ParseQuery.getQuery(this.parent.getClassName());
            query.redirectClassNameForKey(this.key);
        } else {
            query = ParseQuery.getQuery(this.targetClass);
        }
        query.whereRelatedTo(this.parent, this.key);
        return query;

    }

    public JSONObject encodeToJSON(ParseObjectEncodingStrategy objectEncoder) throws JSONException {
        JSONObject relation = new JSONObject();
        relation.put("__type", "Relation");
        relation.put("className", this.targetClass);
        JSONArray knownObjectsArray = new JSONArray();
        for (ParseObject knownObject : this.knownObjects) {
            try {
                knownObjectsArray.put(objectEncoder.encodeRelatedObject(knownObject));
            } catch (Exception e) {
            }
        }
        relation.put("objects", knownObjectsArray);
        return relation;
    }
}
