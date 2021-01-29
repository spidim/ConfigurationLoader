package me.sdimopoulos.config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.javafaker.Faker;

import me.sdimopoulos.config.Config;
import me.sdimopoulos.config.ConfigLoader;

/**
 * Long test of loading with random inputs and multiple queries
 * 
 * This test mostly measures the performance of the loader and the querying.
 * 
 * Creates a settings file with a large number of input lines (default is 100
 * group settings sections with 200 * 12 lines each, totaling 240000 lines)
 * Then this file is given to the ConfigLoader to load it. Load average is 
 * reported on a number of iterations (default: 10).
 *
 */
public class ConfigLoaderLongTest {

	String settingsHugeFilename = "src/test/resources/huge.conf";
	static int numOfSections = 100;
	static int numOfSettingsPerSection = 200;
	Config config;
	Faker faker = new Faker();
	
	@Before
	public void prepareAndLoad() throws Exception {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(settingsHugeFilename));
	   
		for (int i = 0 ; i < numOfSections ; i++)
		{
			writer.write(String.format("[%s] ;= %s\n", 
					faker.letterify("section??"),
					faker.letterify("????????????")));
			writer.write(String.format(";%s = \"%s\"\n", 
					faker.letterify("????????????"),
					faker.letterify("????????????")));
			for(int j = 0; j < numOfSettingsPerSection ; j++)
			{
				writer.write(String.format("%s = \"%s\"\n", 
						faker.letterify("????????????"),
						faker.letterify("????????????")));
				writer.write(String.format("%s = %s\n", 
						faker.letterify("????????????"),
						faker.numerify("############")));
				writer.write(String.format("%s = %s\n", 
						"query_meA",
						faker.numerify("############")));
				writer.write(String.format("%s = \"%s\"\n", 
						"query_meB",
						faker.letterify("????????????")));
				writer.write(String.format("   ;%s\n", 
						faker.letterify("??????????????????????")));
				writer.write(String.format("%s = %s\n", 
						faker.letterify("????????????"),
						faker.letterify("/???????/?????/")));
				writer.write(String.format("%s = %s\n", 
						faker.letterify("????????????"),
						faker.letterify("yes")));
				writer.write(String.format("%s = %s\n", 
						faker.letterify("????????????"),
						faker.letterify("false")));
				writer.write(String.format("%s = %s\n", 
						faker.letterify("????????????"),
						faker.letterify("false")));
				writer.write(String.format("%s = %s\n", 
						faker.letterify("????????????"),
						faker.letterify("false")));
				writer.write(String.format("%s = %s\n", 
						faker.letterify("?????_???_????<test>"),
						faker.letterify("?????,?????,????")));
				writer.write(String.format("%s = %s\n", 
						faker.letterify("????????????<staging>"),
						faker.letterify("\"??????????????\"")));
			}
		}
		writer.close();
	}

	@Test
	public void testLoadingTimeExecutionDFSM() {
		List<String> overrides = Arrays.asList(new String[] {"test", "staging"});
		ConfigLoader configLoader = new ConfigLoader();
		int iterations = 10;
		long totalTime = 0;
		for (int i = 0 ; i < iterations ; i++)
		{
			long startTime = System.nanoTime();
			config = configLoader.loadConfig(settingsHugeFilename, overrides);
			long estimatedTime = System.nanoTime() - startTime;
			Assert.assertNotNull(config);
			totalTime += estimatedTime;
		}
		System.out.println(String.format("DFSM: Long test average configuration loading time was %.6f millies",
				totalTime/10e6/iterations));
	}
	
	@Test
	public void testLoadingTimeExecutionNDFSM() {
		List<String> overrides = Arrays.asList(new String[] {"test", "staging"});
		ConfigLoader configLoader = new ConfigLoader();
		int iterations = 10;
		long totalTime = 0;
		for (int i = 0 ; i < iterations ; i++)
		{
			long startTime = System.nanoTime();
			config = configLoader.loadConfigRegEx(settingsHugeFilename, overrides);
			long estimatedTime = System.nanoTime() - startTime;
			Assert.assertNotNull(config);
			totalTime += estimatedTime;
		}
		System.out.println(String.format("NDFSM(RegEx): Long test average configuration loading time was %.6f millies",
				totalTime/10e6/iterations));
	}
	
	@Test
	public void testQueryTimeExecutionDFSM() {
		List<String> overrides = Arrays.asList(new String[] {"test", "staging"});
		ConfigLoader configLoader = new ConfigLoader();
		config = configLoader.loadConfig(settingsHugeFilename, overrides);
		Assert.assertNotNull(config);
		int iterations = 25000;
		long totalTime = 0;
		for (int i = 0 ; i < iterations ; i++)
		{
			String key1 = faker.letterify("section??");
			String key2 = faker.letterify("?????????");
			String key3 = faker.letterify("??????????????");
			long startTime = System.nanoTime();
			config.get(key1).get("query_meA");
			config.get(key1).get("query_meB");
			config.get(key1).get(key2);
			config.get(key1).get(key3);
			long estimatedTime = System.nanoTime() - startTime;
			
			totalTime += estimatedTime;
		}
		System.out.println(String.format("DFSM: Long test average query time (N=%d) was %.6f millies",
				iterations*4, totalTime/10e6/iterations*4));
	}
	
	@Test
	public void testQueryTimeExecutionNDFSM() {
		List<String> overrides = Arrays.asList(new String[] {"test", "staging"});
		ConfigLoader configLoader = new ConfigLoader();
		config = configLoader.loadConfigRegEx(settingsHugeFilename, overrides);
		Assert.assertNotNull(config);
		int iterations = 25000;
		long totalTime = 0;
		for (int i = 0 ; i < iterations ; i++)
		{
			String key1 = faker.letterify("section??");
			String key2 = faker.letterify("?????????");
			String key3 = faker.letterify("??????????????");
			long startTime = System.nanoTime();
			config.get(key1).get("query_meA");
			config.get(key1).get("query_meB");
			config.get(key1).get(key2);
			config.get(key1).get(key3);
			long estimatedTime = System.nanoTime() - startTime;
			
			totalTime += estimatedTime;
		}
		System.out.println(String.format("NDFSM(RegEx): Long test average query time (N=%d) was %.6f millies",
				iterations*4, totalTime/10e6/iterations*4));
	}
	
	@After
	public void tearDown() throws IOException
	{
		Files.deleteIfExists(Paths.get(settingsHugeFilename));
	}

}
