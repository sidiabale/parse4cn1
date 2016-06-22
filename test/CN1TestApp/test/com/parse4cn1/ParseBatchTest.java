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

import java.util.ArrayList;
import java.util.Map.Entry;

/**
 *
 * @author sidiabale
 */
public class ParseBatchTest extends BaseParseTest {

    private final int MAX_BATCH_SIZE = 50;
    private final String classGameScore = "GameScore";
    private final String classPlayer = "Player";
    private final String classCar = "Car";
    private final String classKitchen = "Kitchen";

    @Override
    public boolean runTest() throws Exception {
        testRestApiExample();
        testValidBatch();
        testValidMixedBatch();
//        testBatchSizeExceedingLimit();
        testBatchIncludingFailures();
        testBatchWithInvalidObjects();
        return true;
    }

    @Override
    protected void resetClassData() {
        batchDeleteObjects(classGameScore);
        batchDeleteObjects(classPlayer);
        batchDeleteObjects(classCar);
        batchDeleteObjects(classKitchen);
    }
    
    private void testRestApiExample() throws ParseException {
        System.out.println("============== testRestApiExample()");

        final ParseObject gameScore1 = ParseObject.create(classGameScore);
        gameScore1.put("playerName", "Sean Plott");
        gameScore1.put("score", 1337);
        
        final ParseObject gameScore2 = ParseObject.create(classGameScore);
        gameScore2.put("playerName", "ZeroCool");
        gameScore2.put("score", 1338);
        
        // Run a batch to create the objects
        ParseBatch batch = ParseBatch.create();
        batch.addObject(gameScore1, ParseBatch.EBatchOpType.CREATE);
        batch.addObject(gameScore2, ParseBatch.EBatchOpType.CREATE);

        assertTrue(batch.execute(), "Batch operation should succeed");

        gameScore1.put("score", 999999);
        batch.addObject(gameScore1, ParseBatch.EBatchOpType.UPDATE);
        batch.addObject(gameScore2, ParseBatch.EBatchOpType.DELETE);
        
        assertTrue(batch.execute(), "Batch operation should succeed");
    }

    private void testValidBatch() throws ParseException {
        System.out.println("============== testValidBatch()");

        ArrayList<ParseObject> objects = new ArrayList<ParseObject>();
        for (int i = 0; i < MAX_BATCH_SIZE; ++i) {
            ParseObject gameScore = ParseObject.create(classGameScore);
            gameScore.put("batchNumber", (i + 1));
            objects.add(gameScore);
        }

        ParseBatch batch = ParseBatch.create();
        batch.addObjects(objects, ParseBatch.EBatchOpType.CREATE);

        assertTrue(batch.execute(), "Batch operation should succeed");

        // Check update of objects
        for (ParseObject object : objects) {
            assertNotNull(object.getObjectId(), "Saved object should have objectId");
            assertNotNull(object.getCreatedAt(), "Saved object should have creation date");
            assertEqual(object.getCreatedAt(), object.getUpdatedAt(),
                    "Updated date should equal creation date (parse4cn1 feature)");
        }
    }

    private void testValidMixedBatch() throws ParseException {
        System.out.println("============== testValidMixedBatch()");

        // Mix operations on objects of different classes
        ArrayList<ParseObject> objects = new ArrayList<ParseObject>();
        for (int i = 0; i < MAX_BATCH_SIZE / 4; ++i) {
            ParseObject object = ParseObject.create(classGameScore);
            object.put("batchNumber", (i + 1));
            objects.add(object);

            object = ParseObject.create(classPlayer);
            object.put("batchNumber", (i + 1));
            objects.add(object);

            object = ParseObject.create(classCar);
            object.put("batchNumber", (i + 1));
            objects.add(object);

            object = ParseObject.create(classKitchen);
            object.put("batchNumber", (i + 1));
            objects.add(object);
        }

        final ParseBatch batch = ParseBatch.create();
        // 1st 1/3 for creation, next 1/3 for update and rest for deletion
        final int createCount = objects.size() / 3;
        final int updateOffset = createCount;
        final int updateCount = createCount;
        final int deleteOffset = (updateOffset + updateCount);

        // Batch subset of objects for creation
        batch.addObjects(objects.subList(0, createCount),
                ParseBatch.EBatchOpType.CREATE);

        // Batch next subset for update
        for (int i = updateOffset; i < deleteOffset; ++i) {
            objects.get(i).save();
            objects.get(i).put("update", true);
            batch.addObject(objects.get(i), ParseBatch.EBatchOpType.UPDATE);
        }

        // Batch final subset for deletion
        for (int i = deleteOffset; i < objects.size(); ++i) {
            objects.get(i).save();
            batch.addObject(objects.get(i), ParseBatch.EBatchOpType.DELETE);
        }

        assertTrue(batch.execute(), "Mixed batch operation should succeed");
        assertTrue(batch.isSucceeded(),
                "Mixed batch operation isSucceeded should match execute result");

        // Check update of objects
        for (int i = 0; i < createCount; ++i) { // Create subset
            final ParseObject object = objects.get(i);
            assertNotNull(object.getObjectId(), "Saved object should have objectId");
            assertNotNull(object.getCreatedAt(), "Saved object should have creation date");
            assertEqual(object.getCreatedAt(), object.getUpdatedAt(),
                    "Updated date should equal creation date (parse4cn1 feature)");
        }

        for (int i = updateOffset; i < deleteOffset; ++i) { // Update subset
            final ParseObject object = objects.get(i);
            assertNotNull(object.getUpdatedAt(), "Updated object should have last update date");
            assertTrue(object.getCreatedAt().before(object.getUpdatedAt()),
                    "Updated date should be more recent than creation date");
        }

        for (int i = deleteOffset; i < objects.size(); ++i) { // Delete subset
            final ParseObject object = objects.get(i);
            assertNull(object.getObjectId(),
                    "Deleted object should have no objectId");
            assertNull(object.getCreatedAt(),
                    "Deleted object should have no creation date");
            assertNull(object.getUpdatedAt(),
                    "Deleted object should have no update date");
            assertTrue(object.keySet().isEmpty(),
                    "Deleted object should have no keys");
        }
    }

//    private void testBatchSizeExceedingLimit() throws ParseException {
//        System.out.println("============== testBatchSizeExceedingLimit()");
//
//        ParseBatch batch = ParseBatch.create();
//        for (int i = 0; i <= MAX_BATCH_SIZE * 2; ++i) {
//            ParseObject gameScore = ParseObject.create(classGameScore);
//            gameScore.put("batchNumber", (i + 1));
//            batch.addObject(gameScore, ParseBatch.EBatchOpType.CREATE);
//        }
//
//        try {
//            batch.execute();
//            fail("Executing batch with too many operations should fail. "
//                    + "Batch size = " + (MAX_BATCH_SIZE + 1)
//                    + "; expected limit = " + MAX_BATCH_SIZE);
//        } catch (ParseException ex) {
//            assertEqual(ParseException.TOO_MANY_COMMANDS_IN_BATCH_REQUEST,
//                    ex.getCode(), "Batch with too many operations should fail with code "
//                    + ParseException.TOO_MANY_COMMANDS_IN_BATCH_REQUEST
//                    + " but got code " + ex.getCode());
//        }
//    }

    private void testBatchIncludingFailures() throws ParseException {
        System.out.println("============== testBatchIncludingFailures()");

        ArrayList<ParseObject> objects = new ArrayList<ParseObject>();
        for (int i = 0; i < Math.min(MAX_BATCH_SIZE, 4); ++i) {
            ParseObject object = ParseObject.create(classGameScore);
            object.put("batchNumber", (i + 1));
            object.save();
            objects.add(object);
        }

        // Delete an object individually then try to delete again in batch (should fail)
        final String toBeDeletedObjectId = objects.get(0).getObjectId();
        ParseObject.fetch(classGameScore, toBeDeletedObjectId).delete();

        ParseBatch batch = ParseBatch.create();
        batch.addObjects(objects, ParseBatch.EBatchOpType.DELETE);
        assertFalse(batch.execute(), "At least one operation should fail");

        for (Entry<ParseObject, ParseException> entry : batch.getErrors().entrySet()) {
            if (entry.getKey().getObjectId() == null) {
                assertNull(entry.getValue(), "Reset object should have been deleted successfully");
            } else {
                assertEqual(toBeDeletedObjectId, entry.getKey().getObjectId());
                assertNotNull(entry.getValue(), "Double deletion should fail");
                assertEqual(ParseException.OBJECT_NOT_FOUND, entry.getValue().getCode());
            }
        }
    }

    private void testBatchWithInvalidObjects() throws ParseException {
        System.out.println("============== testBatchWithInvalidObjects()");
        
        ParseObject obj = ParseObject.create(classGameScore);
        obj.put("key", "value");
        
        try {
           ParseBatch.create().addObject(obj, ParseBatch.EBatchOpType.UPDATE);
           fail("Exception expected on attempt to batch update an object without an objectId");
        } catch (ParseException ex) {
            assertEqual("Cannot update or delete an object without an objectId.", 
                    ex.getMessage());
        }
        
        try {
           ParseBatch.create().addObject(obj, ParseBatch.EBatchOpType.DELETE);
           fail("Exception expected on attempt to batch delete an object without an objectId");
        } catch (ParseException ex) {
            assertEqual("Cannot update or delete an object without an objectId.", 
                    ex.getMessage());
        }
        
        obj.save();
        
        try {
           ParseBatch.create().addObject(obj, ParseBatch.EBatchOpType.CREATE);
           fail("Exception expected on attempt to batch create an object with an objectId");
        } catch (ParseException ex) {
            assertEqual("Cannot create an object already having an objectId.", 
                    ex.getMessage());
        }
    }
}
