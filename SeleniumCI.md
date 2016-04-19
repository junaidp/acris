# Maven integration #

| **Task** | **Maven command** | **Decription** |
|:---------|:------------------|:---------------|
| **Eclipse integration** | mvn eclipse:clean eclipse:eclipse | This creates you eclipse based project with all settings required for annotation processor including the .factorypath, .org.eclipse.jdt.apt.core.prefs, .etc. **This command does not execute processors anymore!** |
| **Executing processors** | mvn processor:process processor:process-tests | This executes processor against the java sources and against the test sources |
| **Install selenium project** | mvn clean install -DskipTests | Executes available annotation processors and installs JAR with all the test classes (with suffix -tests.jar) |
| **Executing tests** | mvn test          | Execute tests with default configuration |

# Executing processors #

Processors are executed in 2 ways:
  * in the manual way using `mvn processor:process processor:process-tests` command or
  * in the automatic way using `mvn install` command

_Note: if do not want to execute processors against the test source just skip the processor:process-tests (= use only mvn processor:process) command or skip tests using -Dmaven.test.skip parameter._

# Install selenium project #

This allows you to reuse selenium common classes/tests in more project. Just install the project using `mvn clean install` _(or mvn clean install -DskipTests if you don't want to execute tests during the installation)_ command and dependency to your new project:

```
<dependency>
	<groupId>__YOUR_GROUP_ID__</groupId>
	<artifactId>__YOUR_ARTIFACT_ID__</artifactId>
	<version>__VERSION__</version>
	<type>test-jar</type>
	<scope>test</scope>
</dependency>
```

# Executing tests #

Tests can be executed using `mvn test` command using default configuration listend in the SuiteRunner: