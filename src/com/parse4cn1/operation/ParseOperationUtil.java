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
import com.parse4cn1.util.Logger;

/**
 * This class defines general utilities related to Parse operations.
 */
public class ParseOperationUtil {

    /**
     * Adds the specified objects if they are of a supported number type.
     * 
     * @param first The object to add.
     * @param second The other object to add.
     * @return The result of adding first and second.
     * @throws ParseException if the addition could not be performed.
     * @see #isSupportedNumberType(java.lang.Object)
     */
    static Object addNumbers(Object first, Object second) throws ParseException {
        if (!isSupportedNumberType(first) || !isSupportedNumberType(second)) {
            Logger.getInstance().error(first + " and/or " + second + " is of an unsupported number type.");
            throw new ParseException(ParseException.OTHER_CAUSE, 
                    ParseException.ERR_INTERNAL);
        }
        
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
        
        Logger.getInstance().error("Addition semantics not yet defined for the provided number types.");
        throw new ParseException(ParseException.OTHER_CAUSE, ParseException.ERR_INTERNAL);
    }

    /**
     * Check is the provided object is of a supported number type. 
     * <p>
     * This approach is needed since at the time of writing, CN1 does not support 
     * the Number Java class.
     * 
     * @param o The object whose type is to be checked.
     * @return {@code true} if o is of type Double, Float, Long, Integer, Short or Byte.
     */
    public static boolean isSupportedNumberType(Object o) {
        return (o instanceof Double) || (o instanceof Float) || (o instanceof Long)
                || (o instanceof Integer) || (o instanceof Short) || (o instanceof Byte);
    }
}
