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
package com.parse4cn1.callback;

import com.parse4cn1.ParseException;

/**
 * A generic Parse callback.
 *
 * @param <T> The type of data associated with this callback.
 */
public abstract class ParseCallback<T> {

    /**
     * This method is invoked when the operation is question is completed.
     *
     * @param paramT The parameter(s) associated with the operation.
     * @param parseException The exception that occurred during the operation if
     * applicable; otherwise null.
     */
    abstract void internalDone(T paramT, ParseException parseException);
}
