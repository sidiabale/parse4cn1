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
//import org.json.JSONObject;
//import com.parse4cn1.ParseObject;
//import com.parse4cn1.encode.ParseObjectEncodingStrategy;
//
//public class IncrementFieldOperation implements ParseFieldOperation {
//
//	private Number amount;
//	private boolean needIncrement = true;
//
//	public IncrementFieldOperation(Number amount) {
//		this.amount = amount;
//	}
//
//	@Override
//	public Object apply(Object oldValue, ParseObject parseObject, String key) {
//		
//		if (oldValue == null) {
//			needIncrement = false;
//			return amount;
//		}
//		
//		if ((oldValue instanceof Number)) {
//			return OperationUtil.addNumbers((Number) oldValue, this.amount);
//		}
//		
//		throw new IllegalArgumentException("You cannot increment a non-number. Key type ["+oldValue.getClass().getCanonicalName()+"]");
//	}
//
//	@Override
//	public Object encode(ParseObjectEncodingStrategy objectEncoder)
//			throws JSONException {
//		if(needIncrement) {
//			JSONObject output = new JSONObject();
//			output.put("__op", "Increment");
//			output.put("amount", this.amount);
//			return output;
//		}
//		else {
//			return amount;
//		}
//	}
//
//}
