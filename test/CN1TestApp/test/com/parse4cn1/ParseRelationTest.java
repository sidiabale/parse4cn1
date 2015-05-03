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
import com.parse4cn1.encode.ParseObjectEncodingStrategy;
import com.parse4cn1.encode.PointerEncodingStrategy;
import com.parse4cn1.operation.ParseOperation;
import com.parse4cn1.operation.RelationOperation;
import com.parse4cn1.operation.RelationOperation.ERelationType;
import static com.parse4cn1.operation.RelationOperation.ERelationType.AddRelation;

/**
 *
 * @author sidiabale
 */
public class ParseRelationTest extends BaseParseTest {

    private final ParseObjectEncodingStrategy encoder = new PointerEncodingStrategy();
    private final String targetClass = "Contributor";
    private final String parentKey = "contributors";
    private final ParseObject report = new ParseObject("Report") {

            @Override
            void performOperation(String key, ParseOperation operation) {
                super.performOperation(key, operation);
                if (parentKey.equals(key)) {
                    parseOperation = operation;
                }
            }

        };
    private ParseOperation parseOperation = null;

    @Override
    public boolean runTest() throws Exception {
        testCreateFromJsonObject();
        testAddOrRemove();
        testAddOrRemoveNullObject();
        testQuery();
        testIllegalState();
        return true;
    }

    private void testAddOrRemove() throws ParseException, JSONException {
        ParseObject contributor1 = ParseObject.create(targetClass);
        contributor1.setObjectId("Contributor1");

        ParseObject contributor2 = ParseObject.create(targetClass);
        contributor2.setObjectId("Contributor2");

        ParseObject contributor3 = ParseObject.create(targetClass);
        contributor3.setObjectId("Contributor3");

        ParseRelation<ParseObject> relation = new ParseRelation<ParseObject>(
                report, parentKey, null);
        assertNull(relation.getTargetClass(), "Target class is null initially");

        relation.add(contributor1);
        relation.add(contributor2);
        relation.add(contributor3);
        assertEqual(targetClass, relation.getTargetClass(),
                "Target class is initialized on first add/remove call");

        int count = 3;
        checkParseRelation(relation, count, "Elements are added to objects field");
        checkRelationInParent(report, count, parentKey, RelationOperation.ERelationType.AddRelation);

        relation.add(contributor1);
        checkParseRelation(relation, count, "Duplicate add request is ignored");

        relation.remove(contributor1);
        --count;

        checkParseRelation(relation, count, "Element is removed from objects field");
        // Removing should replace the add operation so count = 1 = # to remove
        checkRelationInParent(report, 1, parentKey, RelationOperation.ERelationType.RemoveRelation);

        relation.remove(contributor1);
        checkParseRelation(relation, count, "Duplicate remove request is ignored");

        relation.remove(contributor3);
        checkRelationInParent(report, 2 /* count in RemoveRelation */,
                parentKey, RelationOperation.ERelationType.RemoveRelation);

        assertEqual(targetClass, relation.getTargetClass(),
                "Target class is not changed by valid add/remove calls");
    }

    private void testAddOrRemoveNullObject() {
        ParseRelation<ParseObject> relation = new ParseRelation<ParseObject>(null, null, null);
        boolean exceptionOccurred = false;
        try {
            relation.add(null);
            relation.remove(null);
        } catch (IllegalArgumentException ex) {
            exceptionOccurred = true;
        }

        assertTrue(exceptionOccurred, "Adding or removing a null object should cause an exception");
    }

    private void testCreateFromJsonObject() throws JSONException {
        JSONObject relationObject = new JSONObject();
        relationObject.put(ParseConstants.KEYWORD_TYPE, "Relation");
        
        ParseRelation<ParseObject> relation;
        try {
            relation = new ParseRelation<ParseObject>(relationObject);
            fail("An exception should occur if the JSON data has no className field");
        } catch (IllegalArgumentException ex) {
            assertTrue(ex.getMessage().startsWith("A target class must be specified"));
        }
        
        relationObject.put(ParseConstants.FIELD_CLASSNAME, targetClass);
        relation = new ParseRelation<ParseObject>(relationObject);
        assertEqual(targetClass, relation.getTargetClass(), 
                "Target class is read from className field of JSON data");
    }

    private void testQuery() throws ParseException, JSONException {
       ParseObject contributor = ParseObject.create(targetClass);
       contributor.setObjectId("Contributor");
       report.setObjectId(getCurrentTimeInHex());
       
       ParseRelation<ParseObject> relation = 
               new ParseRelation<ParseObject>(report, parentKey, targetClass);
       final JSONObject query = relation.getQuery().encode();
       System.out.println("ParseRelation query: " + query);
       
       final JSONObject where = query.getJSONObject("where");
       assertEqual(1, where.length(), "Query's where clause contains a single element");
       
       final JSONObject relatedTo = where.getJSONObject("$relatedTo");
       assertEqual(parentKey, relatedTo.getString("key"), 
               "Key in query is the parent key");
       
       final JSONObject object = relatedTo.getJSONObject("object");
       assertEqual("Pointer", object.getString(ParseConstants.KEYWORD_TYPE),
               "__op type is pointer");
       assertEqual(report.getClassName(), object.getString(ParseConstants.FIELD_CLASSNAME),
               "Query class name is parent's class name");
       assertEqual(report.getObjectId(), object.getString(ParseConstants.FIELD_OBJECT_ID),
               "Query object id is parent's object id");
    }

    private void testIllegalState() {
        ParseObject contributor = ParseObject.create("IncorrectClass");
        contributor.setObjectId("Contributor");
        
        ParseRelation<ParseObject> relation;
        
        relation = new ParseRelation<ParseObject>(report, parentKey, targetClass);
        assertEqual(targetClass, relation.getTargetClass(), "Target class is initialized");
        
        try {
            relation.add(contributor);
            fail("Expected exception on target class mismatch");
        } catch (IllegalStateException ex) {
            assertTrue(ex.getMessage().startsWith("Target class mismatch"));
        }
        
        contributor = ParseObject.create(targetClass);
        contributor.setObjectId("Contributor");
        
        relation = new ParseRelation<ParseObject>(null, parentKey, null);
        try {
            relation.add(contributor);
            fail("Expected exception on null parent");
        } catch (IllegalStateException ex) {
            assertTrue(ex.getMessage().equals("Parent ParseObject is null"));
        }
        
        relation = new ParseRelation<ParseObject>(report, null, null);
        try {
            relation.add(contributor);
            fail("Expected exception on null key");
        } catch (IllegalStateException ex) {
            assertTrue(ex.getMessage().equals("Relation key is null"));
        }
    }

    private void checkRelationInParent(final ParseObject parent,
            int expectedCount, final String key,
            final ERelationType relationType) throws ParseException, JSONException {

        ParseRelation<ParseObject> retrievedRelation = parent.getRelation(key);
        assertNotNull(retrievedRelation, "Relation should be defined on parent after adding/removing");
        assertTrue(parseOperation instanceof RelationOperation, "Expect RelationOperation");
        JSONObject encoded = ((RelationOperation) parseOperation).encode(encoder);
        System.out.println("JSON from RelationOperation: " + encoded);

        assertEqual(expectedCount, encoded.getJSONArray("objects").length(),
                "Number of elements in encoded JSON should match expectedCount");
        assertEqual((relationType == AddRelation) ? "AddRelation" : "RemoveRelation",
                encoded.getString(ParseConstants.KEYWORD_OP),
                "__op field should match relation operation type");
    }

    private void checkParseRelation(final ParseRelation<?> relation, int expectedCount,
            final String message) throws JSONException {
        JSONObject encoded = relation.encode(encoder);
        System.out.println("JSON from ParseRelation: " + encoded);

        assertEqual(3, encoded.length(), "Encoded object should contain only two elements");
        assertEqual(targetClass, encoded.getString(ParseConstants.FIELD_CLASSNAME));
        assertEqual("Relation", encoded.getString(ParseConstants.KEYWORD_TYPE));
        assertEqual(expectedCount, encoded.getJSONArray("objects").length(), message);
    }
}
