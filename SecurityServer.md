# Server security #

Security is not only about the client but especially the server where all relevant business data is stored. Therefore acris-security solves the server side and uses (currently) Spring Security to allow you to connect almost any authentication and authorization mechanism you want. Not only it allows you to transparently propagate authorities from the client but also secure service methods and apply Access Control Lists (ACL) as a tool of fine-grained security.

# Filters, Listeners, Sessions #

Server side from the point of toolkit view is a standard J2EE container application. Of course there are ways of deploying it in different environments but currently it is the one supported. Therefore you need to define your web application descriptor _web.xml_.

```
	<filter>
		<filter-name>SessionFilter</filter-name>
		<filter-class>sk.seges.acris.security.server.SessionRemoteServiceFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>SessionFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<listener>
		<listener-class>sk.seges.acris.security.server.SessionHandlerListener</listener-class>
	</listener>
```

`SessionRemoteServiceFilter` is a filter responsible for stripping Session ID information from RPC request. `SessionHandlerListener` is responsible for managing valid sessions and pairing session ID string with session object.

# Optional (but supported) service configuration #

Consider this chapter as a "proven" configuration of GWT-RPC services with acris-security. We use [gwt-sl](http://gwt-widget.sourceforge.net/) library to integrate Spring with GWT services (and Gilead also). The configuration consists of two parts:
  * web.xml dispatcher definition
  * service mapping in Spring context

```
	<!-- define the location of service mapping Spring context -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:sk/seges/acris/security/showcase/gwt-url-mapping.xml</param-value>
	</context-param>
	
	...
	
	<!-- dispatcher servlet responsible for catching GWT-RPC requests -->
	<servlet>
		<servlet-name>service</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>

	<!-- root URL for all services catched by the DispatcherServlet -->
	<servlet-mapping>
		<servlet-name>service</servlet-name>
		<url-pattern>/sk.seges.acris.security.showcase.Showcase/showcase-service/*</url-pattern>
	</servlet-mapping>
```

NOTE: you also need an empty Spring context prefixed with the name of the DispatcherServlet defined in the servlet-name tag in the form &lt;servlet-name&gt;-servlet.xml located in the WEB-INF directory

Because we are using Gilead to transfer persistent objects from Hibernate the mapping definition is enriched with Gilead service exporter:

```
	<bean id="serviceExporter" class="org.gwtwidgets.server.spring.gilead.GileadRPCServiceExporter" scope="prototype">
        <property name="beanManager" ref="showcaseHibernateBeanManager" />
    </bean>

	<bean id="configurableExporterFactory" class="sk.seges.acris.rpc.ConfigurableRPCServiceExporterFactory" />

	<bean id="urlMapping"
		class="org.gwtwidgets.server.spring.GWTHandler">
		<property name="serviceExporterFactory" ref="configurableExporterFactory" />
		<property name="mappings">
			<map>
				<entry key="/userService" value-ref="userService" />
			</map>
		</property>
	</bean>
```

More about various ways of integrating server with client you can find [in the wiki about Separating client and server](SeparateClientAndServer.md).

# Spring contexts for your application #

To fasten your development we have several contexts prepared for you where you just need to import them and slightly configure. Here we describe the meaning, specific steps you can find in the [quick-start](SecurityQuickStart.md).

The most general contexts are using:
  * JPA entity manager
  * Spring Security's `DaoAuthenticationProvider` in conjunction with a `IGenericUserDao` implementation.
    * `UserWithAuthoritiesDao` responsible for fetching `UserWithAuthorities` user object - the most basic usable one.
  * `UserService` implementation providing login and logout using defined DAO
  * predefined logic JNDI resource jdbc/acris-security

Deeper you go you will get more space to configure things in your own way and composing various predefined aspects: authentication providers, user services, ACL, role voters, session handling, user services...
There are predefined "API" contexts and they are located in the package **sk.seges.acris.security.server.context.api** modeling common use-cases:
  * `acris-security-generic-user-context.xml` - this one is your one-line hero. It includes everything, you just need to point **jdbc/acris-security** JNDI resource to correct database.
  * `acris-security-custom-user-dao-context.xml` - allows you to define your custom DAO fetching user by a name from the database. Define a bean under **genericAuthenticationDao** name you like, it must implement `IGenericUserDao` interface
  * `acris-security-custom-authentication-context.xml` - useful in the case you would like to define your own chain of authentication providers (like supporting authentication against LDAP). You have to define:
    * bean with name **acrisSecurityEntityManagerFactory** pointing to your entity manager factory where required security persistent objects are stored
    * **dataSource** bean pointing to a `javax.sql.DataSource` pointing to the database where persistent objects are stored
    * **authenticationManager** bean defining the chain of authentication providers
    * and by the requirements of your authentication implementation maybe a **user service**
  * `acris-security-custom-object-definition-context.xml` - **TBW.**

# Annotating service methods #

  * using `javax.annotation.security.RolesAllowed` or `org.springframework.security.annotation.Secured`
  * authority is specified WITHOUT the authority prefix `SecurityConstants.AUTH_PREFIX`

# Interesting objects #

Under the term "security" you would imagine various combinations of approaches, use-cases and steps needed for your application scenario. Before extending acris-security with your own one maybe you would find something from existing components useful.

## SecurityRole ##

Security role serves as the holder of authorities for specific user (or any other entity). It is the entity grouping authorities but the security mechanism is not dependent on it directly (nor the user). Security role might be used in a custom user service implementation where you can model user &lt;-&gt; role &lt;-&gt; authority relation. An entity joining user with `SecurityRole` will be operated with the custom user service on. There are at least two approaches how to use it in meaningful way:
  * extend `SecurityRole` and provide an association to the user object (probably also a class extending from `GenericUser`)
  * implement standalone entity joining user object class and `SecurityRole` - usually when you need to provide additional information e.g. for which web application in which role a user is in

## UserPreferences ##

A helper entity connected with `GenericUser` holding configuration information about the user. Currently preferred locale is there.

## more ... ##

**TBW.**

# Extending the authentication chain & custom user service #
  * extending authentication provider
  * own user service
    * `AbstractUserService`
  * complex "user" with role

# Security exporters #

  * propagating security exceptions
  * **TBW.**