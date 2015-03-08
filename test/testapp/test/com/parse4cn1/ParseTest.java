/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.parse4cn1;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author sidiabale
 */
public class ParseTest extends BaseParseTest {
    
    @Override
    public boolean runTest() throws Exception {
        testKeyInitialization();
        testGetParseApiUrl();
        testIsReservedKey();
        testJoin();
        return true;
    }
    
    private void testKeyInitialization() {
        reset();
        assertNull(Parse.getApplicationId(), "Test app ID should be null");
        assertNull(Parse.getRestAPIKey(), "Test app REST API key should be null");
        
        init();
        assertEqual(TEST_APPLICATION_ID, Parse.getApplicationId(), 
                "Test app ID is not initialized");
        assertEqual(TEST_REST_API_KEY, Parse.getRestAPIKey(), 
                "Test app REST API key is not initialized");
    }
    
    private void testGetParseApiUrl() {
        assertEqual("https://api.parse.com/1/", Parse.getParseAPIUrl(null));
        assertEqual("https://api.parse.com/1/", Parse.getParseAPIUrl(""));
        assertEqual("https://api.parse.com/1/classes", Parse.getParseAPIUrl("classes"));
        assertEqual("https://api.parse.com/1/classes/myEntity", Parse.getParseAPIUrl("classes/myEntity"));
    }
    
    private void testIsReservedKey() {
        assertBool(Parse.isReservedKey("createdAt"), "createdAt is a reserved key");
        assertBool(Parse.isReservedKey("updatedAt"), "updatedAt is a reserved key");
        assertBool(Parse.isReservedKey("objectID"), "objectId is a reserved key");
        assertBool(!Parse.isReservedKey("sessionID"), "sessionId is NOT a reserved key");
    }
    
    private void testJoin() {
        Collection<String> strings = new ArrayList();
        assertEqual("", Parse.join(strings, ","), "Empty collection should yield empty string");
        
        strings.add("A");
        assertEqual("A", Parse.join(strings, ","), "Single element collection should contain no terminal delimiter");
        
        strings.add("B");
        strings.add("C");
        assertEqual("A, B, C", Parse.join(strings, ", "), "Multi-element collection should be joined properly");
        assertEqual("A#B#C", Parse.join(strings, "#"), "Spaces in delimiter should be preserved");
        assertEqual("AnullBnullC", Parse.join(strings, null), "Null delimiter should still result in proper join");
        try {
            Parse.join(null, ",");
            assertBool(false, "Null collection failed to result in exception");
        } catch (Exception ex) {
            assertBool(true, ex.getMessage());
        }
    }
}
