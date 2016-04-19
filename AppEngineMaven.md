Change default output directory and add webapp as source directory
```
<build>
	<resources>
		<resource>
			<directory>${basedir}/src/main/resources</directory>
		</resource>
		<resource>
			<directory>${basedir}/src/main/webapp</directory>
		</resource>
	</resources>
	<outputDirectory>${project.build.directory}/${artifactId}-${version}/WEB-INF/classes</outputDirectory>
</build>
```

GWT maven configuration (building GWT sources with maven)
```
<plugin>
	<groupId>org.codehaus.mojo</groupId>
	<artifactId>gwt-maven-plugin</artifactId>
	<version>1.3-SNAPSHOT</version>

	<configuration>
		<gwtVersion>${gwt.version}</gwtVersion>
		<logLevel>INFO</logLevel>
		<port>8890</port>
		<!--<modules>-->
			<module>${gwt.client.module}</module>
		<!--</modules>-->
		<runTarget>${gwt.client.module}/${gwt.client.html}</runTarget>

		<!-- style>DETAILED</style--><!-- use OBF for prod -->
		<style>${gwt-output-style}</style><!-- use OBF for prod -->
		<noServer>false</noServer>
		<extraJvmArgs>-Xmx724M -Xss16M -Dfile.encoding=utf-8
		</extraJvmArgs>
		<debugPort>8990</debugPort>
		<shellServletMappingURL>/gwt-shell-servlet
		</shellServletMappingURL>
		<mergedWebXml>war/WEB-INF/web.xml</mergedWebXml>
		<hostedWebapp>${project.build.directory}/${artifactId}-${version}</hostedWebapp>
		<webappDirectory>${project.build.directory}/${artifactId}-${version}</webappDirectory>
		<soyc>false</soyc>
		<disableCastChecking>true</disableCastChecking>
		<!--disableClassMetadata>true</disableClassMetadata>
		<draftCompile>true</draftCompile-->
	</configuration>
	<executions>
		<execution>
			<goals>
				<goal>mergewebxml</goal>
				<!--goal>generateAsync</goal-->
				<goal>compile</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

Copying dependencies into webapp directory:
```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-dependency-plugin</artifactId>
	<version>2.1</version>
	<executions>
		<execution>
			<id>copy-dependencies</id>
			<phase>generate-sources</phase>
			<goals>
				<goal>copy-dependencies</goal>
			</goals>
			<configuration>
				<outputDirectory>${project.build.directory}/${artifactId}-${version}/WEB-INF/lib/</outputDirectory>
				<overWriteReleases>true</overWriteReleases>
				<overWriteSnapshots>true</overWriteSnapshots>
				<overWriteIfNewer>true</overWriteIfNewer>
				<excludeTransitive>false</excludeTransitive>
			</configuration>
		</execution>
	</executions>
</plugin>
```

```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-eclipse-plugin</artifactId>
	<version>2.7</version>
	<configuration>
		<excludes>
			<exclude>com.google.gwt:gwt-dev</exclude>
			<exclude>com.google.appengine:appengine-api-1.0-sdk</exclude>
			<exclude>com.google.appengine:appengine-api-labs</exclude>
			<exclude>org.apache.geronimo.specs:geronimo-jpa_3.0_spec</exclude>
			<exclude>org.apache.geronimo.specs:geronimo-jta_1.1_spec</exclude>
			<exclude>javax.jdo:jdo2-api</exclude>
		</excludes>
		<additionalBuildcommands>
			<buildCommand>
				<name>com.google.gwt.eclipse.core.gwtProjectValidator</name>
			</buildCommand>
			<buildCommand>
				<name>com.google.gdt.eclipse.core.webAppProjectValidator</name>
			</buildCommand>
			<buildCommand>
				<name>com.google.appengine.eclipse.core.projectValidator</name>
			</buildCommand>
		</additionalBuildcommands>

		<additionalProjectnatures>
			<projectnature>com.google.gwt.eclipse.core.gwtNature</projectnature>
			<projectnature>com.google.appengine.eclipse.core.gaeNature</projectnature>
			<projectnature>com.google.gdt.eclipse.core.webAppNature</projectnature>
		</additionalProjectnatures>

		<classpathContainers>
			<classpathContainer>com.google.gwt.eclipse.core.GWT_CONTAINER</classpathContainer>
			<classpathContainer>com.google.appengine.eclipse.core.GAE_CONTAINER</classpathContainer>
			<classpathContainer>org.eclipse.jdt.launching.JRE_CONTAINER</classpathContainer>
		</classpathContainers>

		<additionalConfig>
			<file>
				<name>.settings/com.google.gdt.eclipse.core.prefs</name>
				<content><![CDATA[
					eclipse.preferences.version=1
					jarsExcludedFromWebInfLib=
					warSrcDir=target/${artifactId}-${version}
					warSrcDirIsOutput=true
					]]>
				</content>
			</file>
			<file>
				<name>.factorypath</name>
				<content><![CDATA[<factorypath>
						<factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/acris/acris-binding/${pom.version}/acris-binding-${pom.version}.jar" enabled="true" runInBatchMode="false"/>
						<factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/acris/acris-json/${pom.version}/acris-json-${pom.version}.jar" enabled="true" runInBatchMode="false"/>
						<factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/acris/acris-client-core/${pom.version}/acris-client-core-${pom.version}.jar" enabled="true" runInBatchMode="false"/>
				  	</factorypath>
					  ]]>
				</content>
			</file>
			<file>
				<name>.settings/org.eclipse.jdt.apt.core.prefs</name>
				<content><![CDATA[
					eclipse.preferences.version=1
       						org.eclipse.jdt.apt.aptEnabled=true
       						org.eclipse.jdt.apt.genSrcDir=${jsr269.generated.dir}
       						org.eclipse.jdt.apt.reconcileEnabled=true
					]]>
				</content>
			</file>
		</additionalConfig>

		<buildOutputDirectory>target/${artifactId}-${version}/WEB-INF/classes</buildOutputDirectory>
		<wtpversion>2.0</wtpversion>

	</configuration>
</plugin>
```

```
<!-- GWT dependencies -->
<dependency>
	<groupId>com.google.gwt</groupId>
	<artifactId>gwt-user</artifactId>
	<version>2.0.0</version>
	<scope>compile</scope>
</dependency>
<dependency>
	<groupId>com.google.gwt</groupId>
	<artifactId>gwt-dev</artifactId>
	<version>2.0.0</version>
	<scope>compile</scope>
</dependency>

//TODO add app engine deps here
```