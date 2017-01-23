package com.parse4cn1.nativeinterface;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.codename1.impl.android.AndroidNativeUtil;

public class ParseInstallationNativeImpl {
    public void initialize(String apiEndPoint, String applicationId, String clientKey) {
        String endPoint = apiEndPoint;
        if (endPoint != null && !endPoint.endsWith("/")) {
            endPoint += "/"; // Note: Url needs to have a trailing slash 
        }
        
        Parse.initialize(new Parse.Configuration.Builder(AndroidNativeUtil.getActivity())
            .applicationId(applicationId)
            .clientKey(clientKey)
            .server(endPoint)
            .build()
        );
    }

    public String getInstallationObjectId() {
        String objectId = null;
        
        // Save to make sure that the installation can (immediately) be retrieved from Parse by the caller using the installationId
        ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
        
        // Installation could be null
        // See: https://github.com/ParsePlatform/Parse-SDK-Android/blob/master/Parse/src/main/java/com/parse/ParseInstallation.java
        if (currentInstallation != null) {
            // Note: The first time this method is called, the installation is created on the fly
            // and needs to be saved. Other times, this save operation is redundant/without side effects
            currentInstallation.saveInBackground(); // Avoid blocking; caller will take this into account and retry if needed
            objectId = currentInstallation.getObjectId();
        }
        
        return objectId;
    }

    public boolean isSupported() {
        return true;
    }

}
