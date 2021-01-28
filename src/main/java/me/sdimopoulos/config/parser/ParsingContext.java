package me.sdimopoulos.config.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.sdimopoulos.config.Config;

/**
 * It is used throughout the file parsing to store the extracted variables
 *
 * Variables are saved in a Map. This decision was made in order to make the
 * parsed variable set extendible. If a new variable name needs to get parsed
 * in the future, it can be stores in the Map in the same way. Additionally,
 * the current Config object, the input overrides and the current group settings
 * name is kept here.
 */
public class ParsingContext {
	private String currentSettingsGroup;
	private List<String> overrides;
	private Config config;
	private Map<String, Object> parsedVariables;
	private String [] parsedVariablesName;

	/**
	 * Constructor
	 * 
	 * @param config A {@link Config} object that will contain the configuration
	 * @param overrides The overrides list given as input from user
	 */
	public ParsingContext (Config config, List<String> overrides) {
		super();
		this.setCurrentSettingsGroup("");
		this.setOverrides(overrides);
		this.setConfig(config);
		this.parsedVariables = new HashMap<String,Object>();
	}

	/**
	 * Saves an array with the parsed variable names.
	 * 
	 * It has been used in a first parsing attempt using RegEx, but left because
	 * it seemed cool to have the names for reference.
	 * 
	 * @param variableNames An array of names
	 */
	public void setupParsedVariables(String [] variableNames) {
		this.parsedVariablesName = variableNames;
	}

	@Override
	public String toString() {
		return "ParsingContext [currentSettingsGroup=" + currentSettingsGroup + ", parsedVariables=" + parsedVariables
				+ "]";
	}

	/**
	 * Resets the Map of parsed variables to empty
	 */
	public void resetParsedVariables() {
		this.parsedVariables = new HashMap<String,Object>();
	}

	/* Getters and Setters begin here */
	
	public Object getParsedVariableValue(String variableName) {
		return parsedVariables.get(variableName);
	}

	public void setParsedVariableValue(String variableName, Object variableValue) {
		parsedVariables.put(variableName, variableValue);
	}


	public String getCurrentSettingsGroup() {
		return currentSettingsGroup;
	}

	public ParsingContext setCurrentSettingsGroup(String currentSettingsGroup) {
		this.currentSettingsGroup = currentSettingsGroup;
		return this;
	}


	public Config getConfig() {
		return config;
	}


	private void setConfig(Config config) {
		this.config = config;
	}

	public List<String> getOverrides() {
		return overrides;
	}

	private ParsingContext setOverrides(List<String> overrides) {
		this.overrides = overrides;
		return this;
	}

}
