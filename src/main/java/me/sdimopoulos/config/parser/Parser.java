package me.sdimopoulos.config.parser;

import java.util.Arrays;
import java.util.List;

public interface Parser {

	/**
	 * Enum containing possible variables to parse
	 *
	 */
	enum ParseVariable {
		GROUP,
		SETTING,
		OVERRIDE,
		VALUE,
		NA
	}

	/**
	 * Enum containing the accepted types that a value variable can take
	 */
	enum ParseType {
		STRING,
		BOOLEAN,
		NUMBER,
		ARRAY,
		NA
	}

	/**
	 * Resets the parser and the associated FSM to make them ready for next line
	 */
	void resetParser();

	/**
	 * Parses a single line of input and updates the parsing context
	 * 
	 * The input line is provided as String. First it wipes out leading white-
	 * space and then processes remaining character by character using the FSM.
	 * Context is updated in states that extract variable of interest values.
	 * If it encounters input that is not accepted by the FSM, throws an 
	 * Exception and exits. 
	 * 
	 * @param line The input line to parse
	 * @param parsingCtx The context of parsing object
	 * @throws ParsingConfigurationException When unparsable input is met
	 */
	void parseSingleLineAndUpdateContext(String line, ParsingContext parsingCtx) throws ParsingConfigurationException;

	
	/**
	 * Extracts a value of specified type
	 * 
	 * A parsing type is given as argument. Tries to extract that type of value
	 * from the output buffer. If the value does not met the type requirements
	 * a {@link ParsingConfigurationException} is thrown
	 * 
	 * @param value The String value to extract
	 * @param type The ParseType to extract
	 * @return An Object that contains the value in the specified type
	 * @throws ParsingConfigurationException thrown if the value is not of the 
	 * expected type.
	 */
	static Object getValueWithProperType(String value, ParseType type) 
			throws ParsingConfigurationException {
		Object returnValue;
		switch(type)
		{
		case STRING:
			returnValue = value;
			break;
		case BOOLEAN:
			returnValue = convertToBoolean(value);
			break;
		case NUMBER:
			returnValue = convertToLong(value);
			break;
		case ARRAY:
			returnValue = convertToArray(value);
			break;
		default:
			returnValue = null;
			break;
		}
		return returnValue;
	}

	/**
	 * Extracts a boolean value from the given string
	 * 
	 * Accepted values are yes, no, true, false.
	 * 
	 * @param value The String to convert
	 * @return A boolean value
	 * @throws ParsingConfigurationException thrown on unexpected type
	 */
	static boolean convertToBoolean(String value) 
			throws ParsingConfigurationException
	{
		boolean valueBoolean;
		String toCheck = value.toLowerCase();
		if (toCheck.contains("true")||toCheck.contains("yes"))
		{
			valueBoolean = true;
		}
		else if ((toCheck.contains("false")||toCheck.contains("no")))
		{
			valueBoolean = false;
		}
		else
		{
			throw new ParsingConfigurationException("Boolean value must be "
					+ "one of true, yes, false, no ["+value+"]");
		}
		return valueBoolean;
	}

	/**
	 * Extracts a long value from the given string
	 * 
	 * Accepted values are all integer up to long precision.
	 * 
	 * @param value The String to convert
	 * @return A Long value
	 * @throws ParsingConfigurationException thrown on unexpected type
	 */
	static Long convertToLong(String value) 
			throws ParsingConfigurationException
	{
		Long valueInteger;
		try {
			valueInteger = Long.parseLong(value);
		}
		catch (NumberFormatException ex)
		{
			throw new ParsingConfigurationException(String.format("Cannot "
					+ "convert %s to integer: %s",value));
		}
		return valueInteger;
	}

	/**
	 * Converts a String to an ArrayList
	 * 
	 * @param value The String to convert
	 * @return The ArrayList
	 */
	static List<String> convertToArray(String value)
	{
		return Arrays.asList(value.split(","));

	}
}