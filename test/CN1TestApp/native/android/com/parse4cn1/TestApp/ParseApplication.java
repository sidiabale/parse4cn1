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

import android.app.Application;
import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * An Android application class that enables correct initialization of the Parse
 * Android native SDK.
 */
public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Parse native SDK should be initialized in the application class and not in the activity
        // See: http://stackoverflow.com/questions/26637730/where-to-place-the-parse-initialize
        // and https://parse.com/questions/cannot-send-push-to-android-after-app-is-closed-until-screen-unlock
        Parse.initialize(this, 
                "j1KMuH9otZlHcPncU9dZ1JFH7cXL8K5XUiQQ9ot8", /* Application ID */
                "V6ZUyBtfERtzbq6vjeAb13tiFYij980HN9nQTWGB" /* Client Key */);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}
