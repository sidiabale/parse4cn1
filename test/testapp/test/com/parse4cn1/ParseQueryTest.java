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

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sidiabale
 */
public class ParseQueryTest extends BaseParseTest {

    private List<ParseObject> gameScoreObjects;
    private final String classGameScore = "GameScore";
    private final String classTeam = "Team";
    private final String fieldScore = "score";
    private final String fieldPlayerName = "playerName";
    private ParseQuery query;
    
    @Override
    public boolean runTest() throws Exception {
        testQueryFormat();
        testRestApi();

        return true;
    }

    @Override
    public void prepare() {
        super.prepare();
        gameScoreObjects = new ArrayList<ParseObject>();
    }

    @Override
    protected void resetClassData() {
        deleteAllUsers();
        deleteObjects(classGameScore);
        deleteObjects(classTeam);
    }

    private void testRestApi() throws ParseException {
        ParseObject object = ParseObject.create(classGameScore);
        object.put(fieldScore, 4);
        gameScoreObjects.add(object);
        
        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 5);
        gameScoreObjects.add(object);
        
        object = ParseObject.create(classGameScore);
        object.put(fieldPlayerName, "Jonathan Walsh");
        gameScoreObjects.add(object);
        
        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 9);
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 1000);
        object.put(fieldPlayerName, "Zabro Wunsch");
        gameScoreObjects.add(object);
        
        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 1000);
        object.put(fieldPlayerName, "Dario Wunsch");
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 2050);
        object.put(fieldPlayerName, "Shawn Simon");
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 3500);
        object.put(fieldPlayerName, "Mr. Winner");
        gameScoreObjects.add(object);

        saveObjects(gameScoreObjects);

        // TODO Uncomment all
        checkGreaterAndLessThanConstraints();
        checkInConstraint();
        checkNotInConstraints();
        checkExistsConstraints();
        checkNotExistsConstraints();
        checkMatchesOrDoesNotMatchKeyInQueryConstraints();
        checkSortConstraints();
    }

    private void testQueryFormat() throws ParseException, JSONException {
        final ParseGeoPoint geoPoint1 = new ParseGeoPoint(-10.0, 10.0);
        final ParseGeoPoint geoPoint2 = new ParseGeoPoint(18.0, 5.0);

        ParseQuery<ParseObject> subQuery = ParseQuery.create("players");
        subQuery.whereExists("games");

        ParseQuery<ParseObject> query = ParseQuery.create("games");
        String[] allowedNames = {"Jang Min Chul", "Sean Plott"};
        String[] disallowedNames = {"Don't Allow"};
        String[] keys = {"key1", "key2"};
        query.orderByAscending("age")
//                .orderByDescending("tournaments")
//                .addAscendingOrder("loosingScore")
//                .addDescendingOrder("score2")
//                .setLimit(10)
//                .setTrace(false)
//                .setCaseSensitive(false)
//                .setSkip(5)
                .whereGreaterThan("score1", -6)
                .whereGreaterThanOrEqualTo("wins", 10)
                .whereLessThan("losses", 2)
                .whereLessThanOrEqualTo("score2", 2)
                .whereContainedIn("playerName1", Arrays.asList(allowedNames))
                .whereNotContainedIn("playerName2", Arrays.asList(disallowedNames))
//                .whereContains("stringKey", "substring")
                .whereDoesNotExist("bestScore")
                .whereExists("worstScore")
//                .setCaseSensitive(true)
//                .whereStartsWith("stringKey1", "prefix")
//                .whereEndsWith("stringKey2", "suffix")
//                .whereEqualTo("dob", "15-12-1900")
//                .whereNotEqualTo("rank", "amateur")
//                .whereMatches("regexKey1", "^.*(p)?")
//                .whereMatches("regexKey2", "^.*", "m")
                .whereMatchesKeyInQuery("player", "username", subQuery)
//                .whereDoesNotMatchKeyInQuery("opponent", "username", subQuery)
//                .whereMatchesQuery("queryKey", subQuery)
//                .whereDoesNotMatchQuery("anotherQueryKey", subQuery)
//                .whereMatchesQuery("query", subQuery)
//                .whereWithinGeoBox("bounds", geoPoint1, geoPoint2)
//                .whereWithinKilometers("home", geoPoint1, 6371.0D)
//                .whereWithinMiles("work", geoPoint1, 3958.8000000000002D)
//                .whereWithinRadians("city", geoPoint2, 10000)
//                .whereNear("tournament", geoPoint2)
//                .include("quotes")
//                .include("location.x")
                .selectKeys(Arrays.asList(keys));

        // TODO check fields
        System.out.println(query.encode());
        final JSONObject queryJson = query.encode();
        final JSONObject where = queryJson.getJSONObject("where");
//        assertEqual("{\"$gt\":-6}", where.getJSONObject("score1").toString());
//        assertEqual("{\"$gte\":10}", where.getJSONObject("wins").toString());
//        assertEqual("{\"$lt\":2}", where.getJSONObject("losses").toString());
//        assertEqual("{\"$lte\":2}", where.getJSONObject("score2").toString());
//        assertEqual("{\"$in\":[\"Jang Min Chul\",\"Sean Plott\"]}", where.getJSONObject("playerName1").toString());
//        assertEqual("{\"$nin\":[\"Don't Allow\"]}", where.getJSONObject("playerName2").toString());
//        assertEqual("{\"$exists\":true}", where.getJSONObject("worstScore").toString());
//        assertEqual("{\"$exists\":false}", where.getJSONObject("bestScore").toString());
//        assertEqual("{\"$select\":{\"query\":{\"className\":\"players\","
//                + "\"where\":{\"games\":{\"$exists\":true}}},\"key\":\"username\"}}", 
//                where.getJSONObject("player").toString());
        
        /* Raw result
        
          {"include":"quotes,location.x",
          "keys":"key1,key2",
          "limit":10,
          "className":"games",
          "where":{
            "wins":{"$gte":10},
            "queryKey":{"$inQuery":{"className":"players","where":{"games":{"$exists":true}}}},
            "worstScore":{"$exists":true},
            "score2":{"$lte":2},
            "playerName":{"$in":["Jang Min Chul","Sean Plott"]},
            "city":{"$nearSphere":{"__type":"GeoPoint","latitude":18,"longitude":5},"$maxDistance":10000},
            "bestScore":{"$exists":false},
            "work":{"$nearSphere":{"__type":"GeoPoint","latitude":-10,"longitude":10},"$maxDistance":1},
            "query":{"$inQuery":{"className":"players","where":{"games":{"$exists":true}}}},
            "tournament":{"$nearSphere":{"__type":"GeoPoint","latitude":18,"longitude":5}},
            "losses":{"$lt":2},
            "home":{"$nearSphere":{"__type":"GeoPoint","latitude":-10,"longitude":10},"$maxDistance":1},
            "regexKey2":{"$regex":"^.*","$options":"m"},
            "anotherQueryKey":{"$notInQuery":{"className":"players","where":{"games":{"$exists":true}}}},
            "regexKey1":{"$regex":"^.*(p)?"}, 
            "dob":"15-12-1900",
            "opponent":{"$dontSelect":{"query":{"className":"players","where":{"games":{"$exists":true}}},"key":"username"},"$nin":["Dont Allow"]},
            "bounds":{"$within":{"$box":[{"__type":"GeoPoint","latitude":-10,"longitude":10},{"__type":"GeoPoint","latitude":18,"longitude":5}]}},
            "stringKey":{"$regex":"(?i)\\Qsubstring\\E"},
            "rank":{"$ne":"amateur"},
            "score1":{"$gt":6},
            "stringKey1":{"$regex":"^\\Qprefix\\E"},
            "stringKey2":{"$regex":"\\Qsuffix\\E$"},
            "player":{"$select":{"query":{"className":"players","where":{"games":{"$exists":true}}},"key":"username"}},
            "skip":5,
            "order":"-tournaments,loosingScore,-score2"}
        */
    }

    private void checkGreaterAndLessThanConstraints() throws ParseException {
        query = ParseQuery.create(classGameScore);
        query.whereGreaterThanOrEqualTo(fieldScore, 1000);
        query.whereLessThanOrEqualTo(fieldScore, 3000);
        List<ParseObject> results = query.find();
        
        assertTrue(results.size() > 0, "$lte + $gte query is expected to return results");

        // All results must match query criteria
        for (ParseObject output : results) {
            assertTrue(output.getInt(fieldScore) >= 1000 && output.getInt(fieldScore) <= 3000,
                    "Retrieved output should meet $lte + $gte query constraints");
        }

        // All inputs matching criteria must be in the output
        for (ParseObject input : gameScoreObjects) {
            if ((input.getInt(fieldScore) != null) 
                    && input.getInt(fieldScore) >= 1000 && input.getInt(fieldScore) <= 3000) {
                boolean found = false;
                for (ParseObject output : results) {
                    if (output.getObjectId().equals(input.getObjectId())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Input with objectId " + input.getObjectId()
                        + " is expected in output based on $lte + $gte query "
                        + "constraints but not found");
            }
        }
    }

    private void checkInConstraint() throws ParseException {
        List<Integer> allowedScores = Arrays.asList(new Integer[]{1, 3, 5, 9});
        query = ParseQuery.create(classGameScore);
        query.whereContainedIn(fieldScore, allowedScores);
        List<ParseObject> results = query.find();
        
        assertTrue(results.size() > 0, "$in Query is expected to return results");

        // All results must match query criteria
        for (ParseObject output : results) {
            assertTrue(allowedScores.contains(output.getInt(fieldScore)),
                    "Retrieved output should meet $in query constraints");
        }

        // All inputs matching criteria must be in the output
        for (ParseObject input : gameScoreObjects) {
            if (allowedScores.contains(input.getInt(fieldScore))) {
                boolean found = false;
                for (ParseObject output : results) {
                    if (output.getObjectId().equals(input.getObjectId())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Input with objectId " + input.getObjectId()
                        + " is expected in output based on $in query constraints "
                        + "but not found");
            }
        }
    }

    private void checkNotInConstraints() throws ParseException {
        List<String> allowedPlayers = Arrays.asList(new String[]{"Jonathan Walsh","Dario Wunsch","Shawn Simon"});
        query = ParseQuery.create(classGameScore);
        query.whereNotContainedIn(fieldPlayerName, allowedPlayers);
        List<ParseObject> results = query.find();
        
        assertTrue(results.size() > 0, "$nin Query is expected to return results");

        // All results must match query criteria
        for (ParseObject output : results) {
            assertTrue(!allowedPlayers.contains(output.getString(fieldPlayerName)),
                    "Retrieved output should meet $nin query constraints");
        }

        // All inputs matching criteria must be in the output
        for (ParseObject input : gameScoreObjects) {
            if (!allowedPlayers.contains(input.getString(fieldPlayerName))) {
                boolean found = false;
                for (ParseObject output : results) {
                    if (output.getObjectId().equals(input.getObjectId())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Input with objectId " + input.getObjectId()
                        + " is expected in output based on $nin query constraints "
                        + "but not found");
            }
        }
    }

    private void checkExistsConstraints() throws ParseException {
        query = ParseQuery.create(classGameScore);
        query.whereExists(fieldScore);
        List<ParseObject> results = query.find();
        
        assertTrue(results.size() > 0, "$exists=true Query is expected to return results");

        // All results must match query criteria
        for (ParseObject output : results) {
            assertNotNull(output.getInt(fieldScore),
                    "Retrieved output should meet $exists=true query constraints");
        }

        // All inputs matching criteria must be in the output
        for (ParseObject input : gameScoreObjects) {
            if (input.getInt(fieldScore) != null) {
                boolean found = false;
                for (ParseObject output : results) {
                    if (output.getObjectId().equals(input.getObjectId())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Input with objectId " + input.getObjectId()
                        + " is expected in output based on $exists=true query constraints "
                        + "but not found");
            }
        }
    }

    private void checkNotExistsConstraints() throws ParseException {
        query = ParseQuery.create(classGameScore);
        query.whereDoesNotExist(fieldScore);
        List<ParseObject> results = query.find();
        
        assertTrue(results.size() > 0, "$exists=false Query is expected to return results");

        // All results must match query criteria
        for (ParseObject output : results) {
            assertNull(output.getInt(fieldScore),
                    "Retrieved output should meet $exists=false query constraints");
        }

        // All inputs matching criteria must be in the output
        for (ParseObject input : gameScoreObjects) {
            if (input.getInt(fieldScore) == null) {
                boolean found = false;
                for (ParseObject output : results) {
                    if (output.getObjectId().equals(input.getObjectId())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Input with objectId " + input.getObjectId()
                        + " is expected in output based on $exists=false query "
                        + "constraints but not found");
            }
        }
    }

    private void checkMatchesOrDoesNotMatchKeyInQueryConstraints() throws ParseException {
        List<ParseUser> users = new ArrayList<ParseUser>();
        List<ParseObject> teams = new ArrayList<ParseObject>();
        final String fieldWinRecord = "winPct";
        final String fieldCity = "city";
        final String fieldUsername = "username";
        final String fieldHometown = "hometown";
        final String cityEindhoven = "Eindhoven";
        final String cityRotterdam = "Rotterdam";
        final String cityVenlo = "Venlo";
        
        // Teams
        ParseObject object = ParseObject.create(classTeam);
        object.put(fieldWinRecord, 0.7);
        teams.add(object);
        
        object = ParseObject.create(classTeam);
        object.put(fieldWinRecord, 0.3);
        object.put(fieldCity, cityVenlo);
        teams.add(object);
        
        object = ParseObject.create(classTeam);
        object.put(fieldWinRecord, 1.3);
        object.put(fieldCity, cityRotterdam);
        teams.add(object);
        
        object = ParseObject.create(classTeam);
        object.put(fieldWinRecord, 1.9);
        object.put(fieldCity, cityEindhoven);
        teams.add(object);
        
        saveObjects(teams);
        
        // Users
        ParseUser user1 = ParseUser.create("user1", "user1");
        user1.signUp();
        users.add(user1);
        
        ParseUser user2 = ParseUser.create("user2", "user2");
        user2.put(fieldHometown, cityEindhoven);
        user2.signUp();
        users.add(user2);
        
        ParseUser user3 = ParseUser.create("user3", "user3");
        user3.put(fieldHometown, cityEindhoven.toLowerCase()); // Different city for case-sensitive search
        user3.signUp();
        users.add(user3);
        
        ParseQuery<ParseObject> subQuery = ParseQuery.create(classTeam);
        subQuery.whereGreaterThan(fieldWinRecord, 0.5);
        
        ParseQuery<ParseUser> mainQuery = ParseQuery.create(ParseConstants.CLASS_NAME_USER);
        mainQuery.whereMatchesKeyInQuery(fieldHometown, fieldCity, subQuery);
        List<ParseUser> results = mainQuery.find();
        
        // In JSON: where={"hometown":{"$select":{"query":{"className":"Team","where":{"winPct":{"$gt":0.5}}},"key":"city"}}}' 
        // In words: Select all users whose hometown is the same as the 
        // city field of any team with win records greater than 0.5
        // Expected only user2
        assertEqual(1, results.size(), "Only one user is expected to match query");
        assertEqual(user2.getUsername(), results.get(0).getUsername(), 
                "User2 is the matching user");
        
        // Now change main query
        mainQuery = ParseQuery.create(ParseConstants.CLASS_NAME_USER);
        mainQuery.whereDoesNotMatchKeyInQuery(fieldHometown, fieldCity, subQuery);
        mainQuery.orderByDescending(fieldUsername);
        results = mainQuery.find();
        
        // In JSON: where={"hometown":{"$dontSelect":{"query":{"className":"Team","where":{"winPct":{"$gt":0.5}}},"key":"city"}}}' 
        // In words: Select all users whose hometown is different from the 
        // city field of any team with win records greater than 0.5
        // Expected user1 (no city) and user3
        assertEqual(2, results.size(), "user1 and user3 are expected to match query");
        assertEqual(user3.getUsername(), results.get(0).getUsername(), 
                "User3 is the first matching user (descending order on username)");
        assertEqual(user1.getUsername(), results.get(1).getUsername(), 
                "User1 is the second matching user (descending order on username)");
        
        deleteObjects(users);
        deleteObjects(teams);
    }

    private void checkSortConstraints() throws ParseException {
        query = ParseQuery.create(classGameScore);
        query.addAscendingOrder(fieldScore).addDescendingOrder(fieldPlayerName);
        List<ParseObject> results = query.find();
        
        assertEqual(results.size(), gameScoreObjects.size(), 
            "$sort query is expected to return all results in sorted order");

        // Results are sorted
        String playerName = null;
        Integer score = null;
        for (ParseObject output : results) {
            if (output.getInt(fieldScore) != null) {
                assertTrue(score == null || output.getInt(fieldScore) >= score,
                        "New object's score is expected to be greater than the previous one's");

                if ((playerName != null) && (output.getString(fieldPlayerName) != null)
                        && (score != null) && score.equals(output.getInt(fieldScore))) {
                    assertTrue(output.getString(fieldPlayerName).compareTo(playerName) <= 0,
                            "Player name is sorted in descending order when scores are equal ("
                            + "previous=" + playerName + "," + score + "; current="
                            + output.getString(fieldPlayerName) + "," + output.getInt(fieldScore) + ")");
                }
            }

            score = output.getInt(fieldScore);
            playerName = output.getString(fieldPlayerName);
        }
    }
}
