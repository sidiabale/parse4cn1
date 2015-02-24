package com.parse4cn1.callback;

import com.parse4cn1.ParseException;

public abstract class GetDataCallback extends ParseCallback<byte[]> {

	public abstract void done(byte[] data, ParseException e);

	final void internalDone(byte[] returnValue, ParseException e) {
		done(returnValue, e);
	}

}
