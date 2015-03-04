//package com.parse4cn1.callback;
//
//import java.util.List;
//
//import com.parse4cn1.ParseException;
//import com.parse4cn1.ParseObject;
//
//public abstract class FindCallback<T extends ParseObject> extends ParseCallback<List<T>> {
//
//	public abstract void done(List<T> list, ParseException parseException);
//	
//	@Override
//	void internalDone(List<T> list, ParseException parseException) {
//		done(list, parseException);
//	}
//	
//}
