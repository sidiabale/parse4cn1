package com.parse4cn1.callback;

import com.parse4cn1.ParseException;

public abstract class SignUpCallback extends ParseCallback<Void> {

	public abstract void done(ParseException parseException);
	
	@Override
	void internalDone(Void paramVoid, ParseException parseException) {
		done(parseException);
	}
	
}
