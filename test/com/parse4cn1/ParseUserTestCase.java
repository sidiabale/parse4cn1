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
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//
//import org.junit.Test;
//
//public class ParseUserTestCase extends Parse4JTestCase {
//
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void signupNoUsername() {
//		System.out.println("signupNoUsername(): initializing...");
//		
//		ParseUser parseUser = new ParseUser();
//		try {
//			parseUser.signUp();
//		}
//		catch(ParseException e) {
//			assertNull("ParseException should be null", e);
//		}
//		
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void signupNoPassword() {
//		System.out.println("signupNoPassword(): initializing...");
//		
//		ParseUser parseUser = new ParseUser();
//		try {
//			parseUser.setUsername("parse4j-user");
//			parseUser.signUp();
//		}
//		catch(ParseException e) {
//			assertNull("ParseException should be null", e);
//		}
//		
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void signupWithObjectId() {
//		System.out.println("signupWithObjectId(): initializing...");
//		
//		ParseUser parseUser = new ParseUser();
//		try {
//			parseUser.setUsername("parse4j-user");
//			parseUser.setPassword("parse4j-password");
//			parseUser.setObjectId("tempObjectId");
//			parseUser.signUp();
//		}
//		catch(ParseException e) {
//			assertNull("ParseException should be null", e);
//		}
//		
//	}	
//	
//	@Test
//	public void signup()  {
//		System.out.println("signup(): initializing...");
//			
//		try {
//			ParseUser parseUser = getParseUser("1");
//			parseUser.signUp();
//			assertNotNull("objectId should not be null", parseUser.getObjectId());
//			assertNotNull("createdAt should not be null", parseUser.getCreatedAt());
//			assertNotNull("sessionToken should not be null", parseUser.getSessionToken());
//		}
//		catch(ParseException e) {
//			assertNull("signup(): should not have thrown ParseException", e);
//		}
//		
//	}	
//	
//	@Test(expected = ParseException.class)
//	public void signupExistingUsername() throws ParseException {
//		System.out.println("signupExistingUsername(): initializing...");
//			
//		try {
//			
//			ParseUser parseUser = getParseUser("2");
//			parseUser.signUp();
//			
//			parseUser = getParseUser("2");
//			parseUser.signUp();
//			
//			assertNotNull("objectId should not be null", parseUser.getObjectId());
//			assertNotNull("createdAt should not be null", parseUser.getCreatedAt());
//			assertNotNull("sessionToken should not be null", parseUser.getSessionToken());
//		}
//		catch(ParseException e) {
//			throw e;
//		}
//		
//	}	
//	
//	@Test
//	public void login()  {
//		System.out.println("login(): initializing...");
//			
//		try {
//			
//			ParseUser pu = getParseUser("3");
//			pu.signUp();
//			
//			ParseUser parseUser =  ParseUser.login(pu.getUsername(), "parse4j-password");
//			System.out.println(parseUser.getString("city"));
//			System.out.println(parseUser.getString("state"));
//			System.out.println(parseUser.getDate("dob"));
//			assertNotNull("objectId should not be null", parseUser.getObjectId());
//			assertNotNull("createdAt should not be null", parseUser.getCreatedAt());
//			assertNotNull("sessionToken should not be null", parseUser.getSessionToken());
//		}
//		catch(ParseException e) {
//			assertNull("login(): should not have thrown ParseException", e);
//		}
//		
//	}	
//	
//	@Test
//	public void verifyEmail()  {
//		System.out.println("verifyEmail(): initializing...");
//			
//		try {
//			ParseUser pu = getParseUser("4");
//			pu.signUp();
//			ParseUser.requestPasswordReset(pu.getEmail());
//		}
//		catch(ParseException e) {
//			assertNull("verifyEmail(): should not have thrown ParseException", e);
//		}
//		
//	}	
//	
//	@Test(expected = ParseException.class)
//	public void verifyInvalidEmail() throws ParseException  {
//		System.out.println("verifyEmail(): initializing...");
//			
//		try {	
//			ParseUser.requestPasswordReset("invalid@gamil.com");
//		}
//		catch(ParseException e) {
//			throw e;
//		}
//		
//	}
//	
//	
//
//}
