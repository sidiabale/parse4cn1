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

package com.parse4cn1.util;

import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.parse4cn1.ParseException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

/**
 * Minimalist file logger. Logs to the default CN1 log file.
 * <p>
 * By default, debug logging is disabled and should <em>not</em> not be 
 * enabled in production code for performance and security reasons 
 * (sensitive information may be inadvertently logged).
 */
public class Logger {
   
    private static Logger wrapper;
    private static final String PREFIX = "[parse4cn1] ";
    // Use the CN1 default Log filename (see Log.createWriter() that other logging is also captured.
    // Of course, this can break in whichh case only parse4cn1 logging will be present which is file.
    private static final String LOG_FILENAME = "CN1Log__$"; 
    private static final DateFormat timeStampFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static String bufferedLog;
    private final Log log;

    public static Logger getInstance() {
        if (wrapper == null) {
            wrapper = new Logger();
        }
        return wrapper;
    }
    
    private Logger() {
        log = Log.getInstance();
        setLogLevel(Log.INFO);
    }

    /**
     * Checks if debug logging is enabled.
     * @return {@code true} if the debug logging is enabled; otherwise, returns {@code false}.
     */
    public boolean isDebugEnabled() {
        return (Log.getLevel() == Log.DEBUG);
    }
    
    /**
     * Sets the desired log level.
     * <p>
     * {@code logLevel} and all higher log levels will be enabled after calling this method.
     * @param logLevel The minimum log level.
     */
    public final void setLogLevel(int logLevel) {
        Log.setLevel(logLevel);
    }

    /**
     * Logs a debug message.
     * @param data The data to be logged.
     */
    public void debug(final String data) {
        log.p(PREFIX + data, Log.DEBUG);
    }
    
    /**
     * Logs an information message.
     * @param data The data to be logged.
     */
    public void info(final String data) {
        log.p(PREFIX + data, Log.INFO);
    }
    
    /**
     * Logs a warning message.
     * @param data The data to be logged.
     */
    public void warn(final String data) {
        log.p(PREFIX + data, Log.WARNING);
    }
    
    /**
     * Logs an error message.
     * @param data The data to be logged.
     */
    public void error(final String data) {
        log.p(PREFIX + data, Log.ERROR);
    }
    
    /**
     * Retrieves the entire log content as a single long string to be used by
     * the application in any way it deems fit.
     * <p>
     * Note that if any buffered log is present (cf. {@link #logBuffered(java.lang.String)}),
     * it will be appended to the end of the string returned by this method.
     * 
     * @return The log data if successfully retrieved or null.
     * @throws com.parse4cn1.ParseException if anything goes wrong.
     */
    public String getLogContent() throws ParseException {
        String text = null;
        try {
            Reader r = new InputStreamReader(Storage.getInstance().createInputStream(LOG_FILENAME));
            char[] buffer = new char[1024];
            int size = r.read(buffer);
            while (size > -1) {
                text += new String(buffer, 0, size);
                size = r.read(buffer);
            }
            r.close();
        } catch (Exception ex) {
            throw new ParseException("Retrieving log file contents failed:" 
                    + ex.getMessage(), ex);
        }
        
        if (bufferedLog != null) {
            text += "\n\n================="
                    + "\nBuffered logging:"
                    + "\n=================\n"
                    + bufferedLog;
        }
        
        return text;
    }
    
    /**
     * Creates and shows a form that contains the log data and an option to refresh it.
     * <p>Back navigation from this form returns the app to the form that 
     * was visible before this method was called.
     * 
     * @throws com.parse4cn1.ParseException if anything goes wrong.
     * @see #getLogContent() 
     */
    public void showLog() throws ParseException {
        try {
            final String text = getLogContent();
            final TextArea area = new TextArea(text, 5, 20);
            area.setEditable(false);
            final Form f = new Form("Log");
            f.setScrollable(false);
            final Form current = Display.getInstance().getCurrent();
            Command back = new Command("Back") {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    current.show();
                }
            };
            
            final Button refreshButton = new Button("Refresh");
            refreshButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent evt) {
                    try {
                        area.setText(getLogContent());
                    } catch (ParseException ex) {
                        error("Unable to show log contents: " + ex);
                   }
                }
            });
            
            f.addCommand(back);
            f.setBackCommand(back);
            f.setLayout(new BorderLayout());
            f.addComponent(BorderLayout.CENTER, area);
            f.addComponent(BorderLayout.NORTH, refreshButton);
            f.show();
        } catch (Exception ex) {
            throw new ParseException("Unable to show log contents: " 
                    + ex.getMessage(), ex);
        }
    }
    
    /**
     * Utility for writing logs when the initialization state of the CN1 
     * framework is not known. This is only intended for debugging native code
     * in the absence of a real debugger, without having to worry whether the 
     * underlying CN1 logging has been initialized or not.
     * <p>
     * The data logged here will be added to the end of the string returned by
     * {@link #getLogContent()} and thus, also displayed in the form generated by 
     * {@link #showLog()}
     * @param data The data to be logged (at debug level).
     */
    public static void logBuffered(final String data) {
        bufferedLog += "[" + timeStampFormat.format(new Date()) + "]" + PREFIX + data + "\n";
    }
}
