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
 *
 * Original implementation adapted from Thiago Locatelli's Parse4J project
 * (see https://github.com/thiagolocatelli/parse4j)
 */

//package com.parse4cn1;
//
//import com.parse4cn1.ParseAnalytics;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Test;
//
//public class ParseAnalyticsTestCase extends Parse4JTestCase {
//
//	
//	@Test
//	public void trackAppOpened() {
//		System.out.println("trackAppOpened(): initializing...");
//		ParseAnalytics.trackAppOpened();
//		sleep(1000);
//	}
//
//	@Test
//	public void trackJunitTest() {
//		System.out.println("trackJunitTest(): initializing...");
//		ParseAnalytics.trackEvent("JUnitTestStarted");
//		sleep(1000);
//	}	
//	
//	@Test
//	public void trackJunitTest2() {
//		System.out.println("trackJunitTest2(): initializing...");
//		Map<String, String> dimensions = new HashMap<String, String>();
//		dimensions.put("attr1", "10");
//		dimensions.put("attr2", "127.0.0.1");
//		ParseAnalytics.trackEvent("JUnitTestStarted");
//		sleep(1000);
//	}	
//
//}
