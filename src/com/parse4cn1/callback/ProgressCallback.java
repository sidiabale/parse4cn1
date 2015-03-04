package com.parse4cn1.callback;

import com.parse4cn1.ParseException;

public abstract class ProgressCallback extends ParseCallback<Integer> {

    public abstract void done(Integer percentDone);

    @Override
    void internalDone(Integer paramT, ParseException parseException) {
        done(paramT);
    }

}
