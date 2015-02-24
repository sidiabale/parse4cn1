package com.parse4cn1.callback;

import com.parse4cn1.ParseException;

public abstract class FunctionCallback<T> extends ParseCallback<T> {

    public abstract void done(T result, ParseException parseException);
	
	@Override
	void internalDone(T result, ParseException parseException) {
		done(result, parseException);
	}
	
}
