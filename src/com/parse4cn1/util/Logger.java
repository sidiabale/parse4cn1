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

/**
 * Minimalist file logger. Logs to the default CN1 log file.
 * <p>
 * By default, debug logging is disabled and should <em>not</em> not be 
 * enabled in production code for performance and security reasons.
 */
public class Logger {
   
    private static Logger wrapper;
    private static final String PREFIX = "[parse4cn1] ";
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

    public boolean isDebugEnabled() {
        return (Log.getLevel() == Log.DEBUG);
    }
    
    public final void setLogLevel(int logLevel) {
        Log.setLevel(logLevel);
    }

    public void debug(String data) {
        log.p(PREFIX + data, Log.DEBUG);
    }
    
    public void info(String data) {
        log.p(PREFIX + data, Log.DEBUG);
    }
    
    public void warn(String data) {
        log.p(PREFIX + data, Log.DEBUG);
    }
    
    public void error(String data) {
        log.p(PREFIX + data, Log.ERROR);
    }
}
