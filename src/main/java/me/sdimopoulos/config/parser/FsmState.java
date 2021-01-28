package me.sdimopoulos.config.parser;

/**
 * Keeps track of the FSM current and the output.
 * 
 * Initial, current and next states are kept here. Also flags that show
 * if the next state is going to receive the output buffer of the previous
 * and the current input character. The output buffer is kept in a
 *  {@link StringBuilder} which is updated conditionally when changing state.
 *  A reset method is used to set the FsmState to the initial state, preparing
 *  it to process another input line.
 */
public class FsmState {
	
	private StringBuilder outputStringBld;
	private State nextState;
	private State currentState;
	private State initState;
	private boolean nextStateWillReceiveInput;
	private boolean nextStateWillReceiveParsed;
	
	
	/**
	 * Constructor
	 * 
	 * Expects the initial State of the FSM as argument
	 * @param init A {@link State} that is the init state of the FSM.
	 */
	public FsmState(State init)
	{
		this.initState = init;
		this.outputStringBld = new StringBuilder();
		this.currentState = initState;
		this.nextState = null;
		this.nextStateWillReceiveInput = false;
		this.nextStateWillReceiveParsed = false;
	}
	
	
	/**
	 * Resets the FSM in its initial state. Output buffer is discarded and the 
	 * current state is set to the initial
	 */
	public void reset()
	{
		this.currentState = this.initState;
		this.outputStringBld.setLength(0);
		this.nextState = null;
		this.nextStateWillReceiveInput = false;
		this.nextStateWillReceiveParsed = false;
	}
	
	
	/**
	 * Updates the FSM next state based on an input Transition
	 * 
	 * Must be run before a state change. Updates nextState and relates flags
	 * based on a transition that is given as argument
	 * 
	 * @param transition {@link Transition} used to prepare the next state
	 */
	public void updateUsingTransition(Transition transition)
	{
		this.setNextState(transition.transitToState);
		this.setNextStateWillReceiveInput(transition.willTransmitInput);
		this.setNextStateWillReceiveParsed(transition.willTransmitParsed);
	}
	
	/**
	 * Changes the FSM state according to nextState and flags
	 * 
	 * It should be run after {@link #updateUsingTransition(Transition)}
	 * It changes the currentState to the next. Before the state change checks
	 * if the nextState is to receive the current output buffer, and if not
	 * it resets it. Also checks if the current character is to be emitted to the
	 * next state to be used in the output buffer. If there is no actual state
	 * change (transition to self), it just consumes the input char  and places it 
	 * to the output buffer.
	 * 
	 * @param currentChar The currently parsed character
	 */
	public void changeState(char currentChar)
	{
		if (this.getCurrentState()!=this.getNextState() &&
				!this.isNextStateWillReceiveParsed())
		{
			this.outputStringBld.setLength(0);
		}
		
		if (this.getCurrentState()!=this.getNextState() &&
				this.isNextStateWillReceiveInput())
		{
			this.emitPreviousInputChar(currentChar);
		}
		else if(this.getCurrentState()==this.getNextState())
		{
			this.outputStringBld.append(currentChar);
		}
		this.setCurrentState(this.getNextState());

	}

	
	/**
	 * Emits the character to the next state.
	 * 
	 * A character given as argument is emitted to the next state and places
	 * in its output buffer.
	 * 
	 * @param prevInput The input character
	 */
	private void emitPreviousInputChar(char prevInput)
	{
		this.outputStringBld.append(prevInput);
	}
	
	
	/**
	 * Returns current output buffer as String
	 * 
	 * @return A String containing the output buffer content
	 */
	public String returnOutput()
	{
		return outputStringBld.toString();

	}

	/* Getters and Setters begin here */
	
	public State getNextState() {
		return nextState;
	}

	public FsmState setNextState(State nextState) {
		this.nextState = nextState;
		return this;
	}

	public boolean isNextStateWillReceiveInput() {
		return nextStateWillReceiveInput;
	}

	private FsmState setNextStateWillReceiveInput(boolean nextStateWillReceiveInput) {
		this.nextStateWillReceiveInput = nextStateWillReceiveInput;
		return this;
	}
	

	public boolean isNextStateWillReceiveParsed() {
		return nextStateWillReceiveParsed;
	}

	public FsmState setNextStateWillReceiveParsed(boolean nextStateWillReceiveParsed) {
		this.nextStateWillReceiveParsed = nextStateWillReceiveParsed;
		return this;
	}


	public State getCurrentState() {
		return currentState;
	}


	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}


	public State getInitState() {
		return initState;
	}


	public void setInitState(State initState) {
		this.initState = initState;
	}


}
