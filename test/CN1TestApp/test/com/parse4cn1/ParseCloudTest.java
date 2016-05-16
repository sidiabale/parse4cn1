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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sidiabale
 */
public class ParseCloudTest extends BaseParseTest {

    final String classGameScore = "GameScore";

    @Override
    public boolean runTest() throws Exception {
        testRestApiExample();
        return true;
    }
    
    @Override
    protected void resetClassData() {
        System.out.println("============== resetClassData()");
        deleteAllUsers();
    }

    private void testRestApiExample() throws ParseException {
        System.out.println("============== testRestApiExample()");
        
        // Cloud function
        final String helloWorld = ParseCloud.callFunction("hello", null);
        assertEqual("Hello world!", helloWorld);
        
        // [16-05-16] Background jobs are not supported by parse server
        // https://github.com/ParsePlatform/parse-server/wiki/Compatibility-with-Hosted-Parse#jobs
        
//        // Cloud job via function wrapper
//        for (int i = 0; i < 5; ++i) {
//            ParseUser user = ParseUser.create("User" + (i + 1) + "_" + getCurrentTimeInHex(), 
//                    BaseParseTest.TEST_PASSWORD);
//            user.signUp();
//        }
//        
//        final Map<String, String> params = new HashMap<String, String>();
//        params.put("plan", "Paid");
//        final String jobTriggerResult = ParseCloud.callFunction("userMigrationJobWrapper", params);
//        assertEqual("{}", jobTriggerResult.trim(), "On successful trigger, result should be empty");
//        
//        waitFor(5000); // Wait a bit to ensure that job has been completed
//        
//        // Check that job did what was expected
//        ParseQuery<ParseUser> query = ParseQuery.getQuery(ParseConstants.CLASS_NAME_USER);
//        List<ParseUser> results = query.find();
//        for (ParseUser user : results) {
//            assertEqual("Paid", user.getString("plan"));
//        }
    } 
}
