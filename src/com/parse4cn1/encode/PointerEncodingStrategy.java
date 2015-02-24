package com.parse4cn1.encode;

import org.json.JSONObject;
import com.parse4cn1.ParseObject;

public class PointerEncodingStrategy extends PointerOrLocalIdEncodingStrategy {

	private static final PointerEncodingStrategy instance = new PointerEncodingStrategy();

	public static PointerEncodingStrategy get() {
		return instance;
	}

	public JSONObject encodeRelatedObject(ParseObject object) {
		if (object.getObjectId() == null) {
			throw new IllegalStateException(
					"unable to encode an association with an unsaved ParseObject");
		}
		return super.encodeRelatedObject(object);
	}

}
