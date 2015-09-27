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
import com.codename1.system.NativeLookup;
import com.parse4cn1.nativeinterface.ParseInstallationNative;
import com.parse4cn1.nativeinterface.ParsePushNative;
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
    private static boolean parseSdkInitialized = false;

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
     * 
     * @throws com.parse4cn1.ParseException if any error occurs while 
     * trying to retrieve the current installation.
     */
    public static ParseInstallation getCurrentInstallation() throws ParseException {
        
        if (currentInstallation == null) {
            final String installationId;
            installationId = retrieveInstallationId();
            if (installationId != null) {
              
                try {
                    currentInstallation = fetchInstallation(installationId);
                } catch (ParseException ex) {
                    if (ex.getCode() == ParseException.PARSE4CN1_INSTALLATION_NOT_FOUND) {
                        // No result (yet?)... It could be that saveInBackground call in native code 
                        // (used to avoid blocking) has not yet completed so retry.
                        // Under normal conditions, this won't happen but with slow connections, 
                        // for example, we may end up here.
                        final long delayInMilliSeconds = 1500;
                        Logger.getInstance().warn("First attempt to retrieve current installation "
                                + "with installation ID '" + installationId 
                                + "' failed. Will retry in " + delayInMilliSeconds + " milliseconds.");
                        try {
                            Thread.sleep(delayInMilliSeconds);
                            currentInstallation = fetchInstallation(installationId);
                        } catch (InterruptedException e) {
                            throw new ParseException(ParseException.PARSE4CN1_INSTALLATION_NOT_FOUND,
                                    "Found no installation with ID (after retry) " + installationId);
                        }
                    } else {
                        throw ex;
                    }  
                }
            } else {
                throw new ParseException(ParseException.PARSE4CN1_INSTALLATION_ID_NOT_RETRIEVED_FROM_NATIVE_SDK,
                        "Failed to retrieve installation ID");
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
     * (iOS only) Sets the app batch that is shown on the app icon to the specified
     * count for this installation.
     * @param count The badge count to be set
     * @throws ParseException if anything goes wrong.
     */
    public void setBadge(final int count) throws ParseException {
        if (Parse.getPlatform() == Parse.EPlatform.IOS) {
            final ParsePushNative nativePush
                    = (ParsePushNative) NativeLookup.create(ParsePushNative.class);
            if (nativePush != null && nativePush.isSupported()) {
                try {
                    nativePush.setBadge(count);
                } catch (Exception ex) {
                    throw new ParseException("Resetting badge failed."
                            + (ex != null ? " Error: " + ex.getMessage() : ""), ex);
                }
            }
        } else {
            throw new ParseException(ParseException.PARSE4CN1_SETTING_BADGE_NOT_SUPPORTED,
                    "Setting badge of current installation is not "
                    + "supported for this platform.");
        }
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
     * Executes a query for retrieving an installation by ID.
     * @param installationId The installation ID of the installation to be queried for.
     * @return The retrieved installation.
     * @throws ParseException if no installation is found or multiple installations are found.
     */
    private static ParseInstallation fetchInstallation(final String installationId) throws ParseException {

        final ParseQuery<ParseInstallation> query
                = ParseInstallation.getQuery().whereEqualTo(KEY_INSTALLATION_ID, installationId);
        final List<ParseInstallation> results = query.find();
        if (results.size() == 1) {
            return results.get(0);
        } else if (results.size() > 1) {
            throw new ParseException(ParseException.PARSE4CN1_MULTIPLE_INSTALLATIONS_FOUND,
                    "Found multiple installations with ID "
                    + installationId + " (installation IDs must be unique)");
        } else {
            throw new ParseException(ParseException.PARSE4CN1_INSTALLATION_NOT_FOUND,
                    "Found no installation with ID " + installationId);
        }
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
        String installationId = null;
        if (Parse.getPlatform() == Parse.EPlatform.ANDROID
                || Parse.getPlatform() == Parse.EPlatform.IOS) {
            final ParseInstallationNative nativeInstallation = 
                    (ParseInstallationNative)NativeLookup.create(ParseInstallationNative.class);
            if (nativeInstallation != null && nativeInstallation.isSupported()) {
                try {
                    if (!parseSdkInitialized) {
                        if (!Parse.isInitialized()) {
                           throw new ParseException("The Parse library is not yet initialized.", null); 
                        }
                        try {
                            nativeInstallation.initialize(Parse.getApplicationId(), Parse.getClientKey());
                            parseSdkInitialized = true;
                        } catch (Exception ex) {
                            // Something went wrong but it could be a false alarm
                            // for example the Android SDK may throw if it is already initialized.
                            // So we proceed with attempting to retrieve the installation ID.
                            // If that also goes wrong, then there's a real problem and an exception will be thrown.
                            parseSdkInitialized = false;
                            Logger.getInstance().warn("Attempting to retrieve the installation ID "
                                    + "though initialization of Parse SDK failed!"
                                    + "\n\nError: " + ex.getMessage());
                        }
                    }
                    
                    installationId = nativeInstallation.getInstallationId();
                    parseSdkInitialized = (installationId != null && installationId.length() > 0);
                } catch (Exception ex) {
                   throw new ParseException(ParseException.PARSE4CN1_INSTALLATION_ID_NOT_RETRIEVED_FROM_NATIVE_SDK,
                           "Failed to retrieve installation ID." +
                           (ex != null ? " Error: " + ex.getMessage() : ""), ex); 
                }
            } else {
                throw new ParseException(ParseException.PARSE4CN1_NATIVE_INTERFACE_LOOKUP_FAILED, 
                        "Failed to retrieve installation ID");
            }
        } else {
            installationId = Preferences.get(PARSE_INSTALLATION_ID_SETTING_KEY, null);
        }
        
        return installationId;
    }
}
