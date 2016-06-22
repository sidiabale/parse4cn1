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

import com.codename1.io.Storage;

/**
 * Tests for the {@link ParseGeoPoint} class.
 */
public class ParseGeoPointTest extends BaseParseTest {
    
    private final String classPlaceObject = "PlaceObject";
    
    @Override
    public boolean runTest() throws Exception {
        testDistanceCalculations();
        testSerialization();
        testParseGeoPointConstraints(); // Not sure if it's wise to test constraints that might be lifted at any time...
        return true;
    }
    
    private void testParseGeoPointConstraints() throws ParseException {
        System.out.println("============== testParseGeoPointConstraints()");
        
        /*
        // [16-05-16] Lat./long. range constraints are no longer enforced
        //            although they're still mentioned in the API doc.
        //            https://parse.com/docs/rest/guide#geopoints-caveats
        checkLatLonConstraint(null, -91, 0, "Latitude must be in [-90, 90]: -91.0");
        checkLatLonConstraint(null, 91, 0, "Latitude must be in [-90, 90]: 91.0");
        checkLatLonConstraint(null, 0, 182, "Longitude must be in [-180, 180): 182.0");
        checkLatLonConstraint(null, 0, -187, "Longitude must be in [-180, 180): -187.0");
        checkLatLonConstraint(new ParseGeoPoint(), -93, 0, "Latitude must be in [-90, 90]: -93.0");
        checkLatLonConstraint(new ParseGeoPoint(), 0, 181, "Longitude must be in [-180, 180): 181.0");
        */
        
        checkMultipleGeoPointsInParseObjectNotAllowed();
    }
    
    private void testDistanceCalculations() {
        System.out.println("============== testDistanceCalculations()");
        
        final ParseGeoPoint p1 = new ParseGeoPoint(20, -50);
        final ParseGeoPoint p2 = new ParseGeoPoint(-30, 10);
        final int meanEarthRadiusKm = 6371;
        final double roundedDistanceKm = 8490.397767799224;
        final double roundedDistanceMiles = roundedDistanceKm * 0.621371;
        final double roundedDistanceRad = roundedDistanceKm / meanEarthRadiusKm;

        assertEqual(p1.distanceInKilometersTo(p2), p2.distanceInKilometersTo(p1));
        assertEqual(p1.distanceInMilesTo(p2), p2.distanceInMilesTo(p1));
        assertEqual(p1.distanceInRadiansTo(p2), p2.distanceInRadiansTo(p1));
        
        assertEqual(roundedDistanceRad, p1.distanceInRadiansTo(p2));
        assertEqual((int)roundedDistanceMiles, (int)p1.distanceInMilesTo(p2));
        assertEqual((int)roundedDistanceKm, (int)p1.distanceInKilometersTo(p2));
    }

    private void testSerialization() {
        System.out.println("============== testSerialization()");
        ParseGeoPoint geoPoint = new ParseGeoPoint(-32.4, 110.23);
        final String id = "geoPoint";
        
        assertTrue(Storage.getInstance().writeObject(id, geoPoint));

        Storage.getInstance().clearCache(); // Absolutely necessary to force retrieval from storage
        final ParseGeoPoint retrieved = (ParseGeoPoint)Storage.getInstance().readObject(id);
        
        assertEqual(geoPoint.getLatitude(), retrieved.getLatitude());
        assertEqual(geoPoint.getLongitude(), retrieved.getLongitude());
    }
    
    private void checkLatLonConstraint(final ParseGeoPoint aPoint, 
            final double aLatitude, final double aLongitude, final String aErrorMsg) throws ParseException { 
        deleteObjects(classPlaceObject);
        final ParseObject parseObject = ParseObject.create(classPlaceObject);
        
        Exception e = null;
        try {
            ParseGeoPoint point = aPoint;
            if (point == null) {
                point = new ParseGeoPoint(aLatitude, aLongitude);
            }

            point.setLatitude(aLatitude);
            point.setLongitude(aLongitude);
            
            parseObject.put("location", point);
            parseObject.save();

        } catch (ParseException ex) {
            e = ex;
        } finally {
            if (parseObject.getObjectId() != null) {
                parseObject.delete();
            }
        }
        
        assertNotNull(e, "Expected exception did not occur");
        assertEqual(e.getMessage(), aErrorMsg);
    }

    private void checkMultipleGeoPointsInParseObjectNotAllowed() throws ParseException {
        final ParseObject parseObject = ParseObject.create(classPlaceObject);

        Exception e = null;
        try {
            ParseGeoPoint point1 = new ParseGeoPoint(10, -10);
            ParseGeoPoint point2 = new ParseGeoPoint(10, -15);

            parseObject.put("home", point1);
            parseObject.put("work", point2);
            parseObject.save();

        } catch (ParseException ex) {
            e = ex;
        } finally {
            if (parseObject.getObjectId() != null) {
                parseObject.delete();
            }
        }

        assertNotNull(e, "Expected exception did not occur");
        assertTrue("There can only be one geopoint field in a class".equalsIgnoreCase(e.getMessage())
            || "Currently, only one GeoPoint field may exist in an object. Adding work when home already exists.".equalsIgnoreCase(e.getMessage()));
    }
}
