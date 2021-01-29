package me.sdimopoulos.config.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExParser implements Parser {

	Matcher [] matcherArray;
	String currentLine;
	int currentCharIndex;

	/**
	 * Constructor
	 * 
	 * @param A pattern matcher that can parse input lines
	 */
	RegExParser(Matcher [] matcherArray)
	{
		this.matcherArray = matcherArray;
		this.resetParser();
	}

	/**
	 * Resets the parser and the associated RegExc matcher to 
	 * prepare for next line
	 */
	@Override
	public void resetParser()
	{
		this.currentCharIndex = 0;
		this.currentLine = "";
		for (Matcher matcher : this.matcherArray)
			matcher.reset();
	}

	/**
	 * Parses a single line of input and updates the parsing context
	 * 
	 * The input line is provided as String. First it wipes out leading white-
	 * space and then processes the rest using regular expression matcher.
	 * Context is updated using regex groups.
	 * If it encounters input that is not accepted by the FSM, throws an 
	 * Exception and exits. 
	 * 
	 * @param line The input line to parse
	 * @param parsingCtx The context of parsing object
	 * @throws ParsingConfigurationException When unparsable input is met
	 */
	@Override
	public void parseSingleLineAndUpdateContext(String line, ParsingContext parsingCtx)
			throws ParsingConfigurationException
	{
		this.currentLine = line;
		Character currChar = nextCharacter();
		boolean parsingSuccess = false;
		// First skip leading whitespace
		while (currChar!=null && Character.isWhitespace(currChar))
		{
			currChar = nextCharacter();
		}
		// Parse the remaining line if any left
		if(currChar!=null)
		{
			for (Matcher matcher : this.matcherArray)
			{
				/*System.out.println(String.format("Will reset matcher %s with string %s", matcher.toString(),this.currentLine.substring(currentCharIndex-1)));*/
				matcher.reset(this.currentLine.substring(currentCharIndex-1));
			    if (matcher.matches()) {
			    	parsingSuccess = true;
			    	updateContext(matcher, parsingCtx);
			    	break;
			    }
			}
			if(!parsingSuccess)
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
	 * Updates a parsing context using matcher groups
	 * 
	 * The parsing context is provided as argument and is updated with the results
	 * of the current parsing step. Depending on the the extracted variables (
	 * if any at all) the context gets updated.
	 * 
	 * @param matcher A RegEx Matcher object
	 * @param parsingCtx A ParsingContext object
	 * @throws ParsingConfigurationException is thrown if a value is not expected
	 */
	private void updateContext(Matcher matcher, ParsingContext parsingCtx) 
			throws ParsingConfigurationException
	{
		String [] groupNames = {"group", "setting", "override"};
		for (String groupName : groupNames)
		{
			try
			{
				String groupValue = matcher.group(groupName);
				if (groupValue != null) {
					parsingCtx.setParsedVariableValue(groupName, 
							groupValue);
					/*System.out.println(String
							.format("%s found and update to value %s",
									groupName, groupValue));*/
				}
			}
			catch (IllegalArgumentException exception)
			{
				/*System.out.println(String.format("No %s in RegEx",groupName));*/
			}
		}
		
		HashMap<String,Parser.ParseType> groupValueMap = 
				new HashMap<>();
		groupValueMap.put("valueString", Parser.ParseType.STRING);
		groupValueMap.put("valuePath", Parser.ParseType.STRING);
		groupValueMap.put("valueBoolean", Parser.ParseType.BOOLEAN); 
		groupValueMap.put("valueArray", Parser.ParseType.ARRAY);
		groupValueMap.put("valueNumber", Parser.ParseType.NUMBER);

		for (Map.Entry<String, Parser.ParseType> groupEntry : groupValueMap.entrySet())
		{
			try
			{
				
				String groupName = groupEntry.getKey();
				String groupValue = matcher.group(groupName);
				if (groupValue != null) {
					parsingCtx.setParsedVariableValue("value", 
						Parser.getValueWithProperType(groupValue,
								groupEntry.getValue()));
					/*System.out.println(String
							.format("%s found and update to value %s",
									groupName, groupValue));*/
				break;
				}
			}
			catch (IllegalArgumentException exception)
			{
				/*System.out.println(String.format("No %s in RegEx",groupEntry.getKey()));*/
			}
		}
	}

}
