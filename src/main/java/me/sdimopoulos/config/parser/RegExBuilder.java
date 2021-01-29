package me.sdimopoulos.config.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builder for the Regular Expression used to parse the configuration settings file
 * 
 * The builder constructs an Non-Deterministic FSM structure based on Java's
 * own Regular Expression engine.
 */

public class RegExBuilder {

		
	/** Builds the Regular Expression required to parse the given text file
	 * 
	 * First the states are defined and then the transitions. The state array is used
	 * mostly for convenience, only the reference to the first state is required
	 * to keep the whole structure in memory and this is passed to FsmState. 
	 * A 13 state FSM is used to parse all possible input lines and extract
	 * information about groups, setting names, setting values and overrides.
	 * The FsmState is returned and can be used by the {@link FsmParser}
	 * 
	 * 
	 * @return Matcher a matcher object that can parse input lines
	 */
		public Matcher[] buildRegEx() {
			String regularExpressionCommentStr = "^\\s*;.*$";
			String regularExpressionGroupStr = "^\\s*\\[(?<group>[\\w_]+)\\]\\s*(?:;.*)?$|";
			
			String regularExpressionSettingStr = "^\\s*(?<setting>\\p{Alpha}[\\w_]*)(?:<(?<override>\\p{Alpha}[\\w_]*)>)?\\s*=\\s*"
					+ "(\"(?<valueString>.*)\"|"
					+ "(?<valuePath>\\/[\\w\\/_]*)|"
					+ "(?<valueBoolean>\\p{Alpha}+)|"
					+ "(?<valueArray>\\p{Alpha}+(,\\p{Alpha}+)+)|"
					+ "(?<valueNumber>\\d+))\\s*(?:;.*)?$";
			Pattern patternComment = Pattern.compile(regularExpressionCommentStr);
			Pattern patternGroup = Pattern.compile(regularExpressionGroupStr);
			Pattern patternSetting = Pattern.compile(regularExpressionSettingStr);
			return new Matcher [] {
					patternComment.matcher(""),
					patternGroup.matcher(""),
					patternSetting.matcher("")};
		}
	
}
