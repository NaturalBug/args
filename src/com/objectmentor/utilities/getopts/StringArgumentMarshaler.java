package com.objectmentor.utilities.getopts;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.objectmentor.utilities.args.ArgsException;
import com.objectmentor.utilities.args.ArgsException.ErrorCode;

public class StringArgumentMarshaler extends ArgumentMarshaler {
	private String stringValue = "";

	public void set(Iterator<String> currentArgument) throws ArgsException {
		try {
			stringValue = currentArgument.next();
		} catch (NoSuchElementException e) {
			throw new ArgsException(ErrorCode.MISSING_STRING);
		}
	}

	public void set(String s) {
	}

	public Object get() {
		return stringValue;
	}
}
