The problem is with the org.eclipse.osgi:org.eclipse.osgi.services dependency which has transitive dependeny to org.apache.felix:org.osgi.foundation. This JAR contains files from old java (probably 1.4) and I really don't understand why (but I'm pretty sure that it's reasonable and if someone knows the reason, please let me know in the comments section).

But the problem was that I had these classes available in my JRE/JDK (1.6) and also in org.osgi.foundation library. This leads to the conflict - if the foundation jar is on the classpath before the JRE I had a compilation error:
**The type Comparable is not generic; it cannot be parameterized with arguments`<E>`**.

(This was because foundation jar contains non generic version of the Comparable class)

So, the solution was to exclude this library from the dependency management
```
</dependencies>
	</dependencyManagement>
		<!-- Eclipse equinox -->
		<!-- Plugin deps -->
		<dependency>
			<groupId>org.eclipse.core</groupId>
			<artifactId>org.eclipse.core.runtime</artifactId>
			<version>3.6.0.v20100505</version>
		</dependency>
		<dependency>
			<groupId>org.eclipse.equinox</groupId>
   				<artifactId>org.eclipse.equinox.common</artifactId>
	    		<version>3.6.0.v20100503</version>
		</dependency>
		<dependency>
			<groupId>org.eclipse.osgi</groupId>
			<artifactId>org.eclipse.osgi.services</artifactId>
			<version>3.2.100.v20100503</version>
			<exclusions>
				<exclusion>
					<groupId>org.apache.felix</groupId>
					<artifactId>org.osgi.foundation</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
	</dependencies>
</dependencyManagement>
```

After this exclusion everything works fine.