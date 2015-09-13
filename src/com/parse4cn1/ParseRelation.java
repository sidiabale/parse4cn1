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

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import com.parse4cn1.encode.IParseObjectEncodingStrategy;
import com.parse4cn1.operation.RelationOperation;
import com.parse4cn1.util.ExternalizableParseObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A class that is used to define, modify and/or access all of the children of a
 * many-to-many relationship. Each instance of ParseRelation is associated with
 * a particular parent object and key.
 *
 * @param <T> The type of {@link ParseObject}
 */
public class ParseRelation<T extends ParseObject> implements Externalizable {

    private ParseObject parent;
    private String key;
    private String targetClass;
    private final Set<T> addedObjects = new HashSet<T>();
    private final Set<T> removedObjects = new HashSet<T>();
    
    /**
     * @return A unique class name.
     */
    public static String getClassName() {
        return "ParseRelation";
    }

    public ParseRelation() {
    }
    
    /**
     * Creates a ParseRelation object from JSON data, for example, retrieved
     * from a Parse API call.
     *
     * @param jsonObject The JSON data that defines the ParseRelation. It must
     * contains at least a {@value ParseConstants#FIELD_CLASSNAME} field for the
     * target class and optionally an "objects" array field.
     */
    @SuppressWarnings("unchecked")
    public ParseRelation(JSONObject jsonObject) {
        this(null, null, jsonObject.optString(ParseConstants.FIELD_CLASSNAME, null));

        if (getTargetClass() == null) {
            throw new IllegalArgumentException("A target class must be specified");
        }
    }

    /**
     * Creates a ParseRelation between the {@code parent} and
     * {@code targetClass} using the specified {@code key} in the parent.
     *
     * @param parent The ParseObject on which this ParseRelation is defined.
     * @param key The key in {@code parent} on which the relation is defined.
     * @param targetClass The name of the ParseObject class whose objects are
     * involved in relation.
     *
     * @throws IllegalArgumentException if the {@code targetClass} is null or
     * the provided {@code parent} and/or {@code key} do not match those
     */
    public ParseRelation(final ParseObject parent, final String key,
            final String targetClass) {

        this.parent = parent;
        this.key = key;
        this.targetClass = targetClass;
    }

    /**
     * @return The target class for this relation.
     */
    public final String getTargetClass() {
        return this.targetClass;
    }

    /**
     * Adds an object to this relation.
     *
     * @param object The object to be added.
     * @throws IllegalArgumentException if {@code object} is null.
     * @throws IllegalStateException if any of the members required to getQuery
     * the relation is uninitialized or mismatching.
     */
    public void add(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot add a null object");
        }

        if (contains(object.getObjectId(), addedObjects)) {
            return;
        }

        this.addedObjects.add(object);
        this.removedObjects.remove(object);

        RelationOperation<T> operation = new RelationOperation<T>(
                Collections.unmodifiableSet(this.addedObjects),
                RelationOperation.ERelationType.AddRelation);

        validate(operation.getTargetClass());
        this.parent.performOperation(this.key, operation);
    }

    /**
     * Removes an object from this relation.
     *
     * @param object The object to be removed.
     * @throws IllegalArgumentException if {@code object} is null.
     * @throws IllegalStateException if any of the members required to remove
     * the relation is uninitialized or mismatching.
     */
    public void remove(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Cannot remove a null object");
        }

        if (contains(object.getObjectId(), removedObjects)) {
            return;
        }

        this.addedObjects.remove(object);
        this.removedObjects.add(object);

        RelationOperation<T> operation = new RelationOperation<T>(
                Collections.unmodifiableSet(this.removedObjects),
                RelationOperation.ERelationType.RemoveRelation);

        validate(operation.getTargetClass());
        this.parent.performOperation(this.key, operation);
    }

    /**
     * Gets a query that can be used to query the objects in this relation.
     *
     * @return the query.
     */
    public ParseQuery<T> getQuery() {

        validate(targetClass);
        ParseQuery<T> query = ParseQuery.getQuery(this.targetClass);
        query.whereRelatedTo(this.parent, this.key);
        return query;
    }

    /**
     * Converts the objects in this relation to JSON.
     *
     * @param objectEncoder The encoder to be used to encode the objects.
     * @return The objects in this relation encoded as a Parse "Relation".
     * @throws JSONException if anything goes wrong with JSON encoding.
     */
    public JSONObject encode(IParseObjectEncodingStrategy objectEncoder) throws JSONException {
        JSONObject relation = new JSONObject();
        relation.put(ParseConstants.KEYWORD_TYPE, "Relation");
        relation.put(ParseConstants.FIELD_CLASSNAME, this.targetClass);
        JSONArray knownObjectsArray = new JSONArray();
        for (ParseObject knownObject : this.addedObjects) {
            try {
                knownObjectsArray.put(objectEncoder.encodeRelatedObject(knownObject));
            } catch (ParseException e) {
                throw new JSONException(e);
            }
        }
        relation.put("objects", knownObjectsArray);
        return relation;
    }
    
    /**
     * @see com.codename1.io.Externalizable
     */
    public int getVersion() {
        return Parse.getSerializationVersion();
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void externalize(DataOutputStream out) throws IOException {
        /* 
        Note that ParseRelations are applied on ParseObjects and since only saved
        ParseObjects are serialized by design, the only piece of information 
        needed to reconstruct the relation is the targetClass. However,
        for completeness, we include other fields except the parent since
        serializing the parent will result in an infinite loop. If there's 
        ever a usecase where a ParseRelation is deemed useful outside a ParseObject,
        a smart way to store the parent would be implemented.
        */
        Util.writeUTF(targetClass, out);
        Util.writeUTF(key, out);
        Util.writeObject(setToArray(addedObjects), out);
        Util.writeObject(setToArray(removedObjects), out);
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void internalize(int version, DataInputStream in) throws IOException {
        targetClass = Util.readUTF(in);
        key = Util.readUTF(in);
        arrayToSet((Object[]) Util.readObject(in), addedObjects);
        arrayToSet((Object[]) Util.readObject(in), removedObjects);
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public String getObjectId() {
        return getClassName();
    }
    
    private ExternalizableParseObject[] setToArray(final Set<T> in) {
        if (in == null) {
            return null;
        }
        
        ExternalizableParseObject[] externalizables = new ExternalizableParseObject[in.size()];
        
        int i = 0;
        for (T obj : in) {
            externalizables[i++] = obj.asExternalizable();
        }
        return externalizables;
    }
    
    private void arrayToSet(final Object[] in, final Set<T> out) {
        if (in == null || out == null) {
            return;
        }
        
        out.clear();
        for (Object obj: in) {
            ExternalizableParseObject<T> externalizable = (ExternalizableParseObject)obj; 
            out.add(externalizable.getParseObject());
        }
    }

    /**
     * Checks if an element with {@code objectId} is contained in
     * {@code collection}.
     *
     * @param objectId The objectId to be checked for.
     * @param collection The collection to be searched for {@code objectId}.
     * @return {@code true} if an element with {@code objectId} is found in
     * {@code collection}. Otherwise, returns {@code false}.
     */
    private boolean contains(final String objectId, final Collection<T> collection) {
        if (objectId == null) {
            throw new IllegalArgumentException("Null object id");
        }

        for (ParseObject object : collection) {
            if (objectId.equals(object.getObjectId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the state of this ParseRelation.
     *
     * @param targetClass The new target class to be set if none was previously
     * defined.
     * @throws IllegalStateException if any of the required relation fields is
     * null or if the provided {@code targetClass} is different from the
     * previously defined one.
     */
    private void validate(final String targetClass) {

        if (targetClass == null) {
            throw new IllegalStateException("Target class is null");
        }

        if (this.parent == null) {
            throw new IllegalStateException("Parent ParseObject is null");
        }

        if (this.key == null) {
            throw new IllegalStateException("Relation key is null");
        }

        if (this.targetClass == null) {
            this.targetClass = targetClass;
        }

        if (!this.targetClass.equals(targetClass)) {
            throw new IllegalStateException(
                    "Target class mismatch. Expected '" + this.targetClass
                    + "' but found '" + targetClass + "'");
        }
    }
}
