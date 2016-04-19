# Bean wrapper #

Under the term **bean wrapper** a generated wrapper object is referred. The wrapper is used to allow to:
  * add property change listeners
  * get or set property values

The target objects are domain objects which you don't want to clutter with e.g. PropertyChangeSupport classes or other means of bean introspection. Bean wrappers are also handy when it comes to hooking another functionality to objects they wrap - e.g. validation. These additional functionality generators can be bound to wrapper interfaces and not the domain object itself.

# Creating a wrapper #

You can create a wrapper around the object by:
  * annotating the domain object with `BeanWrapper` annotation and running Annotation processor
  * creating interface implementing `BeanWrapper` interface with parametrized type where the type is the type of the object going to be wrapped by hand

## Annotations ##

```
@BeanWrapper
public class BlogPost {
	private String text;
	private Date created;
	
	... getters and setters...
}
```

By simply annotating the object you tell `BeanWrapperProcessor` to process the object and generate `BeanWrapper` based interface for you.

To enable the processor put these Maven plugings to the project containing the objects to be wrapped:

```
             <plugin>
                <groupId>org.bsc.maven</groupId>
                <artifactId>maven-processor-plugin</artifactId>
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
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
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
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-eclipse-plugin</artifactId>
                <configuration>
                    <additionalConfig>
                        <file>
                            <name>.factorypath</name>
                            <content><![CDATA[<factorypath>
          <factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/acris/acris-binding/${pom.version}/acris-binding-${pom.version}.jar" enabled="true" runInBatchMode="false" />
		  <factorypathentry kind="VARJAR" id="M2_REPO/sk/seges/acris/acris-client-core/${pom.version}/acris-client-core-${pom.version}.jar" enabled="true" runInBatchMode="false" />
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
                </configuration>
            </plugin>
```

If you already have an annotation on your object you can instruct the processor to accept it. Create `/META-INF/bean-wrapper.properties` file and put there something like this:

```
annotations=javax.persistence.Entity
interfaces=
```

It will tell the generator to accept `javax.persistence.Entity` annotation and create `BeanWrapper` for every persistent entity object.

## Manual way ##

Creating a bean wrapper interface for an object is trivial:

```
public interface BlogPostBeanWrapper
		extends
			sk.seges.acris.binding.client.wrappers.BeanWrapper<your.package.BlogPost> {
}
```

# Generating the wrapper #

Final step is to generate the wrapper in the code:

```
BeanWrapper<BlogPost> wrapper = GWT.create(BlogPostBeanWrapper.class);
// fill the wrapper
wrapper.setContent(new BlogPost());
// get the value by specifying the name of the field ...
wrapper.getAttribute("created");
// ... and set it
wrapper.setAttribute("text", "This is the post");
```

Bean wrapper generator has to be enabled in your GWT module:

```
	<generate-with class="sk.seges.acris.rebind.bind.BeanWrapperGenerator">
    	<any>
			<when-type-assignable class="sk.seges.acris.binding.client.wrappers.BeanWrapper"/>
		</any>
	</generate-with>
```

# Introspecting beans and wrappers #

**TBW.**