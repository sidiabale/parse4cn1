package com.parse4cn1.callback;

import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;

public abstract class GetCallback<T extends ParseObject> extends ParseCallback<T> {

    public abstract void done(T t, ParseException parseException);
	
	@Override
	void internalDone(T t, ParseException parseException) {
		done(t, parseException);
	}
	
}
