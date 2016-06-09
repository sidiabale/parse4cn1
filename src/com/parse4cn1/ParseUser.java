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
 *
 * Original implementation adapted from Thiago Locatelli's Parse4J project
 * (see https://github.com/thiagolocatelli/parse4j)
 */

package com.parse4cn1;

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Util;
import com.parse4cn1.command.ParseCommand;
import com.parse4cn1.command.ParseDeleteCommand;
import com.parse4cn1.command.ParseGetCommand;
import com.parse4cn1.command.ParsePostCommand;
import com.parse4cn1.command.ParseResponse;
import com.parse4cn1.util.Logger;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The ParseUser is a local representation of user data that can be saved and 
 * retrieved from the Parse cloud.
 */
public class ParseUser extends ParseObject {

    private static final Logger LOGGER = Logger.getInstance();
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_EMAIL_VERIFIED = "emailVerified";
    private static final String OBJECT_ID_CURRENT = "me";
    private static final String ENDPOINT_LOGIN = "login";
    private static final String ENDPOINT_LOGOUT = "logout";
    private static final String ENDPOINT_PASSWORD_RESET = "requestPasswordReset";

    private String password;
    private String sessionToken;

    private static ParseUser current;
    
    public static ParseUser getCurrent() {
        return current;
    }
    
    protected ParseUser() {
        super(ParseConstants.CLASS_NAME_USER);
    }

    @Override
    public void remove(String key) {
        if (KEY_USERNAME.equals(key)) {
            LOGGER.error("Can't remove the username key.");
            throw new IllegalArgumentException("Can't remove the username key.");
        }

        super.remove(key);
    }

    @Override
    public void delete() throws ParseException {
        if (!isAuthenticated()) {
            LOGGER.error("Cannot delete a ParseUser that is not authenticated.");
            throw new ParseException(ParseException.SESSION_MISSING,
                    "Cannot delete a ParseUser that is not authenticated.");
        }
        
        ParseCommand command = new ParseDeleteCommand(getEndPoint(), getObjectId());
        command.addHeader(ParseConstants.HEADER_SESSION_TOKEN, getSessionToken());
        ParseResponse response = command.perform();
        if (response.isFailed()) {
            throw response.getException();
        }
        
        setSessionToken(null);
        current = null;
        
        reset();
    }

    /**
     * Sets the username. Usernames cannot be null or blank.
     * @param username The username to be set.
     */
    public void setUsername(String username) {
        if (username == null || username.trim().length() == 0) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        put(KEY_USERNAME, username);
    }

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

    public void setPassword(String password) {
        this.password = password;
        dirty = true;
    }

    public void setEmail(String email) {
        put(KEY_EMAIL, email);
    }

    public String getEmail() {
        return getString(KEY_EMAIL);
    }
    
    /**
     * Retrieves the email verification status of this user.
     * @return null if the field is not present or otherwise a boolean value representing
     * the verification status.
     */
    public Boolean getEmailVerified() {
        return getBoolean(KEY_EMAIL_VERIFIED);
    }

    /**
     * Retrieves the session token associated with this user. Only logged in 
     * users have a valid session.
     * 
     * @return The session token associated with this user.
     */
    public String getSessionToken() {
        return sessionToken;
    }

    public static ParseUser create(String username, String password) throws ParseException {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
    
    /**
     * Retrieves the user associated with {@code sessionToken}.
     * 
     * @param sessionToken A session token associated with a ParseUser.
     * @return The ParseUsser associated with {@code sessionToken}.
     * @throws ParseException if anything goes wrong.
     */
    public static ParseUser fetchBySession(final String sessionToken) throws ParseException {
        ParseUser user = null;
        ParseCommand command = 
            new ParseGetCommand(ParseConstants.ENDPOINT_USERS, OBJECT_ID_CURRENT);
        command.addHeader(ParseConstants.HEADER_SESSION_TOKEN, sessionToken);
    
        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                LOGGER.error("Empty response.");
                throw response.getException();
            }
            
            user = new ParseUser();
            user.setData(jsonResponse);
        } else {
            LOGGER.error("Request failed.");
            throw response.getException();
        }
        current = user;
        return user;
    }

    /**
     * Requests a password reset email to be sent to the specified email address 
     * associated with the user account. This email allows the user to securely 
     * reset their password on the Parse site.
     * 
     * @param email The email address associated with the user that forgot their password.
     * @throws ParseException if anything goes wrong.
     */
    public static void requestPasswordReset(String email) throws ParseException {

        try {
            ParsePostCommand command = new ParsePostCommand(ENDPOINT_PASSWORD_RESET);
            JSONObject data = new JSONObject();
            data.put(KEY_EMAIL, email);
            command.setMessageBody(data);
            ParseResponse response = command.perform();
            if (!response.isFailed()) {
                JSONObject jsonResponse = response.getJsonObject();
                if (jsonResponse == null) {
                    LOGGER.error("Empty response.");
                    throw response.getException();
                }
            } else {
                LOGGER.error("Request failed.");
                throw response.getException();
            }
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, 
                    "An error occurred while trying to initiate a password reset.", ex);
        }
    }

    /**
     * Checks whether the ParseUser has been authenticated on this device. 
     * This will be true if the ParseUser was obtained via a logIn or signUp method. 
     * Only an authenticated ParseUser can be saved (with altered attributes) and deleted.
     * 
     * @return {@code true} if this user is authenticated.
     */
    public boolean isAuthenticated() {
        return (getSessionToken() != null && getObjectId() != null);
    }
    
    /**
     * Signs up a new user. You should call this instead of {@link ParseObject#save()} 
     * for new ParseUsers.
     * <p>
     * A username and password must be set before calling signUp.
     * 
     * @throws ParseException if anything goes wrong
     */
    public void signUp() throws ParseException {

        if ((getUsername() == null) || (getUsername().length() == 0)) {
            LOGGER.error("Username cannot be missing or blank");
            throw new IllegalArgumentException(
                    "Username cannot be missing or blank");
        }

        if (password == null || (password.length() == 0)) {
            LOGGER.error("Password cannot be missing or blank");
            throw new IllegalArgumentException(
                    "Password cannot be missing or blank");
        }

        if (getObjectId() != null) {
            LOGGER.error("Cannot sign up a user that has already signed up.");
            throw new IllegalArgumentException(
                    "Cannot sign up a user that has already signed up.");
        }

        ParsePostCommand command = new ParsePostCommand(getEndPoint());
        try {
            JSONObject parseData = getParseData();
            parseData.put(KEY_PASSWORD, password);
            command.setMessageBody(parseData);
            ParseResponse response = command.perform();
            if (!response.isFailed()) {
                JSONObject jsonResponse = response.getJsonObject();
                if (jsonResponse == null) {
                    LOGGER.error("Empty response");
                    throw response.getException();
                }

                setObjectId(jsonResponse.getString(ParseConstants.FIELD_OBJECT_ID));
                setSessionToken(jsonResponse.getString(ParseConstants.FIELD_SESSION_TOKEN));
                current = this;
                String createdAt = jsonResponse.getString(ParseConstants.FIELD_CREATED_AT);
                setCreatedAt(Parse.parseDate(createdAt));
                setUpdatedAt(Parse.parseDate(createdAt));
                setData(new JSONObject()); // Resest dirty flag, etc.

            } else {
                LOGGER.error("Request failed.");
                throw response.getException();
            }
        } catch (JSONException e) {
            LOGGER.error(ParseException.ERR_INVALID_RESPONSE + " Error: " + e);
            throw new ParseException(
                    ParseException.INVALID_JSON,
                    ParseException.ERR_INVALID_RESPONSE,
                    e);
        }
    }

    /**
     * Logs the current user in. If successful, a session is created for this 
     * user which can be retrieved via {@link #getSessionToken()}.
     * 
     * @throws ParseException if anything goes wrong.
     */
    public void login() throws ParseException {

        ParseGetCommand command = new ParseGetCommand(ENDPOINT_LOGIN);
        command.addArgument(KEY_USERNAME, getUsername());
        command.addArgument(KEY_PASSWORD, password);
        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                LOGGER.error("Empty response.");
                throw response.getException();
            }
            setData(jsonResponse);
            current = this;
        } else {
            LOGGER.error("Request failed.");
            throw response.getException();
        }
    }

    /**
     * Logs this user out if they were previously logged in, for example, 
     * via a {@link #signUp()} or {@link #login()} call.
     * 
     * @throws ParseException if anything goes wrong.
     */
    public void logout() throws ParseException {

        if (isAuthenticated()) {
            ParseCommand command = new ParsePostCommand(ENDPOINT_LOGOUT);
            command.addHeader(ParseConstants.HEADER_SESSION_TOKEN, getSessionToken());
            ParseResponse response = command.perform();
            if (response.isFailed()) {
                throw response.getException();
            }
            setSessionToken(null);
            current = null;
        }
    }
    
    /**
     * Serializes ParseUser-specific data in addition to regular ParseObject data.
     *
     * @param out The data stream to serialize to.
     * @throws IOException if any IO error occurs
     * @throws ParseException if the object is {@link #isDirty() dirty}
     * @see ParseObject#externalize(java.io.DataOutputStream) 
     */
    @Override
    public void externalize(DataOutputStream out) throws IOException, ParseException {
        super.externalize(out);
        
        Util.writeUTF(sessionToken, out);
        Util.writeUTF(password, out);
    }

    /**
     * Deserializes ParseUser-specific data in addition to regular ParseObject data.
     * 
     * @param version The version of the previously serialized object (defaults to {@link ParseConstants#API_VERSION}).
     * @param in The data input stream to deserialize from.
     * @throws IOException if any IO error occurs
     * @see ParseObject#internalize(int, java.io.DataInputStream) 
     */
    @Override
    public void internalize(int version, DataInputStream in) throws IOException, ParseException {
        super.internalize(version, in);
        
        sessionToken = Util.readUTF(in);
        password = Util.readUTF(in);
    }

    @Override
    public void setData(JSONObject jsonObject) {
        if (jsonObject.has(ParseConstants.FIELD_SESSION_TOKEN)) {
            setSessionToken(jsonObject.optString(ParseConstants.FIELD_SESSION_TOKEN));
            jsonObject.remove(ParseConstants.FIELD_SESSION_TOKEN);
        }
        super.setData(jsonObject);
    }

    @Override
    protected void validateSave() throws ParseException {

        if (getObjectId() == null) {
            LOGGER.error("Cannot save a ParseUser that is not yet signed up.");
            throw new ParseException(ParseException.MISSING_OBJECT_ID,
                    "Cannot save a ParseUser that is not yet signed up.");
        }

        if ((!isAuthenticated()) && dirty && getObjectId() != null) {
            LOGGER.error("Cannot save a ParseUser that is not authenticated.");
            throw new ParseException(ParseException.SESSION_MISSING,
                    "Cannot save a ParseUser that is not authenticated.");
        }
    }

    @Override
    protected void performSave(final ParseCommand command) throws ParseException {
        command.addHeader(ParseConstants.HEADER_SESSION_TOKEN, getSessionToken());
        super.performSave(command);
    }
    
    protected void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
