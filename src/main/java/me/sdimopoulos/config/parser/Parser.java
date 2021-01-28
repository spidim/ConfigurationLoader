package me.sdimopoulos.config.parser;

import java.util.Arrays;
import java.util.List;

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
 * Parses input lines from the configuration file using an FSM
 * 
 * It requires an FsmState object in initialization, which is used to parse the
 * input lines. After each line the Parser must be reset to start again with the
 * next. It uses a {@link ParsingContext} object to keep track of various aspects
 * of the parsing. This object is provided as argument in method calls. After 
 * successful parsing of a line, the context gets updated and it can be used to
 * update the Config object.
 *
 * TODO: Accept another type of parsing engine besides FSM, by using an interface
 * 
 */
public class Parser {

	FsmState fsmState;
	String currentLine;
	int currentCharIndex;

	/**
	 * Constructor
	 * 
	 * @param fsmState A newly created FsmState object that contains a properly
	 * setup FSM used for line parsing.
	 */
	Parser(FsmState fsmState)
	{
		this.fsmState = fsmState;
		this.resetParser();
	}

	/**
	 * Resets the parser and the associated FSM to make them ready for next line
	 */
	public void resetParser()
	{
		this.currentCharIndex = 0;
		this.currentLine = "";
		this.fsmState.reset();
	}

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
	public void parseSingleLineAndUpdateContext(String line, ParsingContext parsingCtx)
			throws ParsingConfigurationException
	{
		this.currentLine = line;
		Transition transition;
		Character currChar = nextCharacter();
		// First skip leading whitespace
		while (currChar!=null && Character.isWhitespace(currChar))
		{
			currChar = nextCharacter();
		}
		// Parse the remaining line if any left
		if(currChar!=null)
		{
			for( ; currChar != null; currChar = nextCharacter())
			{
				transition = this.fsmState.getCurrentState().parseInput(currChar);
				this.fsmState.updateUsingTransition(transition);
				boolean willUpdateContext = checkIfContextWillUpdate();
				if(willUpdateContext)
				{
					updateContext(parsingCtx);
				}
				this.fsmState.changeState(currChar);
			}
			if (willTerminate())
			{
				updateContext(parsingCtx);
			}
			else
			{
				throw new ParsingConfigurationException("Found unparsable line with content: "
						+ String.format("%s", line));
			}
		}
	}

	/**
	 * Returns the next character and updates the index
	 * 
	 * @return The Character or null if EOL met
	 */
	Character nextCharacter()
	{
		if (currentCharIndex<currentLine.length())
		{
			return currentLine.charAt(currentCharIndex++);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Checks if there is a character to parse next
	 * 
	 * @return true or false
	 */
	boolean hasNextCharacter()
	{
		return currentCharIndex < currentLine.length();
	}

	/**
	 * Checks to see if the parsing will terminate normally in the current state
	 * 
	 * If the state is final and all the input is consumed we have a normal
	 * termination. Otherwise the input is not acceptable.
	 * @return true or false
	 */
	private boolean willTerminate()
	{
		return this.fsmState.getCurrentState().isFinal() && !hasNextCharacter();
	}

	/**
	 * Check to see if the parsing context should get updated.
	 * 
	 * The parsing context is not updated if the next state is receiving the 
	 * current character or if the transition is in self
	 * 
	 * @return true or false
	 */
	private boolean checkIfContextWillUpdate()
	{
		boolean willUpdateContext;
		if (this.fsmState.isNextStateWillReceiveInput()||
				this.fsmState.getNextState() == this.fsmState.getCurrentState())
			willUpdateContext = false;
		else
			willUpdateContext = true;
		return willUpdateContext;
	}


	/**
	 * Updates a parsing context with extracted variables
	 * 
	 * The parsing context is provided as argument and is updated with the results
	 * of the current parsing step. Depending on the the extracted variables (
	 * if any at all) the context gets updated.
	 * 
	 * @param parsingCtx A ParsingContext object
	 * @throws ParsingConfigurationException is thrown if a value is not expected
	 */
	private void updateContext(ParsingContext parsingCtx) 
			throws ParsingConfigurationException
	{
		switch(this.fsmState.getCurrentState().getParsedVariable())
		{
		case SETTING:
			parsingCtx.setParsedVariableValue("setting", 
					this.fsmState.returnOutput());
			break;
		case OVERRIDE:
			parsingCtx.setParsedVariableValue("override", 
					this.fsmState.returnOutput());
			break;
		case VALUE:
			parsingCtx.setParsedVariableValue("value", 
					getValueWithProperType(this.fsmState
							.getCurrentState().getParsedType()));
			break;
		case GROUP:
			parsingCtx.setParsedVariableValue("group", 
					this.fsmState.returnOutput());
			break;
		default:
			break;
		}
	}

	/**
	 * Extracts a value of specified type from the parsing output.
	 * 
	 * A parsing type is given as argument. Tries to extract that type of value
	 * from the output buffer. If the value does not met the type requirements
	 * a {@link ParsingConfigurationException} is thrown
	 * 
	 * @param type The ParseType to extract
	 * @return An Object that contains the value in the specified type
	 * @throws ParsingConfigurationException thrown if the value is not of the 
	 * expected type.
	 */
	private Object getValueWithProperType(ParseType type) 
			throws ParsingConfigurationException {
		Object returnValue;
		switch(type)
		{
		case STRING:
			returnValue = this.fsmState.returnOutput();
			break;
		case BOOLEAN:
			returnValue = convertToBoolean(this.fsmState.returnOutput());
			break;
		case NUMBER:
			returnValue = convertToLong(this.fsmState.returnOutput());
			break;
		case ARRAY:
			returnValue = convertToArray(this.fsmState.returnOutput());
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
	 * Accepted values are yes, no, true, false. 0 and 1 could not be used
	 * because there was an ambiguity with the integer type and this is not
	 * acceptable in a Deterministic FSM.
	 * 
	 * @param value The String to convert
	 * @return A boolean value
	 * @throws ParsingConfigurationException thown on unexpected type
	 */
	private boolean convertToBoolean(String value) 
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
	private Long convertToLong(String value) 
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
	List<String> convertToArray(String value)
	{
		return Arrays.asList(value.split(","));

	}

}
