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

package com.parse4cn1.command;

import com.parse4cn1.BaseParseTest;
import com.parse4cn1.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author sidiabale
 */
public class ParseGetCommandTest extends BaseParseTest {

    @Override
    public boolean runTest() throws Exception {
        testGetWithoutParams();
        testGetNonExistentPath();
        testGetWithParams();
        return true;
    }

    @Override
    public void prepare() {
        super.prepare();
        init();
    }
    
    public void testGetWithoutParams() {
        ParseGetCommand command = new ParseGetCommand("users");
        try {
            ParseResponse response = command.perform();
            assertFalse(response.isFailed(), "Command should not have failed");
            log("Response: " + response.getJsonObject());
            assertNotNull(response.getJsonObject(), "Non-null reply expected");
        } catch (ParseException ex) {
            assertBool(false, "An exception occurred: " + ex.getMessage());
        }
    }
    
    public void testGetNonExistentPath() {
        ParseGetCommand command = new ParseGetCommand("users", "nonExistentUserID");
        try {
            ParseResponse response = command.perform();
            assertTrue(response.isFailed(), "Command should have failed");
            assertEqual(404, response.getStatusCode());
            assertNotNull(response.getJsonObject(), "Non-null reply expected");
            log("Response: " + response.getJsonObject());
            ParseException serverMsg = ParseResponse.getParseError(response.getJsonObject());
            assertEqual(101, serverMsg.getCode(), "Response code");
            assertEqual("object not found for get", serverMsg.getMessage());
        } catch (ParseException ex) {
            assertBool(false, "Oops! An exception occurred: " + ex);
        }
    }
    
    public void testGetWithParams() {
        
    }
    
}
