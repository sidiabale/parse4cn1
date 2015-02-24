package com.parse4cn1.callback;

import com.parse4cn1.ParseException;

public abstract class ParseCallback<T> {
	
	abstract void internalDone(T paramT, ParseException parseException);
	
}
