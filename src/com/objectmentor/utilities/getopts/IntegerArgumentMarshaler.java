package com.objectmentor.utilities.getopts;

import java.util.Iterator;

import com.objectmentor.utilities.args.ArgsException;

public class IntegerArgumentMarshaler extends ArgumentMarshaler {
	private int intValue = 0;

	@Override
	public void set(Iterator<String> currentArgument) throws ArgsException {
		// TODO Auto-generated method stub

	}

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
