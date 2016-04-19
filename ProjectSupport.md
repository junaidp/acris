# Using the power #

AcrIS is not fanatically dependent on the Maven and can be used in both scenarios (simple and multi-module) without it but as the primary project management tool we have built helper structure and configurations that might be handy.

If you are going to use it you will always be faced with the following terms:
  * **acris-os-gwt-parent** - is a "pom" type Maven project containing the definitions of dependencies relevant to GWT development
    * dependency management of all libraries integrated/supported by AcrIS
    * versions of that libraries
    * plugin configurations (for **gwt-maven-plugin**, building source JARs along with binaries, ...)
  * **sesam-os-base-parent** - is a "pom" type Maven project, the master of all masters defining common libraries not only for GWT development but usually required by server side
    * the principle is the same as acris-os-gwt-parent and sesam-os-base-parent is the parent of acris-os-gwt-parent
    * if you want to stick with our's regularly updated libraries and don't bother, just inherit it (using `<parent>` tag)
  * **parent** project - used especially in multi-module setup where you define a parent project for your project and it should contain
    * dependency management custom to your project, version overrides
    * plugin configuration overrides, plugin management definition
  * **project pom** - because the default setup using acris-os-gwt-parent is that GWT projects are distributed in two JARs (binary and sources) you will usually find in there duplicit definitions of AcrIS dependencies distinguished with the `sources` classifier. In the project pom you define dependencies on other libraries but without versions (they are in the parent in the dependency management section).
    * custom plugin configurations are here also
    * your stuff you/we like....

# One-module project #

This type of project considering using Maven is handy while you are going to create a showcase or simple application:
  * it will be quicker to setup, quicker to run without that much configuration on the server side
  * GWT development mode will start your client and server
  * there will be one output - one WAR package containg client and server

But you have to count with some implications:
  * all dependencies are in one place
  * enterprise deployment is not possible without further interaction
  * developing client and server within one big package requires you to restart development mode and initialize both client and server on every change

# Multi-module enterprise project #

Using multi-module setup is like running long distance. If you do it properly and invest time at the beginning you won't spend that much time in the future. In our humble opinion it also structures the project in cleaner manner having dependencies where they belong and allowing you to manipulate with client and server separately.

![http://acris.googlecode.com/svn/wiki/images/project-structure.png](http://acris.googlecode.com/svn/wiki/images/project-structure.png)

Usually the project consist either of 3 or 5 (or of course more) projects from structural point of view:
  * 3 - `client-war`, `rpc`, `server-war`
    * simple setup where you don't want to reuse parts of client or server in other projects (or subprojects)
    * `rpc` is a synonym for shared components (e.g. service interfaces, domain objects) common for both sides
  * 5 - `client-war`, `client`, `rpc`, `server`, `server-war`
    * relevant logic is within `client`, `rpc` and `server` projects and `client-war` and `server-war` serve as projects that allow you to develop and deploy that part of the project
    * useful when you plan to share `client` or `server` in different pojects
    * you can of course have multiple "client" or "server" projects, they only describe the structure

Using this project structure it leads to running in so-called **no-server** mode where the server is not embedded in the GWT development environment but runs separately. It is like you would develop server in standalone environment (e.g. using Eclipse's WTP plugin and a Tomcat/Jetty/... instance) not bothering about the client. The client requires a running server (of course only if there is a client-server communication). That way you can independently develop and restart both sides until you change the `rpc` project.

You can use the techniques of separating client and server described in [this chapter](SeparateClientAndServer.md). The separation requires a proxy mechanism in development (and depending on the target deployment maybe also in production). The proxy mechanism can be handy solving **Same origin policy** problems.

For comfortable debugging you might also consider reading [the article about](http://mojo.codehaus.org/gwt-maven-plugin/user-guide/comfortable_debugging.html) it in gwt-maven-plugin wiki.