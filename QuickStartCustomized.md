# Introduction #

In the following chapters you become familiar with the most customizable way of setting up an AcrIS project.

# One-module project #

  * Create new project e.g. using `mvn archetype:generate -DarchetypeArtifactId=maven-archetype-quickstart`
    * http://maven.apache.org/archetype/maven-archetype-plugin/usage.html
  * Setup basic GWT project using [gwt-maven plugin](http://mojo.codehaus.org/gwt-maven-plugin/)
  * Enable important AcrIS features by editing **pom.xml**:
    * inherit from acris-os-gwt-parent
```
    <parent>
        <groupId>sk.seges.acris</groupId>
        <artifactId>acris-os-gwt-parent</artifactId>
        <version>1.0.19</version>
    </parent>
```
    * use _settings.xml_ from http://acris.googlecode.com/svn/trunk/settings.xml as a repository source information
    * add handy properties
```
	<properties>
		<gwt.client.module>sk.seges.acris.security.showcase.Site</gwt.client.module>
		<gwt.client.html>Site.html</gwt.client.html>
		
		<acris-os.version>1.0.1</acris-os.version>
	</properties>
```
    * configure Maven plugins relevant for GWT
```
	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-war-plugin</artifactId>
				<configuration>
					<warSourceDirectory>war</warSourceDirectory>
					<webXml>src/main/webapp/WEB-INF/web.xml</webXml>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>gwt-maven-plugin</artifactId>
				<configuration>
					<warSourceDirectory>war</warSourceDirectory>
				</configuration>
			</plugin>
		</plugins>
	</build>
```

# Multi-module enterprise project #

This project setup is more in the level of recommendation then exact steps because it depends on the type of the project. But let's try to put it into quick steps:
  * create 3 or 5 (or as much as you consider) projects based on the [described structure](ProjectSupport.md) + 1 parent project
    * in the quick-start we will count with 3+1:
      * showcase-client
      * showcase-rpc
      * showcase-server
      * showcase-parent
  * showcase-parent:
    * inherits acris-os-gwt-parent
```
    <parent>
        <groupId>sk.seges.acris</groupId>
        <artifactId>acris-os-gwt-parent</artifactId>
        <version>1.0.19</version>
    </parent>
```
    * use _settings.xml_ from http://acris.googlecode.com/svn/trunk/settings.xml as a repository source information
    * add handy properties
```
	<properties>
		<acris-os.version>1.0.1</acris-os.version>
	</properties>
```
    * define dependencies in `dependencyManagement` you need
  * showcase-client, showcase-rpc and showcase-server have to inherit from showcase-parent
```
	<parent>
		<groupId>sk.seges.acris.showcase</groupId>
		<artifactId>showcase-parent</artifactId>
		<version>1.0.0-SNAPSHOT</version>
		<!-- we are using flat structure -->
		<relativePath>../showcase-parent</relativePath>
	</parent>
```
  * showcase-server
    * if you are developing Spring/Hibernate, J2EE or Rest server, do usual steps to setup project structure
    * include dependencies only on server libraries
    * try to avoid including dependencies with `sources` classifier because they are not needed here
  * showcase-client
    * define properties about GWT module required by acris-os-gwt-parent gwt-maven-plugin enhanced configuration - you don't have (but you can) configure gwt-maven-plugin, default values will be used
```
	<properties>
		<gwt.client.module>sk.seges.acris.security.showcase.Site</gwt.client.module>
		<gwt.client.html>Site.html</gwt.client.html>
	</properties>
```
    * define the gwt-maven-plugin - you might see there is no configuration part required, everything is in acris-os-gwt-parent
```
	<build>
		<plugins>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>gwt-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>
```
  * showcase-rpc
    * usually contains dependencies on GWT and they are propagated to the client and the server
```
		<dependency>
			<groupId>com.google.gwt</groupId>
			<artifactId>gwt-user</artifactId>
		</dependency>
		<dependency>
			<groupId>com.google.gwt</groupId>
			<artifactId>gwt-servlet</artifactId>
		</dependency>
		<dependency>
			<groupId>com.google.gwt</groupId>
			<artifactId>gwt-dev</artifactId>
		</dependency>
```

Finally you may run `mvn gwt:eclipse` which generates launcher configuration, copy it to the IDE and start developing.