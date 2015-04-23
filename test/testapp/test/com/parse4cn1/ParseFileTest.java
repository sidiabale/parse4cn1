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

import com.parse4cn1.util.MimeType;
import static com.parse4cn1.util.MimeType.getFileExtension;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sidiabale
 */
public class ParseFileTest extends BaseParseTest {

    @Override
    public boolean runTest() throws Exception {
        testRestApiExample();
        testImageUpload();
        testDataFileUpload();
        testArbitraryExtensionFileUpload();
        return true;
    }
    
    private void testRestApiExample() throws ParseException {
        // Create files
        ParseFile textFile = new ParseFile("hello.txt", "Hello World!".getBytes());
        textFile.save();
        
        // TODO: Test associating file with object
    }
    
    /*
    Note: To be able to use getClass().getResource(/<file>)
    I added /test/resources as the first folder entry in 
    <project> (Right-click) >> Properties >> Libraries >> Compile
    
    If in doubt, run the following to see the root resource path:
    System.out.println("root is:" + getClass().getResource("/"));
    */
    private void testImageUpload() throws ParseException, FileNotFoundException, IOException {
        uploadAndCheck("parse.jpg");
        uploadAndCheck("parse.png");
    }
    
    private void testDataFileUpload() throws ParseException, IOException {
        uploadAndCheck("parse.docx");
        uploadAndCheck("parse.pdf");
    }

    private void testArbitraryExtensionFileUpload() throws ParseException, IOException {
        uploadAndCheck("parse.exr");
    }
    
    private void uploadAndCheck(final String fileName) throws ParseException, IOException {
        assertNotNull(getClass().getResource("/" + fileName), "Test file missing");
        
        byte[] inputBytes = getBytes("/" + fileName);
        ParseFile file = new ParseFile(fileName, inputBytes, 
                MimeType.getMimeType(getFileExtension(fileName)));
        file.save();

        checkFileData(fileName, inputBytes, "retrieved" + fileName, file.getData());
        deleteFile(file.getUrl());
    }
    
    private void checkFileData(final String inputPath, final byte[] inputData, 
            final String outputFilename, final byte[] outputData) throws IOException {
        
        assertNotNull(inputPath,      "Input file path is null");
        assertNotNull(outputFilename, "Output file name is null");
        assertNotNull(inputData,      "Input data is null");
        assertNotNull(outputData,     "Output data is null");
        
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
    
    private void deleteFile(final String url) {
        assertNotNull(url, "File url is null");
        // TODO: Write cloud function to test deletion.
    }
}
