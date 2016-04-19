# Annotation processing (PAP, JSR269) changes #

## Core & util classes ##

**prior to version 1.1.1 were all annotation processing related classes held in acris-client-core project. This was improved and common classes were moved into sesam-annotations-support project**

It is related to following classes:
~~sk.seges.acris.core.jsr269.AbstractConfigurableProcessor~~ -> [sk.seges.sesam.core.pap.AbstractConfigurableProcessor](http://acris.googlecode.com/svn/sesam/branches/1.1.0/sesam-annotations-support/src/main/java/sk/seges/sesam/core/pap/AbstractConfigurableProcessor.java)

~~sk.seges.acris.core.jsr269.ProcessorUtils~~ -> [sk.seges.sesam.core.pap.utils.ProcessorUtils](http://acris.googlecode.com/svn/sesam/branches/1.1.0/sesam-annotations-support/src/main/java/sk/seges/sesam/core/pap/utils/ProcessorUtils.java)

Please be aware of previous classes were located in acris-client-core and most current implementation is located in sesam-annotations-support, so do not forget to change dependency:

from:
```
<dependency>
	<artifactId>acris-client-core</artifactId>
	<version>(,1.1.0]</version>
	<groupId>sk.seges.acris</groupId>
</dependency>
```

to:
```
<dependency>
	<artifactId>sesam-annotations-support</artifactId>
	<version>[1.1.1,)</version>
	<groupId>sk.seges.sesam</groupId>
</dependency>
```

**Added possibility to [test effectively](http://code.google.com/p/acris/wiki/CodeGenerationPlatform#Testing) annotation processors.**

This is also related to maven eclipse configuration. You have list change acris-client-core jar and enlist sesam-annotations-support.jar in your pom.xml

from:
```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-eclipse-plugin</artifactId>
	<configuration>
		<additionalConfig>
			...
			<file>
				<name>.factorypath</name>
				<content><![CDATA[
<factorypath>
  	<factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/acris/acris-client-core/${pom.version}/acris-client-core-${pom.version}.jar" enabled="true" runInBatchMode="false"/>
	<factorypathentry kind="VARJAR" id="..." enabled="true" runInBatchMode="false"/>
</factorypath>]]>
				</content>
			</file>
			...
		</additionalConfig>
	</configuration>
</plugin>
```

to:
```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-eclipse-plugin</artifactId>
	<configuration>
		<additionalConfig>
			...
			<file>
				<name>.factorypath</name>
				<content><![CDATA[
<factorypath>
	<factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/sesam/sesam-core/${sesam-os.version}/sesam-core-${sesam-os.version}.jar" enabled="true" runInBatchMode="false" />
	<factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/sesam/sesam-annotations-support/${sesam-os.version}/sesam-annotations-support-${sesam-os.version}.jar" enabled="true" runInBatchMode="false" />
	<factorypathentry kind="VARJAR" id="..." enabled="true" runInBatchMode="false"/>
</factorypath>]]>
				</content>
			</file>
			...
		</additionalConfig>
	</configuration>
</plugin>

```

## Bean wrapper PAP ##

BeanWrapper annotation processor was primarily used for automatic support for property change listeners. See more details in the [bean wrappers section](http://code.google.com/p/acris/wiki/BeanWrappers).
Later bean wrapper was used also for safer property referencing in the code. It was mostly used in binding:

```
@BindingField(UserBeanWrapper.ADDRESS.STREET)
protected TextBox userStreet = GWT.create(TextBox.class);
```

This references address street in the user bean and you down't have to write error prone strings, like:

```
@BindingField("address.street")
```

Problem was with the refactoring. When you refactor getter for address or street, it is very hard to maintain also string representations of the referenced properties. One very easy solution is to define own String property, like:

```
public interface User {
	public static final String ADDRESS = "address";

	public interface Address {
		public static final String STREET = "street";

		String getStreet();
	}

	Address getAddress();
}

@BindingField(User.ADDRESS + "." + Address.STREET)
```

This allows you to keep meta model information in the constants and whe you refactor property, you have to also maintain constants in your manual metamodel. Bean wrapper PAP does this for you in fully automatic way - that means, when you refactor any property, meta model is recreated and compilation error in your binding (or any reference) occurs.

As we mentioned, this is related to meta model rather than bean wrappers. So BeanWrapperProcessor was split into 2 separate annotation processors:
  * [BeanWrapperProcessor](http://acris.googlecode.com/svn/branches/1.1.0/acris-binding/src/main/java/sk/seges/acris/binding/jsr269/BeanWrapperProcessor.java)
  * [MetaModelProcessor](http://acris.googlecode.com/svn/sesam/branches/1.1.0/sesam-annotation-processors/sesam-model-metadata/src/main/java/sk/seges/sesam/pap/model/MetaModelProcessor.java)

So, when you want to use meta model data, use MetaModelProcessor
```
<dependency>
	<groupId>sk.seges.sesam</groupId>
	<artifactId>sesam-model-metadata</artifactId>
	<version>1.1.1</version>
</dependency>
```

```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-eclipse-plugin</artifactId>
	<configuration>
		<additionalConfig>
			...
			<file>
				<name>.factorypath</name>
				<content><![CDATA[
<factorypath>
	...
	...
	<factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/sesam/sesam-model-metadata/${sesam-os.version}/sesam-model-metadata-${sesam-os.version}.jar" enabled="true" runInBatchMode="false" />
</factorypath>]]>
				</content>
			</file>
			...
		</additionalConfig>
	</configuration>
</plugin>
```

And when you want to use bean wrapper for binding purposes, use BeanWrapperProcessor:

```
<dependency>
	<groupId>sk.seges.acris</groupId>
	<artifactId>acris-binding</artifactId>
	<version>1.1.1</version>
</dependency>
```


```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-eclipse-plugin</artifactId>
	<configuration>
		<additionalConfig>
			...
			<file>
				<name>.factorypath</name>
				<content><![CDATA[
<factorypath>
	...
	...
	<factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/acris/acris-binding/${acris-os.version}/acris-binding-${acris-os.version}.jar" enabled="true" runInBatchMode="false" />
</factorypath>]]>
				</content>
			</file>
			...
		</additionalConfig>
	</configuration>
</plugin>
```