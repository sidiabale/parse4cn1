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
import com.parse4cn1.Parse.IPersistable;
import com.parse4cn1.callback.GetCallback;
import com.parse4cn1.command.ParseCommand;
import com.parse4cn1.command.ParseDeleteCommand;
import com.parse4cn1.command.ParseGetCommand;
import com.parse4cn1.command.ParsePostCommand;
import com.parse4cn1.command.ParsePutCommand;
import com.parse4cn1.command.ParseResponse;
import com.parse4cn1.encode.PointerEncodingStrategy;
import com.parse4cn1.operation.AddOperation;
import com.parse4cn1.operation.AddUniqueOperation;
import com.parse4cn1.operation.DeleteFieldOperation;
import com.parse4cn1.operation.IncrementFieldOperation;
import com.parse4cn1.operation.ParseOperation;
import com.parse4cn1.operation.RelationOperation;
import com.parse4cn1.operation.RemoveOperation;
import com.parse4cn1.operation.SetFieldOperation;
import com.parse4cn1.util.Logger;
import com.parse4cn1.util.ParseDecoder;
import com.parse4cn1.util.ParseRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParseObject implements IPersistable {

    private static final Logger LOGGER = Logger.getInstance();

    private String objectId;
    private String className;
    private String endPoint;
    boolean dirty = false;

    private Map<String, Object> data;
    private Map<String, ParseOperation> operations;
    private List<String> dirtyKeys;

    private Date updatedAt;
    private Date createdAt;

    protected ParseObject(String className) {

        if (className == null) {
            LOGGER.error("You must specify a Parse class name when creating a new ParseObject.");
            throw new IllegalArgumentException(
                    "You must specify a Parse class name when creating a new ParseObject.");
        }
        
        this.className = className;
        this.data = new Hashtable<String, Object>();
        this.operations = new Hashtable<String, ParseOperation>();
        this.dirtyKeys = new ArrayList<String>();
        setEndPoint(toEndPoint(className));
    }

    public static ParseObject create(String className) {
        return new ParseObject(className);
    }
    
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    
    public String getObjectId() {
        return this.objectId;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public String getClassName() {
        return this.className;
    }
    
    public String getEndPoint() {
        return this.endPoint;
    }

    public Set<String> keySet() {
        return Collections.unmodifiableSet(this.data.keySet());
    }

    public ParseFile getParseFile(String key) {

        if (!this.data.containsKey(key)) {
            return null;
        }
        Object value = this.data.get(key);
        if (!(value instanceof ParseFile)) {
            logGetValueError("getParseFile", key, value);
            return null;
        }
        return (ParseFile) value;
    }

    public ParseGeoPoint getParseGeoPoint(String key) {

        if (!this.data.containsKey(key)) {
            return null;
        }
        Object value = this.data.get(key);
        if (!(value instanceof ParseGeoPoint)) {
            logGetValueError("getParseGeoPoint", key, value);
            return null;
        }
        return (ParseGeoPoint) value;
    }

    public Date getDate(String key) {

        if (!this.data.containsKey(key)) {
            return null;
        }
        Object value = this.data.get(key);
        if (!(value instanceof Date)) {
            logGetValueError("getDate", key, value);
            return null;
        }
        return (Date) value;
    }

    public Boolean getBoolean(String key) {

        if (!this.data.containsKey(key)) {
            return null;
        }

        Object value = this.data.get(key);
        if (!(value instanceof Boolean)) {
            logGetValueError("getBoolean", key, value);
            return null;
        }
        return (Boolean) value;
    }

    public Integer getInt(String key) {
        if (!this.data.containsKey(key)) {
            return null;
        }

        Object value = this.data.get(key);
        if (!(value instanceof Integer)) {
            logGetValueError("getInt", key, value);
            return null;
        }
        return (Integer) value;
    }

    public Double getDouble(String key) {
        if (!this.data.containsKey(key)) {
            return null;
        }

        Object value = this.data.get(key);
        if (!(value instanceof Double)) {
            logGetValueError("getDouble", key, value);
            return null;
        }
        return (Double) value;
    }

    public Long getLong(String key) {
        if (!this.data.containsKey(key)) {
            return null;
        }

        Object value = this.data.get(key);
        if (!(value instanceof Long)) {
            logGetValueError("getLong", key, value);
            return null;
        }
        return (Long) value;
    }

    public String getString(String key) {

        if (!this.data.containsKey(key)) {
            return null;
        }
        Object value = this.data.get(key);
        if (!(value instanceof String)) {
            logGetValueError("getString", key, value);
            return null;
        }
        return (String) value;
    }

    public <T> List<T> getList(String key) {

        if (!this.data.containsKey(key)) {
            return null;
        }
        Object value = this.data.get(key);

        if ((value instanceof JSONArray)) {
            value = ParseDecoder.decode(value);
            put(key, value);
        }

        if (!(value instanceof List)) {
            return null;
        }

        List<T> returnValue = (List<T>) value;
        return returnValue;
    }
    
    public ParseObject getParseObject(String key) {
        if (!this.data.containsKey(key)) {
            return null;
        }
        Object value = this.data.get(key);
        if (!(value instanceof ParseObject)) {
            logGetValueError("getParseObject", key, value);
            return null;
        }
        return (ParseObject) value;
    }

    public Object get(String key) {

        if (!this.data.containsKey(key)) {
            return null;
        }

        Object value = this.data.get(key);
        return value;
    }

    /**
     * Creates a <i>temporary</i> ParseRelation object for defining/updating
     * relations associated with {@code key}. 
     * 
     * <p><b>Note:</b> Relations defined via the returned object will override 
     * any existing relations previously defined for the same {@code key}!</p>
     * 
     * @param <T> A {@link ParseObject} or its sub-type.
     * @param key The key associated with this relation.
     * @return The newly created object.
     */
    public <T extends ParseObject> ParseRelation<T> getRelation(String key) {
        String targetClass = null;
        if (has(key)) {
            targetClass = ((ParseRelation<T>) get(key)).getTargetClass();
        }
        return new ParseRelation<T>(this, key, targetClass);
    }

    public boolean has(String key) {
        return containsKey(key);
    }

    public boolean containsKey(String key) {
        return this.data.containsKey(key);
    }

    public boolean hasSameId(ParseObject other) {
        return (getClassName() != null) && (getObjectId() != null)
                && (getClassName().equals(other.getClassName()))
                && (getObjectId().equals(other.getObjectId()));
    }

    public void addToArrayField(String key, Collection<?> values) {
        AddOperation operation = new AddOperation(values);
        performOperation(key, operation);
    }

    public void addUniqueToArrayField(String key, Collection<?> values) {
        AddUniqueOperation operation = new AddUniqueOperation(values);
        performOperation(key, operation);
    }

    public void removeFromArrayField(String key, Collection<?> values) {
        RemoveOperation operation = new RemoveOperation(values);
        performOperation(key, operation);
    }

    public void put(String key, Object value) {

        if (key == null) {
            LOGGER.error("key may not be null.");
            throw new IllegalArgumentException("key may not be null.");
        }

        if (value == null) {
            LOGGER.error("value may not be null.");
            throw new IllegalArgumentException("value may not be null.");
        }

        if (value instanceof IPersistable && ((IPersistable) value).isDirty()) {
            LOGGER.error("Persistable object must be saved before being set on a ParseObject.");
            throw new IllegalArgumentException(
                    "Persistable object must be saved before being set on a ParseObject.");
        }

        if (Parse.isReservedKey(key)) {
            LOGGER.error("reserved value for key: " + key);
            throw new IllegalArgumentException("reserved value for key: "
                    + key);
        }

        if (!Parse.isValidType(value)) {
            LOGGER.error("invalid type for value: " + value.getClass().toString());
            throw new IllegalArgumentException("invalid type for value: "
                    + value.getClass().toString());
        }

        performOperation(key, new SetFieldOperation(value));
    }


    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
       this.dirty = dirty;
    }
    
    @Override
    public void save() throws ParseException {

        if (!isDirty()) {
            Logger.getInstance().warn("Ignoring request to save unchanged/empty"
                    + " object");
            return;
        }
        
        validateSave();

        ParseCommand command;
        if (objectId == null) {
            command = new ParsePostCommand(getEndPoint());
        } else {
            command = new ParsePutCommand(getEndPoint(), getObjectId());
        }
        
        performSave(command);
    }
    
    public void deleteField(String key) {

        if (has(key)) {
            if (objectId != null) {
                // if the object was saved before, we need to add the delete operation
                operations.put(key, new DeleteFieldOperation());
            } else {
                operations.remove(key);
            }
            data.remove(key);
            dirtyKeys.add(key);
            setDirty(true);
        }
    }

    public void decrement(String key) {
        increment(key, -1);
    }

    public void increment(String key) {
        increment(key, 1);
    }

    public void increment(String key, Object amount) {
        IncrementFieldOperation operation = new IncrementFieldOperation(amount);
        Object oldValue = data.get(key);
        Object newValue;
        try {
            newValue = operation.apply(oldValue, this, key);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        data.put(key, newValue);
        operations.put(key, operation);
        dirtyKeys.add(key);
        setDirty(true);
    }

    public void delete() throws ParseException {

        if (getObjectId() == null) {
            LOGGER.error("Attempting to delete an object without an objectId.");
            throw new ParseException(ParseException.MISSING_OBJECT_ID,
                    "Attempting to delete an object without an objectId.");
        }

        ParseCommand command = new ParseDeleteCommand(getEndPoint(), getObjectId());
        ParseResponse response = command.perform();
        if (response.isFailed()) {
            throw response.getException();
        }
        
        reset();
    }

    public JSONObject getParseData() throws ParseException {
        JSONObject parseData = new JSONObject();

        for (String key : operations.keySet()) {
            ParseOperation operation = (ParseOperation) operations.get(key);
            try {
                if (operation instanceof SetFieldOperation) {
                    parseData.put(key, operation.encode(PointerEncodingStrategy.get()));
                } else if (operation instanceof IncrementFieldOperation) {
                    parseData.put(key, operation.encode(PointerEncodingStrategy.get()));
                } else if (operation instanceof DeleteFieldOperation) {
                    parseData.put(key, operation.encode(PointerEncodingStrategy.get()));
                } else if (operation instanceof RelationOperation) {
                    parseData.put(key, operation.encode(PointerEncodingStrategy.get()));
                } else {
                    // TODO: I don't get the original (now commented out) code 
                    // below. Every modification of a ParseObject is done via operations
                    // so if we get here, I expect that we've encountered an unsupported 
                    // operation NOT a sub-ParseObject.
                    throw new ParseException("Unsupported operation " + operation, null);
                    
                    /*
                    //here we deal will sub objects like ParseObject;
                    Object obj = data.get(key);
                    if (obj instanceof ParseObject) {
                        ParseObject pob = (ParseObject) obj;
                        parseData.put(key, pob.getParseData());
                    }
                    */
                }
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ex);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("parseData-> " + parseData);
        }

        return parseData;
    }

    protected void validateSave() throws ParseException {
    }
 
    protected void performSave(final ParseCommand command) throws ParseException {
        
        command.setData(getParseData());
        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                LOGGER.error("Empty response");
                throw response.getException();
            }

            setData(jsonResponse);
            if (getUpdatedAt() == null) {
                setUpdatedAt(getCreatedAt());
            }
        } else {
            LOGGER.error("Request failed.");
            throw response.getException();
        }
    }
    
    void performOperation(String key, ParseOperation operation) {

        if (has(key)) {
            operations.remove(key);
            data.remove(key);
        }

        Object value = null;
        try {
            value = operation.apply(null, this, key);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        
        if (value != null) {
            data.put(key, value);
        } else {
            data.remove(key);
        }
        operations.put(key, operation);
        dirtyKeys.add(key);
        setDirty(true);
    }

    
// TODO: Fix all save- and deleteInBackground() methods
//    public void saveInBackground() {
//        saveInBackground(null);
//    }
//
//    public void deleteInBackground() {
//        deleteInBackground(null);
//    }
//
//    public void saveInBackground(SaveCallback saveCallback) {
//        SaveInBackgroundThread task = new SaveInBackgroundThread(saveCallback);
//        ParseExecutor.runInBackground(task);
//    }
//
//    public void deleteInBackground(DeleteCallback deleteCallback) {
//        DeleteInBackgroundThread task = new DeleteInBackgroundThread(deleteCallback);
//        ParseExecutor.runInBackground(task);
//    }

    protected void reset() {
        updatedAt = null;
        createdAt = null;
        objectId = null;
        setDirty(false);
        dirtyKeys.clear();
        operations.clear();
        data.clear();
    }

    protected void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    protected void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    protected final void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

//    class DeleteInBackgroundThread extends Thread {
//
//        DeleteCallback mDeleteCallback;
//
//        public DeleteInBackgroundThread(DeleteCallback callback) {
//            mDeleteCallback = callback;
//        }
//
//        public void run() {
//            ParseException exception = null;
//            try {
//                delete();
//            } catch (ParseException e) {
//                exception = e;
//            }
//            if (mDeleteCallback != null) {
//                mDeleteCallback.done(exception);
//            }
//        }
//    }
//
//    class SaveInBackgroundThread extends Thread {
//
//        SaveCallback mSaveCallback;
//
//        public SaveInBackgroundThread(SaveCallback callback) {
//            mSaveCallback = callback;
//        }
//
//        public void run() {
//            ParseException exception = null;
//            try {
//                save();
//            } catch (ParseException e) {
//                exception = e;
//            }
//            if (mSaveCallback != null) {
//                mSaveCallback.done(exception);
//            }
//        }
//    }

     public static <T extends ParseObject> T fetch(final String className, 
             final String objectId) throws ParseException {
        
        ParseGetCommand command = 
                new ParseGetCommand(toEndPoint(className), objectId);
        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                throw response.getException();
            }

            T obj = ParseRegistry.getObjectFactory(className).create(className);
            obj.setData(jsonResponse);
            obj.setEndPoint(toEndPoint(className));
            return obj;

        } else {
            throw response.getException();
        }
    }
    
    public <T extends ParseObject> T fetchIfNeeded() throws ParseException {
        if (data.isEmpty() && !isDirty()) {
            return fetch(getEndPoint(), getObjectId());
        } else {
            return (T) this;
        }
    }

    public final <T extends ParseObject> void fetchIfNeeded(GetCallback<T> callback) {
        ParseException exception = null;
        T object = null;

        try {
            object = fetchIfNeeded();
        } catch (ParseException e) {
            exception = e;
        }

        if (callback != null) {
            callback.done(object, exception);
        }
    }

    public void setData(JSONObject jsonObject) {
   
        Iterator<?> it = jsonObject.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object value = jsonObject.opt(key);
            if (Parse.isReservedKey(key)) {
                setReservedKey(key, value);
            } else {
                put(key, ParseDecoder.decode(value));
            }
        }

        setDirty(false);
        this.operations.clear();
        this.dirtyKeys.clear();
    }

    protected void setReservedKey(String key, Object value) {        
        if (ParseConstants.FIELD_OBJECT_ID.equals(key)) {
            setObjectId(value.toString());
        } else if (ParseConstants.FIELD_CREATED_AT.equals(key)) {
            setCreatedAt(Parse.parseDate(value.toString()));
        } else if (ParseConstants.FIELD_UPDATED_AT.equals(key)) {
            setUpdatedAt(Parse.parseDate(value.toString()));
        }
    }

    private static String toEndPoint(final String className) {
        return ParseConstants.CLASSES_PATH + className;
    }
    
    private void logGetValueError(final String methodName, final String key, final Object value) {
        LOGGER.error("Called " + methodName + "(" + key
                + "') but the value is of class type '"
                + value.getClass() + "'");
    }
}
