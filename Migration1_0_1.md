# Sonatype repository #

sonatype repository definition in settings.xml - for AcrIS artifacts we use Sonatype's OSS maven repository from now on. In the future all releases will be synced to Maven Central.
```
	<repository>
		<id>acris-sonatype</id>
		<url>https://oss.sonatype.org/content/groups/staging</url>
		<snapshots>
			<enabled>true</enabled>
		</snapshots>
	</repository>		
```

# Safeway of binding #
Use typesafe annotations instead of string values

Original code (version 1.0.0)
```
@BindingField(Customer.COMPANY + "." + CompanyName.NAME)
protected TextBox tbName = GWT.create(TextBox.class);
```

New approach (version 1.0.1)
```
@BindingField(CustomerBeanWrapper.COMPANY.NAME)
protected TextBox customerName = GWT.create(TextBox.class);
```

# Apapter providers registration #

Use a new apapter providers registration (register all available adapter providers on your classpath in one step)

```
import sk.seges.acris.binding.client.init.AdaptersRegistration;
...
AdaptersRegistration registration = GWT.create(AdaptersRegistration.class);
registration.registerAllAdapters();
```

# Bean wrapper adapter provider #

Register adapter provider for bean wrapper

```
import sk.seges.acris.binding.client.providers.wrapper.BeanWrapperAdapterProvider;
...
BeanAdapterFactory.addProvider(new BeanWrapperAdapterProvider());
```

This adapter provider replaces introspection from gwtx and provides values from bean using bean wrapper (e.g. reflection for javascript). BeanWrapperAdapterProvider is also used for validator delegates and for each extension of the BeanWrapper implementation.

**Why is this used for?**
Previously, the bean wrapper implementation was extending original bean so it was possible to reuse introspection beans from the original type and enable introspection for each super class. In current version, bean wrapper implementation superclass can vary (in order to ensure integration with third party UI libraries) and the introspection bean cannot be delegated anymore.

# Clearing Gilead dependencies #
  * Remove beanManager from UserService. User service is no more dependend on gilead RemotePersistentService so beanManager is not necesary

Original user service definition (version 1.0.0):

```
<bean name="userService" class="sk.seges.acris.security.server.user_management.service.user.UserService">
	<property name="beanManager" ref="acrisHibernateBeanManager" />
	<property name="authenticationManager" ref="authenticationManager"/>
	<property name="sessionIDGenerator">
		<bean class="sk.seges.acris.security.server.session.HttpSessionIDGenerator" />
	</property>
</bean>
```

New user service definition (version 1.0.1):

```
<bean name="userService" class="sk.seges.acris.security.server.user_management.service.user.UserService">
	<property name="authenticationManager" ref="authenticationManager"/>
	<property name="sessionIDGenerator">
		<bean class="sk.seges.acris.security.server.session.HttpSessionIDGenerator" />
	</property>
</bean>
```

# Security revisited #

Goal of the new acris was to provide better reusability of the acris-security. Old version was highly dependended on the spring & hibernate implementation and that does not allows us to migrate from one platform into another. Based on this we decided to rewrite the security in the way the client side implementation & server side implementation will
be highly dependend only on interfaces and concrete implementation will be plugable. This approach allows us to switch from RDBMS into appEngine just with plugging up the correct model & dao implemenetation.

Security changes are related to the following issues:
  * http://code.google.com/p/acris/issues/detail?id=10
  * http://code.google.com/p/acris/issues/detail?id=9
  * http://code.google.com/p/acris/issues/detail?id=14
  * http://code.google.com/p/acris/issues/detail?id=11

## Old approach ##

Overall application used GenericUser class.

```
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "generic_users")
public class GenericUser implements IDomainObject<Long>, UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String password;

        ...
```

## New approach ##

```
public interface UserData extends IMutableDomainObject<Long> {

	String getPassword();
}
```

Hibernate implementation

```
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "generic_users")
public class HibernateGenericUser extends GenericUserDTO {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        public Long getId() {
            return super.getId();
        }

    	@Column
	public String getPassword() {
	    return super.getPassword();
	}
```

Using the implementation:

```
public class HibernateGenericUserDao extends AbstractHibernateCRUD<UserData> implements IGenericUserDao<UserData> {

    public HibernateGenericUserDao() {
        super(HibernateGenericUser.class);
    }
```

See the differences? Instead of using hibernate implementation in the whole application we rather use UserData interface and hibernate implementation is used only in DAO. This
approach is highly pluggable and by switching the DAO implementation you can achieve migration possibilities.

## Extracted interfaces ##

```
sk.seges.acris.security.rpc.user_management.domain.GenericUser -> sk.seges.acris.security.shared.user_management.domain.api.UserData
sk.seges.acris.security.rpc.user_management.domain.RolePermission -> sk.seges.acris.security.shared.user_management.domain.api.UserRolePermission
sk.seges.acris.security.rpc.user_management.domain.SecurityPermission -> sk.seges.acris.security.shared.user_management.domain.api.HierarchyPermission
sk.seges.acris.security.rpc.user_management.domain.SecurityRole -> sk.seges.acris.security.shared.user_management.domain.api.RoleData
```

Each interface has:
  * clear DTO definition without any annotation
  * hibernate implementation
  * jpa implementation
  * appengine implementation

## Package reorganisation ##

### Old approach ###

  * sk.seges.acris.security.rpc for common layer bewteen client & server
  * sk.seges.acris.security.rpc.user\_management.domain for domain model definition
  * sk.seges.acris.security.rpc.user\_management.service for service interface definition
  * sk.seges.acris.security.server.user\_management.dao.user.impl for DAO layer implementation

### New approach ###

  * ~~sk.seges.acris.security.rpc~~ replaced by sk.seges.acris.security.shared
  * ~~sk.seges.acris.security.rpc.user\_management.domain~~ replaced by sk.seges.acris.security.shared.user\_management.domain.api
  * ~~sk.seges.acris.security.rpc.user\_management.service~~ replaced by sk.seges.acris.security.shared.user\_management.service
  * ~~sk.seges.acris.security.server.core.user\_management.dao.user~~ replaced by sk.seges.acris.security.server.core.user\_management.dao.user
  * ~~sk.seges.acris.security.server.core.user\_management.dao.user.impl~~ replaced by

## Gilead implementation ##

![https://acris.googlecode.com/svn/wiki/yuml/gilead_security.png](https://acris.googlecode.com/svn/wiki/yuml/gilead_security.png)

## Hibernate implementation ##

## JPA implementation ##

## Appengine implementation ##