# Enhancing classes #
//TODO
add description

//TODO
add final project setup, pom.xml, persistence.xml, javax.persistence.spi.PersistenceProvider

## File enhancing within JAR file ##
### Problem ###
Datanucleus enhancer tries to enhance files withing the JAR file. Problem is that he is not able to modify the JAR file and the enhancing ends with following error"

ERROR Log4JLogger.java:error Enhancer - An error was encountered whilst enhancing class "sk.seges.acris.security.rpc.user\_management.domain.GenericUser" :
file:\C:\m2\sk\seges\acris\acris-security\1.0.1-SNAPSHOT\acris-security-1.0.1-SNAPSHOT.jar!\sk\seges\acris\security\rpc\user\_management\domain\GenericUser.class (The filename, directory name, or volume label syntax is incorrect)
java.io.FileNotFoundException: file:\C:\m2\sk\seges\acris\acris-security\1.0.1-SNAPSHOT\acris-security-1.0.1-SNAPSHOT.jar!\sk\seges\acris\security\rpc\user\_management\domain\GenericUser.class (The filename, directory name, or volume label syntax is incorrect)
```
        at java.io.FileOutputStream.open(Native Method)
        at java.io.FileOutputStream.<init>(FileOutputStream.java:179)
        at java.io.FileOutputStream.<init>(FileOutputStream.java:131)
        at org.datanucleus.enhancer.AbstractClassEnhancer.save(AbstractClassEnhancer.java:286)
        at org.datanucleus.enhancer.DataNucleusEnhancer.enhanceClass(DataNucleusEnhancer.java:1047)
        at org.datanucleus.enhancer.DataNucleusEnhancer.enhance(DataNucleusEnhancer.java:609)
        at org.datanucleus.enhancer.DataNucleusEnhancer.main(DataNucleusEnhancer.java:1316)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.datanucleus.maven.AbstractEnhancerMojo.executeInJvm(AbstractEnhancerMojo.java:300)
        at org.datanucleus.maven.AbstractEnhancerMojo.enhance(AbstractEnhancerMojo.java:210)
        at org.datanucleus.maven.AbstractEnhancerMojo.executeDataNucleusTool(AbstractEnhancerMojo.java:81)
        at org.datanucleus.maven.AbstractDataNucleusMojo.execute(AbstractDataNucleusMojo.java:119)
        at org.apache.maven.plugin.DefaultPluginManager.executeMojo(DefaultPluginManager.java:490)
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoals(DefaultLifecycleExecutor.java:694)
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoalWithLifecycle(DefaultLifecycleExecutor.ja
va:556)
        at org.apache.maven.lifecycle.DefaultLifecycleExecutor.executeGoal(DefaultLifecycleExecutor.java:535)
```

### Solution ###

Solution is little bit tricky :) You have to change default behaviour, which tries to override existing file, but rather generate enhanced classes into target output directory. So application will have enhanced files available on the classpath without overriding the existing files (jars).

```
<plugin>
	<groupId>org.datanucleus</groupId>
	<artifactId>maven-datanucleus-plugin</artifactId>
	<version>1.1.0</version>
	<configuration>
		<api>JPA</api>
		<enhancerName>ASM</enhancerName>
		<mappingIncludes>
			**/seges/acris/**/*.class
		</mappingIncludes>
		<verbose>true</verbose>
		<targetDirectory>target/${artifactId}-${version}/WEB-INF/classes</targetDirectory>
		<persistenceUnitName>mvp</persistenceUnitName>
		<log4jConfiguration>${basedir}/src/main/resources/log4j.properties</log4jConfiguration>
		<fork>false</fork>
	</configuration>
        ...
</plugin>
```

### Interesting reading ###
[Runtime Enhancement with JDO and Datanucleus](http://brainpicks.wordpress.com/2010/05/04/runtime-enhancement-with-jdo-and-datanucleus/)

## Integration datanucleus with maven ##
### Problem ###
Enhancer is not able to resolve classes on your classpath but classes are located on the classpath. This produces following failure:

Class "sk.seges.acris.mvp.client.action.DefaultAsyncCallback" was not found in the CLASSPATH. Please check your specification and your CLASSPATH.
org.datanucleus.exceptions.ClassNotResolvedException: Class "sk.seges.acris.mvp.client.action.DefaultAsyncCallback" was not found in the CLASSPATH.
```
Please check your specification and your CLASSPATH.
        at org.datanucleus.JDOClassLoaderResolver.classForName(JDOClassLoaderResolver.java:247)
        at org.datanucleus.JDOClassLoaderResolver.classForName(JDOClassLoaderResolver.java:412)
        at org.datanucleus.metadata.MetaDataManager.loadPersistenceUnit(MetaDataManager.java:828)
        at org.datanucleus.enhancer.DataNucleusEnhancer.getFileMetadataForInput(DataNucleusEnhancer.java:850)
        at org.datanucleus.enhancer.DataNucleusEnhancer.enhance(DataNucleusEnhancer.java:582)
        at org.datanucleus.enhancer.DataNucleusEnhancer.main(DataNucleusEnhancer.java:1316)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
        at java.lang.reflect.Method.invoke(Method.java:597)
        at org.datanucleus.maven.AbstractEnhancerMojo.executeInJvm(AbstractEnhancerMojo.java:300)
        at org.datanucleus.maven.AbstractEnhancerMojo.enhance(AbstractEnhancerMojo.java:210)
        at org.datanucleus.maven.AbstractEnhancerMojo.executeDataNucleusTool(AbstractEnhancerMojo.java:81)
        at org.datanucleus.maven.AbstractDataNucleusMojo.execute(AbstractDataNucleusMojo.java:119)

```

### Solution ###
Do not set provided scope on your classpath for gwt-user dependency. This is in coflict with correct [maven GWT setup](http://mojo.codehaus.org/gwt-maven-plugin/user-guide/setup.html) but anyway, provided scope leads to plugin crash. After setting up the compile scope, enhancer plugin works correctly.

Bad:
```
<dependency>
	<groupId>com.google.gwt</groupId>
	<artifactId>gwt-user</artifactId>
	<version>${gwt.version}</version>
	<scope>provided</scope>
</dependency>
```

Good:
```
<dependency>
	<groupId>com.google.gwt</groupId>
	<artifactId>gwt-user</artifactId>
	<version>${gwt.version}</version>
	<scope>compile</scope>
</dependency>
```