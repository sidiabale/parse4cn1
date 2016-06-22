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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author sidiabale
 */
public class ParseConfigTest extends BaseParseTest {
    
    @Override
    public boolean runTest() throws Exception {
        testParseConfig(ParseConfig.getInstance());
        testParseConfig(ParseConfig.getInstance().refresh());
        return true;
    }
    
    private void testParseConfig(final ParseConfig config) throws ParseException {
        // Check pre-defined config values
        assertNotNull(config.getParseFile("backgroundImage"));
        
        // Parse Server version 2.2.7 <= v <= 2.2.13 doesn't save files and geo points correctly
        // Hence the following tests will fail for any of the specified versions.
        // However, they pass on Parse.com and Parse Server version 2.2.6
        // See also: https://github.com/ParsePlatform/parse-server/issues/2103
        assertTrue(config.getParseFile("backgroundImage").getName().endsWith("Tulips.jpg"));

        assertEqual(37.79215, 
            config.getParseGeoPoint("eventLocation").getLatitude());
        assertEqual(-122.390335, 
            config.getParseGeoPoint("eventLocation").getLongitude());

        assertEqual(config.getList("betaTestUserIds"), 
                Arrays.asList("2TWipjNjOQ", "80S3HiJ1iZ", "pcjSHaYtaA"));
        assertTrue(config.getBoolean("configSetup"));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2015);
        cal.set(Calendar.MONTH, 4); // = May; month is 0-based.
        cal.set(Calendar.DAY_OF_MONTH, 16);
        cal.set(Calendar.HOUR_OF_DAY, 18);
        cal.set(Calendar.MINUTE, 28);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        assertEqual(0, cal.getTime().compareTo(config.getDate("lastUpdate")));
        assertEqual("Have fun!", config.getString("welcomeMessage"));
        assertEqual(42, config.getInt("winningNumber").intValue());
        final Map data = (Map) config.get("data");
        assertEqual(3, data.keySet().size());
        assertEqual("value1", data.get("key1"));
        assertEqual(true, data.get("key2"));
        assertEqual(5, data.get("key3"));
        assertEqual("{select={query={className=players, where={games={exists=true}}}, key=username}}",
                config.get("compoundObject").toString());
    }
    
    /*
        {
	"params": {
		"backgroundImage": {
			"__type": "File",
			"name": "tfss-0086e03d-660f-4a53-bee1-6dfb0e1d6906-Tulips.jpg",
			"url": "http://files.parsetfss.com/9499f1cb-d3b8-4514-aa10-05659741bacc/tfss-0086e03d-660f-4a53-bee1-6dfb0e1d6906-Tulips.jpg"
		},
		"betaTestUserIds": ["2TWipjNjOQ",
		"80S3HiJ1iZ",
		"pcjSHaYtaA"],
		"configSetup": true,
		"data": {
			"key1": "value1",
			"key2": true,
			"key3": 5
		},
		"eventLocation": {
			"__type": "GeoPoint",
			"latitude": 37.79215,
			"longitude": -122.390335
		},
		"lastUpdate": {
			"__type": "Date",
			"iso": "2015-05-17T18:28:03.721Z"
		},
		"welcomeMessage": "Have fun!",
		"winningNumber": 42
            }
        }
    */
    
    private void testRefresh() {
        
    }
}
                    
