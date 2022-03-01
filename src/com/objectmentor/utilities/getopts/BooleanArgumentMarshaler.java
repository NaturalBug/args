package com.objectmentor.utilities.getopts;

public class BooleanArgumentMarshaler extends ArgumentMarshaler {
	public void set(String s) {
		booleanValue = true;
	}

	public Object get() {
		return booleanValue;
	}

}
