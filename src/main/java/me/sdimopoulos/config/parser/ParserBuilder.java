package me.sdimopoulos.config.parser;

import java.util.regex.Matcher;

/**
 * Builds a Parser object using a Deterministic FSM based logic or a 
 * Non-deterministic version with the help of Regular Expressions
 *
 */
public class ParserBuilder {

	
	public Parser buildParserWithFSM(FsmState fsmState)
	{
		return new FsmParser(fsmState);
	}
	
	public Parser buildParserWithRegEx(Matcher [] matcherArray)
	{
		return new RegExParser(matcherArray);
	}

}
