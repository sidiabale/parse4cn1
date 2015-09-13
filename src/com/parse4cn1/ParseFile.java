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
import com.parse4cn1.callback.ProgressCallback;
import com.parse4cn1.command.ParseResponse;
import com.parse4cn1.command.ParseUploadCommand;
import com.parse4cn1.util.Logger;
import com.parse4cn1.util.MimeType;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import com.parse4cn1.command.ParseDownloadCommand;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * ParseFile is a local representation of a file that is saved to the Parse cloud.
 */
public class ParseFile implements Parse.IPersistable, Externalizable {

    private static final Logger LOGGER = Logger.getInstance();

    private String endPoint;
    private boolean dirty = false;
    private String name = null;
    private String url = null;
    private String contentType = null;
    byte[] data;
    
    /**
     * @return A unique class name.
     */
    public static String getClassName() {
        return "ParseFile";
    }

    /**
     * Creates a new file from a byte array, file name, and content type.
     *
     * @param name The local file name.
     * @param data The file data.
     * @param contentType The file content type specified as a MIME type.
     */
    public ParseFile(String name, byte[] data, String contentType) {
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
    
    /**
     * Creates a file with all null fields.
     */
    public ParseFile() {
        this(null);
    }

    public String getName() {
        return name;
    }

    /**
     * Sets the file's name. Before save is called, this is just the filename 
     * given by the user (if any). After save is called, that name gets prefixed 
     * with a unique identifier.
     * 
     * @param name The file name.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    /**
     * Sets the URL of the uploaded Parse file. This URL will be used to retrieve 
     * the file's data.
     * 
     * @param url The URL of the Parse file's data.
     * @see #getData() 
     */
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

    /**
     * Sets the content type associated with this file.
     * 
     * @param contentType The content type to be set.
     * @see MimeType#getMimeType(java.lang.String)
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the data associated with this object which also causes the object to
     * become dirty.
     * 
     * @param data The data to be set.
     * @see #isDirty() 
     */
    public void setData(byte[] data) {
        this.data = data;
        setDirty(true);
    }

    protected String getEndPoint() {
        return this.endPoint;
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
    public boolean isDataAvailable() {
        return data != null;
    }

    @Override
    public void save() throws ParseException {
        save(null);
    }

    /**
     * Synchronously saves the file to the Parse cloud with 
     * progress notifications sent to the provided {@code progressCallback}.
     * 
     * @param progressCallback The callback to retrieve progress notifications.
     * @throws ParseException if anything goes wrong while saving the file.
     */
    public void save(ProgressCallback progressCallback) throws ParseException {

        if (!isDirty() || !isDataAvailable()) {
            return;
        }

        ParseUploadCommand command = new ParseUploadCommand(getEndPoint());
        command.setProgressCallback(progressCallback);
        command.setData(data);
        command.setContentType(getContentType());

        ParseResponse response = command.perform();
        if (!response.isFailed()) {
            JSONObject jsonResponse = response.getJsonObject();
            if (jsonResponse == null) {
                LOGGER.error("Empty response.");
                throw response.getException();
            }

            try {
                this.name = jsonResponse.getString("name");
                this.url = jsonResponse.getString("url");
                this.dirty = false;
            } catch (JSONException ex) {
                throw new ParseException(ParseException.INVALID_JSON, ParseException.ERR_PROCESSING_RESPONSE, ex);
            }
        } else {
            LOGGER.error("Request failed.");
            throw response.getException();
        }
    }

    /**
     * Synchronously gets the data for this object if no file data is present;
     * otherwise returns the data available for this object.
     * 
     * @return The data associated with this object.
     * @throws ParseException if retrieving file data from the Parse cloud fails.
     */
    public byte[] getData() throws ParseException {
        if (!isDataAvailable()) {
            final ParseDownloadCommand command
                    = new ParseDownloadCommand(getUrl(), getContentType());

            ParseResponse response = command.perform();
            if (!response.isFailed()) {
                data = response.getResponseData();
            } else {
                LOGGER.error("Request failed.");
                throw response.getException();
            }
        }
        return data;
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
        out.writeBoolean(isDirty());
        Util.writeUTF(getEndPoint(), out);
        Util.writeUTF(getName(), out);
        Util.writeUTF(getUrl(), out);
        Util.writeUTF(getContentType(), out);
        Util.writeObject(data, out);
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public void internalize(int version, DataInputStream in) throws IOException {
        setDirty(in.readBoolean());
        endPoint = Util.readUTF(in);
        setName(Util.readUTF(in));
        setUrl(Util.readUTF(in));
        setContentType(Util.readUTF(in));
        data = (byte[]) Util.readObject(in);
    }

    /**
     * @see com.codename1.io.Externalizable
     */
    public String getObjectId() {
        return getClassName();
    }
}
