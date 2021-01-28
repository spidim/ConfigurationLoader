package me.sdimopoulos.config.parser;

/**
 * Thrown when parsing of the input file fails
 *
 */
public class ParsingConfigurationException extends Exception {

	public ParsingConfigurationException(String string) {
		super(string);
	}

	private static final long serialVersionUID = -5470164902552222477L;

}
