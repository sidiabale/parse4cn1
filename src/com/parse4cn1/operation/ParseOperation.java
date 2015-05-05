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

/**
 * A parse operation is an operation that can be performed on a parse object
 * to change its state, e.g., add, delete or modify fields, or create relations 
 * with other objects.
 */
public interface ParseOperation {

    /**
     * Applies the operation to the specified object.
     * @param oldValue The old value of the field referenced by key.
     * @param parseObject The parse object on which the operation is to be applied.
     * @param key The field on which the operation is to be applied.
     * @return The new value of key or null if the value is to be removed.
     * @throws ParseException 
     */
    abstract Object apply(Object oldValue, ParseObject parseObject, String key)
            throws ParseException;

    /**
     * Encodes this parse operation in a form that is understood by the Parse REST API.
     * @param objectEncoder The encoder to be used for encoding.
     * @return The encoded parse operation
     * @throws ParseException 
     */
    abstract Object encode(IParseObjectEncodingStrategy objectEncoder)
            throws ParseException;

}
