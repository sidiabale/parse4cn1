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

import com.codename1.testing.AbstractTest;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    protected static final String TEST_APPLICATION_ID = "j1KMuH9otZlHcPncU9dZ1JFH7cXL8K5XUiQQ9ot8";
    protected static final String TEST_CLIENT_KEY = "V6ZUyBtfERtzbq6vjeAb13tiFYij980HN9nQTWGB";
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
        Parse.initialize(TEST_APPLICATION_ID, TEST_CLIENT_KEY);
    }
    /**
     * Resets the parse application ID and client key.
     */
    protected final void reset() {
        Parse.initialize(null, null);
    }
    
    protected void deleteAllUsers() {
        // TODO: Replace with batch deletion when batch operations are implemented
        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseConstants.CLASS_NAME_USER);
        try {
            List<ParseUser> results = query.find();
            for (ParseUser user : results) {
                user.setPassword(TEST_PASSWORD);
                user.login(); // Authenticate to get session required for deletion
                user.delete();
            }
        } catch (ParseException ex) {
            fail("Deleting objects failed\n" + ex);
        }
    }
    
    protected void deleteObjects(final String className) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(className);
        try {
            deleteObjects(query.find());
        } catch (ParseException ex) {
            fail("Deleting objects failed\n" + ex);
        }
    }
    
    protected void deleteObjects(List<? extends ParseObject> objects) {
        // TODO: Replace with batch deletion when batch operations are implemented
        for (ParseObject object : objects) {
            if (object.getObjectId() != null) {
                try {
                    object.delete();
                } catch (ParseException ex) {
                    fail("Deleting object failed\n" + ex);
                }
            }
        }
    }
    
    protected void saveObjects(List<? extends ParseObject> objects) throws ParseException {
        // TODO: Replace with batch creation when batch operations are implemented
        for (ParseObject object: objects) {
            object.save();
        }
    }
    
    protected void fail() {
        assertTrue(false);
    }
    
    protected void fail(String errorMessage) {
        assertTrue(false, errorMessage);
    }
    
    protected void assertTrue(boolean value) {
        assertTrue(value, "");
    }
    
    protected void assertTrue(boolean value, String errorMessage) {
        assertBool(value, errorMessage);
    }
    
    protected void assertFalse(boolean value) {
        assertTrue(!value);
    }
    
    protected void assertFalse(boolean value, String errorMessage) {
        assertTrue(!value, errorMessage);
    }
    
    protected void assertNotNull(Object obj) {
        assertNotNull(obj != null, "Object is null but should not be");
    } 
    
    protected void assertNotNull(Object obj, String errorMessage) {
        assertBool((obj != null), errorMessage + " (obj=" + obj + ")");
    }
    
    protected void assertNull(Object obj) {
        assertBool(obj == null, "Object '" + obj + "' should be null but is not");
    } 
    
    protected void assertNull(Object obj, String errorMessage) {
        assertBool((obj == null), errorMessage + " (obj=" + obj + ")");
    }
    
    protected void assertEqual(Object obj1, Object obj2) {
        assertBool((obj1 == null) ? (obj2 == null) : (obj1.equals(obj2)), 
                "Objects '" + obj1 + "' and '" + obj2 + "' are not equal");
    }
    
    protected void assertEqual(Object obj1, Object obj2, String errorMessage) {
        assertBool((obj1 == null) ? (obj2 == null) : (obj1.equals(obj2)),
                errorMessage + " (obj1=" + obj1 + ", obj2=" + obj2 + ")");
    }
    
    protected void assertNotEqual(Object obj1, Object obj2) {
        assertBool((obj1 == null) ? (obj2 != null) : !(obj1.equals(obj2)), 
                "Objects '" + obj1 + "' and '" + obj2 + "' are equal (expected to be unequal)");
    }
    
    protected void assertNotEqual(Object obj1, Object obj2, String errorMessage) {
        assertBool((obj1 == null) ? (obj2 != null) : !(obj1.equals(obj2)),
                errorMessage + " (obj1=" + obj1 + ", obj2=" + obj2 + ")");
    }
    
    protected String getCurrentTimeInHex() {
        return Long.toString((new Date()).getTime(), 16);
    }
}
