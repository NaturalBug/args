package com.objectmentor.utilities.getopts;

public abstract class ArgumentMarshaler {

	protected Boolean booleanValue = false;
	private String stringValue;
	private int integerValue;

	public void setString(String s) {
		stringValue = s;
	}

	public String getString() {
		return stringValue == null ? "" : stringValue;
	}

	public void setInteger(int i) {
		integerValue = i;
	}

	public int getInteger() {
		return integerValue;
	}

	public abstract void set(String s);

	public abstract Object get();
}
