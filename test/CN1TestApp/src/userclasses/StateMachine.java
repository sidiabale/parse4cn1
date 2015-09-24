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

package userclasses;

import com.codename1.components.SpanLabel;
import generated.StateMachineBase;
import com.codename1.ui.*; 
import com.codename1.ui.util.Resources;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseInstallation;
import com.parse4cn1.ParseUser;

/**
 *
 * @author Your name here
 */
public class StateMachine extends StateMachineBase {
    public StateMachine(String resFile) {
        super(resFile);
        // do not modify, write code in initVars and initialize class members there,
        // the constructor might be invoked too late due to race conditions that might occur
    }
    
    /**
     * this method should be used to initialize variables instead of
     * the constructor/class scope to avoid race conditions
     */
    protected void initVars(Resources res) {
        Parse.initialize("j1KMuH9otZlHcPncU9dZ1JFH7cXL8K5XUiQQ9ot8", 
                "V6ZUyBtfERtzbq6vjeAb13tiFYij980HN9nQTWGB");
    }
    
    @Override
    protected void postMain(Form f) {
        SpanLabel installationLabel = findLabelInstallation(f);
        
        if (installationLabel != null) {
            boolean failed = true;
            String installationIdText = "Unspecified error";
  
            try {
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();

                if (installation != null) {
                    installationIdText = installation.getInstallationId();
                    failed = false;
                } else {
                    installationIdText = "Could not retrieve current installation!";
                }
            } catch (ParseException ex) {
                installationIdText = "An exception occurred: " + ex.getMessage();
            }
            
            installationLabel.setText(installationIdText);
            if (failed) {
                installationLabel.setTextUIID("WarningLabel");
            }
        }
    }
}
