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
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class defines a command for uploading resources to the Parse server.
 */
public class ParseUploadCommand extends ParseCommand {

    private final String endPoint;
    private String contentType;
    private byte[] uploadData;

    public ParseUploadCommand(String endPoint) {
        this.endPoint = endPoint;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setData(byte[] uploadData) {
        this.uploadData = uploadData;
    }

    @Override
    void setUpRequest(ConnectionRequest request) throws ParseException {
        setupDefaultHeaders();
        request.setPost(true);
        request.setHttpMethod("POST");
        request.setUrl(getUrl(endPoint, null));

        if (contentType != null) {
            request.addRequestHeader(ParseConstants.HEADER_CONTENT_TYPE, contentType);
        }
    }

    @Override
    protected ConnectionRequest createConnectionRequest(final ParseResponse response) {
        /*
         Normally, a multipart request is typically used for uploading files.
         However, using with the parse API results in some extra bytes at the 
         beginning and end of the retrieved files. This results in corrupted 
         files upon attempting to read or download via the URL returned upon creation.
         (See also: http://stackoverflow.com/questions/21966299/uploading-image-to-parse-com-with-afnetworking-causing-corrupt-image)
         Instead, sending the raw bytes in the payload as done below works just fine.
         */
        final ConnectionRequest request = new ConnectionRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                response.setConnectionError(code, message);
            }

            @Override
            protected void handleException(Exception err) {
                response.setConnectionError(new ParseException(ParseException.CONNECTION_FAILED, 
                    ParseException.ERR_NETWORK, err));
            }

            @Override
            protected void buildRequestBody(OutputStream os) throws IOException {
                os.write(uploadData);
            }
        };

        request.setReadResponseForErrors(true);
        request.setDuplicateSupported(true);
        return request;
    }
}
