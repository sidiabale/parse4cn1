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
package com.parse4cn1.encode;

import com.parse4cn1.ParseObject;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.ParseException;

/**
 * An interface that specifies a strategy for encoding ParseObject
 * relationships.
 */
public interface IParseObjectEncodingStrategy {

    /**
     * Encodes a relationship with the provided ParseObject.
     * @param parseObject The related object whose relationship is to be encoded.
     * @return The encoded Parse object relationship.
     * @throws ParseException if anything goes wrong.
     */
    public abstract JSONObject encodeRelatedObject(final ParseObject parseObject) throws ParseException;

}
