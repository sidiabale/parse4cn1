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

package com.parse4cn1;

import ca.weblite.codename1.json.JSONArray;
import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.util.ParseDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author sidiabale
 */
public class ParseUserTest extends BaseParseTest {
   
    private List<ParseUser> usersToDelete;
    /*
    TODO: Interesting test cases
    1. Sign up
       - Happy flow (OK)
       - Save before signup
       - Email verification (setting of field)
    2. Logging in (OK)
    3. Password reset
       - User without email (should fail)
       - User with email 
    4. Retrieving users
       - By ID
       - By sessionToken
       - By user root endpoint (all)
    5. Updating users
       - Check uniqueness of username and email (nothing is mentioned in API doc
         about changing though I expect that to be possible
       - Check changing username and/or email
       - Check adding new fields
    5. Deleting users
    6. Linking users
       - Facebook
       - Twitter
    7.
    */
    
    @Override
    public boolean runTest() throws Exception {
        usersToDelete = new ArrayList<ParseUser>();
        
        testRestApiExample();
        testDeleteUser();
        
        return true;
    }
    
    private void testRestApiExample() throws ParseException {
        // Create and signUp
        final String username = "user_" + getCurrentTimeInHex();
        final String password = "p_n7!-e8";
        ParseUser user = ParseUser.create(username, password);
        
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
        assertNull(user.getSessionToken());
        assertNull(user.getObjectId());
        
        user.put("phone", "415-392-0202");
        user.signUp();
        usersToDelete.add(user); // Ensure deletion even if other assertions fail.
        
        assertNotNull(user.getObjectId(), 
                 "Object id should be set upon successful signup");
        assertNotNull(user.getCreatedAt(), 
                "Creation time should be set upon successful signup");
        assertEqual(user.getCreatedAt(), user.getUpdatedAt(), 
                "Update time is set to created time by Parse4CN1");
        assertNotNull(user.getSessionToken(), 
                "Session token should be created upon successful signup");
        
        ParseUser loggedIn = ParseUser.create(username, password);
        loggedIn.login();
        
        assertNotNull(user.getSessionToken(),
                "Session token is created upon successful login");
        assertEqual(user.getObjectId(), loggedIn.getObjectId(),
                "Object id is preserved after sign up");
        assertEqual(user.getString("phone"), loggedIn.getString("phone"),
                "User data is preserved");
        
        loggedIn.logout();
        assertNull(loggedIn.getSessionToken(), "Session token is invalidated on logout");
        loggedIn.login();
        assertNotNull(user.getSessionToken(), 
            "Session token should be created upon successful login");
    }
    
    private void testDeleteUser() throws ParseException {
        for (ParseUser user: usersToDelete) {
            user.delete();
        }
    }
    
    
    
//    private void checkData(final ParseObject obj, HashMap<String, Object> data) { 
//        for (Entry<String, Object> entry : data.entrySet()) {
//            if (entry.getValue() instanceof JSONArray) {
//                assertEqual(ParseDecoder.convertJSONArrayToList((JSONArray) entry.getValue()), 
//                        obj.getList(entry.getKey()));
//            } else if (entry.getValue() instanceof JSONObject) {
//                assertEqual(ParseDecoder.convertJSONObjectToMap((JSONObject) entry.getValue()), 
//                        obj.get(entry.getKey()));
//            } else {
//                assertEqual(entry.getValue(), obj.get(entry.getKey()));
//            }
//        }
//    }
//    
//    private void addData(ParseObject obj, HashMap<String, Object> dataToAdd) {
//        for (Entry<String, Object> entry : dataToAdd.entrySet()) {
//            obj.put(entry.getKey(), entry.getValue());
//        }
//    }  
    
}
