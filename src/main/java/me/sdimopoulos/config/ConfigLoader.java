package me.sdimopoulos.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import me.sdimopoulos.config.parser.FsmBuilder;
import me.sdimopoulos.config.parser.FsmParser;
import me.sdimopoulos.config.parser.Parser;
import me.sdimopoulos.config.parser.ParserBuilder;
import me.sdimopoulos.config.parser.ParsingConfigurationException;
import me.sdimopoulos.config.parser.ParsingContext;
import me.sdimopoulos.config.parser.RegExBuilder;
/**
 * The Configuration Loader, contains the loadConfig method
 * 
 * Acts as the main entry point for demonstration run plus performs some top-level
 * actions, such as creating the configuration group sections and adding settings.
 * 
 *  TODO: Add a logger and replace all std out prints
 */ 
public class ConfigLoader {

	/**
	 * Use this main function for testing.
	 * 
	 * A simple invocation of the loadConfig() function done for demo
	 * Loads the provided sample file located in the description of
	 * the challenge. After successful loading prints the loading time.
	 * Then prints on standard output the example settings.
	 * @param args This function ignores cmd arguments
	 */
	public static void main(String[] args) {
		
		try {
			List<String> overrides = Arrays.asList("stage", "production");
			ConfigLoader configLoader = new ConfigLoader();
			long startTime = System.nanoTime();
			Config config = configLoader.loadConfig("src/test/resources/server.conf", 
					overrides);
			long estimatedTime = System.nanoTime() - startTime;
			System.out.println(String.format("Configuration loaded in %.4f seconds",
					estimatedTime/10e9));
			System.out.println(config.get("core").get("max_bytes_per_request"));
			System.out.println(config.get("websockets").get("description"));
			System.out.println(config.get("websockets").get("enabled"));
			System.out.println(config.get("rest").get("arguments"));
			System.out.println(config.get("rest").get("config_path"));
			System.out.println(config.get("core"));
		}
		catch(RuntimeException e)
		{
			System.err.println(e.getMessage());
			System.err.println("Error detected. Exiting...");
		}
	}

	
	/**
	 * Loads the configuration from path given a list of overrides
	 * 
	 * This method is called with a local path where a file containing
	 * the settings is located. A list of overrides is given to select
	 * the suitable settings in case there are many available.
	 * A {@link ParsingContext} is used to have available the configuration
	 * object and the list of overrides given as input throughout the process.
	 * A {@link FsmParser} object is used to do the line by line parsing of input.
	 * {@link ParserBuilder} is the builder of FsmParser objects.
	 * Parsing logic is based on a Deterministic FSM. The state of the FSM is 
	 * saved and updated in {@link FsmState}. {@link FsmBuilder} generates the
	 * FSM object with a structure that can parse the files in question.
	 * 
	 * @param filePath The path where to find the settings file
	 * @param overrides A list of overrides
	 * @return A {@link Config} objects containing the parsed configuration
	 * @throws RuntimeException in case it encounters a non-parsable line
	 * in the file
	 */
	public Config loadConfig(String filePath, List<String> overrides)
			throws RuntimeException
	{
		Config config = new Config();
		try(BufferedReader buffReader = 
				Files.newBufferedReader(Paths.get(filePath))) {
			System.out.print("\n");
			ParsingContext parsingCtx = new ParsingContext(config, overrides);
			parsingCtx.setupParsedVariables(new String [] {"group","setting",
					"override","value"});
			FsmBuilder fsmBuilder = new FsmBuilder();
			ParserBuilder parserBuilder = new ParserBuilder();
			Parser fsmParser = parserBuilder.buildParserWithFSM(
												fsmBuilder.buildFSM());
			for(String line=buffReader.readLine();
					line!=null&&!line.isEmpty();
					line=buffReader.readLine())
			{
				fsmParser.parseSingleLineAndUpdateContext(line, parsingCtx);
				updateConfig(parsingCtx);
				parsingCtx.resetParsedVariables();
				fsmParser.resetParser();
			}
			System.out.print("\n");
		}
		catch(ParsingConfigurationException|IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return config;
	}
	
	public Config loadConfigRegEx(String filePath, List<String> overrides)
			throws RuntimeException
	{
		Config config = new Config();
		try(BufferedReader buffReader = 
				Files.newBufferedReader(Paths.get(filePath))) {
			System.out.print("\n");
			ParsingContext parsingCtx = new ParsingContext(config, overrides);
			parsingCtx.setupParsedVariables(new String [] {"group","setting",
					"override","value"});
			RegExBuilder regexBuilder = new RegExBuilder();
			ParserBuilder parserBuilder = new ParserBuilder();
			Parser regexParser = parserBuilder.buildParserWithRegEx(regexBuilder.buildRegEx());
			for(String line=buffReader.readLine();
					line!=null&&!line.isEmpty();
					line=buffReader.readLine())
			{
				regexParser.parseSingleLineAndUpdateContext(line, parsingCtx);
				updateConfig(parsingCtx);
				parsingCtx.resetParsedVariables();
				regexParser.resetParser();
			}
			System.out.print("\n");
		}
		catch(ParsingConfigurationException|IOException e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return config;
	}

	
	/**
	 * Updates the Config object inside ParsingContext
	 * 
	 * Given the current parsing context, it checks if there is new information
	 * and the Config object must be updated. Checks if a new group section was
	 * found and adds the group in the object. Similarly, if a setting is read
	 * it adds it in the current section.
	 * 
	 * @param parsingCtx {@link ParsingContext} that contains current parsing info
	 */
	void updateConfig(ParsingContext parsingCtx)
	{
		if(checkCreateNewGroup(parsingCtx))
		{
			System.out.print(".");
		}
		else if(checkCreateNewSetting(parsingCtx))
		{
			//intentionally left empty. add symbol to track parsing progress
			System.out.print(""); 
		}
		else
		{
			//intentionally left empty. add symbol to track parsing progress
			System.out.print("");
		}
	}

	/**
	 * Checks for new group sections and adds it to Config object
	 * 
	 * If a new group section was discovered in the file, it puts it in the
	 * {@link Config} object and sets the current group to it in the context.
	 * If the group was already in the object, because there was another section
	 * in the file for it, it just updates current group in the context
	 * @param parsingCtx Parsing context
	 * @return true if the group was created or false if not.
	 */
	boolean checkCreateNewGroup(ParsingContext parsingCtx)
	{
		boolean groupCreated;
		String newGroup = (String) parsingCtx.getParsedVariableValue("group");
		if (newGroup != null && !checkIfGroupSectionIsAlreadyIn(newGroup, parsingCtx))
		{
			parsingCtx.getConfig().put(newGroup, new ConfigGroup());
			parsingCtx.setCurrentSettingsGroup(newGroup);
			groupCreated = true;
		}
		else if (newGroup != null && 
				checkIfGroupSectionIsAlreadyIn(newGroup, parsingCtx))
		{
			parsingCtx.setCurrentSettingsGroup(newGroup);
			groupCreated = false;
		}
		else
		{
			groupCreated = false;
		}
		return groupCreated;
	}

	
	/**
	 * Checks and adds a setting to ConfigGroup object
	 * 
	 * Checks if there is a valid setting in the current context. Valid setting
	 * means that one the following is true: 
	 * 1. the setting name (key) is not null and it is a valid override 
	 * {@link #checkIfOverride(String, ParsingContext))}
	 * 2. the setting name is not null and is not an override but it is a new 
	 * setting name, thus avoiding replacing a previous override.
	 * @param parsingCtx Parsing context
	 * @return true if the group was created or false if not.
	 */
	boolean checkCreateNewSetting(ParsingContext parsingCtx)
	{
		boolean settingCreated;
		String newKey = (String) parsingCtx.getParsedVariableValue("setting");
		Object newValue = parsingCtx.getParsedVariableValue("value");
		String override = (String) parsingCtx.getParsedVariableValue("override");
		boolean isOverride = checkIfOverride(override, parsingCtx);
		if (newKey != null && (isOverride || 
				!isOverride && !checkIfSettingIsAlreadyIn(newKey, parsingCtx)))
		{
			parsingCtx.getConfig().get(parsingCtx.getCurrentSettingsGroup())
				.put(newKey, newValue);
			settingCreated = true;
		}
		else
		{
			settingCreated = false;
		}
		return settingCreated;
	}
	
	/**
	 * Checks if the group name is already in the Config object.
	 * @param key The group name to check
	 * @param parsingCtx The current parsing context
	 * @return true or false
	 */
	boolean checkIfGroupSectionIsAlreadyIn(String key, ParsingContext parsingCtx)
	{
		return parsingCtx.getConfig().containsKey(key);
	}

	/**
	 * Checks if the setting name is already in the current group.
	 * @param key The setting name to check
	 * @param parsingCtx The current parsing context
	 * @return true or false
	 */
	boolean checkIfSettingIsAlreadyIn(String key, ParsingContext parsingCtx)
	{
		return parsingCtx.getConfig().get(parsingCtx.getCurrentSettingsGroup())
				.containsKey(key);
	}

	/**
	 * Checks if it is valid override
	 * A valid override needs to have a non null override parsed name and
	 * must be included in the overrides list provided by the user
	 * @param override The override variable to check
	 * @param parsingCtx The current parsing context
	 * @return true or false
	 */
	boolean checkIfOverride(String override, ParsingContext parsingCtx)
	{
		return (override != null && parsingCtx.getOverrides().stream()
				.anyMatch(item -> item.equals(override)));
	}

}
