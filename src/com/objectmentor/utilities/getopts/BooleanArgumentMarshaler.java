package com.objectmentor.utilities.getopts;

import java.util.Iterator;

import com.objectmentor.utilities.args.ArgsException;

public class BooleanArgumentMarshaler implements ArgumentMarshaler {
	private Boolean booleanValue = false;

	public void set(Iterator<String> currentArgument) throws ArgsException {
		booleanValue = true;
	}

	public Object get() {
		return booleanValue;
	}
}
