# Introduction #

The purpose of this feature is to start-up an OpenMQ listener automatically upon Tomcat startup. It is useful in scenarios where everything is embedded.

### Related artifacts ###

```
<groupId>sk.seges.acris</groupId>
<artifactId>acris-tomcat-support</artifactId>
```

# Details #

  * necessary jars to copy to **tomcat/lib**:
    * acris-tomcat-support
    * [JMS API](http://search.maven.org/#artifactdetails%7Cjavax.jms%7Cjms%7C1.1%7Cjar)
    * [imq](http://search.maven.org/#search%7Cga%7C1%7Cimq)
    * [imqutil](http://search.maven.org/#search%7Cga%7C1%7Cimqutil)
    * [imqbroker](http://search.maven.org/#search%7Cga%7C1%7Cimqbroker)
    * [imqjmx](http://search.maven.org/#search%7Cga%7C1%7Cimqjmx)

Tomcat configuration in server.conf:

```
<Listener className="sk.seges.acris.jeesupport.tomcat.ImqBrokerListener" />
```

Specify resource with direct connection in **GlobalNamingResources** section:
  * imqAddressList="mq://localhost/direct"
  * also original TCP connection can be used

```
<Resource auth="Container" factory="com.sun.messaging.naming.AdministeredObjectFactory" imqAddressList="mq://localhost/direct" imqReconnectAttempts="100" name="jms/acris/generalQCF" readOnly="true" type="com.sun.messaging.QueueConnectionFactory" version="3.0"/>
```

If you want to override startup parameters, then you can do it in **setenv.sh**. This is an example with some reasonable defaults also part of the listener itself, so you don't have to define it, unless you want to change it:
```
CATALINA_OPTS="$CATALINA_OPTS -Dimq.imqhome=$CATALINA_HOME/mq -Dimq.varhome=$CATALINA_HOME/mq/var -Dimq.name=imqEmbedded -Dimq.port=7676"
export IMQ_HOME="$CATALINA_HOME/mq"
```