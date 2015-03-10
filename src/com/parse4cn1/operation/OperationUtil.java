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

public class OperationUtil {

    static Object addNumbers(Object first, Object second) throws ParseException {
        if (((first instanceof Double)) || ((second instanceof Double))) {
            return (Double) first + (Double) second;
        }
        if (((first instanceof Float)) || ((second instanceof Float))) {
            return (Float) first + (Float) second;
        }
        if (((first instanceof Long)) || ((second instanceof Long))) {
            return (Long) first + (Long) second;
        }
        if (((first instanceof Integer)) || ((second instanceof Integer))) {
            return (Integer) first + (Integer) second;
        }
        if (((first instanceof Short)) || ((second instanceof Short))) {
            return (Short) first + (Short) second;
        }
        if (((first instanceof Byte)) || ((second instanceof Byte))) {
            return (Byte) first + (Byte) second;
        }
        throw new ParseException(ParseException.OTHER_CAUSE, "Unknown number type.");
    }

    public static boolean isSupportedNumberType(Object o) {
        return (o instanceof Double) || (o instanceof Float) || (o instanceof Long)
                || (o instanceof Integer) || (o instanceof Short) || (o instanceof Byte);
    }
}
