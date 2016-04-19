# Security Concept #

To correctly understand what is going on in acris-security we have to define common vocabulary. Doing that way it is possible to base our understanding and to ease up the usability. It will be quick and sharp :)

When talking about security there are two parts of the channel worth to mention:
  * client = your GUI application with "secured" UI components (buttons, panels, labels, combo boxes,...)
  * server = the executive logic with restricted access to methods and granted-privileges driven functionality

Current implementation supports one type of the communication channel between the sides: GWT RPC. GWT RPC serves as a transport of service-required data and (from security point of view) payload filled with so called **session ID**. Session ID is a string identifying current/opened user session between client and server with known authorization set of information. That way the server will know if and which client requests secured treatment.

Because server-side security is a problem already solved by existing framework, focus of acris-security is to bridge and integrate server-side security with client's - transfering authorities and enabling/disabling components based on it.

Current implementation uses Spring Security for the server. The client uses annotations, modified RPC proxy mechanism and set of generators to enable or show components. There are several extension points where skilled developer can enhance the existing functionality.

## Authentication and Authorization ##

Let's define logical components of the security:
  * **user** - when talking about a user it is usually meant class **GenericUser** and its derivatives. The result of successful authentification is GenericUser with filled authorities
  * **authority** - a string token representing "what the user is allowed to do".
  * **user service** - an implementation of IUserService interface responsible for filling the user with a set of authorities

Because authority is not that friendly string as you might expect let's discuss its structure. Some constraints put by Spring Security framework resulted in using constant prefix ROLE.

Example authority: `ROLE_ordermanagement_VIEW`

This authority says that a user is allowed to see but not modify everything regarding some order management logic. You might notice 3 parts of the authority:
  * **authority prefix** - already mentioned constraint of the framework to identify the authority, defined in `SecurityConstants.AUTH_PREFIX`
  * **grant** - "ordermanagement" - friendly representation of the authority describing what for it is
  * **permission** - VIEW - fine-grained specification whether user can see, edit,... or whatever do based on the logic of the grant. Currently there are four permissions defined in **Permission** enumeration, with suffix constants like `Permission.VIEW_SUFFIX`.

To finish the concept of authentication client side and also server side needs some input values like username, password,... User service:
  * evaluates client's request for authentication based on an implementation of login token (represented by the interface **LoginToken**),
  * validates the request by transforming to Authentication (Spring's interface) implementation passed to a chain of authentication providers