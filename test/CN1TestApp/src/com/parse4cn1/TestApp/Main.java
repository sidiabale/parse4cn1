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

package com.parse4cn1.TestApp;


import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Preferences;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.parse4cn1.ParsePush;
import userclasses.StateMachine;

public class Main {
   
    private Form current;

    public void init(Object context) {
        // Pro users - uncomment this code to get crash reports sent to you automatically
        /*Display.getInstance().addEdtErrorHandler(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                evt.consume();
                Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
                Log.p("OS " + Display.getInstance().getPlatformName());
                Log.p("Error " + evt.getSource());
                Log.p("Current Form " + Display.getInstance().getCurrent().getName());
                Log.e((Throwable)evt.getSource());
                Log.sendLog();
            }
        });*/
    }

    public void start() {
        
        final String pushReceivedInBackground = 
                Preferences.get(StateMachine.KEY_APP_IN_BACKGROUND_PUSH_PAYLOAD, null);
        
        if (pushReceivedInBackground != null) {
            Dialog.show("Push received; app in background", 
                    "The following push messages were received while the app was in background:\n\n"
                            + pushReceivedInBackground, "OK", null);
            Preferences.set(StateMachine.KEY_APP_IN_BACKGROUND_PUSH_PAYLOAD, null);
        }
        
        if (ParsePush.isAppOpenedViaPushNotification()) {
            final JSONObject pushPayload = ParsePush.getPushDataUsedToOpenApp();
            Dialog.show("App opened via push", 
                    "The app was opened via clicking a push notification with payload:\n\n"
                            + pushPayload.toString(), "OK", null);
            ParsePush.resetPushDataUsedToOpenApp();
        }
        
        if (ParsePush.isPushReceivedWhileAppNotRunning()) {
            final JSONArray pushPayload = ParsePush.getAppNotRunningPushData();
            Dialog.show("Push received while app not running", 
                    "The following push messages were received while the app was not running:\n\n"
                            + pushPayload.toString(), "OK", null);
            ParsePush.resetAppNotRunningPushData();
        }
        
        if(current != null){
            current.show();
            return;
        }
        new StateMachine("/theme"); 
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
    }
}
