package com.objectmentor.utilities.getopts;

import java.text.ParseException;
import java.util.*;

import javax.swing.text.html.HTMLDocument.RunElement;

import com.objectmentor.utilities.args.ArgsException;

public class Args {
	private String schema;
	private String[] args;
	private boolean valid = true;
	private Set<Character> unexpectedArguments = new TreeSet<Character>();
	private Map<Character, ArgumentMarshaler> booleanArgs = new HashMap<Character, ArgumentMarshaler>();
	private Map<Character, ArgumentMarshaler> stringArgs = new HashMap<Character, ArgumentMarshaler>();
	private Map<Character, ArgumentMarshaler> intArgs = new HashMap<Character, ArgumentMarshaler>();
	private Map<Character, ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();
	private Set<Character> argsFound = new HashSet<Character>();
	private int currentArgument;
	private char errorArgumentId = '\0';
	private String errorParameter = "TILT";
	private ErrorCode errorCode = ErrorCode.OK;

	enum ErrorCode {
		OK, MISSING_STRING, MISSING_INTEGER
	}

	public Args(String schema, String[] args) throws ParseException, ArgsException {
		this.schema = schema;
		this.args = args;
		valid = parse();
	}

	private boolean parse() throws ParseException, ArgsException {
		if (schema.length() == 0 && args.length == 0)
			return true;
		parseSchema();
		parseArguments();
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
		if (isBooleanSchemaElement(elementTail))
			parseBooleanSchemaElement(elementId);
		else if (isStringSchemaElement(elementTail))
			parseStringSchemaElement(elementId);
		else if (isIntegerSchemaElement(elementTail))
			parseIntegerSchemaElement(elementId);
	}

	private void validateSchemaElementId(char elementId) throws ParseException {
		if (!Character.isLetter(elementId)) {
			throw new ParseException(
					"Bad character:" + elementId + "in Args format: " + schema, 0);
		}
	}

	private boolean isStringSchemaElement(String elementTail) {
		return elementTail.equals("*");
	}

	private void parseStringSchemaElement(char elementId) {
		ArgumentMarshaler m = new StringArgumentMarshaler();
		stringArgs.put(elementId, new StringArgumentMarshaler());
		marshalers.put(elementId, m);
	}

	private boolean isBooleanSchemaElement(String elementTail) {
		return elementTail.length() == 0;
	}

	private void parseBooleanSchemaElement(char elementId) {
		ArgumentMarshaler m = new BooleanArgumentMarshaler();
		booleanArgs.put(elementId, new BooleanArgumentMarshaler());
		marshalers.put(elementId, m);
	}

	private boolean isIntegerSchemaElement(String elementTail) {
		return false;
	}

	private void parseIntegerSchemaElement(char elementId) {
		ArgumentMarshaler m = new IntegerArgumentMarshaler();
		intArgs.put(elementId, new IntegerArgumentMarshaler());
		marshalers.put(elementId, m);
	}

	private boolean parseArguments() throws ArgsException {
		for (currentArgument = 0; currentArgument < args.length; currentArgument++) {
			String arg = args[currentArgument];
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
		boolean set = true;
		if (isBooleanArg(argChar))
			setBooleanArg(argChar, true);
		else if (isStringArg(argChar))
			setStringArg(argChar, "");
		else if (isIntegerArg(argChar))
			setIntArgs(argChar);
		else
			set = false;

		return set;
	}

	private boolean isBooleanArg(char argChar) {
		ArgumentMarshaler m = marshalers.get(argChar);
		return m instanceof BooleanArgumentMarshaler;
	}

	private void setBooleanArg(char argChar, boolean value) {
		try {
			booleanArgs.get(argChar).set("true");
		} catch (ArgsException e) {
		}
	}

	private boolean isStringArg(char argChar) {
		ArgumentMarshaler m = marshalers.get(argChar);
		return m instanceof StringArgumentMarshaler;
	}

	private void setStringArg(char argChar, String s) throws ArgsException {
		currentArgument++;
		try {
			stringArgs.get(argChar).set(args[currentArgument]);
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_STRING;
		}
	}

	private boolean isIntegerArg(char argChar) {
		ArgumentMarshaler m = marshalers.get(argChar);
		return m instanceof IntegerArgumentMarshaler;
	}

	private void setIntArgs(char argChar) throws ArgsException {
		currentArgument++;
		String parameter = null;
		try {
			parameter = args[currentArgument];
			intArgs.get(argChar).set(parameter);
		} catch (ArrayIndexOutOfBoundsException e) {
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_INTEGER;
			throw new ArgsException();
		} catch (ArgsException e) {
			valid = false;
			errorArgumentId = argChar;
			errorParameter = parameter;
			errorCode = ErrorCode.MISSING_INTEGER;
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
		if (unexpectedArguments.size() > 0) {
			return unexpectedArgumentMessage();
		} else
			switch (errorCode) {
				case MISSING_STRING:
					return String.format("Could not find string parameter for -%c.",
							errorArgumentId);
				case OK:
					throw new Exception("TILT: Should not get here.");
				case MISSING_INTEGER:
					break;
				default:
					break;
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
		ArgumentMarshaler am = booleanArgs.get(arg);
		return am != null && (Boolean) am.get();
	}

	public String getString(char arg) {
		ArgumentMarshaler am = stringArgs.get(arg);
		return am == null ? "" : (String) am.get();
	}

	public int getInt(char arg) {
		ArgumentMarshaler am = intArgs.get(arg);
		return am == null ? 0 : (Integer) am.get();
	}

	public boolean has(char arg) {
		return argsFound.contains(arg);
	}

	public boolean isValid() {
		return valid;
	}

}