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
package com.parse; // Keep inside the parse package to access package internal classes

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Notification;
import com.parse4cn1.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;
import com.parse4cn1.util.Logger;
import android.util.Log;
import com.parse4cn1.nativeinterface.CN1AndroidApplication; // TODO: Remember to update if Application class is different!

/**
 * A custom broadcast receiver for handling push messages received via Parse.
 * <p>
 * It handles forwards pushes received to {@link ParsePush} based on the app's 
 * state as specified in the reference push behavior.
 */
public class CN1ParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {
    private static final String TAG = "CN1ParsePushBroadcastReceiver";
    
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        /*
         Adapted from ParsePushBroadcastReceiver.onPushReceived(). Main changes:
         1. Implemented callbacks to ParsePush with the push payload based on
            app state
         */
        
        JSONObject pushData = null;
        try {
            pushData = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));
        } catch (JSONException e) {
            writeErrorLog("Unexpected JSONException when parsing received push data:\n" + e);
        }
        writeDebugLog("Push received: " + (pushData == null ? "<no payload>" : pushData.toString()));

        boolean handled = false;
        if (pushData != null && CN1AndroidApplication.isAppRunning()) {
            if (CN1AndroidApplication.isAppInForeground()) {
                writeDebugLog("App in foreground; will allow app to directly handle push message, if desired");
                handled = ParsePush.handlePushReceivedForeground(pushData.toString());
            } else if (CN1AndroidApplication.isAppInBackground()) {
                writeDebugLog("App in background; will allow app to directly handle push message, if desired");
                handled = ParsePush.handlePushReceivedBackground(pushData.toString());
            }
        }

        if (!handled) {
            // If the push data includes an action string, that broadcast intent is fired.
            String action = null;
            if (pushData != null) {
                action = pushData.optString("action", null);
            }
            if (action != null) {
                writeDebugLog("Firing broadcast for action " + action);
                Bundle extras = intent.getExtras();
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtras(extras);
                broadcastIntent.setAction(action);
                broadcastIntent.setPackage(context.getPackageName());
                context.sendBroadcast(broadcastIntent);
            }

            Notification notification = getNotification(context, intent);

            if (notification != null) {
                writeDebugLog("Scheduling notification for push message since it was not handled by app");
                ParseNotificationManager.getInstance().showNotification(context, notification);
            } else {
                // If, for any reason, creating the notification fails (typically because
                // the push is a 'hidden' push with no alert/title fields),
                // store it for later processing.
                if (pushData != null) {
                    writeDebugLog("Requesting ParsePush to handle unprocessed (hidden?) push message");
                    ParsePush.handleUnprocessedPushReceived(pushData.toString());
                }
            }
        } else {
            writeDebugLog("Push already handled by app so not scheduling any notification");
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        /*
         Adapted from ParsePushBroadcastReceiver. Main changes:
         1. Remove analytics call to log app open; CN1 app should decide if and where to do that
         2. Adapted code for starting app activity since it caused problems (see 
            comments towards the end of the method starting from line 'Original code'
         3. Implemented necessary ParsePush callback to set push data in advance
            so that it will be available when the app activity is started/resumed
         */
        JSONObject pushData = null;
        String uriString = null;
        try {
            pushData = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));
            uriString = pushData.optString("uri", null);
        } catch (JSONException e) {
            writeErrorLog("Unexpected JSONException when parsing "
                    + "push data from opened notification: " + e);
        }
        
        writeDebugLog("Push opened: " + (pushData == null ? "<no payload>" : pushData.toString()));
        
        if (pushData != null) {
            // Forward payload so that it is available when app is opened via the push message
            ParsePush.handlePushOpen(pushData.toString(), CN1AndroidApplication.isAppInForeground());
            writeDebugLog("Notified ParsePush of opened push notification");
        }

        Class<? extends Activity> cls = getActivity(context, intent);
                
        Intent activityIntent;
        if (uriString != null) {
            writeDebugLog("Creating an intent to view URI: " +  uriString);
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        } else {
            activityIntent = new Intent(context, cls);
        }

        activityIntent.putExtras(intent.getExtras());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        // Original code
//        /*
//         In order to remove dependency on android-support-library-v4
//         The reason why we differentiate between versions instead of just using context.startActivity
//         for all devices is because in API 11 the recommended conventions for app navigation using
//         the back key changed.
//         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            TaskStackBuilderHelper.startActivities(context, cls, activityIntent);
//        } else {
//            context.startActivity(activityIntent);
//        }
        
        // The task stack builder approach causes only the title and a white (blank) screen 
        // to be shown when the app is in the foreground and the push notification is 
        // opened (see also problem report to CN1 support forum: https://groups.google.com/d/msg/codenameone-discussions/Z3F924j_BG4/7rn7v7oABwAJ)
        // As a result, the context.startActivity() approach is currently taken always.
        // Not sure yet if it has any undesirable side effects for sdk version 
        // before JELLY_BEAN (actually HONEYCOMB (v3.0) according to TaskStackBuilder documentation 
        // at: http://developer.android.com/reference/android/support/v4/app/TaskStackBuilder.html.
        context.startActivity(activityIntent);
    }
    
    /**
     * A utility method to log messages to both CN1 Logger and console for
     * easier/faster debugging.
     * 
     * @param message The message to be logged.
     * @param isError if {@code true} message is logged at error level; otherwise, 
     * it is logged as debug (which should be off in production code).
     */
    private static void writeLog(final String message, boolean isError) {
        if (!isError) {
            Logger.getInstance().debug(message);
            Log.d(TAG, message);
        } else {
            Logger.getInstance().error(message);
            Log.e(TAG, message);
        }
    }
    
    /**
     * Writes a debug log message to both CN1 logger and console.
     * @param message The message to be logged.
     */
    private static void writeDebugLog(final String message) {
        writeLog(message, false);
    }
    
    /**
     * Writes an error log message to both CN1 logger and console.
     * @param message The message to be logged.
     */
    private static void writeErrorLog(final String message) {
        writeLog(message, true);
    }
}
