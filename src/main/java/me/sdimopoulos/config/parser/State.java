package me.sdimopoulos.config.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a State of the FSM
 * 
 * The state has 1. an Id that is used mainly for human reference, 2. list of 
 * transitions that begin from this state and go to other states., 3. a ParseVariable
 * value that shows the variable name that this state is supposed to extract, 
 * 4. the data type of the value that is expected in case the variable is the value
 * and 5. if the state is final (a state that given input buffer is empty it would
 * allow the parsing to complete successfully).
 *
 */
public class State {
	public int Id;
	private List<Transition> transitions;
	private Parser.ParseVariable parsedVariable;
	private Parser.ParseType parsedType;
	private boolean isFinal;

	
	/**
	 * Constructor
	 * 
	 * The id is given for reference. Other properties are initialised to defs
	 * 
	 * @param id
	 */
	public State(int id)
	{
		Id = id;
		transitions = new ArrayList<>();
		parsedVariable = Parser.ParseVariable.NA;
		parsedType = Parser.ParseType.NA;
		isFinal = false;
	}


	/**
	 * Decides if a rule can accept the input and return the Transition
	 * 
	 * If the input is accepted by a Transition rule, the method returns the 
	 * relevant transition. Rules are inspected sequentially. A rule can be a
	 * wildcard rule:
	 * 1. \\w all alpha
	 * 2. \\d all digits
	 * 3. \\* all chars
	 * 
	 *   or an exact character input rule (only first character is inspected)
	 * 
	 * @param input The Character to check
	 * @return Transition that accepted the input
	 * @throws ParsingConfigurationException thrown if no rule accepts the input
	 */
	Transition parseInput(char input)
			throws ParsingConfigurationException
	{
		boolean inputAccepted = false;
		Transition transitionAccepted = null;
		//System.out.println(String.format("DEBUG: State %d parses char %c",this.Id,input));
		for (Transition transition : this.transitions)
		{
			//System.out.println(String.format("DEBUG: Eval transition (%s,%d,%b)",transition.inputExpected,transition.transitToState.Id,transition.willTransmitInput));
			if(transition.inputExpected.equals("\\w") && Character.isAlphabetic(input)||
					transition.inputExpected.equals("\\d") && Character.isDigit(input)||
					transition.inputExpected.equals("\\s") && Character.isWhitespace(input)||
					transition.inputExpected.startsWith(Character.toString(input))||
					transition.inputExpected.equals("\\*") // any character wildcard
					)
			{
				inputAccepted = true;
				transitionAccepted = transition;
				break;
			}
		}
		if(!inputAccepted)
		{
			throw new ParsingConfigurationException(
					String.format("Found unparsable character [%c]",input)
					);
		}
		else
		{
			return transitionAccepted;
		}
	}

	/**
	 * Adds a new transition in the list of state transitions
	 * 
	 * @param transition The {@link Transition} to add
	 */
	void addTransition(Transition transition)
	{
		transitions.add(transition);
	}

	/* Getters and Setters begin here */
	public Parser.ParseVariable getParsedVariable() {
		return parsedVariable;
	}

	public State setParsedVariable(Parser.ParseVariable parsedVariable) {
		this.parsedVariable = parsedVariable;
		return this;
	}

	public Parser.ParseType getParsedType() {
		return parsedType;
	}

	public State setParsedType(Parser.ParseType parsedType) {
		this.parsedType = parsedType;
		return this;
	}

	public boolean isFinal()
	{
		return isFinal;
	}

	public State setFinal(boolean isFinal)
	{
		this.isFinal = isFinal;
		return this;
	}

}
