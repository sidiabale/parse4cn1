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
 */
package com.parse4cn1.util;

import com.codename1.io.Externalizable;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A wrapper around the {@link com.parse4cn1.ParseObject} class to make it
 * externalizable.
 */
public class ExternalizableParseObject<T extends ParseObject> implements Externalizable {

    private T parseObject;
    private String className;
    
    public ExternalizableParseObject() { 
    }
    
    public ExternalizableParseObject(final T parseObject) {
        if (parseObject == null) {
            throw new NullPointerException("Null ParseObject cannot be serialized");
        }
        this.parseObject = parseObject;
        className = parseObject.getClassName();
        
        if (className == null) {
            throw new NullPointerException("Object cannot be serialized (className is null)");
        }
    }
    
    /**
     * @return A unique class name.
     */
    public static String getClassName() {
        return "ExternalizableParseObject";
    }
    
    /**
     * @return The {@link java.util.Date} wrapped by this object.
     */
    public T getParseObject() {
        return parseObject;
    }
    
    /**
     * @see com.codename1.io.Externalizable
     */
    public int getVersion() {
        return Parse.getSerializationVersion();
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void externalize(DataOutputStream out) throws IOException {
        if (parseObject != null) {
            try {
                Util.writeUTF(className, out);
                parseObject.externalize(out);
            } catch (ParseException ex) {
                Logger.getInstance().error(
                        "Unable to serialize ParseObject with objectId=" + parseObject.getObjectId());
                throw new IOException(ex.getMessage());
            }
        }
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void internalize(int version, DataInputStream in) throws IOException {
        className = Util.readUTF(in);
        
        if (className != null) {
            parseObject = ParseObject.create(className);
            try {
                parseObject.internalize(version, in);
            } catch (ParseException ex) {
                Logger.getInstance().error(
                        "An error occurred while trying to deserialize ParseObject");
                throw new IOException(ex.getMessage());
            }
        } else {
           final String msg = "Unable to deserialize ParseObject "
                   + "(null class name). Is class properly registered?";
           Logger.getInstance().error(msg); 
           throw new RuntimeException(msg);
        }
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    // Note: This is a unique identifier for this class not individual objects serialized by it!
    public String getObjectId() {
        return getClassName();
    }
}
