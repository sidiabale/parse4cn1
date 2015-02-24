package com.parse4cn1.operation;

import org.json.JSONException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.encode.ParseObjectEncodingStrategy;

public interface ParseFieldOperation {
	
	abstract Object apply(Object oldValue, ParseObject parseObject, String key);
	
	abstract Object encode(ParseObjectEncodingStrategy objectEncoder) throws JSONException;

}
