package com.objectmentor.utilities.getopts;

import java.text.ParseException;
import java.util.*;

import com.objectmentor.utilities.args.ArgsException;

public class Args {
	private String schema;
	private boolean valid = true;
	private Set<Character> unexpectedArguments = new TreeSet<Character>();
	private Map<Character, ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();
	private Set<Character> argsFound = new HashSet<Character>();
	private Iterator<String> currentArgument;
	private char errorArgumentId = '\0';
	private String errorParameter = "TILT";
	private ErrorCode errorCode = ErrorCode.OK;
	private List<String> argList;

	enum ErrorCode {
		OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT
	}

	public Args(String schema, String[] args) throws ParseException {
		this.schema = schema;
		argList = Arrays.asList(args);
		valid = parse();
	}

	private boolean parse() throws ParseException {
		if (schema.length() == 0 && argList.size() == 0)
			return true;
		parseSchema();
		try {
			parseArguments();
		} catch (ArgsException e) {
		}
		return valid;
	}

	private boolean parseSchema() throws ParseException {
		for (String element : schema.split(",")) {
			if (element.length() > 0) {
				String trimmedElement = element.trim();
				parseSchemaElement(trimmedElement);
			}
		}
		return true;
	}

	private void parseSchemaElement(String element) throws ParseException {
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);
		if (isBooleanSchemaElement(elementTail)) {
			marshalers.put(elementId, (ArgumentMarshaler) new BooleanArgumentMarshaler());
		} else if (isStringSchemaElement(elementTail)) {
			marshalers.put(elementId, (ArgumentMarshaler) new StringArgumentMarshaler());
		} else if (isIntegerSchemaElement(elementTail)) {
			marshalers.put(elementId, (ArgumentMarshaler) new IntegerArgumentMarshaler());
		} else {
			throw new ParseException(String.format("Argument: %c has invalid format: %s", elementId, elementTail), 0);
		}
	}

	private void validateSchemaElementId(char elementId) throws ParseException {
		if (!Character.isLetter(elementId)) {
			throw new ParseException("Bad character:" + elementId + "in Args format: " + schema, 0);
		}
	}

	private boolean isBooleanSchemaElement(String elementTail) {
		return elementTail.length() == 0;
	}

	private boolean isStringSchemaElement(String elementTail) {
		return elementTail.equals("*");
	}

	private boolean isIntegerSchemaElement(String elementTail) {
		return false;
	}

	private boolean parseArguments() throws ArgsException {
		for (currentArgument = argList.iterator(); currentArgument.hasNext();) {
			String arg = currentArgument.next();
			parseArgument(arg);
		}
		return true;
	}

	private void parseArgument(String arg) throws ArgsException {
		if (arg.startsWith("-"))
			parseElements(arg);
	}

	private void parseElements(String arg) throws ArgsException {
		for (int i = 1; i < arg.length(); i++)
			parseElement(arg.charAt(i));
	}

	private void parseElement(char argChar) throws ArgsException {
		if (setArgument(argChar))
			argsFound.add(argChar);
		else {
			unexpectedArguments.add(argChar);
			valid = false;
		}
	}

	private boolean setArgument(char argChar) throws ArgsException {
		ArgumentMarshaler m = marshalers.get(argChar);
		if (m == null)
			return false;
		try {
			m.set(currentArgument);
			return true;
		} catch (ArgsException e) {
			valid = false;
			errorArgumentId = argChar;
			throw e;
		}
	}

	public int cardinality() {
		return argsFound.size();
	}

	public String usage() {
		if (schema.length() > 0)
			return "-[" + schema + "]";
		else
			return "";
	}

	public String errorMessage() throws Exception {
		switch (errorCode) {
			case OK:
				throw new Exception("TILT: Should not get here.");
			case UNEXPECTED_ARGUMENT:
				return unexpectedArgumentMessage();
			case MISSING_STRING:
				return String.format("Could not find string parameter for -%c.", errorArgumentId);
			case INVALID_INTEGER:
				return String.format("Argument -%c expects an integer but was '%s' .", errorArgumentId, errorParameter);
			case MISSING_INTEGER:
				return String.format("Could not find integer parameter for -%c.", errorArgumentId);
		}
		return "";
	}

	private String unexpectedArgumentMessage() {
		StringBuffer message = new StringBuffer("Argument(s) -");
		for (char c : unexpectedArguments) {
			message.append(c);
		}
		message.append(" unexpected.");

		return message.toString();
	}

	public boolean getBoolean(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		boolean b = false;
		try {
			b = am != null && (Boolean) am.get();
		} catch (ClassCastException e) {
			b = false;
		}
		return b;
	}

	public String getString(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		try {
			return am == null ? "" : (String) am.get();
		} catch (ClassCastException e) {
			return "";
		}
	}

	public int getInt(char arg) {
		ArgumentMarshaler am = marshalers.get(arg);
		try {
			return am == null ? 0 : (Integer) am.get();
		} catch (Exception e) {
			return 0;
		}

	}

	public boolean has(char arg) {
		return argsFound.contains(arg);
	}

	public boolean isValid() {
		return valid;
	}
}