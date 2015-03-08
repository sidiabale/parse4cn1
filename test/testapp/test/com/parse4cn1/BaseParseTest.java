/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.parse4cn1;

import com.codename1.testing.AbstractTest;

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
    
    public boolean runTest() throws Exception {
        return true;
    }
    
    /**
     * Initializes the parse application ID and REST API key.
     * Update with your own keys.
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
}
