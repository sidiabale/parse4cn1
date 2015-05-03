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
 */
package com.parse4cn1.command;

import com.codename1.io.ConnectionRequest;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import com.parse4cn1.util.MimeType;

/**
 * This class defines a command for downloading resources from the Parse server.
 */
public class ParseDownloadCommand extends ParseCommand {

    private final String url;
    private final String contentType;

    public ParseDownloadCommand(final String url, final String contentType) {
        if (url == null) {
            throw new NullPointerException("Null URL");
        }

        this.url = url;
        
        if (contentType != null) {
            this.contentType = contentType;
        } else {
            this.contentType = MimeType.getMimeType(MimeType.getFileExtension(url));
        }
    }

    @Override
    void setUpRequest(ConnectionRequest request) throws ParseException {
        request.setPost(false);
        request.setHttpMethod("GET");
        request.setUrl(url);

        if (contentType != null) {
            request.addRequestHeader(ParseConstants.HEADER_CONTENT_TYPE, contentType);
        }
    }
}
