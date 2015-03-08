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
//import com.parse4cn1.Parse;
//import com.parse4cn1.ParseException;
//import com.parse4cn1.ParseExecutor;
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//
//public class Parse4JTestCase {
//	
//	public static String CLASS_NAME = "parse4j";
//	private static String APP_ID = "RWjFpDDbwCIXF8Gy9dHEBpR7Fs2PZ0UzcNdxhAvf";
//	private static String APP_REST_API_ID = "EWpTGoOFgGr9vXfPLBRYZjhDL0pg4MQ1F7i3wWAq";
//
//	@BeforeClass
//	public static void setupParse() {
//		System.out.println("setupParse(): initializing...");
//		Parse.initialize(APP_ID, APP_REST_API_ID);
//	}
//	
//	@AfterClass
//	public static void tearDown() {
//		System.out.println("tearDown(): finalizing...");
//		ParseExecutor.getExecutor().shutdown();
//		//while(!ParseExecutor.getExecutor().isTerminated()) { }
//	}
//	
//	protected ParseObject getEmptyParseObject(String className) {
//		ParseObject parseObject = new ParseObject(className);
//		return parseObject;
//	}
//	
//	protected ParseObject getParseObject(String className) {
//		ParseObject parseObject = new ParseObject(className);
//		parseObject.put("name", "parse developer");
//		parseObject.put("int", 10);
//		parseObject.put("long", 10l);
//		parseObject.put("boolean", true);
//		parseObject.put("dob", new Date());
//		parseObject.put("double", 10.5);
//		
//		List<String> types = new ArrayList<String>();
//		types.add("type1");
//		types.add("type2");
//		types.add("type3");
//		parseObject.put("types", types);
//		
//		ParseGeoPoint gp  = new ParseGeoPoint(40.0, -30.0);
//		parseObject.put("location", gp);
//		
//		return parseObject;
//	}
//	
//	protected ParseUser getParseUser(String number) {
//		ParseUser parseUser = new ParseUser();
//		parseUser.setUsername("parse4j-user" + number);
//		parseUser.setPassword("parse4j-password");
//		parseUser.setEmail("parse4j-email"+number+"@gmail.com");
//		parseUser.put("dob", new Date());
//		parseUser.put("city", "westbury");
//		parseUser.put("state", "ny");
//		return parseUser;
//	}
//	
//	protected void sleep(int millis) {
//		try {
//			Thread.sleep(millis);
//		}
//		catch(InterruptedException e) {
//			
//		}
//	}
//	
//	public byte[] getBytes(String fileName) throws ParseException {
//		try {
//			RandomAccessFile f = new RandomAccessFile(getClass().getResource(fileName).getFile(), "r");
//			byte[] b = new byte[(int)f.length()];
//			f.read(b);
//			f.close();
//			return b;
//		}
//		catch(IOException e) {
//			throw new ParseException(e);
//		}
//	}
//	
//}
