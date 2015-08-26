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
 *
 * @author Is
 */
public class ParseGeoPointTest extends BaseParseTest {

    @Override
    public boolean runTest() throws Exception {
        // TODO: Test other methods
        testSerialization();
        return true;
    }

    private void testSerialization() {
        ParseGeoPoint geoPoint = new ParseGeoPoint(-32.4, 110.23);
        final String id = "geoPoint";
        
        assertTrue(Storage.getInstance().writeObject(id, geoPoint));

        Storage.getInstance().clearCache(); // Absolutely necessary to force retrieval from storage
        final ParseGeoPoint retrieved = (ParseGeoPoint)Storage.getInstance().readObject(id);
        
        assertEqual(geoPoint.getLatitude(), retrieved.getLatitude());
        assertEqual(geoPoint.getLongitude(), retrieved.getLongitude());
    }
}
