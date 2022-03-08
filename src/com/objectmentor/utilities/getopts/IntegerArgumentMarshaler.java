package com.objectmentor.utilities.getopts;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.objectmentor.utilities.args.ArgsException;
import com.objectmentor.utilities.args.ArgsException.ErrorCode;

public class IntegerArgumentMarshaler implements ArgumentMarshaler {
	private int intValue = 0;

	public void set(Iterator<String> currentArgument) throws ArgsException {
		String parameter = null;
		try {
			parameter = currentArgument.next();
			intValue = Integer.parseInt(parameter);
		} catch (NoSuchElementException e) {
			throw new ArgsException(ErrorCode.MISSING_INTEGER);
		} catch (NumberFormatException e) {
			throw new ArgsException(ErrorCode.INVALID_INTEGER);
		}
	}

	public Object get() {
		return intValue;
	}
}
