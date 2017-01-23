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

package com.parse4cn1.nativeinterface; // TODO: Remember to update if changed!
                                       // location must also match i.e. /native/android/com/parse4cn1/nativeinterface/
                                       // is the corresponding location for this package name.

import android.app.Application;
import android.os.Bundle;
import com.codename1.impl.android.LifecycleListener;
import com.codename1.impl.android.AndroidNativeUtil;
import com.parse.Parse; // com.parse.* includes are already available from the Parse SDK already integrated in parse4cn1.cn1lib
import com.parse.ParseInstallation;
import com.parse.SaveCallback;
import com.parse4cn1.ParsePush;

/**
 * An Android application class that enables correct initialization of the Parse
 * Android native SDK. It also tracks application state.
 */
public class CN1AndroidApplication extends Application {
    
    /*
      An enumeration of relevant application states.
    */
    public enum EAppState {
        STATE_FOREGROUND,
        STATE_BACKGROUND,
        STATE_NOT_RUNNING,
        STATE_UNKNOWN
    }
    
    private static LifecycleListener lifecycleListener = null;
    private static EAppState appState = EAppState.STATE_UNKNOWN;
    
    public static boolean isAppInForeground() {
        return appState == EAppState.STATE_FOREGROUND;
    }
    
    public static boolean isAppInBackground() {
        return appState == EAppState.STATE_BACKGROUND;
    }
    
    public static boolean isAppRunning() {
        return isAppInForeground() || isAppInBackground();
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        // Parse native SDK should be initialized in the application class 
        // and not in the activity otherwise push notifications will only be received 
        // when the application is running (which is generally undesirable).
        //
        // See: http://stackoverflow.com/questions/26637730/where-to-place-the-parse-initialize
        // and https://parse.com/questions/cannot-send-push-to-android-after-app-is-closed-until-screen-unlock
        Parse.initialize(new Parse.Configuration.Builder(this)
            .applicationId("OiTzm1ivZovdmMktQnqk8ajqBVIPgl4dlgUxw4dh")
            .clientKey("fHquv9DA0SA5pd7VPO38tNzOrzrgTgfd7yY3nXbo")
            .server("https://parseapi.back4app.com")
            .build()
        );
        
        // Creates a unique installation representing the given device
        // and persists it to the Parse backend. 
        // Without an installation, a device cannot be targeted for push notifications.
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {

               @Override
               public void done(com.parse.ParseException error) {
                    if (error == null) {
                        ParsePush.handlePushRegistrationStatus(null, 0); // success
                    }  else {
                        ParsePush.handlePushRegistrationStatus(error.getMessage(), 3); // saving installation related failure
                    }
                }
            }
        );
        
        // Tracks the application state so that pushes can be handled according to the 
        // state.
        initializeLifecycleListener();
        AndroidNativeUtil.addLifecycleListener(lifecycleListener);
    }
    
    @Override
    public void onTerminate() {
        AndroidNativeUtil.removeLifecycleListener(lifecycleListener);
        super.onTerminate();
    }
             
    private static void initializeLifecycleListener() {
        // Note: CN1 uses a single activity model so the state of this 
        // activity effectively represents the state of the application.
        
        lifecycleListener = new LifecycleListener() {
            
            @Override
            public void onCreate(Bundle savedInstanceState) {
                // Not interesting for now
            }
            
            @Override
            public void onResume() {
                appState = EAppState.STATE_FOREGROUND;
            }
            
            @Override
            public void onPause() {
                appState = EAppState.STATE_BACKGROUND;
            }
            
            @Override
            public void onDestroy() {
                appState = EAppState.STATE_NOT_RUNNING;
            }
            
            @Override
            public void onSaveInstanceState(Bundle b) {
                // Not interesting for now
            }
            
            @Override
            public void onLowMemory() {
                // Not interesting for now
            }
        };
    }   
}
