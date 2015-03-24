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
import com.codename1.io.MultipartRequest;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseException;
import com.parse4cn1.callback.ProgressCallback;

public class ParseUploadCommand extends ParseCommand {

    private final String endPoint;
    private String contentType;
    private byte[] uploadData;
    private ProgressCallback progressCallback;

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
        setupDefaultHeaders(request, false);
        request.setPost(true);
        request.setHttpMethod("POST");
        request.setUrl(getUrl(endPoint, null));
        
        if (contentType != null) {
            request.addRequestHeader(ParseConstants.HEADER_CONTENT_TYPE, contentType);
        }

        // TODO Check if this works!!! Not sure what the key (currently 'data') should be
        if (uploadData != null) {
            if (!(request instanceof MultipartRequest)) {
                throw new ParseException(ParseException.INCORRECT_TYPE, 
                        "Request is not a MultipartRequest");
            }
            ((MultipartRequest) request).addData("data", uploadData, contentType);
        }
    }

    protected ConnectionRequest getConnectionRequest(final ParseResponse response) {
        final MultipartRequest request = new MultipartRequest() {

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                response.setStatusCode(code);
                response.setError(new ParseException(code, message));
            }

            @Override
            protected void handleException(Exception err) {
                response.setError(new ParseException(ParseException.CONNECTION_FAILED, err.getMessage()));
            }
        };

        if (progressCallback != null) {
            NetworkManager.getInstance().addProgressListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    if (evt instanceof NetworkEvent) {
                        final NetworkEvent networkEvent = (NetworkEvent) evt;
                        if (request.equals(networkEvent.getConnectionRequest())) {
                            progressCallback.done(networkEvent.getProgressPercentage());
                        }
                    }
                }
            });
        }

        request.setReadResponseForErrors(true);
        return request;
    }

    public void setProgressCallback(ProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }
}
