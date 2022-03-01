package com.objectmentor.utilities.getopts;

public abstract class ArgumentMarshaler {
	private int integerValue;

	public void setInteger(int i) {
		integerValue = i;
	}

	public int getInteger() {
		return integerValue;
	}

	public abstract void set(String s);

	public abstract Object get();
}
