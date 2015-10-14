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

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.components.SpanLabel;
import com.codename1.io.Preferences;
import generated.StateMachineBase;
import com.codename1.ui.*; 
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseInstallation;
import com.parse4cn1.ParsePush;
import com.parse4cn1.ParsePush.IPushCallback;
import com.parse4cn1.util.Logger;
import java.util.Hashtable;

/**
 *
 * @author Your name here
 */
public class StateMachine extends StateMachineBase implements IPushCallback {
    
    public final static String KEY_APP_IN_BACKGROUND_PUSH_PAYLOAD = "parse4cn1TestApp_AppInBackgroundPush";
    public final static String KEY_APP_IN_BACKGROUND_PUSH_ERROR = "parse4cn1TestApp_AppInBackgroundPushError";
    private boolean handleForegroundPush;
    private boolean handleBackgroundPush;
    
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
        getInstallationId(f);
    }
    
    public static void checkForPushMessages() {
        // Handle any pending push messages...
        // This is a good place because this method is called each time the
        // app comes to the foreground.
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
    
    @Override
    protected void onMain_RetryInstallationAction(Component c, ActionEvent event) {
        getInstallationId(Display.getInstance().getCurrent());
    }

    private void getInstallationId(Form f) {
        
        SpanLabel installationLabel = findLabelInstallation(f);
        
        if (installationLabel != null) {
            boolean failed = true;
            String installationIdText = null;
  
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
                if (installationIdText == null) {
                    installationIdText = "Failed to retrieve installation. ";
                } else {
                    installationIdText += "\nFailed to subscribe to test channel. ";
                }
                installationIdText += "Error: " + ex.getMessage();
                int code = ex.getCode();
                installationIdText += " Error code = " + code + ((code < 0) ? " (local)" : " (from Parse)");
            }

            installationLabel.setText(installationIdText + "\n"); // crude layouting
            if (failed) {
                installationLabel.setTextUIID("WarningLabel");
            } else {
                installationLabel.setTextUIID("Label");
                Button button = findRetryInstallation(f);
                if (button != null) {
                    findContainer4(f).removeComponent(button);
                }
            }
            
            initPushButton(f, !failed, 
                    (failed ? "Sending push is disabled since installation id could not be retrieved" : ""));
        }
    }
    
    @Override
    protected void beforeMain(Form f) {
        if (Parse.getPlatform() != Parse.EPlatform.WINDOWS_PHONE) {
//            // Background push (i.e., intercepting an incoming push message when app is in 
//            // background and possibly preventing it from being added to the notification bar)
//            // is not supported on windows phone
//            findContainer9(f).removeComponent(findCheckBoxHandleBackgroundPush(f));
//        } else {
            findContainer4(f).removeComponent(findRetryInstallation(f));
        }
        
        final SpanLabel pushNotes = findSpanLabelPushNotes(f);
        pushNotes.setText("Note:"
                + "\n- Messages will be delivered to ALL subscribers of the \"test\" channel (which includes this device)."
                + "\n- (Part of) the installation ID of the sender will automatically be included in the push message so that you can distinguish your messages :)"
                + "\n- The Parse backend for this app is a free Parse App so free quota limits apply. Use sparingly or the limit might be hit and your messages will fail."
                + "\n- To aid debugging, you can use the 'Show app logging' in the 'Demo' tab (though it may fail on some platforms e.g. simulator due to dependency on the filesystem API).");
        handleForegroundPush = findCheckBoxHandleForegroundPush(f).isSelected();
        handleBackgroundPush = findCheckBoxHandleBackgroundPush(f).isSelected();
        
            
        // A trick copied from the Kitchen Sink demo which is particularly
        // useful on Windows phone where the title is shown in small font by default
        // It's sufficient to do this only on the first form as it changes the theme
        // which in turn propagates to the other forms
        setTitleFont(f);
    }

    private void setTitleFont(Form parent) {
        Font largeFont = Font.createSystemFont(Font.FACE_SYSTEM, Font.SIZE_LARGE, Font.STYLE_BOLD);
        Hashtable themeAddition = new Hashtable();
        themeAddition.put("Title.font", largeFont);

        UIManager.getInstance().addThemeProps(themeAddition);
        final Form c = Display.getInstance().getCurrent();
        if (c != null) {
            c.refreshTheme();
        }
        
        parent.refreshTheme();
        parent.revalidate();
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
    
    
    @Override
    protected void onMain_CheckBoxHandleForegroundPushAction(Component c, ActionEvent event) {
        handleForegroundPush = ((CheckBox) c).isSelected();
    }

    @Override
    protected void onMain_CheckBoxHandleBackgroundPushAction(Component c, ActionEvent event) {
        if (Parse.getPlatform() != Parse.EPlatform.ANDROID) {
            Dialog.show("Info",
                    "Handling background messages (i.e. intercepting them and preventing them from "
                            + "showing up in the 'notification bar' "
                            + "only works on Android). For iOS consider hidden messages "
                            + "using the 'content-available' field",
                    "OK",
                    null);
            ((CheckBox) c).setSelected(false);
            return;
        }
        
        handleBackgroundPush = ((CheckBox) c).isSelected();
       
        if (!handleBackgroundPush) {
            Dialog.show("Info",
                    "Disabling handling of background push means that any push messages "
                            + "received while the app is not in the foreground "
                            + "will automatically go to the notification bar.",
                    "OK",
                    null);
        }
    }

    @Override
    protected void onMain_ButtonAction(Component c, ActionEvent event) {
        try {
            Logger.getInstance().showLog();
        } catch (ParseException ex) {
            Dialog.show("Error", 
                    "Showing log failed:\n\n" + ex.getMessage(), 
                    Dialog.TYPE_ERROR, null, "OK", null);
        }
    }

    @Override
    protected void onMain_ButtonClearBadgeAction(Component c, ActionEvent event) {
        if (Parse.getPlatform() != Parse.EPlatform.IOS) {
            Dialog.show("Info", "Badging is only supported on iOS", "OK", null);
        } else {
            try {
                ParseInstallation.getCurrentInstallation().setBadge(0);
            } catch (ParseException ex) {
                Dialog.show("Error", 
                    "Clearing badge failed:\n\n" + ex.getMessage(), 
                    Dialog.TYPE_ERROR, null, "OK", null);
            }
        }
    
    }

    @Override
    public boolean onPushReceivedForeground(final JSONObject pushPayload) {
        if (handleForegroundPush) {
            Display.getInstance().callSerially(new Runnable() {

                public void run() {
                    Dialog.show("Push received (foreground)",
                            (pushPayload == null ? "<Null payload>" : pushPayload.toString()),
                            "OK",
                            null);
                }

            });
        } else {
            Display.getInstance().callSerially(new Runnable() {

                public void run() {
                    Dialog.show("Ignoring push (foreground)",
                            "Ignoring push received while app in foreground. Will be handled as native implementation deems fit "
                                    + "(e.g. status bar notification on Android or message box on Windows Phone)",
                            "OK",
                            null);
                }

            });
        }
        return handleForegroundPush;
    }

    @Override
    public boolean onPushReceivedBackground(JSONObject pushPayload) {
        if (handleBackgroundPush) {
            try {
                final String jsonStr = Preferences.get(KEY_APP_IN_BACKGROUND_PUSH_PAYLOAD, null);
                JSONArray existing;

                if (jsonStr == null) {
                    existing = new JSONArray();
                } else {
                    existing = new JSONArray(jsonStr);
                }
                existing.put(pushPayload);
                
                Preferences.set(KEY_APP_IN_BACKGROUND_PUSH_ERROR, null);
                Preferences.set(KEY_APP_IN_BACKGROUND_PUSH_PAYLOAD, existing.toString());
            } catch (JSONException ex) {
                Preferences.set(KEY_APP_IN_BACKGROUND_PUSH_ERROR, 
                        "An error occurred while trying to parse "
                        + "and cache push message '" + pushPayload 
                        + "' received in background. Error: " + ex.getMessage());
            }
        }
        return handleBackgroundPush;
    }
    
    @Override
    public void onPushOpened(final JSONObject pushPayload) {
        Display.getInstance().callSerially(new Runnable() {

            public void run() {
                Dialog.show("Push opened (foreground)",
                        "Push notification opened while app is in foreground. Payload:\n\n"
                        + pushPayload.toString(), "OK", null);
            }

        });
    }
}
