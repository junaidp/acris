# Migration from Gilead 1.2.3 to Gilead 1.3.2 #

## Casting HashMap ##

You can get following exception while migrating:
```
java.lang.RuntimeException: java.lang.ClassCastException: java.util.HashMap cannot be cast to net.sf.gilead.pojo.gwt.IGwtSerializableParameter
```

### Solution ###

ProxyStore should have defined proxy serializer in the correct way, so you have to change original instantiating from:
```
StatelessProxyStore proxyStore = new StatelessProxyStore();
```

into:
```
StatelessProxyStore proxyStore = new StatelessProxyStore();
proxyStore .setProxySerializer(new GwtProxySerialization());
persistentBeanManager.setProxyStore(proxyStore );
```

or using spring XML configuration:
```
<bean id="proxyStore"
	class="net.sf.gilead.core.store.stateless.StatelessProxyStore">
	<property name="proxySerializer">  
		<bean class="net.sf.gilead.core.serialization.GwtProxySerialization"/>
	</property>
</bean> 
```

## LightEntity super class ##

Change LightEntity super class from
```
net.sf.gilead.pojo.gwt15.LightEntity
```

into:
```
net.sf.gilead.pojo.gwt.LightEntity
```

Or new ILightEntity implementation
```
protected Map<String, IGwtSerializableParameter> _proxyInformations;

protected Map<String, Boolean> _initializationMap;

public void addProxyInformation(String property, Object proxyInfo) {
	if (_proxyInformations == null) {
		_proxyInformations = new HashMap<String, IGwtSerializableParameter>();
	}
	_proxyInformations.put(property, (IGwtSerializableParameter) proxyInfo);
}

public void removeProxyInformation(String property) {
	if (_proxyInformations != null) {
		_proxyInformations.remove(property);
	}
}

@Transient
public Object getProxyInformation(String property) {
	if (_proxyInformations != null) {
		return _proxyInformations.get(property);
	} else {
		return null;
	}
}

@Transient
public String getDebugString() {
	if (_proxyInformations != null) {
		return _proxyInformations.toString();
	} else {
                return null;
	}
}

@Transient
public boolean isInitialized(String property) {
	if (_initializationMap == null) {
		return true;
	}

	Boolean initialized = _initializationMap.get(property);
	if (initialized == null) {
		return true;
	}
	return initialized.booleanValue();
}

public void setInitialized(String property, boolean initialized) {
	if (_initializationMap == null) {
		_initializationMap = new HashMap<String, Boolean>();
	}
	_initializationMap.put(property, initialized);
}

@Transient
public Object getUnderlyingValue() {
	return this;
}
```

## GWT module naming change ##

Change original GWT module from
```
<inherits name='net.sf.gilead.Adapter4Gwt15'/>
```

into:

```
<inherits name='net.sf.gilead.Gilead4Gwt'/>
```

## Gilead projects naming change ##
~~adapter-core~~ -> gilead-core

~~hibernate-util~~ -> gilead-hibernate

~~adapter4gwt~~ -> gilead4gwt

All libraries can be obtained from
https://gilead.svn.sourceforge.net/svnroot/gilead/gilead/maven-repo/net/sf/gilead/

# Simultaneously Fetch Multiple Bags #

When an entity has more than one non-lazy association that might be interpreted as a bag (e.g., java.util.List or java.util.Collection properties annotated with @CollectionOfElements or @OneToMany or @ManyToMany and not annotated with @IndexColumn) hibernate will fail to fetch the entity correctly.

## Solution ##

[Use the Set, Luke](http://www.jroller.com/eyallupu/entry/hibernate_exception_simultaneously_fetch_multiple)

# UnsupportedOperationException in AbstractList #

Let's have a test case where we are working with Hibernate. The test creates a list of items using `Arrays.asList` method. If we persist such list and retrieve it later, the list will be wrapped into `PersistentBag` (or any PersistentSomething class of Hibernate). In case we are trying to put/remove an item from such bag we might get an `UnsupportedOperationException` on add or remove methods of the bag.

## Solution ##

It is because `Arrays.asList` creates internal list implementation `Arrays$ArrayList` that is different from `java.util.ArrayList`. It doesn't have methods to add or remove at specific index overriden. When Hibernate tries to add/remove to specific index it delegates the call to `Arrays$ArrayList` which throws the exception.

So don't use `Arrays.asList` or wrap it e.g. using new `java.util.ArrayList(collection)`.

# Unsupported Image Type for JPEG using Java Image I/O #

If you have a problem reading JPEG images using ImageIO.read(File file) and searched for the right library to read it, you might come to a conclusion - there is none suitable.

## Solution ##

Grab https://jai-imageio.dev.java.net/ and extend your JDK/JRE/Classpath. It contains extended set of formats.