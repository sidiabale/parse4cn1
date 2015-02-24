package com.parse4cn1.callback;

import com.parse4cn1.ParseException;

public abstract class DeleteCallback extends ParseCallback<Void> {
	
	public abstract void done(ParseException parseException);
	
	@Override
	void internalDone(Void paramT, ParseException parseException) {
		done(parseException);
	}

}