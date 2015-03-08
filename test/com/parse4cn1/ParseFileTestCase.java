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
//import com.parse4cn1.ParseFile;
//import com.parse4cn1.ParseException;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import junit.framework.Assert;
//
//import org.junit.Test;
//import com.parse4cn1.callback.ProgressCallback;
//
//public class ParseFileTestCase extends Parse4JTestCase {
//
//	@Test
//	public void uploadTxt() {
//		System.out.println("uploadTxt(): initializing...");
//		try {
//			byte[] data = "Working at Parse is great!".getBytes();
//			ParseFile file = new ParseFile("resume.txt", data);
//			file.save();
//			testParseFile(file);
//		}
//		catch(ParseException pe) {
//			assertNull("uploadTxt(): should not have thrown ParseException", pe);
//		}
//	}
//	
//	@Test
//	public void uploadPng() {
//		System.out.println("uploadPng(): initializing...");	
//		assertNotNull("Test file missing", getClass().getResource("/parse.png"));
//		
//		try {
//			byte[] data = getBytes("/parse.png");
//			ParseFile file = new ParseFile("parse.png", data);
//			file.save();
//			testParseFile(file);
//		}
//		catch(ParseException pe) {
//			assertNull("uploadTxt(): should not have thrown ParseException", pe);
//		}		
//	}
//	
//	@Test
//	public void uploadDoc() {
//		System.out.println("uploadDoc(): initializing...");
//		try {
//			byte[] data = getBytes("/parse.docx");
//			ParseFile file = new ParseFile("parse.docx", data);
//			file.save();
//			testParseFile(file);
//		}
//		catch(ParseException pe) {
//			assertNull("uploadDoc(): should not have thrown ParseException", pe);
//		}		
//	}
//	
//	@Test
//	public void uploadExr() {
//		System.out.println("uploadExr(): initializing...");
//		try {
//			byte[] data = getBytes("/parse.exr");
//			ParseFile file = new ParseFile("parse.exr", data);
//			file.save();
//			testParseFile(file);
//		}
//		catch(ParseException pe) {
//			assertNull("uploadExr(): should not have thrown ParseException", pe);
//		}		
//	}
//	
//	@Test
//	public void uploadPdf() {
//		System.out.println("uploadPdf(): initializing...");
//		try {
//			byte[] data = getBytes("/parse.pdf");
//			ParseFile file = new ParseFile("parse.pdf", data);
//			file.save();
//			testParseFile(file);
//		}
//		catch(ParseException pe) {
//			assertNull("uploadPdf(): should not have thrown ParseException", pe);
//		}		
//	}
//	
//	public void uploadPdfWithProgressCallback() {
//		System.out.println("uploadPdf(): initializing...");
//		try {
//			byte[] data = getBytes("/ml-1m.zip");
//			ParseFile file = new ParseFile("ml-1m.zip", data);
//			file.save(new ProgressCallback() {
//				
//				@Override
//				public void done(Integer percentDone) {
//					System.out.println("uploadPdf(): progress " + percentDone + "%");
//				}
//			});
//			sleep(2000);
//			testParseFile(file);
//		}
//		catch(ParseException pe) {
//			assertNull("uploadPdfWithProgressCallback(): should not have thrown ParseException", pe);
//		}		
//	}
//	
//	@Test
//	public void uploadDocAndGetData() {
//		System.out.println("uploadDocAndGetData(): initializing...");
//		try {
//			byte[] data = getBytes("/parse.docx");
//			ParseFile file = new ParseFile("parse.docx", data);
//			file.save();
//			testParseFile(file);
//			
//			ParseFile getFile = new ParseFile(file.getName(), file.getUrl());
//			byte[] getData = getFile.getData();
//			System.out.println("uploadDocAndGetData(): data.length: " + data.length);
//			System.out.println("uploadDocAndGetData(): getData.length: " + getData.length);
//			assertEquals(data.length, getData.length);
//		}
//		catch(ParseException pe) {
//			assertNull("uploadDocAndGetData(): should not have thrown ParseException", pe);
//		}		
//	}
//	
//	public void testParseFile(ParseFile file) {
//		System.out.println("Name: " + file.getName());
//		assertNotNull("name should not be null", file.getName());
//		System.out.println("File: " + file.getUrl());
//		assertNotNull("url should not be null", file.getUrl());
//	}
//	
//
//	
//	
//}
