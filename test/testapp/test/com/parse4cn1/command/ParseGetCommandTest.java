/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
