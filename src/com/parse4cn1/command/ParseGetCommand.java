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

package com.parse4cn1.command;

import com.codename1.io.ConnectionRequest;
import com.parse4cn1.ParseException;
import com.parse4cn1.util.Logger;

public class ParseGetCommand extends ParseCommand {

    private static Logger LOGGER = Logger.getInstance();

    private final String endPoint;
    private String objectId;

    public ParseGetCommand(String className, String objectId) {
        this.endPoint = className;
        this.objectId = objectId;
    }

    public ParseGetCommand(String endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    void setUpRequest(ConnectionRequest request) throws ParseException {
        setupHeaders(request, addJson);
        request.setPost(false);
        request.setHttpMethod("GET");
        request.setUrl(getUrl(endPoint, objectId));
    }

    public void addJson(boolean addJson) {
        this.addJson = addJson;
    }
}
