package com.objectmentor.utilities.getopts;

import com.objectmentor.utilities.args.ArgsException;

public class IntegerArgumentMarshaler extends ArgumentMarshaler {
	private int intValue = 0;

	public void set(String s) throws ArgsException {
		try {
			intValue = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw new ArgsException();
		}
	}

	public Object get() {
		return intValue;
	}

}
