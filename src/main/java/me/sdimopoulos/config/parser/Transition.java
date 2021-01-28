package me.sdimopoulos.config.parser;

/**
 * Transition rule used in the FSM
 * 
 * A transition between states. The transitions are attached to a {@link State}
 * object which shows the origin of the transition. A property transitToState
 * contains the target transition. The rule accepts a certain type of input
 * defined in the inputExpected String. Also flags define if during the transition
 * the current state will trasmit its input and its output to the next state.
 *
 */
public class Transition {

	String inputExpected;
	State transitToState;
	boolean willTransmitInput;
	boolean willTransmitParsed;

	Transition(String input, State transitTo, boolean willTransmit)
	{
		inputExpected = input;
		transitToState = transitTo;
		willTransmitInput = willTransmit;
		willTransmitParsed = false;
	}
	
	/* Getters and Setters begin here */
	public boolean isWillTransmitParsed() {
		return willTransmitParsed;
	}

	public Transition setWillTransmitParsed(boolean willTransmitParsed) {
		this.willTransmitParsed = willTransmitParsed;
		return this;
	}
}
