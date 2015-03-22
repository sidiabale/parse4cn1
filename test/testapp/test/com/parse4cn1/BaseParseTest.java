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
    protected static final String TEST_REST_API_KEY = "pW7IhlgwwB2WgmvK1yYguSaUgTofjCmyOX6vUh8k";
    
    @Override
    public void prepare() {
        super.prepare();
        init();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        reset();
    }
    
    public boolean runTest() throws Exception {
        return true;
    }
    
    /**
     * Initializes the parse application ID and REST API key.
     * Update with your own keys to avoid the chance of false negatives 
     * due to excessive API calls if multiple persons run tests with 
     * these keys simultaneously.
     * 
     * TODO: Write app id and rest api key to storage and prompt user
     * to provide if not present. In this way, own keys are never exposed
     */
    protected void init() {
        Parse.initialize(TEST_APPLICATION_ID, TEST_REST_API_KEY);
    }
    /**
     * Resets the parse application ID and REST API key.
     */
    protected void reset() {
        Parse.initialize(null, null);
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
    
    protected String getCurrentTimeInHex() {
        return Long.toString((new Date()).getTime(), 16);
    }
}
