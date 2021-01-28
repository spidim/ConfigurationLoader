# ConfigurationLoader
Parses and loads a configuration file into an object.
The object can be queried at 2 levels, the first is the group section and the
second is the settings in each section.

# Design choices
1. Both top-level sections and second-level settings are implemented as HashMaps.
The Hashmap provides constant get and put operations, as long as there are not
many conflicts. Java implementation of HashMap adapts the size of the Map when
the size increases above a fraction of the initial capacity, all these
can be configured. Re-hashing is done when resizing, but in this case the
performance was not degraded as you can see from the performance results.

1. The file is read using a BufferedReader and a line by line fashion.

1. The parsing algorithm uses a Deterministic Finite State Machine to decide
about the acceptance of the input and where to extract the variables from. It
works very fast because there is no backtracking. It can be easily generalized 
in order to parse other templates of input files. I must note here that a first
attempt to parse the lines using Regular Expressions was quickly abandoned due
to inefficiency.

1. Builder-like patterns are used to construct the Parser and FSM objects, but
no further generalization of the concepts of parsing and underlying algorithm was
made due to lack of time (Interfaces etc.)

1. The parser fails fast, if a character is not parsable, stops the file parsing
and exits with an exception.

1. The FSM used to parse all possible combinations of the input file, needed
13 states. A figure would be very handy here, but I have only one in a paper written
with pencil. An ASCII version of the figure in this README was envisioned, but
no time was found.

# How to compile
The solution is provided as a Maven project. It requires Java 1.8+ and it has
only minor dependencies during the testing stage. To compile simply run

`mvn compile`

Classes will be found under targer sub-directory. To run the demonstration just
issue at the command line

`java -cp java -cp target/classes me.sdimopoulos.config.ConfigLoader`

This command will use loadConfig() to load the sample configuration given as part
of the challenge. A time measurment of the process is given and then example
queries are run.

# How to test
The sample configuration file and all the example queries were added to a Junit
test file named ConfigLoaderTest. Running this test ensures that the configuration
file is loaded successfully and the test queries run as expected. Another longer
test was added to test different random versions of the configuration file and also
measure the performance of the loading process and the queries. For more information
check the ConfigLoaderLongTest.java but, to give you a summary, using the default
test parameters the performance tests in an i7 CPU, give: 26-27 milliseconds on 
average to load a file of 240,000 entries (100 sections with 2400 settings each)
and an average query time of 0.1 microseconds (on average for 100,000 random
queries).

You can run the tests using the following command:

`mvn test`

# Future TODO
* Add logging. Curently there are some standard output prints mainly kept for demonstration.

* Add interfaces for the Parser and the Parsing Algorithm (FSM). This will
generalize the parsing and make it more easy to add other file formats and 
parsing algorithms.
 
