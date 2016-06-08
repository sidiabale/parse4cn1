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
        testIsEmpty();
        return true;
    }
    
    private void testKeyInitialization() {
        reset();
        assertNull(Parse.getApplicationId(), "Test app ID should be null");
        assertNull(Parse.getClientKey(), "Test app client key should be null");
        
        init();
        assertEqual(testAppId, Parse.getApplicationId(), 
                "Test app ID is not initialized");
        assertEqual(testClientKey, Parse.getClientKey(), 
                "Test app client key is not initialized");
    }
    
    private void testGetParseApiUrl() {
        assertEqual(testApiEndPoint + "/", Parse.getParseAPIUrl(null));
        assertEqual(testApiEndPoint + "/", Parse.getParseAPIUrl(""));
        assertEqual(testApiEndPoint + "/classes", Parse.getParseAPIUrl("classes"));
        assertEqual(testApiEndPoint + "/classes/myEntity", Parse.getParseAPIUrl("classes/myEntity"));
    }
    
    private void testIsReservedKey() {
        assertBool(Parse.isReservedKey("createdAt"), "createdAt is a reserved key");
        assertBool(Parse.isReservedKey("updatedAt"), "updatedAt is a reserved key");
        assertBool(Parse.isReservedKey("objectId"), "objectId is a reserved key");
        assertBool(!Parse.isReservedKey("sessionId"), "sessionId is NOT a reserved key");
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
    
    private void testIsEmpty() {
        assertTrue(Parse.isEmpty(null));
        assertTrue(Parse.isEmpty(""));
        assertFalse(Parse.isEmpty(" "));
        assertFalse(Parse.isEmpty("Non-empty"));
    }
}
