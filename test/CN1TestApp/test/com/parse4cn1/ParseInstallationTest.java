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

import com.codename1.io.Preferences;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for the {@link ParseInstallation} class.
 */
public class ParseInstallationTest extends BaseParseTest {
    
    // Use a valid, predefined installation for testing since creation from client is not supported by parse4cn1
    private static final String installationId = "9306c879-6096-4a43-9259-71af6da5169f";
    private static ParseInstallation currentInstallation;
    
    @Override
    public boolean runTest() throws Exception {
        testUserDefinedFields();
        testSubscriptionToChannels();
        testSerialization();
        return true;
    }
    
    @Override
    public void prepare() {
        super.prepare();
        Preferences.set(ParseInstallation.PARSE_INSTALLATION_ID_SETTING_KEY, installationId);
        currentInstallation = ParseInstallation.getCurrentInstallation();
        assertNotNull(currentInstallation, "Current installation is null");
    }
    
    @Override
    public void cleanup() {
        try {
            assertNotNull(currentInstallation, "Current installation should still be valid after tests");
            assertTrue(currentInstallation.getSubscribedChannels().contains("test"),
                    "[Invariant] After all tests, test installation must still be subscribed to the 'test' channel");
            // Test installation is used for tracking ParseTestApp test pushes so it should never be removed
        } catch (ParseException ex) {
            fail("An unexpected error occurred: " + ex);
        } finally {
            super.cleanup();
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
         return ParseObject.fetch(ParseConstants.CLASS_NAME_INSTALLATION, 
                 currentInstallation.getObjectId());
    }
}
