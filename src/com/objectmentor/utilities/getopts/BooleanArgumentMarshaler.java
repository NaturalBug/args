package com.objectmentor.utilities.getopts;

public class BooleanArgumentMarshaler extends ArgumentMarshaler {
	private Boolean booleanValue = false;

	public void set(String s) {
		booleanValue = true;
	}

	public Object get() {
		return booleanValue;
	}
}
