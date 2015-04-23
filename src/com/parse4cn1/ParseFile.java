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

import ca.weblite.codename1.json.JSONException;
import com.parse4cn1.callback.GetDataCallback;
import com.parse4cn1.callback.ProgressCallback;
import com.parse4cn1.callback.SaveCallback;
import com.parse4cn1.command.ParseResponse;
import com.parse4cn1.command.ParseUploadCommand;
import com.parse4cn1.util.Logger;
import com.parse4cn1.util.MimeType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import org.apache.http.Header;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.impl.client.DefaultHttpClient;
import ca.weblite.codename1.json.JSONObject;
import com.parse4cn1.command.ParseDownloadCommand;

@SuppressWarnings("deprecation")
public class ParseFile implements Parse.IPersistable {

    private static final Logger LOGGER = Logger.getInstance();

    private String endPoint;
    private boolean uploaded = false;
    private boolean dirty = false;
    private String name = null;
    private String url = null;
    private String contentType = null;
    byte[] data;

    /**
     * Creates a new file from a byte array, file name, and content type.
     *
     * @param name The local file name.
     * @param data The file data.
     * @param contentType The file content type specified as a MIME type.
     */
    public ParseFile(String name, byte[] data, String contentType) {
        if (data.length > ParseConstants.MAX_PARSE_FILE_SIZE_IN_BYTES) {
            LOGGER.error("ParseFile must be less than "
                    + ParseConstants.MAX_PARSE_FILE_SIZE_IN_BYTES
                    + " bytes, current " + data.length);
            throw new IllegalArgumentException("ParseFile must be less than "
                    + ParseConstants.MAX_PARSE_FILE_SIZE_IN_BYTES
                    + " bytes, current " + data.length);
        }

        this.endPoint = ParseConstants.FILES_PATH + ((name != null) ? name : "file.dat");
        this.name = name;
        this.data = data;
        this.contentType = contentType;
        setDirty(true);
    }

    /**
     * Creates a new file from a byte array. The content type will be inferred
     * from the file name extension if one is set or default to
     * application/octet-stream.
     *
     * @param data The file data.
     */
    public ParseFile(byte[] data) {
        this(null, data, null);
    }

    /**
     * Creates a new file from a byte array and a name. The content type will be
     * inferred from the file name extension if one is set or default to
     * application/octet-stream.
     *
     * @param name The local file name.
     * @param data The file data.
     */
    public ParseFile(String name, byte[] data) {
        this(name, data, null);
    }

    /**
     * Creates a new file from a byte array, and content type.
     *
     * @param data The file data.
     * @param contentType The file content type specified as a MIME type.
     */
    public ParseFile(byte[] data, String contentType) {
        this(null, data, contentType);
    }

    /**
     * Creates a file without data.
     *
     * @param name The file name.
     * @param url The URL from which the file data can be retrieved.
     */
    public ParseFile(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContentType() {
        if (contentType == null) {
            final String fileExtension = MimeType.getFileExtension(getName());
            contentType = MimeType.getMimeType(fileExtension);
        }
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setData(byte[] data) {
        this.data = data;
        setDirty(true);
    }

    protected String getEndPoint() {
        return this.endPoint;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public final void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void save() throws ParseException {
        save(null);
    }

    public void save(ProgressCallback progressCallback) throws ParseException {

        if (!isDirty() || data == null) {
            return;
        }

        ParseUploadCommand command = new ParseUploadCommand(getEndPoint());
        command.setProgressCallback(progressCallback);
        command.setData(data);
        command.setContentType(getContentType());

        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            System.out.println(jsonResponse);
            if (jsonResponse == null) {
                LOGGER.error("Empty response.");
                throw response.getException();
            }
            
            try {
                this.name = jsonResponse.getString("name");
                this.url = jsonResponse.getString("url");
                this.dirty = false;
                this.uploaded = true;
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ex);
            }
        } else {
            LOGGER.error("Request failed.");
            throw response.getException();
        }
    }
    // TODO: Fix all saveInBackground methods
//    public void saveInBackground() {
//        saveInBackground(null, null);
//    }
//
//    public void saveInBackground(SaveCallback saveCallback) {
//        saveInBackground(saveCallback, null);
//    }
//
//    public void saveInBackground(ProgressCallback progressCallback) {
//        saveInBackground(null, progressCallback);
//    }
//
//    public void saveInBackground(SaveCallback saveCallback,
//            ProgressCallback progressCallback) {
//
//        SaveInBackgroundThread task = new SaveInBackgroundThread(saveCallback,
//                progressCallback);
//        ParseExecutor.runInBackground(task);
//
//    }

    public byte[] getData() throws ParseException {
        final ParseDownloadCommand command = 
                new ParseDownloadCommand(getUrl(), getContentType());

        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            data = response.getResponseData();
        } else {
            LOGGER.error("Request failed.");
            throw response.getException();
        }
        return data;
    }
    
    // TODO: Fix all getDataInBackground() methods
//    public void getDataInBackground() {
//        getDataInBackground(null);
//    }
//
//    public void getDataInBackground(GetDataCallback dataCallback) throws ParseException {
//
//        try {
//            byte[] result = getData();
//            dataCallback.done(result, null);
//        } catch (ParseException pe) {
//            dataCallback.done(null, pe);
//        }
//    }
    
//    class SaveInBackgroundThread extends Thread {
//
//        SaveCallback saveCallback;
//        ProgressCallback progressCallback;
//
//        public SaveInBackgroundThread(SaveCallback saveCallback,
//                ProgressCallback progressCallback) {
//            this.saveCallback = saveCallback;
//            this.progressCallback = progressCallback;
//        }
//
//        public void run() {
//            ParseException exception = null;
//            try {
//                save(saveCallback, progressCallback);
//            } catch (ParseException e) {
//                LOGGER.debug("Request failed " + e.getMessage());
//                exception = e;
//            }
//            if (saveCallback != null) {
//                saveCallback.done(exception);
//            }
//        }
//    }
//
//    class GetDataInBackgroundThread extends Thread {
//
//        GetDataCallback getDataCallback;
//        byte[] data;
//
//        public GetDataInBackgroundThread(byte[] data, GetDataCallback getDataCallback) {
//            this.getDataCallback = getDataCallback;
//            this.data = data;
//        }
//
//        public void run() {
//            ParseException exception = null;
//            // TODO Fix;
//            exception = new ParseException(
//                    new RuntimeException("Implement getData()!")); // Remove when lines below are fixed
////            try {
////                getData(getDataCallback);
////            } catch (ParseException e) {
////                LOGGER.debug("Request failed " + e.getMessage());
////                exception = e;
////            }
//            if (getDataCallback != null) {
//                getDataCallback.done(data, exception);
//            }
//        }
//    }
}
