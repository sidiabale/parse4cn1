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
import android.os.Build;
import android.os.Bundle;
import android.app.Notification;
import android.app.TaskStackBuilder;
import com.parse4cn1.ParsePush;

import org.json.JSONException;
import org.json.JSONObject;
import com.parse4cn1.util.Logger;
import android.util.Log;

/**
 * A custom broadcast receiver for handling push messages received via Parse.
 * <p>
 * It handles forwards pushes received to {@link ParsePush} based on the app's 
 * state as specified in the reference push behavior.
 */
public class CN1ParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {
    private final static String TAG = "CN1ParsePushBroadcastReceiver";
    
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        JSONObject pushData = null;
        try {
            pushData = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));
        } catch (JSONException e) {
            Logger.getInstance().error("Unexpected JSONException when receiving push data: " + e);
        }
        Logger.getInstance().info("Push received with payload: " + pushData);
        Log.i(TAG, "Push received with payload: " + pushData);

        boolean handled = false;
        if (pushData != null && CN1AndroidApplication.isAppRunning()) {
            if (CN1AndroidApplication.isAppInForeground()) {
                Log.d(TAG, "App in foreground; will request app to handle push message, if desired");
                handled = ParsePush.handlePushReceivedForeground(pushData.toString());
            } else if (CN1AndroidApplication.isAppInBackground()) {
                Log.d(TAG, "App in background; will request app to handle push message, if desired");
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
                Bundle extras = intent.getExtras();
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtras(extras);
                broadcastIntent.setAction(action);
                broadcastIntent.setPackage(context.getPackageName());
                context.sendBroadcast(broadcastIntent);
            }

            Notification notification = getNotification(context, intent);

            if (notification != null) {
                Log.d(TAG, "Scheduling notification for push message");
                ParseNotificationManager.getInstance().showNotification(context, notification);
            } else {
                // If, for any reason, creating the notification fails (typically because
                // the push is a 'hidden' push with no alert/title fields),
                // store it for later processing.
                if (pushData != null) {
                    Log.d(TAG, "Requesting ParsePush to handle unprocessed push message");
                    handled = ParsePush.handleUnprocessedPushReceived(pushData.toString());
                }
            }
        } else {
            Log.d(TAG, "Push already handled so not scheduling any notification");
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        /*
         Adapted from ParsePushBroadcastReceiver. Main changes
         1. Remove analytics call to log app open; CN1 app should decide if and where to do that
         2. Fixed issue that new activity (/task?) was being opened because the activityIntent
         flags were set in the else block of 'if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)'
         thus, not being applied to the if section.
         */
        JSONObject pushData = null;
        String uriString = null;
        try {
            pushData = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));
            uriString = pushData.optString("uri", null);
        } catch (JSONException e) {
            Logger.getInstance().error("Unexpected JSONException when receiving push data: " + e);
        }
        
        if (pushData != null) {
            // Forward payload so that it is available when app is opened via the push message
            ParsePush.handlePushOpen(pushData.toString());
        }

        Class<? extends Activity> cls = getActivity(context, intent);
        Intent activityIntent;
        if (uriString != null) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        } else {
            activityIntent = new Intent(context, cls);
        }

        activityIntent.putExtras(intent.getExtras());
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        /*
         In order to remove dependency on android-support-library-v4
         The reason why we differentiate between versions instead of just using context.startActivity
         for all devices is because in API 11 the recommended conventions for app navigation using
         the back key changed.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilderHelper.startActivities(context, cls, activityIntent);
        } else {
            context.startActivity(activityIntent);
        }
    }
}
