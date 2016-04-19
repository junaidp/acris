# Introduction #

In this guide we will talk about how to use the projects acris-security-core, acris-security-openid and acris-security-ui to integrate both classic username/password and OpenID based login methods, for example through Google or Yahoo, into your Java web application.

![http://acris.googlecode.com/svn/wiki/images/open_id_login_dialog.png](http://acris.googlecode.com/svn/wiki/images/open_id_login_dialog.png)

So what we want to do is to provide users with a user interface where they can enter a username and password and log in normally through our local user service, or instead select from a list of OpenID providers, open a popup window and log in through the chosen provider. This UI will exist in a lightweight GWT module, which will only handle the login process, after which it will redirect the browser to a url with another module which then processes this login information.

When communicating with the OpenID provider, three steps are necessary. First, discovery on the provider's web site for the end point URL. This URL must be the base of any OpenID authentication request. Second, authentication of the user at the provider. No specific ID's are sent, we let the user enter all information. During the authentication process, we ask the user to share the e-mail address with us, which we then use for identification. Third is verification of a positive or negative answer from the provider, plus a check if the request for authentication really came from us.

Note that our login module is also responsible for verification. This means that when the user logs in in the OpenID provider's popup window, another instance of the module is launched in the popup which decides whether to close the popup if the login failed, or to redirect the parent window to the redirect URL if the login was successful.

For more information about OpenID visit http://openid.net.
The projects also use the openid4java library, for more info visit http://code.google.com/p/openid4java.

An example entry point can be found in the [acris-showcase-openid project](http://acris.googlecode.com/svn/trunk/acris-showcase/acris-showcase-openid/), which showcases setting up a guice servlet and a mock user service to demonstrate logging in through Google, Yahoo, Aol, Seznam and MyOpenID, or by using a classic login method. Let's go over it in detail.


# Configuration #

In your module's gwt.xml file, add the following inherit:

```
<module>
	<inherits name='sk.seges.acris.security.OpenID' />
</module>
```


Since we are using google-guice for dependency injection in this example, we will need this in our web.xml:

```
<web-app>
  <filter>
        <filter-name>guiceFilter</filter-name>
    	<filter-class>com.google.inject.servlet.GuiceFilter</filter-class>
  </filter>

  <filter-mapping>
    	<filter-name>guiceFilter</filter-name>
    	<url-pattern>/*</url-pattern>
  </filter-mapping>

  <listener>
    	<listener-class>sk.seges.acris.openid.server.service.ServletContextListener</listener-class>
  </listener>

</web-app>
```

Here we simply enable guice filtering for all requests and inject our guice modules, [ShowcaseGuiceServletModule](http://acris.googlecode.com/svn/trunk/acris-showcase/acris-showcase-openid/src/main/java/sk/seges/acris/openid/server/service/ShowcaseGuiceServletModule.java) and [ShowcaseGuiceModule](http://acris.googlecode.com/svn/trunk/acris-showcase/acris-showcase-openid/src/main/java/sk/seges/acris/openid/server/service/ShowcaseGuiceModule.java). Note that we are using guice servlet injection here, this way we can provide a different implementation for certain classes. Google's app engine environment, for example, needs such special implementations.

To be able to use guice dependency injection in servlets, we need two things.
First, all server calls will be sent to [GuiceRemoteServiceServlet](http://acris.googlecode.com/svn/trunk/acris-security/acris-security-openid/src/main/java/sk/seges/acris/security/server/service/GuiceRemoteServiceServlet.java), found in acris-security-openid. This servlet will intercept every call, get the service instance we are trying to access and inject it with whatever dependencies we have defined in our guice modules. This also means that all services injected in this way don't need to extend RemoteServiceServlet, since we are not calling them directly. Second, all services we want to inject with guice dependencies must be mapped to the same entry point url as the [GuiceRemoteServiceServlet](http://acris.googlecode.com/svn/trunk/acris-security/acris-security-openid/src/main/java/sk/seges/acris/security/server/service/GuiceRemoteServiceServlet.java), in this case "GWT.rpc".


# Client #

**[LoginPresenter](http://acris.googlecode.com/svn/trunk/acris-security/acris-security-openid/src/main/java/sk/seges/acris/security/client/presenter/LoginPresenter.java), [LoginView](http://acris.googlecode.com/svn/trunk/acris-security/acris-security-ui/src/main/java/sk/seges/acris/security/client/view/LoginView.java)**

Use for a classic username/password login method. Based on the MVP pattern, we have a presenter class which handles all the logic and a display interface with a view class that displays a user interface that displays a user name and password fields and a login button. The basic [LoginPresenter](http://acris.googlecode.com/svn/trunk/acris-security/acris-security-openid/src/main/java/sk/seges/acris/security/client/presenter/LoginPresenter.java) only needs to be provided with a view, user service broadcaster and redirect url like this:

```
IUserServiceAsync userService = new MockUserService();

String redirectUrl = GWT.getModuleBaseURL() + "Redirect.html";

LoginDisplay display = GWT.create(LoginView.class);

LoginPresenter presenter = new LoginPresenter(display, userService, redirectUrl);
```

Note that in this example we use url redirection for logging in by sending the session id in the query string, but the presenter also supports custom logins without redirection (pass null for 'redirectUrl' in the constructor).
Then you onle need to bind the presenter to a panel:

```
presenter.bind(RootPanel.get());
```

This will register all necessary handlers and display the associated view, in this case by adding it to RootPanel.


**OpenIDConsumerService**

Acts as an OpenID consumer which sends discovery, authentication and verification requests to an OpenID provider like Google. Uses openid4java library.


**OpenIDLoginPresenter, OpenIDLoginView**

In addition to the user name and password fields and a login button, adds five buttons with predefined OpenID provider endpoints, which open popup windows where the user can log in by providing an OpenID identifier. The presenter handles all the logic, including discovery, authentication, verification, as well as the final redirection to the desired url.

For expanded functionality, both the OpenID and the basic presenter have locale and remember me cookies support, as can be seen here:

```
IOpenIDConsumerServiceAsync consumerService = GWT.create(IOpenIDConsumerService.class);
((ServiceDefTarget) consumerService).setServiceEntryPoint("GWT.rpc");

Pair<String, String>[] languages = new Pair[2];
languages[0] = new Pair<String, String>("sk", "slovensky");
languages[1] = new Pair<String, String>("en", "english");

boolean rememberMeAware = true;

OpenIDLoginPresenter presenter = new OpenIDLoginPresenter(display, userService, redirectUrl, languages,
				rememberMeAware, consumerService);
```


# Server #

**ConsumerManager**

Manages OpenID communications with an OpenID Provider. Provided by the openid4java library. Needs special overrides for app engine, that is why it is injected.


**[ServerSessionProvider](http://acris.googlecode.com/svn/trunk/acris-security/acris-security-core/src/main/java/sk/seges/acris/security/server/core/session/ServerSessionProvider.java)**

Provider interface for the server session. The example app uses guice to provide the session through [GuiceServerSessionProvider](http://acris.googlecode.com/svn/trunk/acris-showcase/acris-showcase-openid/src/main/java/sk/seges/acris/openid/server/session/servlet/GuiceServerSessionProvider.java), for a spring session use http://acris.googlecode.com/svn/trunk/acris-security/acris-security-spring/src/main/java/sk/seges/acris/security/server/core/session/spring/SpringServerSessionProvider.java.

Check out our
[OpenID demo](http://1.latest.acris-gwt.appspot.com/sk.seges.acris.demo.OpenID/OpenID.html) on App engine (the implementation is appengine indenpendent and can be used also outside of clouds).