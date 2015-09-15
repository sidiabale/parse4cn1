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
import com.parse4cn1.util.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@code ParseInstallation} represents an instance of an app that has been installed 
 * on a specific device.
 * <p>
 * ParseInstallation objects are simply ParseObjects extended with specific 
 * installation data as required by the different platforms. 
 * Some of these fields are readonly as explained in 
 * <a href="">the REST API documentation</a>.
 * <p>
 * Creation of ParseInstallation objects is not exposed via this library as it 
 * requires specific fields and logic already present in the official Parse SDKs
 * and difficult to realize in CN1 (e.g. generating unique device IDs that fit Parse 
 * requirements which are not crystal clear).
 * <p>
 * Thus, creation is delegated to the respective Parse SDK for each platform, 
 * access via the CN1 native interface mechanism. However, since the Windows 
 * Phone port does not support native interfaces, creation is the responsibility
 * of the user who should make the corresponding installation id available via 
 * {@link com.codename1.io.Preferences} under the key {@value #PARSE_INSTALLATION_ID_SETTING_KEY}.
 * 
 */
public class ParseInstallation extends ParseObject {

    public static final String PARSE_INSTALLATION_ID_SETTING_KEY = "parse4cn1_installationId";
    private static final String KEY_INSTALLATION_ID = "installationId";
    private static final String KEY_CHANNELS = "channels";

    private static ParseInstallation currentInstallation;
    
    /**
     * Retrieves the current installation. On Android and iOS, a new installation 
     * is created, persisted to the Parse backend and returned, if one is not present.
     * On Windows Phone and any other platform, the installation is retrieved from the backend if its
     * installationId is specified in the {@link com.codename1.io.Preferences} 
     * under the key {@value #PARSE_INSTALLATION_ID_SETTING_KEY}.
     * <p>
     * <em>Note</em>Windows Phone is a special case because native interfaces are not (yet) 
     * supported so creating the installation via the .net native Parse SDK is not feasible.
     * 
     * @return The current installation if one exists (all platforms), could be 
     * created (Android and iOS) or could be retrieved (Windows Phone).
     */
    public static ParseInstallation getCurrentInstallation() {
        
        if (currentInstallation == null) {
        
            final String installationId;
            try {
                installationId = retrieveInstallationId();
                if (installationId != null) {
                    final ParseQuery<ParseInstallation> query = 
                            ParseInstallation.getQuery().whereEqualTo(KEY_INSTALLATION_ID, installationId);
                    final List<ParseInstallation> results = query.find();
                    if (results.size() == 1) {
                        currentInstallation = results.get(0);
                    } else if (results.size() > 1) {
                        throw new ParseException("Found multiple installations with ID " 
                                + installationId + " (installation IDs must be unique)", null);
                    }
                }

            } catch (ParseException ex) {
                Logger.getInstance().error("Unable to retrieve installation ID. Error: " + ex);
            }
        }
        
        return currentInstallation;
    }
    
    /**
     * Constructs a query for {@code ParseInstallation}.
     * <p/>
     * <strong>Note:</strong> Parse only allows the following types of queries
     * for installations:
     * <pre>
     * query.get(objectId)
     * query.whereEqualTo("installationId", value)
     * query.whereMatchesKeyInQuery("installationId", keyInQuery, query)
     * </pre>
     * <p/>
     * You can add additional query clauses, but one of the above must appear as
     * a top-level {@code AND} clause in the query.
     *
     * @return The newly created ParseInstallation query.
     * @see ParseQuery#getQuery(java.lang.Class)
     */
    public static ParseQuery<ParseInstallation> getQuery() {
        return ParseQuery.getQuery(ParseInstallation.class);
    }

    /**
     * Returns the unique ID of this installation as contained in the
     * {@code ParseInstallation} object.
     *
     * @return A UUID that represents this device or null if one is not
     * specified.
     * @throws com.parse4cn1.ParseException if the installation Id cannot be retrieved.
     */
    public String getInstallationId() throws ParseException {
        return retrieveInstallationId();
    }

    /**
     * Retrieves all the channels to which this device is currently subscribed
     * to.
     *
     * @return A read-only list of subscribed push notification channels.
     * @throws com.parse4cn1.ParseException if anything goes wrong while retrieving 
     * subscriptions.
     */
    public List<String> getSubscribedChannels() throws ParseException {
        List<String> channels = getList(KEY_CHANNELS);
        if (channels == null) {
            return channels;
        }
        return Collections.unmodifiableList(channels);
    }

    /**
     * Subscribes this device to the specified {@code channel}.
     * <p>
     * This method has no effect if the channel is already subscribed to.
     * 
     * @param channel The channel this device is to be subscribed to.
     * @throws com.parse4cn1.ParseException if subscription fails.
     */
    public void subscribeToChannel(final String channel) throws ParseException {
        subscribeToChannels(Arrays.asList(channel));
    }

    /**
     * Subscribes this device to the specified {@code channels} excluding duplicates.
     * 
     * @param channels The channels this device is to be subscribed to.
     * @throws com.parse4cn1.ParseException if subscription fails.
     */
    public void subscribeToChannels(final List<String> channels) throws ParseException {
        if (channels == null) {
            return;
        }
        
        final List<String> existingChannels = getSubscribedChannels();
        List<String> finalChannels;
        boolean changed = false;
        
        if (existingChannels == null) {
            finalChannels = channels;
            changed = true;
        } else {
            finalChannels = new ArrayList<String>(existingChannels);
            
            for (String channel : channels) {
                if (!finalChannels.contains(channel)) {
                    changed = true;
                    finalChannels.add(channel);
                } else {
                    Logger.getInstance().warn("Ignoring duplicate subscription request for channel: " + channel);
                }
            }
        }
        
        if (changed) {
            put(KEY_CHANNELS, finalChannels);
            save();
        }
    }

    /**
     * Unsubscribes this device from the specified {@code channel}.
     * <p>
     * This method has no effect if the channel is not currently subscribed to.
     * 
     * @param channel The channel this device is to be unsubscribed from.
     * @throws com.parse4cn1.ParseException if desubscription fails.
     */
    public void unsubscribeFromChannel(final String channel) throws ParseException {
        unsubscribeFromChannels(Arrays.asList(channel));
    }

    /**
     * Unsubscribes this device from the specified {@code channels} excluding 
     * non-existing channels.
     * 
     * @param channels The channels this device is to be unsubscribed from.
     * @throws com.parse4cn1.ParseException if desubscription fails.
     */
    public void unsubscribeFromChannels(final List<String> channels) throws ParseException {
        final List<String> existingChannels = getSubscribedChannels();
        
        if (channels == null || existingChannels == null) {
            return;
        }
        
        List<String> finalChannels;
        boolean changed = false;
        
        if (channels.equals(existingChannels)) { // Remove all
            finalChannels = new ArrayList<String>();
            changed = true;
        } else {
            finalChannels = new ArrayList<String>(existingChannels);
            
            for (String channel : channels) {
                if (finalChannels.contains(channel)) {
                    finalChannels.remove(channel);
                    changed = true;
                } else {
                    Logger.getInstance().warn("Ignoring unsubscription request for non-existent channel: " + channel);
                }
            }
        }
        
        if (changed) {
            put(KEY_CHANNELS, finalChannels);
            save();
        }
    }

    /**
     * Unsubscribes this device from all previously subscribed channels.
     * 
     * @throws com.parse4cn1.ParseException if desubscription fails.
     */
    public void unsubscribeFromAllChannels() throws ParseException {
        unsubscribeFromChannels(getSubscribedChannels());
    }

    /**
     * Creates a new ParseInstallation.
     */
    protected ParseInstallation() {
        super(ParseConstants.CLASS_NAME_INSTALLATION);
    }
    
    /**
     * Retrieves the installation id.
     * <p> For Android and iOS, a native call is made to the Parse SDK; for 
     * every other platform, the {@link com.codename1.io.Preferences} approach is used.
     * 
     * @return The installation id if found; otherwise null.
     * @throws ParseException if anything goes wrong
     */
    private static String retrieveInstallationId() throws ParseException {
        if (Parse.getPlatform() == Parse.EPlatform.ANDROID
                || Parse.getPlatform() == Parse.EPlatform.IOS) {
            // TODO invoke native interface
            throw new RuntimeException("Unimplemented");
        }
        
        return Preferences.get(PARSE_INSTALLATION_ID_SETTING_KEY, null);
    }

    /*
     Some known errors when trying to create ParseInstallations via the REST API:
            
     {
        "code": 132,
        "error": "Invalid installation ID: 982021" // Format is apparently fixed but not documented publicly
     }

     {
        "code": 135,
        "error": "deviceType must be specified in this operation"
     }

     {
        "code": 135,
        "error": "at least one ID field (installationId,deviceToken) must be specified in this operation"
     }
     */
}
