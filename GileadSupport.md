# Introduction #

AcrIS is using Gilead as integration component for persistent layer. Gilead adds support for serializing Hibernate POJOs to GWT client. For more information about Gilead please take a look on their page - http://noon.gilead.free.fr/gilead .


# Working with own types #

Gilead supports basic types and collections but in case you developed for example special-purpose collection you need to extend Gilead's persistent manager.

So is the case of our **PagedResult** - class holding a page of result list which is response from a Hibernate query.

In AcrIS we therefore use custom persistent bean manager - sk.seges.acris.rpc.PersistentBeanManager

Let's have BlogService extends PersistentRemoteService ...

Example:

```
	<bean id="blogService"
		class="sk.seges.acris.service.BlogService" scope="prototype">
		<property name="beanManager" ref="acrisHibernateBeanManager" />
	</bean>

	<bean id="acrisHibernateBeanManager" class="sk.seges.acris.rpc.PersistentBeanManager">
		<property name="proxyStore" ref="proxyStore" />
		<property name="classMapper" ref="acrisCloneMapper" />
		<property name="persistenceUtil" ref="acrisPersistenceUtil" />
	</bean>
```