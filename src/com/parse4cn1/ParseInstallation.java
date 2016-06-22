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
import com.codename1.system.NativeLookup;
import com.parse4cn1.nativeinterface.ParseInstallationNative;
import com.parse4cn1.nativeinterface.ParsePushNative;
import com.parse4cn1.util.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * {@code ParseInstallation} represents an instance of an app that has been installed 
 * on a specific device.
 * <p>
 * ParseInstallation objects are simply ParseObjects extended with specific 
 * installation data as required by the different platforms. 
 * Some of these fields are readonly as explained in 
 * <a href="https://parse.com/docs/rest/guide#push-notifications-installations">the REST API documentation</a>.
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
 * {@link ParseInstallation#setInstallationObjectId(java.lang.String)}.
 * 
 */
public class ParseInstallation extends ParseObject {

    private static final String KEY_INSTALLATION_ID = "installationId";
    private static final String KEY_DEVICE_TYPE = "deviceType";
    private static final String KEY_CHANNELS = "channels";
    private static final String KEY_BADGE = "badge";
    private static boolean parseSdkInitialized = false;
    private static String objectId;

    private static ParseInstallation currentInstallation;
    
    /**
     * Retrieves the current installation. On Android and iOS, a new installation 
     * is created, persisted to the Parse backend and returned, if one is not present.
     * On Windows Phone and any other platform, the installation is retrieved from the backend if its
     * objectId has been specified via {@link #setInstallationObjectId(java.lang.String)}.
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
            final String id;
            id = retrieveObjectId();
            if (id != null) {
              
                try {
                    currentInstallation = fetchInstallation(id);
                } catch (ParseException ex) {
                    if (ex.getCode() == ParseException.PARSE4CN1_INSTALLATION_NOT_FOUND) {
                        // No result (yet?)... It could be that saveInBackground call in native code 
                        // (used to avoid blocking) has not yet completed so retry.
                        // Under normal conditions, this won't happen but with slow connections, 
                        // for example, we may end up here.
                        final long delayInMilliSeconds = 1500;
                        Logger.getInstance().warn("First attempt to retrieve current installation "
                                + "with installation ID '" + id 
                                + "' failed. Will retry in " + delayInMilliSeconds + " milliseconds.");
                        try {
                            Thread.sleep(delayInMilliSeconds);
                            Logger.getInstance().info("Trying again to retrieve current installation "
                                + "with installation ID '" + id + "' after timeout");
                            currentInstallation = fetchInstallation(id);
                            Logger.getInstance().info("Second attempt to retrieve current installation "
                                + "with installation ID '" + id + "' succeeded");
                        } catch (InterruptedException e) {
                            throw new ParseException(ParseException.PARSE4CN1_INSTALLATION_NOT_FOUND,
                                    "Found no installation with ID (after retry) " + id);
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
     * Sets the current installation to null.
     */
    public static void resetCurrentInstallation() {
        currentInstallation = null;
    }
    
    /**
     * Sets the object ID of the current installation.
     * <p>This method is intended for use on platforms where parse4cn1 cannot 
     * retrieve the installation object ID e.g. because native interfaces are not supported.
     * It can also be used in unit tests for initializing the installation ID
     * <p><em>Note:</em> It is assumed that at the time this method is invoked,
     * the corresponding ParseInstallation already exists in the Parse backend.
     * @param objectId The current installation's ID to be set. 
     */
    public static void setInstallationObjectId(final String objectId) {
        Logger.logBuffered("setInstallationObjectId(): Installation object ID explicitly set to " 
                + objectId);
        ParseInstallation.objectId = objectId;
    }
    
    /**
     * Constructs a query for {@code ParseInstallation}.
     * <p>
     * <strong>Note:</strong> Parse only allows the following types of queries
     * for installations:
     * <pre>
     * query.get(objectId)
     * query.whereEqualTo("installationId", value)
     * query.whereMatchesKeyInQuery("installationId", keyInQuery, query)
     * </pre>
     * <p>
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
     * Returns the unique ID of this installation object as contained in the
     * {@code ParseInstallation} object.
     *
     * @return A UUID that represents this device or null if one is not
     * specified.
     * @throws com.parse4cn1.ParseException if the installation Id cannot be retrieved.
     */
    public String getInstallationId() throws ParseException {
        return getString(KEY_INSTALLATION_ID);
    }
    
    /**
     * (iOS only) Sets the app batch that is shown on the app icon to the specified
     * count for this installation.
     * <p>If invoked on other platforms, the badge will still be set via the REST API
     * but will not have the desired effect of badging the app icon.
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
            Logger.getInstance().warn("App icon badging is an iOS-only feature. On this platform, "
                    + "the badge will simply be set via the REST API");
            put(KEY_BADGE, count);
            save();
        }
    }
    
    /**
     * Retrieves the current application badge count.
     * @return The app badge count if any or null.
     */
    public Integer getBadge() {
       return getInt(KEY_BADGE);
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
     * Subscribes this device to the specified {@code channels}.
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
        
        if (existingChannels == null) {
            finalChannels = channels;
        } else {
            finalChannels = new ArrayList<String>(existingChannels);
            
            for (String channel : channels) {
                if (!finalChannels.contains(channel)) {
                    finalChannels.add(channel);
                } else {
                    // Although a channel is in the local list, subscription may
                    // have failed resulting in a mismatch between server and device.
                    // So we'll send the request anyway.
                    Logger.getInstance().warn("May already be subscribed to channel: " + channel);
                }
            }
        }
        
        saveChannels(finalChannels);
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
        
        if (channels.equals(existingChannels)) { // Remove all
            finalChannels = new ArrayList<String>();
        } else {
            finalChannels = new ArrayList<String>(existingChannels);
            
            for (String channel : channels) {
                if (finalChannels.contains(channel)) {
                    finalChannels.remove(channel);
                } else {
                    // Although a channel is not in the local list, unsubscription may
                    // have failed resulting in a mismatch between server and device.
                    // So we'll send the request anyway.
                    Logger.getInstance().warn("May already be unsubscribed from channel: " + channel);
                }
            }
        }
        
        saveChannels(finalChannels);
    }

    private void saveChannels(final List<String> channels) throws ParseException {
        put(KEY_CHANNELS, channels);
        // For some strange reason, an error 135 (missing fields) occurs on some platforms (e.g. win phone)
        // if the following fields (which ironically are already in the Parse installation 
        // retrieved from the server) are not included in the request.
        // Seems to be a Parse issue (see https://goo.gl/cwwZdz) but this approach works around it.
        try {
            String installationId = getInstallationId();
            if (installationId != null) {
                put(KEY_INSTALLATION_ID, installationId);
            }
        } catch (ParseException ex) {
            // Ignore
        }
        
        if (getString(KEY_DEVICE_TYPE) != null) {
            put(KEY_DEVICE_TYPE, getString(KEY_DEVICE_TYPE));
        }
        save();
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
     * Executes a query for retrieving an installation by its object ID.
     * @param objectId The installation object's ID of the installation to be queried for.
     * @return The retrieved installation.
     * @throws ParseException if no installation is found or multiple installations are found.
     */
    private static ParseInstallation fetchInstallation(final String objectId) throws ParseException {
        if (Parse.isEmpty(objectId)) {
            return null;
        }

        // [16-05-16] Call below now fails with error:
        // "Clients aren't allowed to perform the find operation on the installation collection."
        // because the operation now requires the master key --> use cloud code
            
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("objectId", objectId);
        String response = ParseCloud.callFunction("getInstallationByObjectId", params);

        ParseInstallation installation = ParseInstallation.create(ParseConstants.CLASS_NAME_INSTALLATION);
        try {
            installation.setData(new JSONObject(response));
            return installation;
        } catch (JSONException ex) {
            throw new ParseException("Retrieval of installation failed", ex);
        }
    }
    
    /**
     * Retrieves the installation object's id.
     * <p> For Android and iOS, a native call is made to the Parse SDK; for 
     * every other platform, the previously set installation object's ID is returned, if any.
     * <p>
     * <b>Note: </b>If the installation object's ID was explicitly set (cf. {@link #setInstallationObjectId(java.lang.String)},
     * it will always take precedence regardless of the platform (so don't use the setter on 
     * Android and iOS!).
     * 
     * @return The installation object's id if found; otherwise null.
     * @throws ParseException if anything goes wrong
     */
    private static String retrieveObjectId() throws ParseException {
        if (objectId != null) {
            return objectId;
        }
        
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
                            // TODO: Add API endpoint
                            nativeInstallation.initialize(Parse.getApiEndpoint(), Parse.getApplicationId(), Parse.getClientKey());
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
                    
                    // TODO: Change native api to retrieve objectId() instead since installation ID requires master key
                    objectId = nativeInstallation.getInstallationId();
                    parseSdkInitialized = !Parse.isEmpty(objectId);
                } catch (Exception ex) {
                   throw new ParseException(ParseException.PARSE4CN1_INSTALLATION_ID_NOT_RETRIEVED_FROM_NATIVE_SDK,
                           "Failed to retrieve installation ID." +
                           (ex != null ? " Error: " + ex.getMessage() : ""), ex); 
                }
            } else {
                throw new ParseException(ParseException.PARSE4CN1_NATIVE_INTERFACE_LOOKUP_FAILED, 
                        "Failed to retrieve installation ID");
            }
        }
        
        return objectId;
    }
}
