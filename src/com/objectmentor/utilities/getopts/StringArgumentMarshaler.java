package com.objectmentor.utilities.getopts;

import java.util.Iterator;

import com.objectmentor.utilities.args.ArgsException;

public class StringArgumentMarshaler extends ArgumentMarshaler {
	private String stringValue = "";

	@Override
	public void set(Iterator<String> currentArgument) throws ArgsException {
		// TODO Auto-generated method stub

	}

	public void set(String s) {
		stringValue = s;
	}

	public Object get() {
		return stringValue;
	}

}
