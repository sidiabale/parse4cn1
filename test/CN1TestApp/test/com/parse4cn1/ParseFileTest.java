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
package com.parse4cn1;

import com.codename1.io.Storage;
import com.parse4cn1.callback.ProgressCallback;
import com.parse4cn1.util.MimeType;
import static com.parse4cn1.util.MimeType.getFileExtension;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author sidiabale
 */
public class ParseFileTest extends BaseParseTest {

    final String classGameScore = "GameScore";

    @Override
    public boolean runTest() throws Exception {
        testRestApiExample();
        testImageUpload();
        testDataFileUpload();
        testArbitraryExtensionFileUpload();
        testSaveWithProgressListener();
        testParseFileSerialization();
        return true;
    }

    @Override
    protected void resetClassData() {
        batchDeleteObjects(classGameScore);
    }

    private void testRestApiExample() throws ParseException {
        System.out.println("============== testRestApiExample()");
        final ParseFile textFile = new ParseFile("hello.txt", "Hello World!".getBytes());
        textFile.save();

        final ParseObject gameScore = ParseObject.create(classGameScore);
        gameScore.put("text", textFile);
        gameScore.save();

        final ParseObject retrievedGameScore = ParseObject.fetch(classGameScore,
                gameScore.getObjectId());
        final ParseFile retrievedFile = retrievedGameScore.getParseFile("text");
        assertTrue(Arrays.equals(textFile.getData(), retrievedFile.getData()),
                "Saved data should match retrieved file data");
        
        deleteFile(textFile.getName());
    }

    /*
     Note: To be able to use getClass().getResource(/<file>)
     I added /test/resources as the first folder entry in 
     <project> (Right-click) >> Properties >> Libraries >> Compile
    
     If in doubt, run the following to see the root resource path:
     System.out.println("root is:" + getClass().getResource("/"));
     */
    private void testImageUpload() throws ParseException, FileNotFoundException, IOException {
        System.out.println("============== testImageUpload()");
        uploadAndCheck("parse.jpg");
        uploadAndCheck("parse.png");
    }

    private void testDataFileUpload() throws ParseException, IOException {
        System.out.println("============== testDataFileUpload()");
        uploadAndCheck("parse.docx");
        uploadAndCheck("parse.pdf");
    }

    private void testArbitraryExtensionFileUpload() throws ParseException, IOException {
        System.out.println("============== testArbitraryExtensionFileUpload()");
        uploadAndCheck("parse.exr");
    }
    
    private void testSaveWithProgressListener() throws ParseException {
        System.out.println("============== testSaveWithProgressListener()");
        final String fileName = "parse.pdf";
        assertNotNull(getClass().getResource("/" + fileName), "Test file missing");

        byte[] inputBytes = getBytes("/" + fileName);
        ParseFile file = new ParseFile(fileName, inputBytes,
                MimeType.getMimeType(getFileExtension(fileName)));
        
        final AtomicInteger percentDone = new AtomicInteger(0);
        
        file.save(new ProgressCallback() {

            @Override
            public void done(Integer done) {
                assertTrue(done >= percentDone.get());
                percentDone.getAndSet(done);
            }
        });
        
        assertEqual(100, percentDone.get(), "100% expected after successful upload");
        deleteFile(file.getName());
    }
    
    private void testParseFileSerialization() throws ParseException {
        System.out.println("============== testParseFileSerialization()");
        assertEqual(ParseFile.getClassName(), "ParseFile");
        
        final ParseFile textFile = new ParseFile("hello.txt", null, null);
        
        // Dirty file can be saved
        ParseFile retrieved  = serializeAndRetrieveFile(textFile);
        compareParseFiles(textFile, retrieved, false);
        
        // Clean file can be saved
        textFile.setData("Hello World!".getBytes());
        textFile.save();
        retrieved  = serializeAndRetrieveFile(textFile);
        compareParseFiles(textFile, retrieved, true);
  
        deleteFile(textFile.getName());
    }
    
    private ParseFile serializeAndRetrieveFile(final ParseFile input) {
        assertTrue(Storage.getInstance().writeObject(input.getName(), input),
                "Serialization of ParseObject failed");
        Storage.getInstance().clearCache(); // Absolutely necessary to force retrieval from storage
        return (ParseFile) Storage.getInstance().readObject(input.getName());
    }

    private void uploadAndCheck(final String fileName) throws ParseException, IOException {
//        System.out.println("Resource root path: " + getClass().getResource("/"));
//        System.out.println("Resource root path (classLoader): " + getClass().getClassLoader().getResource(""));
        
        assertNotNull(getClass().getResource("/" + fileName), "Test file missing");
        
        byte[] inputBytes = getBytes("/" + fileName);
        ParseFile file = new ParseFile(fileName, inputBytes,
                MimeType.getMimeType(getFileExtension(fileName)));
        file.save();

        checkFileData(fileName, inputBytes, "retrieved" + fileName, file.getData());
        deleteFile(file.getName());
    }

    private void checkFileData(final String inputPath, final byte[] inputData,
            final String outputFilename, final byte[] outputData) throws IOException {

        assertNotNull(inputPath, "Input file path is null");
        assertNotNull(outputFilename, "Output file name is null");
        assertNotNull(inputData, "Input data is null");
        assertNotNull(outputData, "Output data is null");

        if (!Arrays.equals(inputData, outputData)) {
            // differences --> write to file to disk for comparison
            final String outputPath = "./build/tmp/" + outputFilename;
            final FileOutputStream fos = new FileOutputStream(outputPath);
            try {
                fos.write(outputData);
            } finally {
                fos.close();
            }
            fail("Input and output image data are not byte equivalent! For visual comparison:\n"
                    + "Input: " + inputPath + "\nOutput: " + outputPath);
        }
    }
    
    private void deleteFile(final String filename) throws ParseException {
        assertNotNull(filename, "File name is null");
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("filename", filename);
        params.put("server", Parse.getApiEndpoint());
        
        assertTrue(((String)ParseCloud.callFunction("deleteFile", params)).isEmpty(),
                "Successful delete should return an empty string");
    }
}
