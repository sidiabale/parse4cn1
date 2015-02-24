package com.parse4cn1.encode;

import org.json.JSONObject;
import com.parse4cn1.ParseObject;

public interface ParseObjectEncodingStrategy {
	
	public abstract JSONObject encodeRelatedObject(ParseObject parseObject);

}
