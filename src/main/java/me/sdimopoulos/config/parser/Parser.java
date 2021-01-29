package me.sdimopoulos.config.parser;

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

}