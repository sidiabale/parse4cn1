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
import com.parse4cn1.encode.ParseEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class defines an operation to 'atomically' remove objects from an array field 
 * of a ParseObject.
 */
public class RemoveFromArrayOperation implements ParseOperation {

    protected Set<Object> objects = new HashSet<Object>();

    public RemoveFromArrayOperation(Collection<?> coll) {
        this.objects.addAll(coll);
    }

    public RemoveFromArrayOperation(Object o) {
        this.objects.add(o);
    }

    @Override
    public Object apply(Object oldValue, ParseObject parseObject, String key) {
        // Return old value; it will be automatically updated when this operation is persisted.
        return oldValue;
    }

    @Override
    public Object encode(IParseObjectEncodingStrategy objectEncoder)
            throws ParseException {
        JSONObject output = new JSONObject();
        try {
            output.put(ParseConstants.KEYWORD_OP, "Remove");
            output.put("objects", ParseEncoder.encode(new ArrayList<Object>(this.objects), objectEncoder));
        } catch (JSONException ex) {
            throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PREPARING_REQUEST, ex);
        }
        return output;
    }

}
