package me.sdimopoulos.config;

import java.util.HashMap;

/**
 * The configuration section object
 * 
 * Extends the HashMap with String key and a Object value.
 * To avoid returning nulls when a setting is missing, it overrides the get(key)
 * method and returns an empty string in case the key is missing */
public class ConfigGroup extends HashMap<String, Object> {

	private static final long serialVersionUID = 6564010360156039943L;

	@Override
	public Object get(Object key)
	{
		Object value = super.get(key);

		if (value==null)
		{
			return "";
		}
		else
		{
			return value;
		}
	}

}
