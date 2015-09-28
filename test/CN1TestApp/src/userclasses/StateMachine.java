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

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.components.SpanLabel;
import generated.StateMachineBase;
import com.codename1.ui.*; 
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.util.Resources;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseInstallation;
import com.parse4cn1.ParsePush;
import com.parse4cn1.ParsePush.IPushCallback;

/**
 *
 * @author Your name here
 */
public class StateMachine extends StateMachineBase implements IPushCallback {
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
        ParsePush.setPushCallback(this);
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
                    installation.subscribeToChannel("test");
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
            initPushButton(f, !failed, 
                    (failed ? "Sending push is disabled since installion id could not be retrieved" : ""));
        }
    }

    @Override
    protected void beforeMain(Form f) {
        final SpanLabel pushNotes = findSpanLabelPushNotes(f);
        pushNotes.setText("Note:"
                + "\n- Message will be delivered to all subscribers of the \"test\" channel (which includes this device)."
                + "\n- (Part of) the installation ID of the sender will automatically be included in the push message.");
    }

    @Override
    protected void onMain_ButtonSendPushAction(Component c, ActionEvent event) {
        final SpanLabel statusLabel = findSpanLabelPushStatus();
        
        final String pushMsg = findTextAreaPush().getText();
        if (pushMsg.length() == 0) {
            Dialog.show("No push text", "Please enter a message to be sent", "Ok", null);
            return;
        }
        
        boolean succeeded = false;
        try {
            statusLabel.setTextUIID("Label");
            statusLabel.setText("Preparing message for sending...");
            final String senderInfo = "[Triggered from " + Parse.getPlatform().name() 
                    + " device with installationId ending in '" + getInstallationIdPrefix() + "']";
            
            ParsePush parsePush = ParsePush.create();
            parsePush.setChannel("test");
            if (((CheckBox)findCheckBoxRawJson()).isSelected()) {
                final JSONObject data = new JSONObject(pushMsg);
                data.put("senderInfo", senderInfo);
                parsePush.setData(data);
            } else {
                parsePush.setMessage(pushMsg + " " + senderInfo);
            }
            
            statusLabel.setText("Sending push to 'test' channel subscribers...");
            parsePush.send();
            succeeded = true;
            statusLabel.setText("Push message successfully sent!");
        } catch (ParseException ex) {
            Dialog.show("Error", "An error occurred while preparing the push message. Error:\n" + ex.getMessage(), 
                    Dialog.TYPE_ERROR, null, "Ok", null);
            statusLabel.setText("An error occurred: " + ex.getMessage());
        } catch (JSONException ex) {
             Dialog.show("Error", "The push message contains invalid JSON. Error:\n" + ex.getMessage(), 
                    Dialog.TYPE_ERROR, null, "Ok", null);
             statusLabel.setText("An error occurred: " + ex.getMessage());
        } catch (Exception ex) {
            statusLabel.setText("An unexpected error occurred: " + ex.getMessage());
        } 
        finally {
            statusLabel.setTextUIID(succeeded ? "Label" : "WarningLabel");
        }
    }
    
    private String getInstallationIdPrefix() throws ParseException {
        String prefix = ParseInstallation.getCurrentInstallation().getInstallationId();
        prefix = prefix.substring(prefix.lastIndexOf('-'));
        return prefix;
    }
    
    private void initPushButton(final Form f, boolean enable, final String statusMsg) {
        final Button pushButton = findButtonSendPush(f);
        pushButton.setEnabled(enable);
        
        final SpanLabel pushStatus = findSpanLabelPushStatus(f);
        pushStatus.setText(statusMsg);
        pushStatus.setTextUIID(enable ? "Label" : "WarningLabel");
    }

    @Override
    protected void onMain_CheckBoxRawJsonAction(Component c, ActionEvent event) {
        CheckBox rawPushCheckbox = (CheckBox)c;
        if (rawPushCheckbox.isSelected()) {
            try {
                final String existing = findTextAreaPush().getText();
                JSONObject data = new JSONObject();
                data.put("alert", existing);
                findTextAreaPush().setText(data.toString());
            } catch (JSONException ex) {
                // Ignore error and initialize to default
                findTextAreaPush().setText("{\n"
                        + "  \"alert\":\"message\""
                        + "\n}");
            }
        } else {
            findTextAreaPush().setText("");
        }
    }

    public boolean onPushReceived(final JSONObject pushPayload) {
        Display.getInstance().callSerially(new Runnable() {

            public void run() {
                Dialog.show("Push received", 
                (pushPayload == null ? "<Null payload>" : pushPayload.toString()), 
                "OK", 
                null);
            }
            
        });
        return true;
    }
}
