# Introduction #

  * showcase project: acris-security-showcase

# Project #

  * Setup a project like described in ProjectQuickStart.
  * Add acris-security dependency
```
		<dependency>
			<groupId>sk.seges.acris</groupId>
			<artifactId>acris-security</artifactId>
			<version>${acris-os.version}</version>
		</dependency>

		<dependency>
			<groupId>sk.seges.acris</groupId>
			<artifactId>acris-security</artifactId>
			<version>${acris-os.version}</version>
			<classifier>sources</classifier>
		</dependency>
```
  * for development with GWT embedded jetty there is also the dependency on jetty-plus & postgreSQL driver:
```
		<dependency>
			<groupId>org.mortbay.jetty</groupId>
			<artifactId>jetty-plus</artifactId>
			<version>6.1.24</version>
		</dependency>
		<dependency>
			<groupId>org.mortbay.jetty</groupId>
			<artifactId>jetty-naming</artifactId>
			<version>6.1.24</version>
		</dependency>

		<dependency>
			<groupId>postgresql</groupId>
			<artifactId>postgresql</artifactId>
			<version>8.4-701.jdbc4</version>
		</dependency>
```

# Client #

  * in your GWT module inherit:
```
<inherits name='sk.seges.acris.Security'/>
```
  * create a panel and implement `ISecuredObject`
    * you can use prepared `SecuredComposite`
```
public class CustomerPanel extends SecuredComposite {
```
  * put some fields into it
```
	@Secured(Grants.SECURITY_MANAGEMENT)
	protected TextBox securityID;
```
    * annotate the panel with `@sk.seges.acris.security.client.annotations.Secured` annotation if you want to propagate grants to all fields without typing it
  * instantiate the panel
```
    	CustomerPanel customerPanel = GWT.create(CustomerPanel.class);
    	customerPanel.setClientSession(clientSession);
```
  * instantiate user service and log in
```
	// standard user service
	final IUserServiceAsync userService = GWT.create(IUserService.class);
	SessionServiceDefTarget endpoint = (SessionServiceDefTarget) userService;
	endpoint.setServiceEntryPoint("showcase-service/userService");
	endpoint.setSession(clientSession);
```
  * log-in logic
```
	userService.login(new UserPasswordLoginToken(username.getText(), password.getText(), null),
			new AsyncCallback<ClientSession>() {
				@Override
				public void onSuccess(ClientSession result) {
					// copy the information about user and session
					// so secured components can rely on it
					clientSession.setSessionId(result.getSessionId());
					clientSession.setUser(result.getUser());
					loggedInCmd.execute();
				}
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Login failure", caught);
				}
			});
```

# Server #

  * modify web.xml
    * add acris-security session filter and handler
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
    * map dispatcher to handle services
```
	<servlet>
		<servlet-name>service</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<load-on-startup>1</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>service</servlet-name>
		<url-pattern>/sk.seges.acris.security.showcase.Showcase/showcase-service/*</url-pattern>
	</servlet-mapping>
```
    * create service servlet context in WEB-INF/service-servlet.xml , it needs to be empty
```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
</beans>
```
    * for GWT embedded Jetty create **jetty-web.xml** with required JNDI [entries](http://acris.googlecode.com/svn/trunk/acris-security-showcase/war/WEB-INF/jetty-web.xml)
    * acris-security stores user information in database by default, we will use available user with authorities, JPA, Hibernate, Gilead and Spring.
    * create [persistence.xml](http://acris.googlecode.com/svn/trunk/acris-security-showcase/src/main/resources/META-INF/persistence.xml) and map UserWithAuthorities and UserPreferences
    * create Spring XML configuration [acris-security-showcase-server-context.xml](http://acris.googlecode.com/svn/trunk/acris-security-showcase/src/main/resources/sk/seges/acris/security/showcase/acris-security-showcase-server-context.xml) for server application. It is the basis of all beans especially service beans.
      * it imports acris-security-generic-user-context.xml - all default services, DAOs, domain objects will be injected
      * it also imports persistence configuration context [acris-security-showcase-server-persistence-context.xml](http://acris.googlecode.com/svn/trunk/acris-security-showcase/src/main/resources/sk/seges/acris/security/showcase/acris-security-showcase-server-persistence-context.xml) to separate persistence configuration from logic
        * you don't need to change it much unless you have own setup. Don't forget to setup **correct DB server name, database, user and password** in JNDI definition in jetty-web.xml
      * it aliases `acrisSecurityBeanManager` to your current one `<alias name="showcaseHibernateBeanManager" alias="acrisSecurityBeanManager"/>`
      * wire everything with service dispatcher in [gwt-url-mapping.xml](http://acris.googlecode.com/svn/trunk/acris-security-showcase/src/main/resources/sk/seges/acris/security/showcase/gwt-url-mapping.xml)

# IDE #

Finally you can compile the project, create IDE description (for Eclipse `mvn eclipse:eclipse`), use GWT Eclipse plugin and [debug comfortably](http://mojo.codehaus.org/gwt-maven-plugin/user-guide/comfortable_debugging.html).