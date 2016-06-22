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

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.ui.Display;
import com.parse4cn1.util.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Tests for the {@link ParseInstallation} class.
 */
public class ParseInstallationTest extends BaseParseTest {
    
    // Use a valid, predefined installation for testing since creation from client is not supported by parse4cn1
    // The installation object with this ID is guaranteed not to be deleted in the backend
    // (protected via the cloud code beforeDelete hook)
    private static String objectId = null;
    private static final String[] objectIds = new String[]{
        "uZEbU3FPwa" /* parse.com */,
        "2C8bKd0kdb" /* openshift */, 
        "XVHtg1oqZC" /* back4apps */}; // Possible test ids from different backends
    private static final String installationId = "09a198b7-b6e0-4bd3-8eb0-f2b712f957c2";
    private static ParseInstallation currentInstallation;
    
    @Override
    public boolean runTest() throws Exception {
        testRestApiExample();
        testUserDefinedFields();
        testSubscriptionToChannels();
        testSerialization();
        testBadging();
        return true;
    }
    
    @Override
    public void prepare() {
        super.prepare();
        testRetrieveUnsetInstallation(); // Must be run before initialization of installation ID
        for (String objId: objectIds) {
            ParseInstallation.setInstallationObjectId(objId);
            try {            
                currentInstallation = ParseInstallation.getCurrentInstallation();
                objectId = objId;
                break;
            } catch (ParseException ex) {
                Logger.getInstance().error("Retrieving current installation failed for objectId="
                        + objId + ". Will try other objectIds.\nError: " +  ex);
            }
        }
       
        assertNotNull(currentInstallation, "Current installation is null");
        
        try {
            assertEqual(objectId, currentInstallation.getObjectId());
            assertEqual(installationId, currentInstallation.getInstallationId());
        } catch (ParseException ex) {
            Logger.getInstance().error("Retrieving installation ID failed! Error: " +  ex);
            fail("Retrieving installation ID failed! Error: " + ex.getMessage());
        }
    }
    
    @Override
    public void cleanup() {
        try {
            assertNotNull(currentInstallation, "Current installation should still be valid after tests");
            assertTrue(currentInstallation.getSubscribedChannels().contains("test"),
                    "[Invariant] After all tests, test installation must still be subscribed to the 'test' channel");
            ParseInstallation.resetCurrentInstallation();
            // Test installation is used for tracking ParseTestApp test pushes so it should never be deleted
        } catch (ParseException ex) {
            fail("An unexpected error occurred: " + ex);
        } finally {
            super.cleanup();
        }
    }
    
    private void testRestApiExample() throws ParseException {
        System.out.println("============== testRestApiExample()");
        
        System.out.println("-------------- Updating installations");
        List<String> channels = Arrays.asList("", "foo");
        currentInstallation.subscribeToChannels(channels);
        currentInstallation.save();
        
        ParseInstallation retrieved = retrieveInstallation();
        assertTrue(retrieved.getSubscribedChannels().contains(""), 
                "Subscription to channel '' should succeed");
        assertTrue(retrieved.getSubscribedChannels().contains("foo"),
                "Subscription to channel 'foo' should succeed");
        
        currentInstallation.unsubscribeFromChannels(channels);
        currentInstallation.save();
        
        retrieved = retrieveInstallation();
        assertFalse(retrieved.getSubscribedChannels().contains(""),
                "Unsubscription from channel '' should succeed");
        assertFalse(retrieved.getSubscribedChannels().contains("foo"),
                "Unsubscription from channel 'foo' should succeed");
        
        System.out.println("-------------- Updating installations (custom fields)");
        try {
            assertNull(currentInstallation.getBoolean("scores"));
            assertNull(currentInstallation.getBoolean("gameResults"));
            assertNull(currentInstallation.getBoolean("injuryReports"));

            currentInstallation.put("scores", true);
            currentInstallation.put("gameResults", true);
            currentInstallation.put("injuryReports", true);
            currentInstallation.save();

            retrieved = retrieveInstallation();
            assertTrue(retrieved.getBoolean("scores"));
            assertTrue(retrieved.getBoolean("gameResults"));
            assertTrue(retrieved.getBoolean("injuryReports"));
        } finally {
            // Since we use a fixed installation object for these tests
            // we make sure that it's state is well defined to avoid false positives
            // from previous test runs.
            currentInstallation.remove("scores");
            currentInstallation.remove("gameResults");
            currentInstallation.remove("injuryReports");
            currentInstallation.save();

            retrieved = retrieveInstallation();
            assertNull(retrieved.getBoolean("scores"));
            assertNull(retrieved.getBoolean("gameResults"));
            assertNull(retrieved.getBoolean("injuryReports"));
        }
    }
    
    private void testUserDefinedFields() throws ParseException {
        System.out.println("============== testUserDefinedFields()");
        
        final String key = "userDefined";
        final String value = "ParseRocks!";
        currentInstallation.put(key, value);
        currentInstallation.save();
        
        final ParseInstallation retrieved = retrieveInstallation();
        
        assertEqual(value, retrieved.getString(key));
    }
    
    private void testSubscriptionToChannels() throws ParseException {
        System.out.println("============== testSubscriptionToChannels()");
        
        System.out.println("-------------- Clear all existing subscriptions");
        currentInstallation.unsubscribeFromAllChannels();
        
        List<String> subscribedChannels = currentInstallation.getSubscribedChannels();
        checkSubscriptions(new ArrayList<String>(), subscribedChannels);
        
        final String channelGlobal = "global";
        final String channelTest = "test";
        final String channelActiveUsers = "activeUsers";
        final String channelFirstTimers = "firstTimers";
        
        System.out.println("-------------- Subscribe to single channel");
        currentInstallation.subscribeToChannel(channelGlobal);
        ParseInstallation retrieved = retrieveInstallation();
        checkSubscriptions(Arrays.asList(channelGlobal), retrieved.getSubscribedChannels());
        
        System.out.println("-------------- Subscribe to existing channel");
        currentInstallation.subscribeToChannel(channelGlobal);
        retrieved = retrieveInstallation();
        checkSubscriptions(Arrays.asList(channelGlobal), retrieved.getSubscribedChannels());
        
        System.out.println("-------------- Subscribe to multiple channels (including duplicates)");
        List<String> channels = Arrays.asList(channelGlobal, channelTest, channelActiveUsers, channelFirstTimers);
        currentInstallation.subscribeToChannels(channels);
        retrieved = retrieveInstallation();
        checkSubscriptions(channels, retrieved.getSubscribedChannels());
        
        System.out.println("-------------- Unsubscribe from non-existing channel");
        currentInstallation.unsubscribeFromChannel("nonExistent");
        retrieved = retrieveInstallation();
        checkSubscriptions(channels, retrieved.getSubscribedChannels());
        
        System.out.println("-------------- Unsubscribe from single channel");
        channels = new ArrayList<String>(currentInstallation.getSubscribedChannels());
        channels.remove(channelGlobal);
        currentInstallation.unsubscribeFromChannel(channelGlobal);
        retrieved = retrieveInstallation();
        checkSubscriptions(channels, retrieved.getSubscribedChannels());
        
        System.out.println("-------------- Unsubscribe from multiple channels (including non-existent)");
        channels = new ArrayList<String>(currentInstallation.getSubscribedChannels());
        channels.remove(channelActiveUsers);
        channels.remove(channelFirstTimers);
        
        currentInstallation.unsubscribeFromChannels(Arrays.asList(channelActiveUsers, channelFirstTimers, "ignoreMe"));
        retrieved = retrieveInstallation();
        checkSubscriptions(channels, retrieved.getSubscribedChannels());
    }
    
    private void testSerialization() {
        System.out.println("============== testSerialization()");

        ParseInstallation retrieved = (ParseInstallation) serializeAndRetrieveParseObject(currentInstallation);
        compareParseObjects(currentInstallation, retrieved, null);
    }

    private void testBadging() throws ParseException {
        System.out.println("============== testBadging()");

        if (Parse.getPlatform() == Parse.EPlatform.IOS) {
            System.out.println("Skipping this test since badging on iOS requires "
                    + "native calls which will fail when running in simulator");
            return;
        }
        
        currentInstallation.setBadge(0);
        assertEqual(0, (int)currentInstallation.getBadge(), 
                "It should be possible to retrieve the badge field on all platforms");
        
        currentInstallation.setBadge(5);
        assertEqual(5, (int)currentInstallation.getBadge(), 
                "It should be possible to retrieve the badge field on all platforms");
    }
    
    private void testRetrieveUnsetInstallation() {
        System.out.println("============== testRetrieveUnsetInstallation()");
        
//        try {
//            if (ParseInstallation.getCurrentInstallation() != null) {
//                System.out.println("Skipping test since installation is already initialized");
//                return;
//                // This can happen e.g. when the test is run via the CN1 test project
//                // where the test app automatically tries to retrieve the
//                // installation ID or when ParseInstallationTest is executed multiple times.
//                // Unfortunately, there's no easy way to distinguish the two...
//            }
//        } catch (ParseException ex) {
//            System.out.println("Ignoring error while trying to retrieve current installation. Error: " + ex.getMessage());
//        }
        
        boolean passed = false;
        try {
            ParseInstallation.setInstallationObjectId(null);
            ParseInstallation.getCurrentInstallation();
        } catch (ParseException ex) {
            if (ex.getCode() == ParseException.PARSE4CN1_INSTALLATION_ID_NOT_RETRIEVED_FROM_NATIVE_SDK /* Occurs in java test project */
                    || ex.getCode() == ParseException.PARSE4CN1_NATIVE_INTERFACE_LOOKUP_FAILED /* Occurs in CN1 test app */) {
                passed = true;
            }
        }
        
        assertTrue(passed, "Retrieval of installation ID should fail when installation ID is not yet initialized");
    }
    
    private void checkSubscriptions(final List<String> expected, final List<String> actual) {
        assertNotNull(expected, "Expected subscriptions should not be null");
        if (expected.size() == 0) {
            assertTrue(actual == null || actual.size() == 0, "Excepted null or empty subscriptions but found: " + actual);
        } else {
            assertEqual(expected, actual, 
                    "Expected channel subscriptions " + expected + " differ from actual subscriptions " + actual);
        }
    }
    
    private ParseInstallation retrieveInstallation() throws ParseException {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("objectId", currentInstallation.getObjectId());
        String response = ParseCloud.callFunction("getInstallationByObjectId", params);

        ParseInstallation installation = ParseInstallation.create(ParseConstants.CLASS_NAME_INSTALLATION);
        try {
            installation.setData(new JSONObject(response));
            return installation;
        } catch (JSONException ex) {
            throw new ParseException("Retrieval of installation failed", ex);
        }
    }
}
