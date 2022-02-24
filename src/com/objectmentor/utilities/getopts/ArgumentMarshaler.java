package com.objectmentor.utilities.getopts;

public class ArgumentMarshaler {

	private Boolean booleanValue = false;
	private String stringValue;

	public void setBoolean(boolean value) {
		booleanValue = value;
	}

	public Boolean getBoolean() {
		return booleanValue;
	}

	public void setString(String s) {
		stringValue = s;
	}

	public String getString() {
		return stringValue == null ? "" : stringValue;
	}
}
