package com.objectmentor.utilities.getopts;

import java.util.Iterator;

import com.objectmentor.utilities.args.ArgsException;

public abstract class ArgumentMarshaler {
	public abstract void set(Iterator<String> currentArgument) throws ArgsException;

	public abstract void set(String s) throws ArgsException;

	public abstract Object get();
}
