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

package com.parse4cn1.operation;

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.encode.IParseObjectEncodingStrategy;

/**
 * This class defines an operation to increment or decrement the value of a 
 * field of a ParseObject.
 */
public class IncrementFieldOperation implements ParseOperation {

    private Object amount;
    private boolean needIncrement = true;

    public IncrementFieldOperation(Object amount) {
        if (!ParseOperationUtil.isSupportedNumberType(amount)) {
            throw new IllegalArgumentException("Type '" + amount.getClass() 
                    + "' is not a supported number type");
        }
        this.amount = amount;
    }

    @Override
    public Object apply(Object oldValue, ParseObject parseObject, String key) 
            throws ParseException {

        if (oldValue == null) {
            return amount;
        }

        if (ParseOperationUtil.isSupportedNumberType(oldValue)) {
           return ParseOperationUtil.addNumbers(oldValue, this.amount); 
        }

        throw new IllegalArgumentException("You cannot increment a non-number."
                + " Key type [" + oldValue.getClass().toString()+ "]");

    }

    @Override
    public Object encode(IParseObjectEncodingStrategy objectEncoder) {
        JSONObject output = new JSONObject();
        try {
            output.put(ParseConstants.KEYWORD_OP, "Increment");
            output.put("amount", this.amount);
        } catch (JSONException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return output;
    }
}
