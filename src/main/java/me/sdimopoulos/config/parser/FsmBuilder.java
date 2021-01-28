package me.sdimopoulos.config.parser;


/**
 * Builder for the FSM used to parse the configuration settings file
 * 
 * The builder constructs an appropriate Deterministic FSM structure for the 
 * given problem. A number of states and the corresponding transitions are
 * defined and finally a new FsmState is returned that can be used to
 * repeatedly parse lines from the input file. 
 * A detail to take into account in the current FSM implementation is that
 *  you have to always add wildcard rules last to evaluate first the specific 
 * input ones. For example if you have a transition accepting input char 'A' 
 * and another one accepting any character, you must add the input A first 
 * and then any character wildcard to ensure that 'A' takes precedence in 
 * the evaluation.
 */

public class FsmBuilder {
	
	
	/** Builds the FSM required to parse the given text file
	 * 
	 * First the states are defined and then the transitions. The state array is used
	 * mostly for convenience, only the reference to the first state is required
	 * to keep the whole structure in memory and this is passed to FsmState. 
	 * A 13 state FSM is used to parse all possible input lines and extract
	 * information about groups, setting names, setting values and overrides.
	 * The FsmState is returned and can be used by the {@link Parser}
	 * 
	 * 
	 * @return FsmState object which can be used for parsing input
	 */
	public FsmState buildFSM()
	{
		int numberOfStates = 13;
		State [] allStates = new State[numberOfStates];
		for(int i = 0; i < numberOfStates; i++)
		{
			allStates[i] = new State(i);
		}
		allStates[1].setFinal(true);
		allStates[2].setParsedVariable(ParseVariable.SETTING);
		allStates[3].setParsedVariable(ParseVariable.GROUP);
		allStates[4].setParsedVariable(ParseVariable.OVERRIDE);
		allStates[5].setFinal(true);
		allStates[8].setParsedVariable(ParseVariable.VALUE)
			.setParsedType(ParseType.STRING);
		allStates[9].setFinal(true)
			.setParsedVariable(ParseVariable.VALUE)
			.setParsedType(ParseType.NUMBER);
		allStates[10].setFinal(true)
			.setParsedVariable(ParseVariable.VALUE)
			.setParsedType(ParseType.BOOLEAN);
		allStates[11].setFinal(true)
			.setParsedVariable(ParseVariable.VALUE)
			.setParsedType(ParseType.ARRAY);
		allStates[12].setFinal(true)
			.setParsedVariable(ParseVariable.VALUE)
			.setParsedType(ParseType.STRING);

		allStates[0].addTransition(new Transition(";",allStates[1],false));
		allStates[0].addTransition(new Transition("\\w",allStates[2],true));
		allStates[0].addTransition(new Transition("_",allStates[2],true));
		allStates[0].addTransition(new Transition("[",allStates[3],false));
		allStates[1].addTransition(new Transition("\\*",allStates[1],false));
		allStates[2].addTransition(new Transition("\\w",allStates[2],true));
		allStates[2].addTransition(new Transition("_",allStates[2],true));
		allStates[2].addTransition(new Transition("<",allStates[4],false));
		allStates[2].addTransition(new Transition(" ",allStates[6],false));
		allStates[3].addTransition(new Transition("\\w",allStates[3],true));
		allStates[3].addTransition(new Transition("_",allStates[3],true));
		allStates[3].addTransition(new Transition("]",allStates[5],false));
		allStates[4].addTransition(new Transition("\\w",allStates[4],true));
		allStates[4].addTransition(new Transition("_",allStates[4],true));
		allStates[4].addTransition(new Transition(">",allStates[6],false));
		allStates[5].addTransition(new Transition("\\s",allStates[5],false));
		allStates[5].addTransition(new Transition(";",allStates[1],false));
		allStates[6].addTransition(new Transition("\\s",allStates[6],false));
		allStates[6].addTransition(new Transition("=",allStates[7],false));
		allStates[7].addTransition(new Transition("\\s",allStates[7],false));
		allStates[7].addTransition(new Transition("\"",allStates[8],false));
		allStates[7].addTransition(new Transition("\\d",allStates[9],true));
		allStates[7].addTransition(new Transition("\\w",allStates[10],true));
		allStates[7].addTransition(new Transition("_",allStates[10],true));
		allStates[7].addTransition(new Transition("/",allStates[12],true));
		allStates[8].addTransition(new Transition("\"",allStates[5],false));
		allStates[8].addTransition(new Transition("\\*",allStates[8],true));
		allStates[9].addTransition(new Transition("\\d",allStates[9],true));
		allStates[9].addTransition(new Transition("\\s",allStates[5],false));
		allStates[9].addTransition(new Transition(";",allStates[1],false));
		allStates[10].addTransition(new Transition("\\w",allStates[10],true));
		allStates[10].addTransition(new Transition("_",allStates[10],true));
		allStates[10].addTransition(new Transition(",",allStates[11],true)
				.setWillTransmitParsed(true));
		allStates[10].addTransition(new Transition("\\s",allStates[5],false));
		allStates[10].addTransition(new Transition(";",allStates[1],false));
		allStates[11].addTransition(new Transition("\\w",allStates[11],true));
		allStates[11].addTransition(new Transition("_",allStates[11],true));
		allStates[11].addTransition(new Transition(",",allStates[11],true));
		allStates[11].addTransition(new Transition("\\s",allStates[5],false));
		allStates[11].addTransition(new Transition(";",allStates[1],false));
		allStates[12].addTransition(new Transition("\\w",allStates[12],true));
		allStates[12].addTransition(new Transition("\\d",allStates[12],true));
		allStates[12].addTransition(new Transition("_",allStates[12],true));
		allStates[12].addTransition(new Transition("/",allStates[12],true));
		allStates[12].addTransition(new Transition("\\s",allStates[5],false));
		allStates[12].addTransition(new Transition(";",allStates[1],false));
		
		return new FsmState(allStates[0]);
	}

}
