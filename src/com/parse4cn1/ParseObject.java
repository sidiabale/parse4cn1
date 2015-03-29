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
import com.parse4cn1.callback.DeleteCallback;
import com.parse4cn1.callback.GetCallback;
import com.parse4cn1.callback.SaveCallback;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParseObject {

    private static final Logger LOGGER = Logger.getInstance();

    private String objectId;
    private String className;
    private String endPoint;
    boolean isDirty = false;

    private Map<String, Object> data;
    private Map<String, ParseOperation> operations;
    private List<String> dirtyKeys;

    private Date updatedAt;
    private Date createdAt;

    protected ParseObject() {
        this("_Parse4J");
    }
    
    protected ParseObject(String className) {

        if (className == null) {
            LOGGER.error("You must specify a Parse class name when creating a new ParseObject.");
            throw new IllegalArgumentException(
                    "You must specify a Parse class name when creating a new ParseObject.");
        }

        if ("_Parse4J".equals(className)) {
            className = ParseRegistry.getClassName(getClass());
        }
        
        this.className = className;
        this.data = new Hashtable<String, Object>();
        this.operations = new Hashtable<String, ParseOperation>();
        this.dirtyKeys = new ArrayList<String>();
        setEndPoint("classes/" + className);
    }

    public static ParseObject create(String className) {
        return new ParseObject(className);
    }

//    // TODO: CN1 does not support reflection. Consider using mirah bindings if absolutely needed
//    @SuppressWarnings("unchecked")
//    public static <T extends ParseObject> T create(Class<T> subclass) {
//        return (T) create(ParseRegistry.getClassName(subclass));
//    }
    
    public static ParseObject createWithoutData(String className, String objectId) {
        ParseObject result = create(className);
        result.setObjectId(objectId);
        result.isDirty = false;
        return result;
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

        // TODO: Investigate if this originally commented out code needs to 
        // be fixed or removed
        /*
         if (((value instanceof ParseACL)) && (key.equals("ACL"))) {
         ParseACL acl = (ParseACL) value;
         if (acl.isShared()) {
         ParseACL copy = acl.copy();
         this.estimatedData.put("ACL", copy);
         addToHashedObjects(copy);
         return getACL();
         }

         }*/
      // TODO: Fix  
//        if ((value instanceof ParseRelation)) {
//            ((ParseRelation<?>) value).ensureParentAndKey(this, key);
//        }
        return value;

    }

    public <T extends ParseObject> ParseRelation<T> getRelation(String key) {
        ParseRelation<T> relation = new ParseRelation<T>(this, key);
        Object value = this.data.get(key);
        if (value != null) {
            if (value instanceof ParseRelation) {
                relation.setTargetClass(((ParseRelation<?>) value).getTargetClass());
            }
        } else {
            this.data.put(key, relation);
        }
        return relation;
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

//    public void add(String key, Object value) {
//        addToArrayField(key, Arrays.asList(new Object[]{value}));
//    }

    public void addToArrayField(String key, Collection<?> values) {
        AddOperation operation = new AddOperation(values);
        performOperation(key, operation);
    }

//    public void addUnique(String key, Object value) {
//        addUniqueToArrayField(key, Arrays.asList(new Object[]{value}));
//    }

    public void addUniqueToArrayField(String key, Collection<?> values) {
        AddUniqueOperation operation = new AddUniqueOperation(values);
        performOperation(key, operation);
    }

    public void removeFromArrayField(String key, Collection<?> values) {
        RemoveOperation operation = new RemoveOperation(values);
        performOperation(key, operation);
    }

    public void put(String key, Object value) {
        put(key, value, false);
    }

    protected void validateSave() throws ParseException {
    }
    
    /**
     *
     * @param key
     * @param value
     * @param disableChecks some checks have to be skipped during fetch.
     * Currently the only effect of passing true here is to disable the check on
     * uploaded files. See issue #17 on github (https://github.com/thiagolocatelli/parse4j/issues/17).
     */
    protected void put(String key, Object value, boolean disableChecks) {

        if (key == null) {
            LOGGER.error("key may not be null.");
            throw new IllegalArgumentException("key may not be null.");
        }

        if (value == null) {
            LOGGER.error("value may not be null.");
            throw new IllegalArgumentException("value may not be null.");
        }

        if (value instanceof ParseObject && ((ParseObject) value).isDirty) {
            LOGGER.error("ParseObject must be saved before being set on another ParseObject.");
            throw new IllegalArgumentException(
                    "ParseObject must be saved before being set on another ParseObject.");
        }

        if (value instanceof ParseFile && !((ParseFile) value).isUploaded() && !disableChecks) {
            LOGGER.error("ParseFile must be saved before being set on a ParseObject.");
            throw new IllegalArgumentException(
                    "ParseFile must be saved before being set on a ParseObject.");
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
    
    public void performOperation(String key, ParseOperation operation) {

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
        isDirty = true;
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
            isDirty = true;
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
        isDirty = true;
    }

    public void save() throws ParseException {

        if (!isDirty) {
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
                    
                    //here we deal will sub objects like ParseObject;
                    Object obj = data.get(key);
                    if (obj instanceof ParseObject) {
                        ParseObject pob = (ParseObject) obj;
                        parseData.put(key, pob.getParseData());
                    }
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
        isDirty = false;
        operations.clear();
        dirtyKeys.clear();
        data.clear();
    }

    protected void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    protected void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    protected void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    protected void setEndPoint(String endPoint) {
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

     public static <T extends ParseObject> T fetch(final String endPoint, 
             final String objectId) throws ParseException {
        
        ParseGetCommand command = new ParseGetCommand(endPoint, objectId);
        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                throw response.getException();
            }

            T obj = null;
            try {
                obj = parseData(jsonResponse);
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, "Error parsing JSON data", ex);
            }
            obj.setEndPoint(endPoint);
            return obj;

        } else {
            throw response.getException();
        }
    }
    
    public <T extends ParseObject> T fetchIfNeeded() throws ParseException {
        // TODO: Why is unconditional fetch done for a method that says ~IF NEEDED?
        // Perhaps we need to first check if the object is dirty?
        return fetch(getEndPoint(), getObjectId());
    }

    public final <T extends ParseObject> void fetchIfNeeded(GetCallback<T> callback) {
        // TODO: Why is unconditional fetch done for a method that says ~IF NEEDED?
        // Perhaps we need to first check if the object is dirty?
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

    private static <T extends ParseObject> T parseData(JSONObject jsonObject) 
            throws JSONException {

        @SuppressWarnings("unchecked")
        T po = (T) new ParseObject(); // TODO: Instantiate real class via factory?
        po.setData(jsonObject);
        return po;
    }

    protected void setData(JSONObject jsonObject) {
        setData(jsonObject, false);
    }

    protected void setData(JSONObject jsonObject, boolean disableChecks) {
   
        Iterator<?> it = jsonObject.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object value = jsonObject.opt(key);
            if (Parse.isReservedKey(key)) {
                setReservedKey(key, value);
            } else {
                put(key, ParseDecoder.decode(value), disableChecks);
            }
        }

        this.isDirty = false;
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

    private void logGetValueError(final String methodName, final String key, final Object value) {
        LOGGER.error("Called " + methodName + "(" + key
                + "') but the value is of class type '"
                + value.getClass() + "'");
    }
}
