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

package com.parse4cn1.test.app;


import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Log;
import com.codename1.io.Preferences;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParsePush;
import com.parse4cn1.util.Logger;
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
        Logger.getInstance().setLogLevel(Log.DEBUG);
    }

    public void start() {
        if(current != null){
            current.show();
        } else {
            new StateMachine("/theme"); 
        }
        // Handle any pending push messages...
        // This is a good place because this method is called each time the
        // app comes to the foreground.
        checkForPushMessages();
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() {
        ParsePush.setPushCallback(null);
    }
    
    private void checkForPushMessages() {
        final String pushReceivedInBackgroundError = 
                Preferences.get(StateMachine.KEY_APP_IN_BACKGROUND_PUSH_ERROR, null);
        
        if (pushReceivedInBackgroundError != null) {
            Logger.getInstance().error(
                    "Apparently an error occurred while processing a push message "
                            + "received while the app was in background:\n\n" 
                            + pushReceivedInBackgroundError);
            
            Display.getInstance().callSerially(new Runnable() {

                public void run() {
                    Dialog.show("Error",
                            "Apparently an error occurred while processing a push message "
                            + "received while the app was in background:\n\n"
                            + pushReceivedInBackgroundError,
                            Dialog.TYPE_ERROR, null, "OK", null);
                    Preferences.set(StateMachine.KEY_APP_IN_BACKGROUND_PUSH_ERROR, null);
                }
            });
        } else {
            final String pushReceivedInBackground = 
                    Preferences.get(StateMachine.KEY_APP_IN_BACKGROUND_PUSH_PAYLOAD, null);

            if (pushReceivedInBackground != null) {
                Logger.getInstance().info("The following push messages were "
                        + "received while the app was in background:\n\n"
                                + pushReceivedInBackground);
                Display.getInstance().callSerially(new Runnable() {

                    public void run() {
                        Dialog.show("Push received (background)",
                                "The following push messages were received while the app was in background:\n\n"
                                + pushReceivedInBackground, "OK", null);
                        Preferences.set(StateMachine.KEY_APP_IN_BACKGROUND_PUSH_PAYLOAD, null);
                    }
                });
            }
        }

        if (ParsePush.isAppOpenedViaPushNotification()) {
            Logger.getInstance().info("App opened via push notification");
            try {
                final JSONObject pushPayload = ParsePush.getPushDataUsedToOpenApp();
                Display.getInstance().callSerially(new Runnable() {

                    public void run() {
                        Dialog.show("App opened via push",
                                "The app was opened via clicking a push notification with payload:\n\n"
                                + pushPayload.toString(), "OK", null);
                        ParsePush.resetPushDataUsedToOpenApp();
                    }
                });

            } catch (final ParseException ex) {
                Display.getInstance().callSerially(new Runnable() {

                    public void run() {
                        Dialog.show("Error", "An error occured while trying to retrieve "
                                + "push payload used to open app.\n\n"
                                + "Error: " + ex.getMessage(),
                                Dialog.TYPE_ERROR, null, "OK", null);
                    }
                });
            }
        } else {
            Logger.getInstance().info("App opened normally");
        }
        
        if (ParsePush.isUnprocessedPushDataAvailable()) {
            Logger.getInstance().info("Unprocessed push data available");
            try {
                final JSONArray pushPayload = ParsePush.getUnprocessedPushData();
                
                Display.getInstance().callSerially(new Runnable() {

                    public void run() {
                        Dialog.show("Unprocessed push data",
                                "The following unprocessed push message(s) were "
                                + "possibly received while the app was not running:\n\n"
                                + pushPayload.toString(), "OK", null);
                        ParsePush.resetUnprocessedPushData();
                    }
                });
                
            } catch (final ParseException ex) {
                
                Display.getInstance().callSerially(new Runnable() {

                    public void run() {
                        Dialog.show("Error", "An error occured while trying to retrieve "
                                + "unprocessed push payload.\n\n"
                                + "Error: " + ex.getMessage(),
                                Dialog.TYPE_ERROR, null, "OK", null);
                    }
                });
            }
        } else {
            Logger.getInstance().info("No unprocessed push data found");
        }
    }
}
