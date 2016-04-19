We are using maven annotation plugin (aka maven-processor-plugin) for supporting JSR269 processor in the java compilation time. Annotation processors replaces old APT technology and is introduced with JDK6 compiler.

Firstly we setup the property (jsr269.generated.dir) where all the generated files resides.
```
<properties>
	<jsr269.generated.dir>target/generated</jsr269.generated.dir>
</properties>
```
Unfortunatelly, maven-compiler plugin can start only one annotation processor during the compile time, we have to disable processors support and use another solution.
```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-compiler-plugin</artifactId>
	<configuration>
        	<compilerArgument>-proc:none</compilerArgument>
	</configuration>
</plugin>
```
During the generating sources we start all available processors on the classpath. Output directory is target/generated.
```
<plugin>
	<groupId>org.bsc.maven</groupId>
	<artifactId>maven-processor-plugin</artifactId>
	<version>1.3.1</version>
	<configuration>
		<!-- source output directory -->
		<outputDirectory>${jsr269.generated.dir}</outputDirectory>
	</configuration>
	<executions>
		<execution>
			<id>process</id>
			<goals>
				<goal>process</goal>
			</goals>
			<phase>generate-sources</phase>
		</execution>
	</executions>
</plugin>
```
Also we have to include new directory (target/generated) as the source path so it will be included on the classpath and will be part of the released JAR.
```
<plugin>
	<groupId>org.codehaus.mojo</groupId>
	<artifactId>build-helper-maven-plugin</artifactId>
	<version>1.3</version>
	<configuration>
		<sources>
			<source>${jsr269.generated.dir}</source>
		</sources>
	</configuration>
	<executions>
		<execution>
			<id>add-source</id>
			<phase>generate-sources</phase>
			<goals>
				<goal>add-source</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```
And finally, integration with the eclipse. We are adding 3 factory libraries into the "processor classpath" so it will be used in the generation process. And why 3 libraries?
  * acris-binding jar contains bean wrapper annotation processor
  * acris-json jar contains JSON aware annotation processor (if you do not want to work with the JSON, you can remove this factory path)
  * acris-client-core jar contains common classes used in annotation processors and it have to be included in the classpath

At the end, we are enablind the annotation processing support in the eclipse with the correct output directory target/generated
```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-eclipse-plugin</artifactId>
	<version>2.7</version>
	<configuration>
		<additionalConfig>
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
			<file>
				<name>.settings/org.eclipse.jdt.core.prefs</name>
				<content><![CDATA[
eclipse.preferences.version=1
org.eclipse.jdt.core.compiler.codegen.targetPlatform=1.6
org.eclipse.jdt.core.compiler.compliance=1.6
org.eclipse.jdt.core.compiler.processAnnotations=enabled
org.eclipse.jdt.core.compiler.source=1.6
					   ]]>
				</content>
			</file>
		</additionalConfig>
	</configuration>
</plugin>
```