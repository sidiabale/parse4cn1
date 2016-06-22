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
import com.codename1.io.Storage;
import com.parse4cn1.encode.ParseDecoder;
import com.parse4cn1.util.ExternalizableParseObject;
import com.parse4cn1.util.ParseRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 *
 * @author sidiabale
 */
public class ParseObjectTest extends BaseParseTest {

    private final String classGameScore = "GameScore";
    private final String classPlayer = "Player";
    private final String classCar = "Car";
    private final String classKitchen = "Kitchen";

    private final ParseGeoPoint location = new ParseGeoPoint(40.2, -24);
    private final String fieldLocation = "fieldLocation";
    
    private static class CustomParseObject extends ParseObject {

        public static final String CLASS_NAME = "CustomParseObject";
        public static final String CUSTOM_FIELD_NAME = "customField";

        private CustomParseObject() {
            super(CLASS_NAME);
            put(CUSTOM_FIELD_NAME, "ReadOnly");
            setDirty(false);
        }

        public String getCustomField() {
            return getString(CUSTOM_FIELD_NAME);
        }

        @Override
        public void put(String key, Object value) {
            if (CUSTOM_FIELD_NAME.equals(key) && !"ReadOnly".equals(value)) {
                throw new IllegalArgumentException("Field " + CUSTOM_FIELD_NAME
                        + " must have value 'ReadOnly'");
            }
            super.put(key, value);
        }
    }

    @Override
    public boolean runTest() throws Exception {
        testRestApiExample();
        testCreateObjectExtended();
        testUpdateObjectExtended();
        testCustomParseObjectClass();
        testSimpleParseObjectSerialization();
        testCollectionInParseObjectSerialization();
        testParseFileInParseObjectSerialization();
        testObjectsInParseObjectSerialization();
        return true;
    }

    @Override
    protected void resetClassData() {
//        batchDeleteObjects(classGameScore);
//        batchDeleteObjects(classPlayer);
//        batchDeleteObjects(classCar);
//        batchDeleteObjects(classKitchen);
//        batchDeleteObjects(CustomParseObject.CLASS_NAME);
    }

    private void testRestApiExample() throws ParseException {
        System.out.println("============== testRestApiExample()");
        // Create
        ParseObject gameScore = ParseObject.create(classGameScore);
        gameScore.put("score", 1337);
        gameScore.put("playerName", "Sean Plott");
        gameScore.put("cheatMode", false);
        gameScore.save();

        // Retrieve
        ParseObject retrieved = ParseObject.fetch(gameScore.getClassName(),
                gameScore.getObjectId());
        assertEqual(Integer.valueOf(1337), gameScore.getInt("score"));
        assertEqual("Sean Plott", gameScore.getString("playerName"));
        assertFalse(gameScore.getBoolean("cheatMode"));

        // Update
        retrieved.put("score", 73453);
        retrieved.save();
        assertEqual(Integer.valueOf(73453), retrieved.getInt("score"));

        // Increment / decrement
        retrieved.increment("score");
        retrieved.save();
        assertEqual(Integer.valueOf(73454), retrieved.getInt("score"));

        // Decrement
        retrieved.increment("score", -4);
        retrieved.save();
        assertEqual(Integer.valueOf(73450), retrieved.getInt("score"));

        // Increment non-number field
        try {
            retrieved.increment("playerName");
            retrieved.save();
            assertFalse(true, "Increment should only work on number fields");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().startsWith("You cannot increment a non-number"));
        }

        // Relations
        ParseObject opponent = ParseObject.create(classPlayer);
        opponent.put("playerName", "Sean Plott");
        opponent.save();

        ParseRelation<ParseObject> opponentsRelation = retrieved.getRelation("opponents");
        opponentsRelation.add(opponent);
        retrieved.save();

        ParseRelation<ParseObject> retrievedRelation
                = (ParseRelation<ParseObject>) retrieved.get("opponents");
        assertEqual(retrievedRelation.getTargetClass(), opponent.getClassName());

        retrieved = ParseObject.fetch(retrieved.getClassName(), retrieved.getObjectId());
        retrievedRelation = (ParseRelation<ParseObject>) retrieved.getRelation("opponents");
        assertNotNull(retrievedRelation);
//        assertEqual(retrievedRelation.getTargetClass(), opponent.getClassName());
        opponent.delete();
        
        assertNull(opponent.getObjectId(),
                "Deleted object should have no objectId");
        assertNull(opponent.getCreatedAt(),
                "Deleted object should have no creation date");
        assertNull(opponent.getUpdatedAt(),
                "Deleted object should have no update date");
        assertTrue(opponent.keySet().isEmpty(),
                "Deleted object should have no keys");

        // Array operations (manually)
        List<String> skills = new ArrayList<String>();
        skills.add("flying");
        skills.add("kunfu");
        retrieved.put("skills", skills);
        retrieved.save();
        assertEqual(skills, retrieved.getList("skills"));

        // Delete field
        retrieved.remove("skills");
        retrieved.save();
        assertNull(retrieved.get("skills"));

        // Array operation ('atomic')
        testArrayOperations(retrieved);

        // Delete object
        retrieved.delete();
    }

    private void testArrayOperations(final ParseObject obj) throws ParseException {
        System.out.println("============== testArrayOperations()");
        final String skillBoxing = "boxing";

        List<String> skills = new ArrayList<String>();
        final String skillWrestling = "wrestling";
        final String skillKunfu = "kunfu";

        skills.add("flying");
        skills.add(skillKunfu);
        skills.add(skillKunfu);
        skills.add("running");

        final String fieldSkills = "skills";
        obj.addAllToArrayField(fieldSkills, skills);
        obj.save();
        assertEqual(skills, obj.getList(fieldSkills), "Duplicate 'kunfu' is allowed");

        skills.add(skillBoxing);
        obj.addToArrayField(fieldSkills, skillBoxing);
        obj.save();
        assertEqual(skills, obj.getList(fieldSkills), "Boxing skill is added");

        obj.addUniqueToArrayField(fieldSkills, skillBoxing);
        obj.save();
        assertEqual(skills, obj.getList(fieldSkills), "Duplicate boxing field is not added");

        List<String> extraSkills = Arrays.asList(skillWrestling, skillBoxing);
        obj.addAllUniqueToArrayField(fieldSkills, extraSkills);
        obj.save();
        skills.add(skillWrestling);
        assertEqual(skills, obj.getList(fieldSkills),
                "Only wrestling skill is added; duplicate boxing skill is ignored");

        obj.removeFromArrayField(fieldSkills, skillKunfu);
        obj.save();
        skills.remove(skillKunfu);
        skills.remove(skillKunfu);
        assertEqual(skills, obj.getList(fieldSkills),
                "All kunfu skills are removed");

        obj.removeAllFromArrayField(fieldSkills, extraSkills);
        obj.save();
        skills.removeAll(extraSkills);

        final ParseObject retrieved = ParseObject.fetch(obj.getClassName(), obj.getObjectId());
        assertEqual(skills, retrieved.getList(fieldSkills),
                "All extra skills (" + extraSkills.toString() + ") are removed");
    }

    private void testCreateObjectExtended() throws ParseException, JSONException {
        System.out.println("============== testCreateObjectExtended()");
        
        ParseObject obj = ParseObject.create(classCar);
        obj.put(fieldLocation, location);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("brand", "Peugeot");
        data.put("model", "208");
        data.put("nrOfDoors", 4);
        data.put("convertible", false);
        data.put("color", "Red");
        data.put("batchNr", getCurrentTimeInHex());

        List<String> pastUsers = new ArrayList<String>();
        pastUsers.add("User 1");
        pastUsers.add("User 2");
        pastUsers.add("User 3");
        data.put("pastUsers", pastUsers);

        JSONObject facilities = new JSONObject();
        facilities.put("navigationSystem", true);
        facilities.put("airConditioner", false);
        facilities.put("parkAssist", "parkingSensors");
        data.put("facilities", facilities);

        addData(obj, data);
        obj.save();

        assertNotNull(obj.getCreatedAt(), "Creation time not set");
        assertNotNull(obj.getObjectId(), "Object ID not set");
        assertEqual(obj.getCreatedAt(), obj.getUpdatedAt(), "Creation time should equal update time for new object");

        ParseObject retrieved = ParseObject.fetch(obj.getClassName(), obj.getObjectId());
        assertEqual(obj.getCreatedAt(), retrieved.getCreatedAt());
        assertEqual(obj.getUpdatedAt(), retrieved.getUpdatedAt());
        assertEqual(obj.getObjectId(), retrieved.getObjectId());
        
        compareGeoLocations(location, retrieved.getParseGeoPoint(fieldLocation));
        
        checkData(retrieved, data);
    }

    private void testUpdateObjectExtended() throws ParseException {
        System.out.println("============== testUpdateObjectExtended()");
        ParseObject obj = ParseObject.create(classKitchen);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("color", "White");
        data.put("style", "modern");
        data.put("renovationYear", 2006);

        List<String> knifeTypes = new ArrayList<String>();
        knifeTypes.add("Wavy Edge");
        knifeTypes.add("Straight Edge");
        knifeTypes.add("Granton Edge");

        HashMap<String, Object> knifeInfo = new HashMap<String, Object>();
        knifeInfo.put("count", knifeTypes.size());
        knifeInfo.put("types", knifeTypes);
        data.put("knives", knifeInfo);

        addData(obj, data);

        obj.save();

        checkData(obj, data);
        ParseObject retrieved = ParseObject.fetch(obj.getClassName(), obj.getObjectId());
        checkData(retrieved, data);

        data.clear();

        // Update existing (both simple and nested - list inside sub-object)
        data.put("renovationYear", 2015);
        HashMap<String, Object> retrievedKnifeInfo
                = (HashMap<String, Object>) retrieved.get("knives");
        knifeTypes.add("Unknown");
        retrievedKnifeInfo.put("types", knifeTypes);
        data.put("knives", retrievedKnifeInfo);

        // Add new
        data.put("floor", "laminate");
        JSONArray appliances = new JSONArray();
        appliances.put("refrigerator");
        appliances.put("electricCooker");
        appliances.put("toaster");
        data.put("appliances", appliances);

        addData(retrieved, data);

        retrieved.save(); // Update
        checkData(retrieved, data);
        retrieved = ParseObject.fetch(obj.getClassName(), obj.getObjectId());
        checkData(retrieved, data);
    }
    
    private void testSimpleParseObjectSerialization() throws ParseException {
        System.out.println("============== testSimpleParseObjectSerialization()");
        assertEqual(ExternalizableParseObject.getClassName(), "ExternalizableParseObject");
        
        final ParseObject gameScore = ParseObject.create(classGameScore);
        gameScore.put("score", 1337);
        gameScore.put("rating", 4.5);
        gameScore.put("playerName", "Sean Plott");
        gameScore.put("cheatMode", false);
        gameScore.save();

        final ParseObject retrieved = serializeAndRetrieveParseObject(gameScore);
        compareParseObjects(gameScore, retrieved, null);

        // Make object dirty object
        gameScore.put("score", 1378);

        System.out.println("-------------- Serialization of dirty ParseObject should fail");
        assertFalse(Storage.getInstance().writeObject(gameScore.getObjectId(), gameScore.asExternalizable()),
                "Serialization of dirty ParseObject should be disallowed");

        gameScore.delete();
    }
    
    private void testCollectionInParseObjectSerialization() throws ParseException, JSONException {
        System.out.println("============== testCollectionInParseObjectSerialization()");
        ParseObject parseObject = ParseObject.create(classCar);
        HashMap<String, Object> specsList = new HashMap<String, Object>();
        specsList.put("brand", "Peugeot");
        specsList.put("model", "208");
        specsList.put("nrOfDoors", 5);
        specsList.put("convertible", true);
        specsList.put("color", "SkyBlue");
        specsList.put("batchNr", getCurrentTimeInHex());

        ArrayList<String> usersLIst = new ArrayList<String>();
        usersLIst.add("User 1");
        usersLIst.add("User 2");
        usersLIst.add("User 3");
        
        final JSONArray usersJsonArray = new JSONArray(usersLIst);
        final JSONObject specsJsonArray = new JSONObject(specsList);
        
        final String keySpecsMap = "specificationsHashMap";
        final String keySpecsJsonObject = "specificationsJsonObject";
        final String keyUsersList = "pastUsersArrayList";
        final String keyUsersJsonArray = "pastUsersJsonArray";
        final String keyNull = "null";
        
        parseObject.put(keySpecsMap, specsList);
        parseObject.put(keySpecsJsonObject, specsJsonArray);
        parseObject.put(keyUsersList, usersLIst);
        parseObject.put(keyUsersJsonArray, usersJsonArray);
        parseObject.put(keyNull, JSONObject.NULL);
        
        parseObject.save();

        ParseObject retrieved = serializeAndRetrieveParseObject(parseObject);

        assertEqual(parseObject.get(keySpecsMap).toString(), retrieved.get(keySpecsMap).toString());
        assertEqual(parseObject.get(keySpecsJsonObject).toString(), retrieved.get(keySpecsJsonObject).toString());
        assertEqual(parseObject.get(keyUsersList).toString(), retrieved.get(keyUsersList).toString());
        assertEqual(parseObject.get(keyUsersJsonArray).toString(), retrieved.get(keyUsersJsonArray).toString());
        assertEqual(parseObject.get(keyNull), retrieved.get(keyNull));
        
        parseObject.delete();
    }

    private void testParseFileInParseObjectSerialization() throws ParseException {
        System.out.println("============== testParseFileInParseObjectSerialization()");
        final ParseFile textFile = new ParseFile("hello.txt", "Hello World!".getBytes());
        textFile.save();
        
        final ParseObject gameScore = ParseObject.create(classGameScore);
        gameScore.put("profile", textFile);
        gameScore.save();
        
        final ParseObject retrieved = serializeAndRetrieveParseObject(gameScore);
        compareParseFiles(textFile, retrieved.getParseFile("profile"), true);
        
        gameScore.delete();
    }
    
    private void testObjectsInParseObjectSerialization() throws ParseException {
        System.out.println("============== testObjectsInParseObjectSerialization()");
        final String key = "aKey";
        final String value = "aValue";
        final String keyCustomParseObject = "customParseObject";
        final String keyInnerParseObject = "innerParseObject";

        CustomParseObject customParseObject = new CustomParseObject();
        customParseObject.put(key, value);
        customParseObject.save();

        ParseObject innerObject = ParseObject.create(classPlayer);
        innerObject.put(key, value);
        innerObject.save();

        ParseObject parseObject = ParseObject.create(classGameScore);
        parseObject.put(keyCustomParseObject, customParseObject);
        parseObject.put(keyInnerParseObject, innerObject);

        parseObject.put(fieldLocation, location);

        parseObject.save();

        final ParseObject retrieved = serializeAndRetrieveParseObject(parseObject);
        compareParseObjects(parseObject.getParseObject(keyCustomParseObject),
                retrieved.getParseObject(keyCustomParseObject), null);
        compareParseObjects(parseObject.getParseObject(keyInnerParseObject),
                retrieved.getParseObject(keyInnerParseObject), null);
        compareGeoLocations(location, retrieved.getParseGeoPoint(fieldLocation));

        customParseObject.delete();
        innerObject.delete();
        parseObject.delete();
    }

    private void checkData(final ParseObject obj, HashMap<String, Object> data) {
        for (Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof JSONArray) {
                assertEqual(ParseDecoder.convertJSONArrayToList((JSONArray) entry.getValue()),
                        obj.getList(entry.getKey()));
            } else if (entry.getValue() instanceof JSONObject) {
                assertEqual(ParseDecoder.convertJSONObjectToMap((JSONObject) entry.getValue()),
                        obj.get(entry.getKey()));
            } else {
                assertEqual(entry.getValue(), obj.get(entry.getKey()));
            }
        }
    }

    private void addData(ParseObject obj, HashMap<String, Object> dataToAdd) {
        for (Entry<String, Object> entry : dataToAdd.entrySet()) {
            obj.put(entry.getKey(), entry.getValue());
        }
    }

    private void testCustomParseObjectClass() throws ParseException {
        try {
            CustomParseObject obj = ParseObject.create(CustomParseObject.CLASS_NAME);
            obj.put("purpose", "test");
            obj.save();
            fail("ClassCastException expected since custom sub-class has not been registered");
        } catch (ClassCastException ex) {
            assertTrue(true);
        }

        ParseRegistry.registerSubclass(CustomParseObject.class, CustomParseObject.CLASS_NAME);
        ParseRegistry.registerParseFactory(CustomParseObject.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (CustomParseObject.CLASS_NAME.equals(className)) {
                    return (T) new CustomParseObject();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        CustomParseObject obj = null;
        try {
            obj = ParseObject.create(CustomParseObject.CLASS_NAME);
            obj.put("purpose", "test");
            obj.save();
        } catch (Exception ex) {
            fail("No exception expected since custom sub-class has been registered "
                    + "but got exception: " + ex);
        }

        assertNotNull(obj, "Class CustomParseObject should be instantiable");
        final CustomParseObject retrieved = ParseObject.fetch(
                CustomParseObject.CLASS_NAME, obj.getObjectId());
        assertNotNull(retrieved, "Saved custom object should be retrievable");
        assertEqual("test", retrieved.getString("purpose"));
        assertEqual("ReadOnly", retrieved.getString(CustomParseObject.CUSTOM_FIELD_NAME));

    }
}
