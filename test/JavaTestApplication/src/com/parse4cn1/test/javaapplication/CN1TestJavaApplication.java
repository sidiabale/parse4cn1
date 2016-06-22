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
package com.parse4cn1.test.javaapplication;

import com.codename1.io.Log;
import com.codename1.ui.Display;
import com.parse4cn1.ParseException;
import com.parse4cn1.BaseParseTest;
import com.parse4cn1.util.ParseRegistry;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.reflections.Reflections;

/**
 * A simple test app to illustrate how to create a regular Java application
 * using parse4cn1.jar.
 *
 * It executes the Parse4CN1 tests written using CodenameOne's (Java) test
 * library.
 *
 * @author sidiabale
 */
public class CN1TestJavaApplication {

    private static class Status {
        private int value = -1;
        
        public void setValue(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }
    /**
     * A helper class to initialize the application's main frame.
     */
    public static class MainFrame extends JFrame {

        public MainFrame() {
            setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            BorderLayout layout = new BorderLayout();
            getContentPane().setLayout(layout);
            this.setSize(new Dimension(500, 500));
            this.setPreferredSize(new Dimension(500, 500));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Status status = new Status();
        try {
//            status = createAppWithoutProperContext();
            status = createAppWithProperContext(false);
        } finally {
            // Return exit code that is possibly checked by caller
            System.exit(status.getValue());
        }
    }

    private static Status createAppWithoutProperContext() {
        // This approach is handy for a non-GUI application
        Status status = new Status();
        Display.init(null);
        status.setValue(runTests());
        return status;
    }

    private static Status createAppWithProperContext(final boolean aShowFrame) {
        final Status status = new Status();
        // This approach is recommended for a GUI-application or in the case
        // where the blank frame shown for createAppWithoutProperContext() is an issue.
        final JFrame f = new MainFrame();
        Display.init(f.getContentPane());

        Display.getInstance().callSeriallyAndWait(new Runnable() {

            @Override
            public void run() {
                f.setVisible(aShowFrame);
                status.setValue(runTests());
                if (status.getValue() == 0) {
                    // Close frame only if all tests passed otherwise, build will still succeed
                    f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
                }
            }
        });
        
        return status;
    }
    
     private static int runTests() {
        int status = 0;
        status += runTests("https://api.parse.com/1", "j1KMuH9otZlHcPncU9dZ1JFH7cXL8K5XUiQQ9ot8", "V6ZUyBtfERtzbq6vjeAb13tiFYij980HN9nQTWGB");
        status += runTests("https://parse-parse4cn1.rhcloud.com/parse" /*openshift*/, "myAppId", null);
//        status += runTests("https://parseapi.back4app.com", "OiTzm1ivZovdmMktQnqk8ajqBVIPgl4dlgUxw4dh", "fHquv9DA0SA5pd7VPO38tNzOrzrgTgfd7yY3nXbo");
        return status;
    }

    private static int runTests(String apiEndPoint, String appId, String clientKey) {
        // Auto-detect defined test classes
        // cf. lib/reflections-0.9.9-RC1-uberjar.jar and its dependencies 
        // (lib/javassist.jar, lib/guava-18.0.jar)
        Reflections reflections = new Reflections("com.parse4cn1");
        final Set<Class<? extends BaseParseTest>> testClasses
                = reflections.getSubTypesOf(BaseParseTest.class);

        final int testCount = testClasses.size();
        System.out.println("Testing Java application based on CN1 Parse port using backend: " + apiEndPoint);
        System.out.println("About to run " + testCount + " tests...\n");
//        com.parse4cn1.util.Logger.getInstance().setLogLevel(Log.DEBUG); // Show extra details e.g. to debug failing test

        ParseRegistry.reset();
        BaseParseTest.setBackend(apiEndPoint, appId, clientKey);
        
        
        int counter = 1;
        List<String> failedTests = new ArrayList<String>();
        for (Class<? extends BaseParseTest> testClass : testClasses) {
         
        /*
            Openshift
            The following tests failed and got modified (changes need to be documented):
            com.parse4cn1.ParseUserTest, 
            com.parse4cn1.ParseInstallationTest: Master key (https://parse.com/docs/rest/guide#push-notifications-querying-installations) 
            com.parse4cn1.ParseQueryTest: GeoQueries https://github.com/ParsePlatform/parse-server/issues/1592
            ]
        */
        
            /*
             Filter for running subsets of tests if necessary (particularly useful since 
             at the time of writing, the CN1 test runner lacks this functionality
             see: https://groups.google.com/d/msg/codenameone-discussions/WVO8xrRvo3I/dklQXs6m4v4J)
             */
//            if (!testClass.getCanonicalName().endsWith("ParseInstallationTest")) {
//                System.err.println("Ignoring test " + testClass.getCanonicalName());
//                continue;
//            }

            try {
                failedTests.add(testClass.getCanonicalName());

                System.out.println("\n:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:");
                System.out.println("Running test " + counter + "/" + testCount
                        + ": " + testClass.getCanonicalName());
                System.out.println(":=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:");
                ++counter;

                BaseParseTest test = (BaseParseTest) testClass.newInstance();
                test.prepare();
                final boolean result = test.runTest();

                test.cleanup();

                if (result) {
                    failedTests.remove(testClass.getCanonicalName());
                }
            } catch (ParseException ex) {
                Logger.getLogger(CN1TestJavaApplication.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CN1TestJavaApplication.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                System.out.println("\n:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:");
                System.out.println("Test: " + testClass.getCanonicalName()
                        + " " + (!failedTests.contains(testClass.getCanonicalName()) ? "PASSED" : "FAILED"));
                System.out.println(":=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:");
            }
        }

        int status = 0;
        System.out.println("\n[TEST RESULT]");
        if (failedTests.isEmpty()) {
            System.out.println("\nALL tests passed!!! Backend: " + apiEndPoint);
        } else {
            status = failedTests.size();
            System.err.println("\nThe following tests failed (backend: " 
                    + apiEndPoint + ")\n" + failedTests);
        }
        return status;
    }
}
