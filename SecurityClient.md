# Client security #

Almost every site or portal on the internet contains a restricted area. It means entering it you have to provide some credentials that authorizes you to access restricted content. Authentication and authorization  is done by a service - we call it **user service**. The result of authentication and authorization is an object containing the information about user and its authorities.

Implementing such a portal means you need user interface (UI) components that are in/visible or enabled/disabled based on the user authorization (set of authorities describe that). You also need some methods on the server side to be secured - access to them must be restricted for unprivileged users.

As a developer in GWT you don't have an integrated solution for that and that is where **acris-security** comes :) In the following paragraphs the description of functionality will follow and we will start with...

# User service #

So what exactly user service does? That's simple. Calls authentication mechanisms responsible for checking whether the user is who he says he is and assigns **authorities** belonging him. The service also initializes security context that is going to be propagated with every secured request. User service is a glue between login panel in UI and security context initialization.

Every user service implementation needs to implement `IUserService` interface. It allows you to log in, log out or check currently logged user (if it is supported by the implementation). To not bother you very much you can use prepared "standard" user service called... `UserService` with following features:

  * authenticates based on the user name and password provided - using `UserPasswordLoginToken`
  * works with `UserWithAuthorities` by default
  * uses Hibernate to persist the information about user and his authorities
  * uses Gilead to strip and transfer user (domain) object back and forth
  * is accessible using GWT-RPC mechanism

User service is designed in the way it supports (hopefully) any model of user information provided by implementing `LoginToken` as the source of the login information. Common model of providing user name and password is handled by `UserPasswordLoginToken` but if for your case another parameter is required you may create your own (good example might be a country or department altering the behaviour of authentication and authorization of your user service implementation).

User service is Gilead-enabled standard GWT-RPC service and can be mapped to an URL of your will. Thus you can access it by a code snippet like this:
```
	final IUserServiceAsync userService = GWT.create(IUserService.class);
	SessionServiceDefTarget endpoint = (SessionServiceDefTarget) userService;
	endpoint.setServiceEntryPoint("showcase-service/userService");
	endpoint.setSession(clientSession);
```

So if you want to log in you just call `login` method providing the `LoginToken` implementation. Every time you are willing to handle security exceptions coming from the server on the client side you can use prepared `SecuredAsyncCallback`. The callback has a template method reserved for handling `SecurityException`s.

# Client session #

The term **client session** is important in the context of acris-security. It is used (but not only) to hold the information about logged user. The user domain object extending from `GenericUser` like `UserWithAuthorities` is is stored in the client session. Every component that wants to be "secured" has to know about client session so it's logic can check what to do based on the authorities.

The session also holds one important string - **session ID**. Session ID is propagated with every RPC service call so the server can bind logged user with server side security context (currently implemented in Spring Security).  Session ID is generated and is unique for each user.

Oh, and guess what... the name of the class representing client session is `ClientSession`.

# Fun around UI components #

OK, we are logged in, we have client session filled and we need our panels and fields and ...(whatever)... to be "secured". And of course we are lazy developers. And maybe we like annotations also but maybe we don't. Or we cannot. But acris-security has many ways how to achieve the goal. In the chapter we will talk about components mainly and because of that we will describe two approaches to securing them:
  * using `ISecuredObject` implementations
    * with `IManageableSecuredObject` extension
  * using `IRuntimeSecuredObject` implementations

As we have mentioned earlier each secured component needs the access to client session. And each secured component implementation is enriched with generated code controlling the logic of visibility. The generated code can control anything else but current logic is about controlling whether field (TextBox, Label, Button,...) is in/visible, enabled or disabled based on the provided authorities in the client session.

Every secured component must be instantiated using `GWT.create`.

## Annotation-driven secured object ##

Let's start with the one with the least effort - `ISecuredObject`. Implementation of this secured object relies heavily on annotations. In combination with `sk.seges.acris.security.client.annotations.Secured` annotation you can describe which field is "secured" and is going to be managed by authorities of the user.

`Secured` annotation is altered with two significant parameters:
  * list of **grants** - see the difference that **not authority** BUT **grant** is used
  * permission

The provided list of grants defines which grants the user needs to manipulate the field annotated with the `Secured` annotation. By default the security logic works with two permissions - **VIEW** and **EDIT**. They control (in the order) the visibility and whether component is enabled or disabled. You are able to override the behaviour and define which permission is required.

_Example:_

Having `Grants.SECURITY_MANAGEMENT` grant which is just a string equal to "security\_management" we are altering the behaviour of the field using following annotation:

```
@Secured(Grants.SECURITY_MANAGEMENT)
protected TextBox securityID;
```

When the user has the authority `ROLE_security_management_VIEW` stored in his profile he will be able _to see_ but _not to edit_ the text box for security ID. If he has `ROLE_security_management_EDIT` he will not only _see_ the field but also _edit_ it.

If you have multiple fields and all of them belong to one grant it wouldn't be efficient to explicitly name the grant on top of every field. You can annotate whole secured object with the same `Secured` annotation, specify the grant and don't bother with it on each field. Only if you want to change it explicitly you have to define it.

## Managing the attributes of the secured object ##

**TBW.** -> IManageableSecuredObject

## Secured object with authorities known in runtime ##

As opposed to annotation-driven secured objects imagine a situation where you don't know the set of authorities yet. While you are not able to define e.g. annotations in compile time you might be able to do it in runtime e.g. by calling a service. A good example for that is a menu item where security is managed by an administrator. The menu item class is of one type but gets different security constraints for different instances.

There is `RuntimeSecurityMediator` allowing to define what authorities secured object will have. Using one of the setter methods you can pass relevant authorities.

```
public class SecuredMenuItem implements IRuntimeSecuredObject {
...
}

...

SecuredMenuItem item = GWT.create(SecuredMenuItem.class);
RuntimeSecurityMediator.setAuthority(Grants.SECURITY_MANAGEMENT, item);
```

## Re-checking authorities in runtime ##

Every type of secured object currently written using either ISecuredObject, IManageableSecuredObject or IRuntimeSecuredObject also implements `CheckableSecuredObject` allowing you to reevaluate user's authorities in runtime and reset attributes of the secured object (visible, enable).

By calling `check()` method you can trigger reevaluation. This functionality might be handy when you users are logging in somewhere in time not at the start up of the application. Triggering controllers (or presenters or any mighty controlling component) with an event/message/call from the log procedure you can reevaluate secured object state.

# RPC augmentation #

**TBW.** session ID in payload & altered proxy generator

# Broadcasting login information #

**TBW.** how to register broadcaster + impact on the server side + describe multiple contexts and separation of logic
