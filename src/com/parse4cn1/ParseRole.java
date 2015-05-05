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
package com.parse4cn1;

import com.parse4cn1.util.Logger;

// Inherited from Parse4J
// TODO: Port to CN1 i.e. review/extend, document, test and release
/**
 * Represents a Role on the Parse server. ParseRoles represent groupings of 
 * ParseUsers for the purposes of granting permissions 
 * (e.g. specifying a ParseACL for a ParseObject). Roles are specified by their 
 * sets of child users and child roles, all of which are granted any permissions 
 * that the parent role has.
 * <p>
 * Roles must have a name (which cannot be changed after creation of the role),
 * and must specify an ACL.
 */
public class ParseRole extends ParseObject {

    private static final Logger LOGGER = Logger.getInstance();

    protected ParseRole() {
        super(ParseConstants.CLASS_NAME_ROLE);
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getName() {
        return getString("name");
    }

    @Override
    protected void validateSave() {
        if ((getObjectId() == null) && (getName() == null)) {
            LOGGER.error("New roles must specify a name.");
            throw new IllegalStateException("New roles must specify a name.");
        }
    }

}
