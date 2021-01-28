package me.sdimopoulos.config.parser;

/**
 * Builds a Parser object using an FSM based logic
 * 
 * TODO: Use here an interface to generalize the parsing algorithm (now only FSM
 * based one is accepted)
 *
 */
public class ParserBuilder {

	
	public Parser buildParserWithFSM(FsmState fsmState)
	{
		return new Parser(fsmState);
	}

}
