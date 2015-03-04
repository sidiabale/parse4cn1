/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.parse4cn1.util;

import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Log;

/**
 *
 * @author sidiabale
 */
public class Logger {
   
    private static Logger wrapper;
    private Log log;
    
    public static Logger getInstance() {
        if (wrapper == null) {
            wrapper = new Logger();
        }
        return wrapper;
    }
    
    private Logger() {
        log = Log.getInstance();
        log.setFileURL("log.txt");
    }

    public boolean isDebugEnabled() {
        return (Log.getLevel() == Log.DEBUG);
    }

    public void debug(String data) {
        log.p(data, Log.DEBUG);
    }
    
}
