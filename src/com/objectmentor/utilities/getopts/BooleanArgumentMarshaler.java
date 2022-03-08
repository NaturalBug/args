package com.objectmentor.utilities.getopts;

import java.util.Iterator;

import com.objectmentor.utilities.args.ArgsException;

public class BooleanArgumentMarshaler extends ArgumentMarshaler {
	private Boolean booleanValue = false;

	public void set(Iterator<String> currentArgument) throws ArgsException {
		booleanValue = true;
	}

	public void set(String s) {
	}

	public Object get() {
		return booleanValue;
	}
}
