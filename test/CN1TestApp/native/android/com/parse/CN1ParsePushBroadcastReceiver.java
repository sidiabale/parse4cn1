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

/**
 * A custom broadcast receiver for handling push messages received via Parse.
 */
public class CN1ParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {

//    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
////        String packageName = context.getPackageName();
////        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
////        if (launchIntent == null) {
////          return null;
////        }
////        String className = launchIntent.getComponent().getClassName();
////        Class<? extends Activity> cls = null;
////        try {
////          cls = (Class <? extends Activity>)Class.forName(className);
////        } catch (ClassNotFoundException e) {
////          // do nothing
////        }
////        return cls;
//        return MainStub.class;
////        return AndroidNativeUtil.getActivity().getClass();
//  }
//    protected void onPushOpen(Context context, Intent intent) {
//        Intent startMain = new Intent(context, getActivity(context, intent));
//        startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(startMain);
////                	this.setResult(Activity.RESULT_OK);
////                	intent.setData(null);
////                	this.finish();
//    }
    @Override
    protected void onPushReceive(Context context, Intent intent) {
        JSONObject pushData = null;
        try {
            pushData = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));
        } catch (JSONException e) {
            Logger.getInstance().error("Unexpected JSONException when receiving push data: " + e);
        }

        boolean handled = false;
        if (pushData != null && CN1AndroidApplication.isAppRunning()) {
            if (CN1AndroidApplication.isAppInForeground()) {
                handled = ParsePush.handlePushReceivedForeground(pushData.toString());
            } else if (CN1AndroidApplication.isAppInBackground()) {
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
                ParseNotificationManager.getInstance().showNotification(context, notification);
            } else {
                if (pushData != null && !CN1AndroidApplication.isAppRunning()) {
                    handled = ParsePush.handlePushReceivedNotRunning(pushData.toString());
                }
            }
        }
    }

    @Override
    protected void onPushOpen(Context context, Intent intent) {
        /*
         Adapted from ParsePushBroadcastReceiver. Main changes
         1. Remove analytics call to log app open; CN1 app should decide if and where to do that
         */
        String uriString = null;
        try {
            JSONObject pushData = new JSONObject(intent.getStringExtra(ParsePushBroadcastReceiver.KEY_PUSH_DATA));
            uriString = pushData.optString("uri", null);
        } catch (JSONException e) {
            Logger.getInstance().error("Unexpected JSONException when receiving push data: " + e);
        }

        Class<? extends Activity> cls = getActivity(context, intent);
        Intent activityIntent;
        if (uriString != null) {
            activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriString));
        } else {
            activityIntent = new Intent(context, cls);
        }

        activityIntent.putExtras(intent.getExtras());

        /*
         In order to remove dependency on android-support-library-v4
         The reason why we differentiate between versions instead of just using context.startActivity
         for all devices is because in API 11 the recommended conventions for app navigation using
         the back key changed.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilderHelper.startActivities(context, cls, activityIntent);
        } else {
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(activityIntent);
        }
    }

}
