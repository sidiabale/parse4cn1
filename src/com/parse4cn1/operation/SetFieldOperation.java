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

import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.encode.IParseObjectEncodingStrategy;
import com.parse4cn1.encode.ParseEncoder;

/**
 * This class defines an operation to set the value of a field (key) of a ParseObject.
 */
public class SetFieldOperation implements ParseOperation {

    private Object value;

    public SetFieldOperation(Object value) {
        this.value = value;
    }

    @Override
    public Object apply(Object oldValue, ParseObject parseObject, String key) {
        return value; // Trigger local update of value even before it is persisted.
    }

    @Override
    public Object encode(IParseObjectEncodingStrategy objectEncoder) throws ParseException {
        return ParseEncoder.encode(value, objectEncoder);
    }
}
