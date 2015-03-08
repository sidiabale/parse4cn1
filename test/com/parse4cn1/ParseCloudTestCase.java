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
//import com.parse4cn1.ParseException;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//
//import java.util.HashMap;
//
//import org.junit.Test;
//import com.parse4cn1.callback.FunctionCallback;
//
//public class ParseCloudTestCase extends Parse4JTestCase {
//
//	/*
//		Parse.Cloud.define("helloWorld", function(request, response) {
//		  response.success("Hello, " + request.params.name + "!!!");
//		});
//	
//		Parse.Cloud.define("Multiply", function(request, response) {
//		  response.success(request.params.A * request.params.B);
//		});
//	
//		Parse.Cloud.define("ForcedError", function(request, response) {
//		  response.error("forced error");
//		});
//	*/
//	
//	@Test
//	public void testInvalidFunction() {
//		System.out.println("InvalidFunction(): initializing...");
//		try {
//			HashMap<String, String> params = new HashMap<String, String>();
//			params.put("name", "Parse");
//			String result = ParseCloud.callFunction("InvalidFunction", params);
//			assertEquals("Hello, Parse!!!", result);
//		}
//		catch(ParseException pe) {
//			assertEquals(ParseException.CLOUD_ERROR, pe.getCode());
//		}
//	}	
//	
//	@Test
//	public void testForcedError() {
//		System.out.println("testForcedError(): initializing...");
//		try {
//			HashMap<String, String> params = new HashMap<String, String>();
//			params.put("name", "Parse");
//			String result = ParseCloud.callFunction("ForcedError", params);
//			assertEquals("Hello, Parse!!!", result);
//		}
//		catch(ParseException pe) {
//			assertEquals(ParseException.CLOUD_ERROR, pe.getCode());
//		}
//	}	
//	
//	@Test
//	public void testHelloWorld() {
//		System.out.println("testHelloWorld(): initializing...");
//		try {
//			HashMap<String, String> params = new HashMap<String, String>();
//			params.put("name", "Parse");
//			String result = ParseCloud.callFunction("helloWorld", params);
//			assertEquals("Hello, Parse!!!", result);
//		}
//		catch(ParseException pe) {
//			assertNull("testHelloWorld(): should not have thrown ParseException", pe);
//		}
//	}
//	
//	@Test
//	public void testMultiply() {
//		System.out.println("testMultiply(): initializing...");	
//		try {
//			HashMap<String, Integer> params = new HashMap<String, Integer>();
//			params.put("A", 12);
//			params.put("B", 4);
//			Integer result = ParseCloud.callFunction("Multiply", params);
//			assertEquals("48", result.toString());
//		}
//		catch(ParseException pe) {
//			assertNull("testMultiply(): should not have thrown ParseException", pe);
//		}		
//		
//	}
//	
//	@Test
//	public void testCallInBackground() {
//		System.out.println("testCallInBackground(): initializing...");	
//		
//		HashMap<String, Integer> params = new HashMap<String, Integer>();
//		params.put("A", 12);
//		params.put("B", 4);
//		ParseCloud.callFunctionInBackground("Multiply", params, new FunctionCallback<Integer>() {
//
//			@Override
//			public void done(Integer result, ParseException parseException) {
//				assertEquals("48", result.toString());
//				
//			}
//			
//		});	
//		sleep(2000);
//	}
//	
//}
