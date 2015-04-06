///*
// * Copyright 2015 Chidiebere Okwudire.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.parse4cn1;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// *
// * @author sidiabale
// */
//public class ParseQueryTest extends BaseParseTest {
//
//    private List<ParseObject> parseObjects;
//    private final String gameScore = "GameScore";
//    private final String score = "score";
//    private ParseQuery query;
//
//    @Override
//    public boolean runTest() throws Exception {
//        testQueryFormat();
//        testRestApi();
//
//        return true;
//    }
//
//    @Override
//    public void prepare() {
//        super.prepare();
//        parseObjects = new ArrayList<ParseObject>();
//    }
//
//    @Override
//    public void cleanup() {
//
//        // TODO: Replace with batch deletion when batch operations are implemeted
//        for (ParseObject object : parseObjects) {
//            if (object.getObjectId() != null) {
//                try {
//                    object.delete();
//                } catch (ParseException ex) {
//                    fail("Deleting object failed");
//                }
//            }
//        }
//
//        super.cleanup();
//    }
//
//    private void testRestApi() throws ParseException {
//        ParseObject object = ParseObject.create(gameScore);
//        object.put(score, 950);
//        parseObjects.add(object);
//
//        object = ParseObject.create(gameScore);
//        object.put(score, 1000);
//        parseObjects.add(object);
//
//        object = ParseObject.create(gameScore);
//        object.put(score, 2050);
//        parseObjects.add(object);
//
//        object = ParseObject.create(gameScore);
//        object.put(score, 3500);
//        parseObjects.add(object);
//
//        // TODO: Replace with batch creation when batch operations are implemented
//        for (ParseObject obj: parseObjects) {
//            obj.save();
//        }
//        checkGreaterAndLessThanConstraints();
//    }
//
//    private void testQueryFormat() throws ParseException {
//        final ParseGeoPoint geoPoint1 = new ParseGeoPoint(-10.0, 10.0);
//        final ParseGeoPoint geoPoint2 = new ParseGeoPoint(18.0, 5.0);
//
//        ParseQuery<ParseObject> subQuery = ParseQuery.create("players");
//        subQuery.whereExists("games");
//
//        ParseQuery<ParseObject> query = ParseQuery.create("games");
//        String[] allowedNames = {"Jang Min Chul", "Sean Plott"};
//        String[] disallowedNames = {"Dont Allow"};
//        String[] keys = {"key1", "key2"};
//        query.orderByAscending("age")
//                .orderByDescending("tournaments")
//                .addAscendingOrder("loosingScore")
//                .addDescendingOrder("score2")
//                .setLimit(10)
//                .setTrace(false)
//                .setCaseSensitive(false)
//                .setSkip(5)
//                .whereGreaterThan("score1", 6)
//                .whereGreaterThanOrEqualTo("wins", 10)
//                .whereLessThan("losses", 2)
//                .whereLessThanOrEqualTo("score2", 2)
//                .whereContainedIn("playerName", Arrays.asList(allowedNames))
//                .whereNotContainedIn("opponent", Arrays.asList(disallowedNames))
//                .whereContains("stringKey", "substring")
//                .whereDoesNotExist("bestScore")
//                .whereExists("worstScore")
//                .setCaseSensitive(true)
//                .whereStartsWith("stringKey1", "prefix")
//                .whereEndsWith("stringKey2", "suffix")
//                .whereEqualTo("dob", "15-12-1900")
//                .whereNotEqualTo("rank", "amateur")
//                .whereMatches("regexKey1", "^.*(p)?")
//                .whereMatches("regexKey2", "^.*", "m")
//                .whereMatchesKeyInQuery("player", "username", subQuery)
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
//                .selectKeys(Arrays.asList(keys));
//
//        // TODO check fields
//        System.out.println(query.encode());
//        
//        /* Raw result
//        
//          {"include":"quotes,location.x",
//          "keys":"key1,key2",
//          "limit":10,
//          "className":"games",
//          "where":{
//            "wins":{"$gte":10},
//            "queryKey":{"$inQuery":{"className":"players","where":{"games":{"$exists":true}}}},
//            "worstScore":{"$exists":true},
//            "score2":{"$lte":2},
//            "playerName":{"$in":["Jang Min Chul","Sean Plott"]},
//            "city":{"$nearSphere":{"__type":"GeoPoint","latitude":18,"longitude":5},"$maxDistance":10000},
//            "bestScore":{"$exists":false},
//            "work":{"$nearSphere":{"__type":"GeoPoint","latitude":-10,"longitude":10},"$maxDistance":1},
//            "query":{"$inQuery":{"className":"players","where":{"games":{"$exists":true}}}},
//            "tournament":{"$nearSphere":{"__type":"GeoPoint","latitude":18,"longitude":5}},
//            "losses":{"$lt":2},
//            "home":{"$nearSphere":{"__type":"GeoPoint","latitude":-10,"longitude":10},"$maxDistance":1},
//            "regexKey2":{"$regex":"^.*","$options":"m"},
//            "anotherQueryKey":{"$notInQuery":{"className":"players","where":{"games":{"$exists":true}}}},
//            "regexKey1":{"$regex":"^.*(p)?"}, 
//            "dob":"15-12-1900",
//            "opponent":{"$dontSelect":{"query":{"className":"players","where":{"games":{"$exists":true}}},"key":"username"},"$nin":["Dont Allow"]},
//            "bounds":{"$within":{"$box":[{"__type":"GeoPoint","latitude":-10,"longitude":10},{"__type":"GeoPoint","latitude":18,"longitude":5}]}},
//            "stringKey":{"$regex":"(?i)\\Qsubstring\\E"},
//            "rank":{"$ne":"amateur"},
//            "score1":{"$gt":6},
//            "stringKey1":{"$regex":"^\\Qprefix\\E"},
//            "stringKey2":{"$regex":"\\Qsuffix\\E$"},
//            "player":{"$select":{"query":{"className":"players","where":{"games":{"$exists":true}}},"key":"username"}}},
//            "skip":5,
//            "order":"-tournaments,loosingScore,-score2"}
//        */
//    }
//
//    private void checkGreaterAndLessThanConstraints() throws ParseException {
//        query = ParseQuery.create(gameScore);
//        query.whereGreaterThanOrEqualTo(score, 1000);
//        query.whereLessThanOrEqualTo(score, 3000);
//        List<ParseObject> results = query.find();
//        
//        assertTrue(results.size() > 0, "Query is expected to return results");
//
//        // All results must match query criteria
//        for (ParseObject output : results) {
//            assertTrue(output.getInt(score) >= 1000 && output.getInt(score) <= 3000,
//                    "Retrieved output meets query criteria");
//        }
//
//        // All inputs matching criteria must be in the output
//        for (ParseObject input : parseObjects) {
//            if (input.getInt(score) >= 1000 && input.getInt(score) <= 3000) {
//                boolean found = false;
//                for (ParseObject output : results) {
//                    if (output.getObjectId().equals(input.getObjectId())) {
//                        found = true;
//                        break;
//                    }
//                }
//                assertTrue(found, "Input with objectId " + input.getObjectId()
//                        + " is expected in output based on query constraints but not found");
//            }
//        }
//    }
//}
