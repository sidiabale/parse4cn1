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

import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.testing.AbstractTest;
import com.parse4cn1.util.ExternalizableParseObject;
import com.parse4cn1.util.Logger;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Ideally, this test should have been abstract but when that is done,
 * a java.lang.InstantiationException occurs apparently because the test executor
 * ignores that this class is abstract and tries to instantiate it anyway.
 * 
 * The current work around implements this as a concrete base class.
 * 
 * @author sidiabale
 */
public class BaseParseTest extends AbstractTest {
    protected static String testApiEndPoint = "https://parse-parse4cn1.rhcloud.com/parse";
    protected static String testAppId = "myAppId";
    protected static String testClientKey = null;
    protected static final String TEST_PASSWORD = "p_n7!-e8";
    
    @Override
    public void prepare() {
        super.prepare();
        init();
        resetClassData();
    }

    @Override
    public void cleanup() {
        resetClassData();
        /*
        // Note: We would like to delete all sessions here but the session tokens 
        // are needed and tracking these across tests is cumbersome. Parse will 
        // clean them up after year so we can live with that.
        
        try {
            deleteObjects(ParseConstants.CLASS_NAME_SESSION);
        } catch (ParseException ex) {
            fail("Deleting sessions during teardown failed!\n" + ex);
        }
        */
        reset();
        super.cleanup();
    }
    
    /**
     * Cleans up data in classes. Called in the {@link #init()} and {@link #cleanup()}
     * methods of {@link BaseParseTest} class.
     */
    protected void resetClassData() {
        // Override in subclasses as needed
    }
    
    public boolean runTest() throws Exception {
        return true;
    }
    
    public static void setBackend(String apiEndPoint, String appId, String clientKey) {
       testApiEndPoint = apiEndPoint;
       testAppId = appId;
       testClientKey = clientKey;
    }
    
    /**
     * Initializes the parse application ID and client key.
     * Update with your own keys to avoid the chance of false negatives 
     * due to excessive API calls if multiple persons run tests with 
     * these keys simultaneously.
     * 
     * TODO: Write app id and client key to storage and prompt user
     * to provide if not present. In this way, own keys are never exposed
     */
    protected final void init() {
        Logger.getInstance().info("Initializing " + getClass().getCanonicalName());
        Log.setLevel(Log.DEBUG);
        Parse.initialize(testApiEndPoint, testAppId, testClientKey);
    }
    /**
     * Resets the parse application ID and client key.
     */
    protected final void reset() {
        Parse.initialize(null, null, null);
    }
    
    protected void deleteAllUsers() {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseConstants.CLASS_NAME_USER);
        try {
            List<ParseUser> results = query.find();
            for (ParseUser user : results) {
                user.setPassword(TEST_PASSWORD);
                user.login(); // Authenticate to get session required for deletion
                user.delete();
            }
        } catch (ParseException ex) {
            fail("Deleting one or more users failed\n" + ex);
        }
    }
    
    protected void batchDeleteObjects(final String className) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(className);
        try {
            batchDeleteObjects(query.find());
        } catch (ParseException ex) {
            fail("Retrieving objects to delete failed\n" + ex);
        }
    }
    
    protected void deleteObjects(final String className) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(className);
        try {
            deleteObjects(query.find());
        } catch (ParseException ex) {
            fail("Retrieving objects to delete failed\n" + ex);
        }
    }
    
    protected void deleteObjects(List<? extends ParseObject> objects) {  
        for (ParseObject object : objects) {
            if (object.getObjectId() != null) {
                try {
                    if (object instanceof ParseUser) {
                        ParseUser user = (ParseUser)object;
                        user.setPassword(TEST_PASSWORD);
                        user.login();
                    }
                    object.delete();
                } catch (ParseException ex) {
                    fail("Deleting object " + object.getObjectId() + " failed\n" + ex);
                }
            }
        }
    }
    
    protected void batchDeleteObjects(List<? extends ParseObject> objects) {
        final int maxBatchSize = 50; // Batch size limit as at May 2015
        
        // Split into acceptably-sized batches if necessary
        int i = 0;
        int remaining = objects.size();
        do {
            int batchSize = Math.min((i + maxBatchSize), remaining);
            try {
                assertTrue(ParseBatch.create().addObjects(
                        objects.subList(i, (i + batchSize)), 
                                ParseBatch.EBatchOpType.DELETE).execute(),
                        "Deleting one or more objects in batch failed");
            } catch (ParseException ex) {
                fail("Deleting one or more objects in batch failed\n" + ex);
            }
            i += maxBatchSize;
            remaining -= batchSize;
        } while (i < objects.size());
    }
    
    protected void saveObjects(List<? extends ParseObject> objects) throws ParseException {
        for (ParseObject object: objects) {
            object.save();
        }
    }
    
    protected ParseObject serializeAndRetrieveParseObject(final ParseObject input) {
        assertTrue(Storage.getInstance().writeObject(input.getObjectId(), input.asExternalizable()), 
                "Serialization of ParseObject failed");
        Storage.getInstance().clearCache(); // Absolutely necessary to force retrieval from storage
        return ((ExternalizableParseObject) Storage.getInstance().readObject(
                input.getObjectId())).getParseObject();
    }
    
    protected void compareParseObjects(final ParseObject obj1, final ParseObject obj2,
            final Set<String> fieldsToSkip) {
        assertEqual(obj1.getObjectId(), obj2.getObjectId());
        assertEqual(obj1.getCreatedAt(), obj2.getCreatedAt());
        assertEqual(obj1.getUpdatedAt(), obj2.getUpdatedAt());
        assertEqual(obj1.getEndPoint(), obj2.getEndPoint());
        assertEqual(obj1.keySet(), obj2.keySet());

        for (String key : obj1.keySet()) {
            if (fieldsToSkip == null || !fieldsToSkip.contains(key)) {
                assertEqual(obj1.get(key), obj2.get(key));
            }
        }
    }
    
    protected void compareGeoLocations(final ParseGeoPoint p1, final ParseGeoPoint p2) {
        assertEqual(p1.getLatitude(), p2.getLatitude(), 
                String.format("Latitudes %f and %f are not equal", p1.getLatitude(), p2.getLatitude()));
        assertEqual(p1.getLongitude(), p2.getLongitude(),
                String.format("Longitudes %f and %f are not equal", p1.getLongitude(), p2.getLongitude()));
    }
    
    protected void compareParseFiles(final ParseFile file1, final ParseFile file2, boolean hasData) throws ParseException {
        assertEqual(file1.getName(), file2.getName());
        assertEqual(file1.getContentType(), file2.getContentType());
        assertEqual(file1.getEndPoint(), file2.getEndPoint());
        assertEqual(file1.getUrl(), file2.getUrl());
        if (hasData) {
            assertTrue(Arrays.equals((byte[]) file1.getData(), (byte[]) file2.getData()),
                    "File byte data should be equal after (de)serialization");
        }
        assertEqual(file1.isDirty(), file2.isDirty());
        assertEqual(file1.isDataAvailable(), file2.isDataAvailable());
    }
 
    protected byte[] getBytes(String fileName) throws ParseException {
        try {
            RandomAccessFile f = new RandomAccessFile(getClass().getResource(fileName).getFile(), "r");
            byte[] b = new byte[(int) f.length()];
            f.read(b);
            f.close();
            return b;
        } catch (IOException e) {
            throw new ParseException("IO exception occurred.", e);
        }
    }
    
    @Override
    public void fail() {
        assertTrue(false);
    }
    
    @Override
    public void fail(String errorMessage) {
        assertTrue(false, errorMessage);
    }
    
    @Override
    public void assertTrue(boolean value) {
        assertTrue(value, "");
    }
    
    @Override
    public void assertTrue(boolean value, String errorMessage) {
        assertBool(value, errorMessage);
    }
    
    @Override
    public void assertFalse(boolean value) {
        assertTrue(!value);
    }
    
    @Override
    public void assertFalse(boolean value, String errorMessage) {
        assertTrue(!value, errorMessage);
    }
    
    @Override
    public void assertNotNull(Object obj) {
        assertNotNull(obj != null, "Object is null but should not be");
    } 
    
    @Override
    public void assertNotNull(Object obj, String errorMessage) {
        assertBool((obj != null), errorMessage + " (obj=" + obj + ")");
    }
    
    @Override
    public void assertNull(Object obj) {
        assertBool(obj == null, "Object '" + obj + "' should be null but is not");
    } 
    
    @Override
    public void assertNull(Object obj, String errorMessage) {
        assertBool((obj == null), errorMessage + " (obj=" + obj + ")");
    }
    
    @Override
    public void assertEqual(Object obj1, Object obj2) {
        assertBool((obj1 == null) ? (obj2 == null) : (obj1.equals(obj2)), 
                "Objects '" + obj1 + "' and '" + obj2 + "' are not equal");
    }
    
    @Override
    public void assertEqual(Object obj1, Object obj2, String errorMessage) {
        assertBool((obj1 == null) ? (obj2 == null) : (obj1.equals(obj2)),
                errorMessage + " (obj1=" + obj1 + ", obj2=" + obj2 + ")");
    }
    
    @Override
    public void assertNotEqual(Object obj1, Object obj2) {
        assertBool((obj1 == null) ? (obj2 != null) : !(obj1.equals(obj2)), 
                "Objects '" + obj1 + "' and '" + obj2 + "' are equal (expected to be unequal)");
    }
    
    @Override
    public void assertNotEqual(Object obj1, Object obj2, String errorMessage) {
        assertBool((obj1 == null) ? (obj2 != null) : !(obj1.equals(obj2)),
                errorMessage + " (obj1=" + obj1 + ", obj2=" + obj2 + ")");
    }
    
    protected String getCurrentTimeInHex() {
        return Long.toString((new Date()).getTime(), 16);
    }
}
