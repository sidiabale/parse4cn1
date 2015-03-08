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

//package com.parse4cn1.operation;
//
//import org.json.JSONException;
//import com.parse4cn1.ParseObject;
//import com.parse4cn1.encode.ParseObjectEncodingStrategy;
//import com.parse4cn1.util.ParseEncoder;
//
//public class SetFieldOperation implements ParseFieldOperation {
//
//	private Object value;
//	
//	public SetFieldOperation(Object value) {
//		this.value = value;
//	}
//	
//	@Override
//	public Object apply(Object oldValue, ParseObject parseObject, String key) {
//		return value;
//	}
//
//	@Override
//	public Object encode(ParseObjectEncodingStrategy objectEncoder) throws JSONException {
//		
//		return ParseEncoder.encode(value, objectEncoder);
//		
//		/*
//		if(value instanceof byte[]) {
//			byte[] bytes = (byte[]) value;
//			JSONObject output = new JSONObject();
//			output.put("__type", "Bytes");
//			output.put("base64", Base64.encodeBase64String(bytes));
//			return output;				
//		}
//		
//		if(value instanceof Date) {
//			Date dt = (Date) value;
//			JSONObject output = new JSONObject();
//			output.put("__type", "Date");
//			output.put("iso", Parse.encodeDate(dt));
//			return output;			
//		}
//		
//		if(value instanceof Map) {
//	        Map<String, Object> map = (Map<String, Object>) value;
//	        JSONObject json = new JSONObject();
//	        for (String key : map.keySet()) {
//	          json.put(key, map.get(key));
//	        }
//	        return json;
//	      }		
//		
//		if(value instanceof ParseFile) {
//			ParseFile file = (ParseFile) value;
//			JSONObject output = new JSONObject();
//			output.put("__type", "File");
//			output.put("name", file.getName());
//			output.put("url", file.getUrl());
//			return output;	
//		}
//		
//		if(value instanceof ParseGeoPoint) {
//			ParseGeoPoint gp = (ParseGeoPoint) value;
//			JSONObject output = new JSONObject();
//			output.put("__type", "GeoPoint");
//			output.put("latitude", gp.getLatitude());
//			output.put("longitude", gp.getLongitude());
//			return output;	
//		}
//		
//		if(value instanceof ParseObject) {
//			ParseObject po = (ParseObject) value;
//			JSONObject output = new JSONObject();
//			output.put("__type", "Pointer");
//			output.put("className", po.getClassName());
//			output.put("objectId", po.getObjectId());
//			return output;	
//		}
//		
//		return value;
//		
//		*/
//	}
//
//}
