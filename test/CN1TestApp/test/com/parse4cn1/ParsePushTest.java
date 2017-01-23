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

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.ParsePush.IPushCallback;
import com.parse4cn1.util.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Tests for the {@link ParsePush} class.
 * <p>
 * Actual sending of push messages is not tested here. Use the CN1TestApp for that.
 */
public class ParsePushTest extends BaseParseTest implements IPushCallback {
    
    private JSONObject receivedForegroundPushPayload;
    private JSONObject receivedBackgroundPushPayload;
    private JSONObject receivedAppOpenPushPayload;
    private ParseException receivedPushRegistrationError;
    
    private String pushPayload1;
    private String pushPayload2;
    
    private boolean foregroundPushHandled = false;
    private boolean backgroundPushHandled = false;
    
    @Override
    public boolean runTest() throws Exception {
        testHandlePushNoCallback();
        testHandlePushReceivedForeground();
        testHandlePushReceivedBackground();
        testHandleUnprocessedPushReceived();
        testHandlePushOpenedForeground();
        testHandlePushOpenedBackground();
        testHandlePushRegistrationStatus();
        testSendPushRestApiExample();
        return true;
    }
    
    @Override
    public void prepare() {
        super.prepare();
        ParsePush.setPushCallback(this);
        JSONObject jsonPayload1 = new JSONObject();
        JSONObject jsonPayload2 = new JSONObject();
        try {
            jsonPayload1.append("alert", "Push test 1");
            jsonPayload1.append("title", "Show this in notification bar");
            jsonPayload1.append("data", new JSONArray(new ArrayList<String>(Arrays.asList("banana", "orange", "mango"))));
            pushPayload1 = jsonPayload1.toString();
            
            jsonPayload2.append("alert", "Push test 2");
            jsonPayload2.append("content-available", 1);
            jsonPayload2.append("category", "aCategory");
            jsonPayload2.append("data", "some extra data");
            pushPayload2 = jsonPayload2.toString();
        } catch (JSONException ex) {
            Logger.getInstance().error("Initializing push notification test data failed! Error: " +  ex);
            fail("Initializing push notification test data failed! Error: " + ex.getMessage());
        }
    }

    @Override
    public boolean onPushReceivedForeground(JSONObject pushPayload) {
        receivedForegroundPushPayload = pushPayload;
        return foregroundPushHandled;
    }

    @Override
    public boolean onPushReceivedBackground(JSONObject pushPayload) {
        receivedBackgroundPushPayload = pushPayload;
        return backgroundPushHandled;
    }

    @Override
    public void onAppOpenedViaPush(JSONObject pushPayload) {
        receivedAppOpenPushPayload = pushPayload;
    }
    
    @Override
    public void onPushRegistrationFailed(ParseException error) {
        receivedPushRegistrationError = error;
    }
    
    private void testHandlePushNoCallback() throws ParseException {
        System.out.println("============== testHandlePushNoCallback()");
        resetPushHandlingData();
        try {
            ParsePush.setPushCallback(null);

            System.out.println("-------------- Push received, app in foreground --> ignore");
            assertFalse(ParsePush.handlePushReceivedForeground(pushPayload2),
                    "Push received in foreground shouldn't be handled when no callback is set");

            System.out.println("-------------- Push received, app in background --> ignore");
            assertFalse(ParsePush.handlePushReceivedBackground(pushPayload1),
                    "Push received in background shouldn't be handled when no callback is set");

            System.out.println("-------------- Push opened, app in foreground --> ignore");
            ParsePush.handlePushOpen(pushPayload1, true);
            assertFalse(ParsePush.isAppOpenedViaPushNotification(), 
                    "App in foreground should have directly consumed app open push payload");
            assertNull(ParsePush.getPushDataUsedToOpenApp(), 
                    "App in foreground should have directly consumed app open push payload");

            System.out.println("-------------- Push opened, app in background --> keep");
            ParsePush.handlePushOpen(pushPayload1, false);
            assertTrue(ParsePush.isAppOpenedViaPushNotification(), 
                    "Push payload used to open app should have been buffered");
            assertEqual(pushPayload1, ParsePush.getPushDataUsedToOpenApp().toString(), 
                    "App open push data should be the same as received");
        } finally {
            ParsePush.setPushCallback(this);
        }
    }
    
    private void testHandlePushReceivedForeground() {
        System.out.println("============== testHandlePushReceivedForeground()");
        resetPushHandlingData();
        
        System.out.println("-------------- Push received, app in foreground and handles immediately");
        foregroundPushHandled = true;
        assertEqual(foregroundPushHandled, ParsePush.handlePushReceivedForeground(pushPayload2),
            "Push received in foreground should be handled");
        assertNotNull(receivedForegroundPushPayload, "Push payload should be received via callback");
        assertEqual(pushPayload2, receivedForegroundPushPayload.toString(),
                "Foreground push payload should be the same as received");
        
        resetPushHandlingData();
        System.out.println("-------------- Push received, app in foreground but does not handle push");
        foregroundPushHandled = false;
        assertEqual(foregroundPushHandled, ParsePush.handlePushReceivedForeground(pushPayload1),
            "Push received in foreground shouldn't be handled");
        assertNotNull(receivedForegroundPushPayload, "Push payload should be received via callback");
        assertEqual(pushPayload1, receivedForegroundPushPayload.toString(),
                "Foreground push payload should be the same as received");
    }
    
    private void testHandlePushReceivedBackground() {
        System.out.println("============== testHandlePushReceivedBackground()");
        resetPushHandlingData();
        
        System.out.println("-------------- Push received, app in background but does not handle push");
        backgroundPushHandled = false;
        assertEqual(backgroundPushHandled, ParsePush.handlePushReceivedBackground(pushPayload1),
            "Push received in background shouldn't be handled");
        assertNotNull(receivedBackgroundPushPayload, "Push payload should be received via callback");
        assertEqual(pushPayload1, receivedBackgroundPushPayload.toString(),
                "Background push payload should be the same as received");
        
        resetPushHandlingData();
        System.out.println("-------------- Push received, app in background and handles immediately");
        backgroundPushHandled = true;
        assertEqual(backgroundPushHandled, ParsePush.handlePushReceivedBackground(pushPayload2),
            "Push received in background should be handled");
        assertNotNull(receivedBackgroundPushPayload, "Push payload should be received via callback");
        assertEqual(pushPayload2, receivedBackgroundPushPayload.toString(),
                "Background push payload should be the same as received");
    }
    
    private void testHandleUnprocessedPushReceived() throws ParseException, JSONException {
        System.out.println("============== testHandleUnprocessedPushReceived()");
        resetPushHandlingData();
        
        assertFalse(ParsePush.isUnprocessedPushDataAvailable(),
                "No unprocessed push data should be available initially (flag)");
        assertNull(ParsePush.getUnprocessedPushData(),
                "No unprocessed push data should be available initially (actual data)");
        
        System.out.println("-------------- First unprocessed push received --> kept till explicitly processed");
        ParsePush.handleUnprocessedPushReceived(pushPayload1);
        assertTrue(ParsePush.isUnprocessedPushDataAvailable(), "Unprocessed push data should be available");
        JSONArray unprocessed = ParsePush.getUnprocessedPushData();
        assertEqual(1, unprocessed.length(), "Only one unprocessed push payload expected");
        assertEqual(pushPayload1, unprocessed.get(0).toString(), 
                "Unprocessed push payload should be same as received");
        
        System.out.println("-------------- Additional unprocessed push received --> "
                + "kept in FIFO order till explicitly processed");
        ParsePush.handleUnprocessedPushReceived(pushPayload2);
        assertTrue(ParsePush.isUnprocessedPushDataAvailable(), "Unprocessed push data should be available");
        unprocessed = ParsePush.getUnprocessedPushData();
        assertEqual(2, unprocessed.length(), "Two unprocessed push payloads expected");
        assertEqual(pushPayload1, unprocessed.get(0).toString(), 
                "Unprocessed push payload should be same as received (in FIFO order)");
        assertEqual(pushPayload2, unprocessed.get(1).toString(), 
                "Unprocessed push payload should be same as received (in FIFO order)");
        
        ParsePush.resetUnprocessedPushData();
        assertFalse(ParsePush.isUnprocessedPushDataAvailable(),
                "No unprocessed push data should be available after reset (flag)");
        assertNull(ParsePush.getUnprocessedPushData(),
                "No unprocessed push data should be available after reset (actual data)");
    }
    
    private void testHandlePushOpenedForeground() throws ParseException {
        System.out.println("============== testHandlePushOpenedForeground()");
        resetPushHandlingData();
        
        ParsePush.handlePushOpen(pushPayload2, true);
        assertFalse(ParsePush.isAppOpenedViaPushNotification(),
                "App in foreground should have directly consumed app open push payload");
        assertNull(ParsePush.getPushDataUsedToOpenApp(),
                "App in foreground should have directly consumed app open push payload");
        assertNotNull(receivedAppOpenPushPayload, "App open push payload should be received via callback");
        assertEqual(pushPayload2, receivedAppOpenPushPayload.toString(),
                "App open push payload should be the same as received");
    }
    
    private void testHandlePushOpenedBackground() throws ParseException {
        System.out.println("============== testHandlePushOpenedBackground()");
        resetPushHandlingData();
        
        ParsePush.handlePushOpen(pushPayload1, false);
        assertTrue(ParsePush.isAppOpenedViaPushNotification(),
                "App not in foreground should have pending app open push payload (flag)");
        assertNull(receivedAppOpenPushPayload, "App open push payload should not be received via callback");
        final JSONObject appOpenPayload = ParsePush.getPushDataUsedToOpenApp();
        assertNotNull(appOpenPayload,
                "App not in foreground should have pending app open push payload (data)");
        assertEqual(pushPayload1, appOpenPayload.toString(),
                "App open push payload should be the same as received");
    }
    
    private void testHandlePushRegistrationStatus() throws JSONException {
        System.out.println("============== testHandlePushRegistrationFailed()");
        
        System.out.println("-------------- No callback set --> "
                + "error is delivered on callback registration");
        runHandlePushRegistrationStatusTest("Generic error", 1, false);
        runHandlePushRegistrationStatusTest("Missing deviceUris", 2, false);
        runHandlePushRegistrationStatusTest("Unable to save installation ID", 3, false);
        runHandlePushRegistrationStatusTest("Undefined type 4 (should be handled)", 4, false);
        
        System.out.println("-------------- No callback set and push succeeded --> "
                + "no error notification at all "
                + "(error string should be null or type should be 0 to suffice as success)");
        // Message should be null or status should be 0 to suffice as success
        runHandlePushRegistrationStatusTest(null, 0, false);
        runHandlePushRegistrationStatusTest(null, 1, false);
        runHandlePushRegistrationStatusTest("Mistake (should be ignored)", 0, false);
        
        System.out.println("-------------- Callback set --> "
                + "error is delivered on immediately");
        runHandlePushRegistrationStatusTest("Generic error 2", 1, true);
        runHandlePushRegistrationStatusTest("Missing other required field", 2, true);
        runHandlePushRegistrationStatusTest("Unable to save installation ID due to network error", 3, true);
        runHandlePushRegistrationStatusTest("Undefined type 5 (should be handled)", 5, true);
        
        System.out.println("-------------- Callback set but push registration succeeded --> "
                + "no error notification at all "
                + "(error string should be null or type should be 0 to suffice as success)");
        // Message should be null or status should be 0 to suffice as success
        runHandlePushRegistrationStatusTest(null, 0, true);
        runHandlePushRegistrationStatusTest(null, 1, true);
        runHandlePushRegistrationStatusTest("Should be ignored", 0, true);

        runHandlePushRegistrationErrorResetTest();   
    }
    
    private void testSendPushRestApiExample() throws JSONException, ParseException {
        System.out.println("============== testSendPushRestApiExample()");
        
        System.out.println("-------------- Sending pushes to channels");
        JSONObject expectedPayload = new JSONObject();
        JSONObject data = new JSONObject();
        expectedPayload.put("data", data);
        data.put("alert", "The Giants won against the Mets 2-3.");
        ArrayList<String> channels = new ArrayList<String>(Arrays.asList("Giants, Mets"));
        expectedPayload.put("channels", new JSONArray(channels));
        
        ParsePush parsePush = ParsePush.create();
        parsePush.setMessage(data.getString("alert"));
        parsePush.setChannel("Test");
        parsePush.send();
        parsePush.setChannels(channels); // Should override previous channel
        JSONObject actualPayload = parsePush.getJSONData();
        assertEqual(expectedPayload.toString(), actualPayload.toString());
        
        System.out.println("-------------- Sending pushes to queries");
        data.put("alert", "The Giants scored a run! The score is now 2-2.");
        JSONObject query = new JSONObject();
        query.put("channels", "Giants");
        query.put("scores", true);
        expectedPayload.put("where", query);
        
        parsePush.setMessage(data.getString("alert"));
        ParseQuery<ParseInstallation> parseQuery = ParseInstallation.getQuery();
        parseQuery.whereEqualTo("channels", "Giants");
        parseQuery.whereEqualTo("scores", true);
        parsePush.setQuery(parseQuery);
        parsePush.send();
        
        expectedPayload.remove("channels"); // Query should nullify channels
        actualPayload = parsePush.getJSONData();
        assertEqual(expectedPayload.toString(), actualPayload.toString());
        
        System.out.println("-------------- Sending pushes with customized sending options");
        data.put("alert", "The Mets scored! The game is now tied 1-1.");
        data.put("badge", "Increment");
        data.put("sound", "cheering.caf");
        data.put("title", "Mets Score!");

        channels = new ArrayList<String>(Arrays.asList("Mets"));
        expectedPayload.put("channels", new JSONArray(channels));
        
        parsePush.setMessage(data.getString("alert"));
        parsePush.setBadge(data.getString("badge"));
        parsePush.setSound(data.getString("sound"));
        parsePush.setTitle(data.getString("title"));
        parsePush.setChannel("Mets"); 
        
        expectedPayload.remove("where"); // Channel should nullify query
        actualPayload = parsePush.getJSONData();
        assertEqual(expectedPayload.toString(), actualPayload.toString());
        
        System.out.println("-------------- Sending pushes with customized sending options (extended)");
        // Add extra elements not in REST API exxample
        data.put("content-available", 1);
        data.put("category", "aCategory");
        data.put("uri", "https://www.parse.com");
        
        parsePush.setContentAvailable(true);
        parsePush.setCategory(data.getString("category"));
        parsePush.setUri(data.getString("uri"));
        actualPayload = parsePush.getJSONData();
        assertEqual(expectedPayload.toString(), actualPayload.toString());
        
        System.out.println("-------------- Setting an expiration date");
        expectedPayload = new JSONObject();
        data = new JSONObject();
        Date currentDate = new Date();
        
        expectedPayload.put("data", data);
        expectedPayload.put("expiration_time", Parse.encodeDate(currentDate));
        data.put("alert", "Season tickets on sale until March 19, 2015");
        
        parsePush = ParsePush.create();
        parsePush.setMessage(data.getString("alert"));
        parsePush.setExpirationTime(currentDate);
        parsePush.setChannels(null); // Clear broadcast channel that is set by default.
        actualPayload = parsePush.getJSONData();
        assertEqual(expectedPayload.toString(), actualPayload.toString());
        
        System.out.println("-------------- Setting an expiration interval");
        expectedPayload.put("push_time", Parse.encodeDate(currentDate));
        expectedPayload.put("expiration_interval", 518400);
                
        parsePush.setExpirationTimeInterval(expectedPayload.getLong("expiration_interval"));
        parsePush.setPushTime(currentDate);
        
        expectedPayload.remove("expiration_time"); // Expiration time interval nullifies expiration time
        actualPayload = parsePush.getJSONData();
        assertEqual(expectedPayload.toString(), actualPayload.toString());
    }
    
    private void resetPushHandlingData() {
        foregroundPushHandled = false;
        backgroundPushHandled = false;
        
        receivedPushRegistrationError = null;
        receivedForegroundPushPayload = null;
        receivedBackgroundPushPayload = null;
        receivedAppOpenPushPayload = null;
        
        ParsePush.resetPushDataUsedToOpenApp();
        ParsePush.resetUnprocessedPushData();
    }
    
    private void runHandlePushRegistrationStatusTest(final String message, int type, boolean useCallback) {
        System.out.println("-------------- " + 
                String.format("message=%s; type=%d; useCallback=%b", message, type, useCallback));
        resetPushHandlingData();
        try {
            if (!useCallback) {
                ParsePush.setPushCallback(null);
            }
            
            int expectedErrorCode;
            switch (type) {
                case 0:
                    expectedErrorCode = type;
                    break;
                case 2:
                    expectedErrorCode = ParseException.PARSE4CN1_PUSH_REGISTRATION_FAILED_MISSING_PARAMS;
                    break;
                case 3:
                    expectedErrorCode = ParseException.PARSE4CN1_PUSH_REGISTRATION_FAILED_INSTALLATION_UPDATE_ERROR;
                    break;
                default:
                    expectedErrorCode = ParseException.PARSE4CN1_PUSH_REGISTRATION_FAILED;
                    break;
            }
            
            if (message == null) {
                expectedErrorCode = 0;
            }
            
            ParsePush.handlePushRegistrationStatus(message, type);
            if (!useCallback) {
                assertNull(receivedPushRegistrationError, "No registration error expected on old PushCallback");
                ParsePush.setPushCallback(this); // Should trigger callback
            }
            
            if (expectedErrorCode != 0) {
                checkPushRegistrationErrorInfo(message, expectedErrorCode);
            } else {
                assertNull(receivedPushRegistrationError, "No registration error callback expected upon succesful registration");
            }
            
        } finally {
            if (!useCallback) {
                resetPushHandlingData();
                ParsePush.setPushCallback(this);
                // Callback if any should already have been triggered so shouldn't occur again
                assertNull(receivedPushRegistrationError, "Registration error should be reported only once");
            }
        }
    }
    
    private void runHandlePushRegistrationErrorResetTest() throws JSONException {
        
        resetPushHandlingData();
        
        try {
            int expectedErrorCode = ParseException.PARSE4CN1_PUSH_REGISTRATION_FAILED;
            String expectedMessage = "A generic failure occurred";
            
            ParsePush.setPushCallback(null);
            ParsePush.handlePushRegistrationStatus(expectedMessage, expectedErrorCode);
            
            System.out.println("-------------- After app open via push, pending "
                    + "registration error should still be delivered when callback is set");
            
            ParsePush.handlePushOpen(pushPayload2, true);
            ParsePush.setPushCallback(this);
            checkPushRegistrationErrorInfo(expectedMessage, expectedErrorCode);

            ParsePush.setPushCallback(null);
            ParsePush.handlePushOpen(pushPayload1, false);
            ParsePush.setPushCallback(this);
            checkPushRegistrationErrorInfo(expectedMessage, expectedErrorCode);
            
            System.out.println("-------------- On receiving push while running, pending "
                    + "registration error be cleared");
            
            resetPushHandlingData();
            ParsePush.setPushCallback(null);
            ParsePush.handlePushRegistrationStatus(expectedMessage, expectedErrorCode);
            ParsePush.handlePushReceivedForeground(pushPayload1);
            ParsePush.setPushCallback(this);
            assertNull(receivedPushRegistrationError, "Previous failed registration error is cleared when push is received in foreground");
            
            ParsePush.setPushCallback(null);
            ParsePush.handlePushRegistrationStatus(expectedMessage, expectedErrorCode);
            ParsePush.handlePushReceivedBackground(pushPayload2);
            ParsePush.setPushCallback(this);
            assertNull(receivedPushRegistrationError, "Previous failed registration error is cleared when push is received in background");
            
            ParsePush.setPushCallback(null);
            ParsePush.handlePushRegistrationStatus(expectedMessage, expectedErrorCode);
            ParsePush.handleUnprocessedPushReceived(pushPayload1);
            ParsePush.setPushCallback(this);
            assertNull(receivedPushRegistrationError, "Previous failed registration error is cleared when unprocessed push is received");
            
        } finally {
            ParsePush.setPushCallback(this);
        }
    }

    private void checkPushRegistrationErrorInfo(String expectedMessage, int expectedErrorCode) {
        assertNotNull(receivedPushRegistrationError, "Registration failure callback expected"); 
        assertEqual(expectedMessage, receivedPushRegistrationError.getMessage());
        assertEqual(expectedErrorCode, receivedPushRegistrationError.getCode());
    }
}
