package me.sdimopoulos.config;

import java.util.HashMap;
import java.util.Map;

/**
 * The configuration object that keeps all sections and settings.
 * Extends the HashMap with String key and a Map<String,Object> value.
 * To avoid returning nulls when a section is missing, it overrides the get(key)
 * method and returns an empty hashmap in case the key is missing.
 */
public class Config extends HashMap<String,Map<String,Object>>{

	private static final long serialVersionUID = -1038974014203658662L;

	@Override
	public Map<String,Object> get(Object key)
	{
		Map<String,Object> value = super.get(key);

		if (value==null)
		{
			return new HashMap<>();
		}
		else
		{
			return value;
		}
	}

}
