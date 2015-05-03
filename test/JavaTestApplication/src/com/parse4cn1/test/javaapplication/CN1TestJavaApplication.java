/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.parse4cn1.test.javaapplication;

import com.codename1.ui.Display;
import com.parse4cn1.ParseException;
import com.parse4cn1.BaseParseTest;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.reflections.Reflections;

/**
 * A simple test app to illustrate how to create a regular Java application 
 * using parse4cn1.jar.
 * 
 * It executes the Parse4CN1 tests written using CodenameOne's (Java) test library.
 * 
 * @author sidiabale
 */
public class CN1TestJavaApplication {

    /**
     * A helper class to initialize the application's main frame.
     */
    public static class CNOJFrame extends JFrame {

        public CNOJFrame() {
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
        createAppWithoutProperContext();
//        createAppWithProperContext();
    }

    private static void createAppWithoutProperContext() {
        // This approach is handy for a non-GUI application
        Display.init(null);
        runTests();
        Display.getInstance().exitApplication();
    }

    private static void createAppWithProperContext() {
        // This approach is recommended for a GUI-application or in the case
        // where the blank frame shown for createAppWithoutProperContext() is an issue.
        final JFrame f = new CNOJFrame();
        Display.init(f.getContentPane());

        Display.getInstance().callSeriallyAndWait(new Runnable() {

            @Override
            public void run() {
                f.setVisible(true);
                runTests();
                // Close frame
                f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
            }
        });
    }

    private static void runTests() {
        // Auto-detect defined test classes
        // cf. lib/reflections-0.9.9-RC1-uberjar.jar and its dependencies 
        // (lib/javassist.jar, lib/guava-18.0.jar)
        Reflections reflections = new Reflections("com.parse4cn1");
        final Set<Class<? extends BaseParseTest>> testClasses
                = reflections.getSubTypesOf(BaseParseTest.class);

        System.out.println("Testing Java application based on CN1 Parse port!!!");
        System.out.println("About to run " + testClasses.size() + " tests...\n");

        try {
            for (Class<? extends BaseParseTest> testClass : testClasses) {
                System.out.println("\n:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:");
                System.out.println("Running test: " + testClass.getCanonicalName());
                System.out.println(":=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:");

                BaseParseTest test = (BaseParseTest) testClass.newInstance();
                test.prepare();
                test.runTest();
                test.cleanup();

                System.out.println("\n:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:");
                System.out.println("Done with test: " + testClass.getCanonicalName());
                System.out.println(":=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:");
            }
        } catch (ParseException ex) {
            Logger.getLogger(CN1TestJavaApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CN1TestJavaApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
