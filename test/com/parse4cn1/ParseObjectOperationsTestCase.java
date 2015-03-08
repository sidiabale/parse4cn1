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
//import static org.junit.Assert.assertFalse;
//
//import org.junit.Test;
//import com.parse4cn1.util.MimeType;
//
//public class ParseObjectOperationsTestCase extends Parse4JTestCase {
//
//	@Test(expected = IllegalArgumentException.class )
//	public void putNullKey() {
//		System.out.println("putNullKey(): initializing...");
//		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
//		parseObject.put(null, "parse developer");
//	}
//	
//	@Test(expected = IllegalArgumentException.class )
//	public void putNullValue() {
//		System.out.println("putNullValue(): initializing...");
//		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
//		parseObject.put("name", null);
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void incrementString() {
//		System.out.println("incrementString(): initializing...");
//		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
//		parseObject.put("field", "value");
//		parseObject.increment("field");
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void decrementString() {
//		System.out.println("decrementString(): initializing...");
//		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
//		parseObject.put("field", "value");
//		parseObject.decrement("field");
//	}
//	
//	@Test
//	public void incrementNumbers() {
//		System.out.println("incrementNumbers(): initializing...");
//		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
//		parseObject.put("field1", 1);
//		parseObject.increment("field1");
//		parseObject.put("field2", 2L);
//		parseObject.increment("field2");
//		parseObject.put("field3", 3.3);
//		parseObject.increment("field3");
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void invalidPutKey1() {
//		System.out.println("invalidPutKey1(): initializing...");
//		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
//		parseObject.put("objectId", "value");
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void invalidPutKey2() {
//		System.out.println("invalidPutKey2(): initializing...");
//		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
//		parseObject.put("createdAt", "value");
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void invalidPutKey3() {
//		System.out.println("invalidPutKey3(): initializing...");
//		ParseObject parseObject = getEmptyParseObject(CLASS_NAME);
//		parseObject.put("updatedAt", "value");
//	}
//	
//	@Test
//	public void extension() {
//		System.out.println("extension(): initializing...");		
//		
//		for(String extension : MimeType.mimeTypes.keySet()) {
//			String fileName = "test." + extension;
//			//System.out.println("File name:" + fileName);
//			assertEquals("Expected " + MimeType.getFileExtension(fileName), MimeType.getFileExtension(fileName), extension);
//		}
//		
//		String fileName = "test";
//		//System.out.println("File name:" + fileName);
//		assertEquals("Expected " + MimeType.getFileExtension(fileName), MimeType.getFileExtension(fileName), "");
//	}
//	
//	@Test
//	public void extensionNotEqual() {	
//		System.out.println("extensionNotEqual(): initializing...");	
//		
//		for(String extension : MimeType.mimeTypes.keySet()) {
//			String fileName = "test." + extension;
//			//System.out.println("File name:" + fileName + ", testing against: " + (extension+"x"));
//			boolean result = (extension+"x").equals(MimeType.getFileExtension(fileName));
//			assertFalse(result);
//		}
//	}
//	
//	@Test
//	public void mimeType() {
//		System.out.println("mimeType(): initializing...");
//		
//		for(String extension : MimeType.mimeTypes.keySet()) {
//			String fileName = "test." + extension;
//			//System.out.print("File name:" + fileName);
//			String mime = MimeType.getMimeType(MimeType.getFileExtension(fileName));
//			//System.out.println(", content-type: " + mime);
//			assertEquals("Expected " + MimeType.getMimeType(extension), MimeType.getMimeType(extension), mime);
//		}
//		
//		String fileName = "test";
//		//System.out.print("File name:" + fileName);
//		String mime = MimeType.getMimeType(MimeType.getFileExtension(fileName));
//		String extension = MimeType.getFileExtension(fileName);
//		//System.out.println(", content-type: " + mime);
//		assertEquals("Expected " + MimeType.getMimeType(extension), MimeType.getMimeType(extension), mime);
//
//	}
//	
//	@Test(expected = IllegalArgumentException.class)
//	public void testFileNotSave() {
//		System.out.println("testFileNotSave(): initializing...");
//		try {
//			byte[] data = getBytes("/parse.png");
//			ParseFile file = new ParseFile("parse.png", data);
//			ParseObject po = getParseObject(CLASS_NAME);
//			po.put("logo", file);
//		}
//		catch(ParseException pe) {
//			
//		}
//	}
//	
//}
