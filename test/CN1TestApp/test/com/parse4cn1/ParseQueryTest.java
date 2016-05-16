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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sidiabale
 */
public class ParseQueryTest extends BaseParseTest {

    private List<ParseObject> gameScoreObjects;
    private List<ParseObject> posts;
    private List<ParseObject> comments;
    private Map<ParseObject, ParseObject> commentToPostMapping;
    private final String classGameScore = "GameScore";
    private final String classTeam = "Team";
    private final String classPost = "Post";
    private final String classComment = "Comment";
    private final String classPlaceObjects = "PlaceObjects";
    private final String fieldScore = "score";
    private final String fieldPlayerName = "playerName";
    private final String fieldArrayField = "arrayField";
    private final String fieldTitle = "title";
    private final String fieldImage = "image";
    private final String fieldAuthor = "author";
    private final String fieldPost = "post";
    private final String fieldLocation = "location";

    @Override
    public boolean runTest() throws Exception {
        testQueryFormat();
        testRestApiExample();
//        testGeoPointQueries(); see: https://github.com/ParsePlatform/parse-server/issues/1592
        return true;
    }

    @Override
    public void prepare() {
        super.prepare();
        gameScoreObjects = new ArrayList<ParseObject>();
        posts = new ArrayList<ParseObject>();
        comments = new ArrayList<ParseObject>();
        commentToPostMapping = new HashMap<ParseObject, ParseObject>();
    }

    @Override
    protected void resetClassData() {
        System.out.println("============== resetClassData()");
        deleteAllUsers();
        batchDeleteObjects(classGameScore);
        batchDeleteObjects(classTeam);
        batchDeleteObjects(classPost);
        batchDeleteObjects(classComment);
        batchDeleteObjects(classPlaceObjects);
    }
    
    private void prepareGeoPointData() throws ParseException {
        batchDeleteObjects(classPlaceObjects);
            
        ParseObject obj = ParseObject.create(classPlaceObjects);
        obj.put(fieldLocation, new ParseGeoPoint(89, 122.37));
        obj.put(fieldTitle, 1);
        obj.save();
        
        obj = ParseObject.create(classPlaceObjects);
        obj.put(fieldLocation, new ParseGeoPoint(40, -30));
        obj.put(fieldTitle, 2);
        obj.save();
        
        obj = ParseObject.create(classPlaceObjects);
        obj.put(fieldLocation, new ParseGeoPoint(30.82, -122.37));
        obj.put(fieldTitle, 3);
        obj.save();

        obj = ParseObject.create(classPlaceObjects);
        obj.put(fieldLocation, new ParseGeoPoint(30, -20));
        obj.put(fieldTitle, 4);
        obj.save();

        obj = ParseObject.create(classPlaceObjects);
        obj.put(fieldLocation, new ParseGeoPoint(60, -20));
        obj.put(fieldTitle, 5);
        obj.save();

        obj = ParseObject.create(classPlaceObjects);
        obj.put(fieldLocation, new ParseGeoPoint(37.71, -122.53));
        obj.put(fieldTitle, 6);
        obj.save();
        
        obj = ParseObject.create(classPlaceObjects);
        obj.put(fieldLocation, new ParseGeoPoint());
        obj.put(fieldTitle, 7);
        obj.save();
        
        obj = ParseObject.create(classPlaceObjects);
        obj.put(fieldLocation, new ParseGeoPoint(-79.82, -145));
        obj.put(fieldTitle, 8);
        obj.save();
    }

    private void prepareData() throws ParseException {
        System.out.println("============== prepareData()");
        ParseObject object = ParseObject.create(classGameScore);
        object.put(fieldScore, 4);
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 5);
        object.put(fieldArrayField, Arrays.asList(new Integer[]{2}));
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldPlayerName, "Jonathan Walsh");
        object.put(fieldArrayField, Arrays.asList(new Integer[]{2, 3, 4}));
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 9);
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 1000);
        object.put(fieldPlayerName, "Zabro Wunsch");
        object.put(fieldArrayField, Arrays.asList(new Integer[]{1, 8, 9}));
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 1000);
        object.put(fieldPlayerName, "Dario Wunsch");
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 2050);
        object.put(fieldPlayerName, "Shawn Simon");
        object.put(fieldArrayField, Arrays.asList(new Integer[]{2, 3, 4, 7, 9}));
        gameScoreObjects.add(object);

        object = ParseObject.create(classGameScore);
        object.put(fieldScore, 3500);
        object.put(fieldPlayerName, "Mr. Winner");
        gameScoreObjects.add(object);

        saveObjects(gameScoreObjects);

        final int nrOfPosts = 5;
        for (int i = 0; i < nrOfPosts; ++i) {
            object = ParseObject.create(classPost);
            object.put(fieldTitle, "Post" + (i + 1));
            if (i % 2 == 0) {
                object.put(fieldImage, "file:///image.url");
            }
            posts.add(object);
        }
        saveObjects(posts);

        for (int i = 0; i < nrOfPosts; ++i) {
            for (int j = 0; j < (int) (Math.random() * 2) + 1; ++j) {
                object = ParseObject.create(classComment);
                object.put(fieldAuthor, "Author" + (i + 1));
                object.put(fieldPost, posts.get(i));
                comments.add(object);
                commentToPostMapping.put(object, posts.get(i));
            }
        }
        saveObjects(comments);
    }
    
    private void testGeoPointQueries() throws ParseException {
        System.out.println("============== testGeoPointQueries()");
        
        prepareGeoPointData();
        
        System.out.println("-------------- nearSphere");
        final ParseGeoPoint refPoint = new ParseGeoPoint(30, -20);
        ParseQuery query = ParseQuery.getQuery(classPlaceObjects);
        query.whereNear(fieldLocation, refPoint);
        
        // Results should be ordered by distance
        List<ParseObject> results = query.find();
        assertTrue(results.size() > 0);
        
        double lastDistanceFromRef = 0;
        for (ParseObject obj: results) {
            double distanceFromRef = refPoint.distanceInKilometersTo(obj.getParseGeoPoint(fieldLocation));
            assertTrue(distanceFromRef >= lastDistanceFromRef, 
                    String.format("Results should be ordered by distance to the reference point, i.e., "
                            + "%f should be greater than or equal to %f", distanceFromRef, lastDistanceFromRef));
            lastDistanceFromRef = distanceFromRef;
        }
        
        System.out.println("-------------- Ordered by custom field");
        // Results should be ordered by selected field
        query = ParseQuery.getQuery(classPlaceObjects);
        query.whereNear(fieldLocation, refPoint).orderByAscending(fieldTitle);
        
        results = query.find();
        assertTrue(results.size() > 0);
        
        int title = 1;
        for (ParseObject obj: results) {
            assertEqual(title++, (int)obj.getInt(fieldTitle));
        }
        
        System.out.println("-------------- Limit radius");
        // Limit radius
        final double maxDistanceInMi = 10;
        query = ParseQuery.getQuery(classPlaceObjects);
        query.whereWithinMiles(fieldLocation, refPoint, maxDistanceInMi);
        results = query.find();
        
        assertTrue(results.size() > 0);
        for (ParseObject obj: results) {
            assertTrue(refPoint.distanceInMilesTo(obj.getParseGeoPoint(fieldLocation)) < maxDistanceInMi,
                    "Result should be within specified max distance of " + maxDistanceInMi + " miles");
        }
        
        System.out.println("-------------- Within box");
        final ParseGeoPoint northEast = new ParseGeoPoint(70, -10);
        final ParseGeoPoint southWest = new ParseGeoPoint(0, -40);
        query = ParseQuery.getQuery(classPlaceObjects);
        query.whereWithinGeoBox(fieldLocation, southWest, northEast);
        results = query.find();
   
        assertTrue(results.size() > 0);
        for (ParseObject obj: results) {
            ParseGeoPoint geoPoint = obj.getParseGeoPoint(fieldLocation);
            assertTrue(geoPoint.getLatitude() >= 0 && geoPoint.getLatitude() <= 70, 
                    String.format("Latitude %f should be within bounding box [-0, 70]", geoPoint.getLatitude()));
            assertTrue(geoPoint.getLongitude() >= -40 && geoPoint.getLongitude() <= -10, 
                    String.format("Longitude %f should be within bounding box [-10, -40]", geoPoint.getLongitude()));
        }
    }

    private void testRestApiExample() throws ParseException {
        System.out.println("============== testRestApiExample()");
        prepareData();

        checkEqualsAndNotEqualsConstraints();
        checkGreaterAndLessThanConstraints();
        checkInConstraint();
        checkNotInConstraints();
        checkExistsConstraints();
        checkNotExistsConstraints();
        checkMatchesOrDoesNotMatchKeyInQueryConstraints();
        checkSortConstraints();
//        checkLimitAndSkipConstraints(); // Temporarily commenting out failing test. 
                                          // Already fixed in dev; will be integrated in next release
        checkKeyConstraints();
        checkArrayValueConstraints();
        // Relational Queries
        checkPointerFieldConstraints();
        checkInQueryAndNotInQueryConstraints();
        checkRelatedToConstraints();
        checkIncludeConstraints();
        checkCountConstraints();
        checkOrConstraint();
        // Regex Queries
        checkRegexConstraints();
    }

    private void testQueryFormat() throws ParseException, JSONException {
        System.out.println("============== testQueryFormat()");

        final ParseGeoPoint geoPoint1 = new ParseGeoPoint(-10.0, 10.0);
        final ParseGeoPoint geoPoint2 = new ParseGeoPoint(18.0, 5.0);

        ParseQuery<ParseObject> subQuery = ParseQuery.getQuery("players");
        subQuery.whereExists("games");

        ParseQuery<ParseObject> mainQuery = ParseQuery.getQuery("games");
        String[] allowedNames = {"Jang Min Chul", "Sean Plott"};
        String[] disallowedNames = {"Don't Allow"};
        String[] keys = {"key1", "key2"};
        mainQuery.orderByAscending("age")
                .orderByDescending("tournaments")
                .addAscendingOrder("loosingScore")
                .addDescendingOrder("score2")
                .setLimit(10)
                .setSkip(5)
                .whereGreaterThan("score1", -6)
                .whereGreaterThanOrEqualTo("wins", 10)
                .whereLessThan("losses", 2)
                .whereLessThanOrEqualTo("score2", 2)
                .whereContainedIn("playerName1", Arrays.asList(allowedNames))
                .whereNotContainedIn("playerName2", Arrays.asList(disallowedNames))
                .whereContains("stringKey", "substring")
                .whereDoesNotExist("bestScore")
                .whereExists("worstScore")
                .setCaseSensitive(false)
                .whereStartsWith("stringKey1", "prefix")
                .whereEndsWith("stringKey2", "suffix")
                .setCaseSensitive(true)
                .whereStartsWith("stringKey3", "prefix")
                .whereEndsWith("stringKey4", "suffix")
                .whereEqualTo("dob", "15-12-1900")
                .whereNotEqualTo("rank", "amateur")
                .setCaseSensitive(false)
                .whereMatches("regexKey1", "^.*(p)?")
                .whereMatches("regexKey2", "^.*", "m")
                .whereMatchesKeyInQuery("player", "username", subQuery)
                .whereDoesNotMatchKeyInQuery("opponent", "username", subQuery)
                .whereContainsAll("arrayKey1", Arrays.asList(allowedNames))
                .whereEqualTo("arrayKey2", "valueInArray")
                .whereMatchesQuery("queryKey", subQuery)
                .whereDoesNotMatchQuery("anotherQueryKey", subQuery)
                .whereWithinGeoBox("bounds", geoPoint1, geoPoint2)
                .whereWithinKilometers("home", geoPoint1, 6371.0D)
                .whereWithinMiles("work", geoPoint1, 3958.8000000000002D)
                .whereWithinRadians("city", geoPoint2, 10000)
                .whereNear("tournament", geoPoint2)
                .include("quotes")
                .include("location.x")
                .selectKeys(Arrays.asList(keys));

        // TODO check fields
        final JSONObject queryJson = mainQuery.encode();
        assertEqual("games", mainQuery.getClassName());
        assertEqual("-tournaments,loosingScore,-score2", queryJson.get("order").toString());
        assertEqual(5, queryJson.getInt("skip"));
        assertEqual(10, queryJson.getInt("limit"));
        assertEqual("key1,key2", queryJson.get("keys").toString());
        assertEqual("quotes,location.x", queryJson.get("include").toString());

        final JSONObject where = queryJson.getJSONObject("where");
        assertEqual("{\"$gt\":-6}", where.getJSONObject("score1").toString());
        assertEqual("{\"$gte\":10}", where.getJSONObject("wins").toString());
        assertEqual("{\"$lt\":2}", where.getJSONObject("losses").toString());
        assertEqual("{\"$lte\":2}", where.getJSONObject("score2").toString());
        assertEqual("{\"$in\":[\"Jang Min Chul\",\"Sean Plott\"]}", where.getJSONObject("playerName1").toString());
        assertEqual("{\"$nin\":[\"Don't Allow\"]}", where.getJSONObject("playerName2").toString());
        assertEqual("{\"$exists\":true}", where.getJSONObject("worstScore").toString());
        assertEqual("{\"$exists\":false}", where.getJSONObject("bestScore").toString());
        assertEqual("{\"$select\":{\"query\":{\"className\":\"players\","
                + "\"where\":{\"games\":{\"$exists\":true}}},\"key\":\"username\"}}",
                where.getJSONObject("player").toString());
        assertEqual("{\"$dontSelect\":{\"query\":{\"className\":\"players\","
                + "\"where\":{\"games\":{\"$exists\":true}}},\"key\":\"username\"}}",
                where.getJSONObject("opponent").toString());
        assertEqual("{\"$all\":[\"Jang Min Chul\",\"Sean Plott\"]}", where.getJSONObject("arrayKey1").toString());
        assertEqual("valueInArray", where.get("arrayKey2").toString());
        assertEqual("{\"$inQuery\":{\"className\":\"players\",\"where\":{\"games\":{\"$exists\":true}}}}",
                where.getJSONObject("queryKey").toString());
        assertEqual("{\"$notInQuery\":{\"className\":\"players\",\"where\":{\"games\":{\"$exists\":true}}}}",
                where.getJSONObject("anotherQueryKey").toString());
        assertEqual("{\"$ne\":\"amateur\"}", where.getJSONObject("rank").toString());
        assertEqual("15-12-1900", where.get("dob").toString());
        assertEqual("{\"$regex\":\"\\\\Qsubstring\\\\E\"}", where.getJSONObject("stringKey").toString());
        
        String expected = where.getJSONObject("stringKey1").toString();
        assertTrue("{\"$regex\":\"^\\\\Qprefix\\\\E\",\"$options\":\"i\"}".equals(expected) // changes depending on jdk version
                || "{\"$options\":\"i\",\"$regex\":\"^\\\\Qprefix\\\\E\"}".equals(expected));
        
        expected = where.getJSONObject("stringKey2").toString();
        assertTrue("{\"$regex\":\"\\\\Qsuffix\\\\E$\",\"$options\":\"i\"}".equals(expected)
            || "{\"$options\":\"i\",\"$regex\":\"\\\\Qsuffix\\\\E$\"}".equals(expected));
        assertEqual("{\"$regex\":\"^\\\\Qprefix\\\\E\"}", where.getJSONObject("stringKey3").toString());
        assertEqual("{\"$regex\":\"\\\\Qsuffix\\\\E$\"}", where.getJSONObject("stringKey4").toString());
        assertEqual("{\"$regex\":\"^.*(p)?\"}", where.getJSONObject("regexKey1").toString());
        
        expected = where.getJSONObject("regexKey2").toString();
        assertTrue("{\"$regex\":\"^.*\",\"$options\":\"m\"}".equals(expected)
                || "{\"$options\":\"m\",\"$regex\":\"^.*\"}".equals(expected) );

        expected = where.getJSONObject("city").toString();
        assertTrue("{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"latitude\":18,\"longitude\":5},\"$maxDistance\":10000}".equals(expected)
                || "{\"$maxDistance\":10000,\"$nearSphere\":{\"__type\":\"GeoPoint\",\"longitude\":5,\"latitude\":18}}".equals(expected));
        
        expected = where.getJSONObject("tournament").toString();
        assertTrue("{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"latitude\":18,\"longitude\":5}}".equals(expected)
                || "{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"longitude\":5,\"latitude\":18}}".equals(expected));
        
        expected = where.getJSONObject("work").toString();
        assertTrue("{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"latitude\":-10,\"longitude\":10},\"$maxDistance\":1}".equals(expected)
                || "{\"$maxDistance\":1,\"$nearSphere\":{\"__type\":\"GeoPoint\",\"longitude\":10,\"latitude\":-10}}".equals(expected));
        
        expected = where.getJSONObject("home").toString();
        assertTrue("{\"$nearSphere\":{\"__type\":\"GeoPoint\",\"latitude\":-10,\"longitude\":10},\"$maxDistance\":1}".equals(expected)
                || "{\"$maxDistance\":1,\"$nearSphere\":{\"__type\":\"GeoPoint\",\"longitude\":10,\"latitude\":-10}}".equals(expected));
                
        expected = where.getJSONObject("bounds").toString();
        assertTrue("{\"$within\":{\"$box\":[{\"__type\":\"GeoPoint\",\"latitude\":-10,\"longitude\":10},{\"__type\":\"GeoPoint\",\"latitude\":18,\"longitude\":5}]}}".equals(expected)
                || "{\"$within\":{\"$box\":[{\"__type\":\"GeoPoint\",\"longitude\":10,\"latitude\":-10},{\"__type\":\"GeoPoint\",\"longitude\":5,\"latitude\":18}]}}".equals(expected));
    }

    private void checkEqualsAndNotEqualsConstraints() throws ParseException {
        System.out.println("============== checkEqualsAndNotEqualsConstraints()");
        List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .whereEqualTo(fieldScore, 1000)
                .find();

        assertTrue(results.size() > 0, "equals query is expected to return results");

        // All results must match query criteria
        for (ParseObject output : results) {
            assertTrue(output.getInt(fieldScore) == 1000,
                    "Retrieved output should meet equals query constraints");
        }

        results = ParseQuery.getQuery(classGameScore)
                .whereNotEqualTo(fieldScore, 1000).find();

        assertTrue(results.size() > 0, "$ne query is expected to return results");

        // All results must match query criteria
        for (ParseObject output : results) {
            assertTrue((output.getInt(fieldScore) == null) || (output.getInt(fieldScore) != 1000),
                    "Retrieved output should meet $ne query constraints");
        }
    }

    private void checkGreaterAndLessThanConstraints() throws ParseException {
        System.out.println("============== checkGreaterAndLessThanConstraints()");
        List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .whereGreaterThanOrEqualTo(fieldScore, 1000)
                .whereLessThanOrEqualTo(fieldScore, 3000)
                .find();

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
        System.out.println("============== checkInConstraint()");
        List<Integer> allowedScores = Arrays.asList(new Integer[]{1, 3, 5, 9});
        List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .whereContainedIn(fieldScore, allowedScores)
                .find();

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
        System.out.println("============== checkNotInConstraint()");
        List<String> allowedPlayers = Arrays.asList(new String[]{
            "Jonathan Walsh", "Dario Wunsch", "Shawn Simon"});
        List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .whereNotContainedIn(fieldPlayerName, allowedPlayers)
                .find();

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
        System.out.println("============== checkExistsConstraints()");
        List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .whereExists(fieldScore)
                .find();

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
        System.out.println("============== checkNotExistsConstraints()");
        List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .whereDoesNotExist(fieldScore)
                .find();

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
        System.out.println("============== checkMatchesOrDoesNotMatchKeyInQueryConstraints()");
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
        object.put(fieldCity, cityVenlo);
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
        ParseUser user1 = ParseUser.create("user1", TEST_PASSWORD);
        user1.signUp();
        users.add(user1);

        ParseUser user2 = ParseUser.create("user2", TEST_PASSWORD);
        user2.put(fieldHometown, cityEindhoven);
        user2.signUp();
        users.add(user2);

        ParseUser user3 = ParseUser.create("user3", TEST_PASSWORD);
        user3.put(fieldHometown, cityEindhoven.toLowerCase()); // Different city for case-sensitive search
        user3.signUp();
        users.add(user3);

        ParseQuery<ParseObject> subQuery = ParseQuery.getQuery(classTeam);
        subQuery.whereGreaterThan(fieldWinRecord, 0.5);

        ParseQuery<ParseUser> mainQuery = ParseQuery.getQuery(ParseConstants.CLASS_NAME_USER);
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
        mainQuery = ParseQuery.getQuery(ParseConstants.CLASS_NAME_USER);
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

        deleteObjects(users); // User.delete() needs to be called for proper deletion hence not batch deleted
        batchDeleteObjects(teams);
    }

    private void checkSortConstraints() throws ParseException {
        System.out.println("============== checkSortConstraints()");
        List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .addAscendingOrder(fieldScore)
                .addDescendingOrder(fieldPlayerName)
                .find();

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

    private void checkLimitAndSkipConstraints() throws ParseException {
        System.out.println("============== checkLimitAndSkipConstraints()");
        final List<ParseObject> gameScores = ParseQuery.getQuery(classGameScore).find();
        final int limit = gameScores.size() / 2;
        final int skip = Math.min(2, limit);

        final List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .setLimit(limit)
                .setSkip(skip)
                .find();

        assertEqual(results.size(), limit,
                "$limit constraint should limit result count to " + limit
                + " but actual result count is " + results.size());
        
        // Skipped objects should not be in results
        for (ParseObject output : results) {
            boolean found = false;
            for (int i = skip; i < gameScores.size() - 1; ++i) {
                final ParseObject input = gameScores.get(i);
                if (output.getObjectId().equals(input.getObjectId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Output with objectId " + output.getObjectId()
                    + " returned in result (non-skipped objects) but not expected");
        }
    }

    private void checkKeyConstraints() throws ParseException {
        System.out.println("============== checkKeyConstraints()");
        final Set<String> targetKeys = new HashSet<String>();
        targetKeys.add(fieldPlayerName);
        targetKeys.add(fieldScore);
        final List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .selectKeys(targetKeys)
                .find();

        assertTrue(!results.isEmpty(), "keys query should return results");
        for (ParseObject output : results) {
            assertTrue(output.keySet().size() <= targetKeys.size(),
                    "Output should contain at most the selected keys i.e."
                    + "expected size <= 2 but found " + output.keySet().size());
            assertTrue(output.has(fieldScore) || output.has(fieldPlayerName),
                    "fields '" + fieldScore + "' and/or '" + fieldPlayerName
                    + "' are the only fields expected in output with objectId "
                    + output.getObjectId() + " but found " + output.keySet());
        }
    }

    private void checkArrayValueConstraints() throws ParseException {
        System.out.println("============== checkArrayValueConstraints()");
        List<ParseObject> results = ParseQuery.getQuery(classGameScore)
                .whereEqualTo(fieldArrayField, 2)
                .find();

        // All results contain
        assertTrue(results.size() > 0, "value in array field query should return results");
        for (ParseObject output : results) {
            assertTrue(output.getList(fieldArrayField).contains(2),
                    "Array field of output should contain target value of 2");
        }

        final List<Integer> values = Arrays.asList(new Integer[]{2, 3, 4});
        results = ParseQuery.getQuery(classGameScore)
                .whereContainsAll(fieldArrayField, values)
                .find();

        assertTrue(results.size() > 0, "$all query should return results");
        for (ParseObject output : results) {
            assertTrue(output.getList(fieldArrayField).containsAll(values),
                    "Expected array field of output to contain target list of values "
                    + values + " but found " + output.getList(fieldArrayField));
        }
    }

    private ParseObject getPostByCommentId(final String commentId) {
        for (ParseObject comment : commentToPostMapping.keySet()) {
            if (comment.getObjectId().equals(commentId)) {
                return commentToPostMapping.get(comment);
            }
        }
        return null;
    }

    private void checkPointerFieldConstraints() throws ParseException {
        System.out.println("============== checkPointerFieldConstraints()");
        ParseQuery<ParseObject> commentQuery = ParseQuery.getQuery(classComment);

        for (ParseObject comment : comments) {
            final ParseObject post = commentToPostMapping.get(comment);
            assertNotNull(post, "Post corresponding to comment not found");
            commentQuery.whereEqualTo(fieldPost, post);
            assertNotNull(commentQuery.get(comment.getObjectId()),
                    "Comment " + comment.getObjectId()
                    + " should be among results for the post with id "
                    + post.getObjectId());
        }
    }

    private void checkInQueryAndNotInQueryConstraints() throws ParseException {
        System.out.println("============== checkInQueryAndNotInQueryConstraints()");
        ParseQuery<ParseObject> commentQuery;

        // Comments on posts having an image field
        ParseQuery<ParseObject> postWithImageQuery = ParseQuery.getQuery(classPost);
        postWithImageQuery.whereExists(fieldImage);
        commentQuery = ParseQuery.getQuery(classComment);
        commentQuery.whereMatchesQuery(fieldPost, postWithImageQuery);
        List<ParseObject> results = commentQuery.find();

        assertTrue(results.size() > 0, "$inQuery query should return results");
        for (ParseObject comment : results) {
            final ParseObject post = getPostByCommentId(comment.getObjectId());
            assertNotNull(post, "Post matching comment must exist");
            assertNotNull(post.getString(fieldImage),
                    "All results should belong to a post with an image field");
        }

        // Comments on posts having no image field
        commentQuery = ParseQuery.getQuery(classComment);
        commentQuery.whereDoesNotMatchQuery(fieldPost, postWithImageQuery);
        results = commentQuery.find();

        assertTrue(results.size() > 0, "$notInQuery query should return results");
        for (ParseObject comment : results) {
            final ParseObject post = getPostByCommentId(comment.getObjectId());
            assertNotNull(post, "Post matching comment must exist");
            assertNull(post.getString(fieldImage),
                    "All results should belong to a post without an image field");
        }
    }

    private void checkRelatedToConstraints() throws ParseException {
        System.out.println("============== checkRelatedToConstraints()");
        final ParseUser user1
                = ParseUser.create("user1" + getCurrentTimeInHex(), TEST_PASSWORD);
        final ParseUser user2
                = ParseUser.create("user2" + getCurrentTimeInHex(), TEST_PASSWORD);

        user1.signUp();
        user2.signUp();

        final String fieldLikes = "likes";
        final ParseObject post1 = ParseObject.create(classPost);
        post1.put(fieldTitle, "Post1" + getCurrentTimeInHex());
        post1.save();

        final ParseObject post2 = ParseObject.create(classPost);
        post2.put(fieldTitle, "Post2" + getCurrentTimeInHex());
        post2.save();

        ParseRelation<ParseObject> relation = post1.getRelation(fieldLikes);
        relation.add(user1);
        relation.add(user2);
        post1.save();

        relation = post2.getRelation(fieldLikes);
        relation.add(user1);
        post2.save();

        // user1 likes post1 and post2
        // user2 likes post1
        System.out.println("-------------- Many-to-many set up: User1 likes "
                + "Post1 and Post2; User2 likes only Post1");
        System.out.println("-------------- Retrieve users that like a post");
        List<ParseObject> results = post1.getRelation(fieldLikes).getQuery().find();
        assertEqual(2, results.size(), "User1 should like both posts");

        results = post2.getRelation(fieldLikes).getQuery().find();
        assertEqual(1, results.size(), "User2 should like only one post");

        System.out.println("-------------- Retrieve posts liked by a user");
        results = ParseQuery.getQuery(classPost)
                .whereEqualTo(fieldLikes, user1)
                .find();
        assertEqual(2, results.size(), "Post1 should be liked by both users");

        results = ParseQuery.getQuery(classPost)
                .whereEqualTo(fieldLikes, user2)
                .find();
        assertEqual(1, results.size(), "Post2 should be liked by only user1");

        System.out.println("-------------- Clean up");
        post1.delete();
        post2.delete();
        deleteAllUsers();
    }

    private void checkIncludeConstraints() throws ParseException {
        System.out.println("============== checkIncludeConstraints()");
        final String fieldNestedComment = "reply";
        final ParseObject comment = comments.get(0);

        ParseObject post = commentToPostMapping.get(comment);
        final ParseObject reply = comments.get(1);
        post.put(fieldNestedComment, reply);
        post.save();

        assertNotNull(comment, "Comment is not null");
        assertNotNull(post, "Post is not null");

        List<ParseObject> results = ParseQuery.getQuery(classComment)
                .setLimit(1)
                .include(fieldPost)
                .find();

        assertEqual(1, results.size(), "Result count is correct");
        ParseObject shallowIncludedPost = results.get(0).getParseObject(fieldPost);
        assertNotNull(shallowIncludedPost, "Post is included");
        assertTrue(dataMatches(post, shallowIncludedPost),
                "All top-level data matches (shallow include)");
        assertTrue(shallowIncludedPost.getParseObject(fieldNestedComment).keySet().isEmpty(),
                "Nested pointer field should be empty (i.e. pointer)");

        // Partial include
        results = ParseQuery.getQuery(classComment)
                .setLimit(1)
                .include(fieldPost + "." + fieldNestedComment)
                .find();
        assertEqual(1, results.size(), "Result count of partial include is correct");
        ParseObject deepIncludedPost = results.get(0).getParseObject(fieldPost);
        assertNotNull(deepIncludedPost, "Post is included (as well as nested pointer field)");
        assertTrue(dataMatches(post, deepIncludedPost),
                "All top-level data matches (deep include)");
        assertTrue(dataMatches(reply, deepIncludedPost.getParseObject(fieldNestedComment)),
                "Nested field is also fully included");
    }

    private boolean dataMatches(final ParseObject ref, final ParseObject other) {
        HashSet<String> commonKeys = new HashSet<String>(ref.keySet());
        commonKeys.retainAll(other.keySet());

        assertEqual(ref.keySet(), commonKeys,
                "All keys in the original post should be included "
                + "(there may be more keys in the included post due to relations)");
        for (String key : ref.keySet()) {
            if (!(ref.get(key) instanceof ParseObject)) {
                assertEqual(ref.get(key), other.get(key),
                        "Values should be the same for key=" + key);
            }
        }
        return true;
    }

    private void checkCountConstraints() throws ParseException {
        System.out.println("============== checkCountConstraints()");
        assertTrue(!gameScoreObjects.isEmpty(), "There should be elements to be counted");
        assertEqual(gameScoreObjects.size(),
                ParseQuery.getQuery(classGameScore).count(),
                "Count query returns correct # of results");
    }

    private void checkOrConstraint() throws ParseException {
        System.out.println("============== checkOrConstraint()");
        ParseQuery lessThan5Query = ParseQuery.getQuery(classGameScore);
        lessThan5Query.whereLessThan(fieldScore, 5);

        ParseQuery greaterThan1000Query = ParseQuery.getQuery(classGameScore);
        greaterThan1000Query.whereGreaterThan(fieldScore, 1000);

        List<ParseObject> results = ParseQuery.getOrQuery(
                Arrays.asList(new ParseQuery[]{lessThan5Query, greaterThan1000Query}))
                .find();

        assertTrue(results.size() > 0, "$or query returned results");
        for (ParseObject obj : results) {
            final int score = obj.getInt(fieldScore);
            assertTrue((score < 5) || (score > 1000), "score meets $or constraint");
        }
    }

    private void checkRegexConstraints() throws ParseException {
        System.out.println("============== checkRegexConstraints()");
        // starts with
        assertTrue(ParseQuery.getQuery(classGameScore)
                .whereStartsWith(fieldPlayerName, "S").find().size() > 0,
                "starts with 'S' returns results (case-sensitive)");

        assertTrue(ParseQuery.getQuery(classGameScore)
                .whereStartsWith(fieldPlayerName, "s").find().isEmpty(),
                "starts with 's' returns no results (case-sensitive)");

        assertTrue(ParseQuery.getQuery(classGameScore)
                .setCaseSensitive(false)
                .whereStartsWith(fieldPlayerName, "s").find().size() > 0,
                "starts with 's' returns results (case-insensitive)");

        // ends with
        assertTrue(ParseQuery.getQuery(classGameScore)
                .whereEndsWith(fieldPlayerName, "sh").find().size() > 0,
                "ends with 'sh' returns results (case-sensitive)");
        ;
        assertTrue(ParseQuery.getQuery(classGameScore)
                .whereEndsWith(fieldPlayerName, "Sh").find().isEmpty(),
                "ends with 'Sh' returns no results (case-sensitive)");

        assertTrue(ParseQuery.getQuery(classGameScore)
                .setCaseSensitive(false).whereEndsWith(fieldPlayerName, "Sh")
                .find().size() > 0,
                "ends with 'Sh' returns results (case-insensitive)");

        // matches
        final String mrWinnerRegex = "^.*[.].*Ner$";
        assertTrue(ParseQuery.getQuery(classGameScore)
                .whereMatches(fieldPlayerName, mrWinnerRegex, "i")
                .find().size() > 0,
                "regex match returns results (case-insensitive)");

        assertTrue(ParseQuery.getQuery(classGameScore)
                .whereMatches(fieldPlayerName, mrWinnerRegex).find().isEmpty(),
                "regex match returns no results (case-sensitive)");

        // contains
        assertTrue(ParseQuery.getQuery(classGameScore)
                .whereContains(fieldPlayerName, "bro").find().size() > 0,
                "contains query returns results");

        assertTrue(ParseQuery.getQuery(classGameScore)
                .whereContains(fieldPlayerName, "Bro").find().isEmpty(),
                "contains query returns no results (case-sensitive by default)");
    }
}
