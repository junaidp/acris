# Introduction #

Common use case: Having multiple servers deployed on different nodes/computers means for GWT client not able to communicate with them because of cross-domain requests policy.

### Related artifacts ###

```
<groupId>sk.seges.acris</groupId>
<artifactId>acris-server-components</artifactId>
```


## Routing requests ##

Using _acris-external_ project, specifically the **ProxyServlet**, you will be able to redirect requests from GWT klient to anywhere.

There are two things you need to define:
  * web.xml in client's context - e.g. in tomcat/jetty of development mode or tomcat/jetty of deployed client
  * routes.properties - routing policy defining what goes where

Routing policy is defined in routes.properties. There is a chain of accepting routes file where ProxyServlet takes:
  * from path defined by init-param of web.xml, where param name is _routingFile_ and value is the location within servlet context
  * if the param is not defined, default value of /WEB-INF/routes.properties is taken from deployed directory
  * if not defined one from classpath is taken - /sk/seges/acris/rpc/routes.properties - usually bundled in the JAR

## Route definition ##

Let's take example routing definition

```
*default-host=localhost
*default-port=8888

.*/customUpload(.*) = /module-server/customer1Upload$1
.*sk\.seges\..*\.Module/(module-server/module-service/.*)=/$1
```

We have defined following:
  * the location where all requests will be directed:
    * it will be server located on localhost machine listening on 8888 port
  * two rules in format `source URI = target URI`
    * regular expressions are used for parsing the source and applying to the target
  * when no criteria are met request will be just forwarded to the context where the client is running

## web.xml configuration ##

Example of **web.xml**:
```
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://java.sun.com/xml/ns/j2ee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="WebApp_ID" version="2.5" xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_5.xsd">
  <servlet>
    <servlet-name>proxy</servlet-name>
    <servlet-class>sk.seges.acris.rpc.ProxyServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>proxy</servlet-name>
    <url-pattern>/*</url-pattern>
  </servlet-mapping>
</web-app>
```

# Accessing RPC files from server #

When you are implementing application written in GWT and you are also using GWT-RPC mechanism to call service methods you probably know that GWT creates description of serializable types of your service in RPC file. RPC file is key to determine whether it is possible to serialize specific type.

There is no problem with it until you try to use "no-server" mode where you separate client and server side. For this purpose you usually define an "rpc" communication layer where all your interfaces and domain/DTO objects are stored. From first point of view there is almost no difference but...

Client side is one context and server side is another... When you are **NOT IN** "no-server" mode these RPC files are accessible within one servlet context but **IN** "no-server" mode they are not. Server serialization mechanism must have access to RPC files but they are generated and accessible only on client side.

AcrIS has answer for this in **acris-server-components** project. Currently we support two ways but the second is preffered:
  * local context serialization policy
  * remote context serialization policy

## Global example we will work with ##

Let's suppose we have client deployed in **/client** context and server in **/server** context... We will use Spring integration using GWT-SL library and our examples are based on Spring configuration in XML files.

Let's have web.xml file on our server side which contains a Spring starting-point context in gwt-url-mapping.xml:

```
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>
			classpath:sk/seges/acris/gwt-url-mapping.xml
		</param-value>
	</context-param>
```

Our gwt-url-mapping.xml defines a bean responsible for mapping URI to GWT-RPC service:

```
	<bean id="urlMapping"
		class="org.gwtwidgets.server.spring.GWTHandler">
		<property name="serviceExporterFactory" ref="configurableExporterFactory" />
		<property name="mappings">
			<map>
				<entry key="/cms" value-ref="contentManagementService" />
				<entry key="/blog" value-ref="blogService" />
			</map>
		</property>
	</bean>
```

And now the fun begins... notice the property **serviceExporterFactory**. It is a point were we define our own exporter able to inject our own serialization policy...

## Configurable exporter factory ##

It is factory for RPC Service exporters that is able to create custom service exporters. It integrates to GWT-SL library and creates an exporter suitable for GWT-RPC service. Exporter class is retrieved from Spring application context using service exporter bean name. Default service exporter bean name is defined in constant DEFAULT\_SERVICE\_EXPORTER\_BEAN\_NAME.

The value of this constant is currently //serviceExporter//.


The only thing we are interested in service exporters (they inherit from GWT's RemoteServiceServlet) is the way how to get serialization policy. Because we want to support other strategies of serialization policy we inject own service exporter:

  * either CustomPolicyRPCServiceExporter
  * or GileadGWTCustomPolicyRPCServiceExporter

By simple guessing the second is used when you want to use Gilead library and their services. First one is for plain old GWT services.

For our example we would define it e.g. like this:

```
	<bean id="configurableExporterFactory" class="sk.seges.acris.rpc.ConfigurableRPCServiceExporterFactory" scope="prototype" />
```

And now we get to the point - **serialization policy**. That is based on our decision whether it will be local or remote...

## Local context ##

### Simple answer ###

RPC files are located in server context

### Configuration ###

```
	<bean id="serviceExporter" class="sk.seges.acris.rpc.CustomPolicyRPCServiceExporter" scope="prototype">
		<property name="serializationPolicy" ref="localContextPolicy" />
	</bean>

	<bean id="localContextPolicy" class="sk.seges.acris.rpc.LocalContextSerializationPolicy" scope="prototype" />
```

### Explanation ###

LocalContextSerializationPolicy expects that all required serialization policy files are present in server context of called service. The policy is loading it from the context path location of the service you are calling. So if the service is under /server context it will find out and use that context instead of module context (in this case /client) it is calling from.

You can achieve working configuration using the policy by creating copy rules in Ant or Maven.

Cons:
  * it requires copying
  * may lead to (usually in development) not actual files
Pros:
  * in deployment it is quick - no HTTP requests

## Remote context ##

### Simple answer ###

RPC files are still in client context but server will ask for them using HTTP requests

### Configuration ###

```
	<bean id="serviceExporter" class="sk.seges.acris.rpc.CustomPolicyRPCServiceExporter" scope="prototype">
		<property name="serializationPolicy" ref="remoteContextPolicy" />
	</bean>

	<bean id="remoteContextPolicy" class="sk.seges.acris.rpc.RemoteContextSerializationPolicy" scope="prototype" />
```

### Explanation ###

From name of the policy you can expect that it will read serialization policy files from another (remote) context. RemoteContextSerializationPolicy is loading RPC file from the context path location of the service you are calling using HTTP connection request to /client/strongName.gwt.rpc file. This RPC file is of-course accessible in client servlet container (be it Jetty or Tomcat or Glassfish or whatever..) so it is easy to forge such request.

Cons
  * until caching is implemented, server must ask client for RPC files
Pros
  * no way to have stale RPC files
  * no manual interaction like copying