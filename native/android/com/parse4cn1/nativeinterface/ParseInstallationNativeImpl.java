package com.parse4cn1.nativeinterface;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.codename1.impl.android.AndroidNativeUtil;

public class ParseInstallationNativeImpl {
    public void initialize(String applicationId, String clientKey, String parseUrl) {
        Parse.initialize(new Parse.Configuration.Builder(myContext)
            .applicationId(applicationId)
            .clientKey(clientKey)
            .server(parseUrl) // Note: Url needs to have a trailing slash 
            .build()
        );
    }

    public String getInstallationId() {
         String installationId = null;
        
        // Save to make sure that the installation can (immediately) be retrieved from Parse by the caller using the installationId
        ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
        
        // Installation could be null
        // See: https://github.com/ParsePlatform/Parse-SDK-Android/blob/master/Parse/src/main/java/com/parse/ParseInstallation.java
        if (currentInstallation != null) {
            currentInstallation.saveInBackground(); // Avoid blocking; caller will take this into account and retry if needed
            installationId = currentInstallation.getInstallationId();
        }
        
        return installationId;
    }

    public boolean isSupported() {
        return true;
    }

}
