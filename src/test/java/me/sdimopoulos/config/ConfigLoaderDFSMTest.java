package me.sdimopoulos.config;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import me.sdimopoulos.config.Config;
import me.sdimopoulos.config.ConfigLoader;


/**
 * Basic unit testing of the ConfigLoader class using DFSM at the top-level parsing capability
 *
 * The sample configuration file is given. Then
 * all the example queries are tested.
 *
 */
public class ConfigLoaderDFSMTest {

	Config config;

	@Before
	public void readConfiguration()
	{
		List<String> overrides = Arrays.asList(new String[] {"deprecated", "production"});
		ConfigLoader configLoader = new ConfigLoader();
		config = configLoader.loadConfig("src/test/resources/server.conf", overrides);
	}
	@Test
	public void testCompletedExecution() {
		Assert.assertNotNull(config);
	}

	@Test
	public void testSimpleLongParsing()
	{
		Assert.assertEquals(52346850l,
				config.get("core").get("max_bytes_per_request"));
	}

	@Test
	public void testSimpleStringParsing()
	{
		Assert.assertEquals("Websocket request hadler",
				config.get("websockets").get("description"));
	}

	@Test
	public void testUnknownKeyFetchAsEmpty()
	{
		Assert.assertEquals("",
				config.get("websockets").get("unknown_key"));
	}

	@Test
	public void testSimpleArrayParsing()
	{
		Assert.assertEquals(Arrays.asList("array", "containing", "arguments"),
				config.get("rest").get("arguments"));
	}

	@Test public void testSimpleBooleanParsing()
	{
		Assert.assertEquals(false, config.get("websockets").get("enabled"));
	}

	@Test
	public void testSimplePathParsing()
	{
		Assert.assertEquals("/opt/yourcompany/restAPI/v1", config.get("rest").get("config_path"));

	}

}
