//package com.parse4cn1.operation;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import com.parse4cn1.ParseObject;
//import com.parse4cn1.encode.ParseObjectEncodingStrategy;
//
//public class DeleteFieldOperation implements ParseFieldOperation {
//
//	@Override
//	public Object apply(Object oldValue, ParseObject paramParseObject, String key) {
//		return null;
//	}
//
//	@Override
//	public Object encode(ParseObjectEncodingStrategy objectEncoder)
//			throws JSONException {
//		JSONObject output = new JSONObject();
//	    output.put("__op", "Delete");
//	    return output;
//	}
//
//}
