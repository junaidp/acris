# Steps to setup the acris project #

## Prerequisites ##
  * JDK 1.6+
    * currently acris-image-utils uses some internal Oracle classes so we recommend to use Oracle JDK 6 as well until we fix it, or you can comment the module in acris/acris-os/pom.xml temporarily
  * Maven [3.0.0,> (currently tested on 3.0.3)
  * Brain

## Steps ##
  * Download sources:
    * https://code.google.com/p/acris/source/checkout?repo=sesam
    * https://code.google.com/p/acris/source/checkout?repo=corpis
    * https://code.google.com/p/acris/source/checkout?repo=default
  * copy (or merge acris-os profile from) settings.xml in acris project to your maven home directory (~/.m2)
    * if you already have [settings.xml](https://acris.googlecode.com/svn/trunk/settings.xml) copy the acris-os profile and activate it
  * setup MAVEN\_OPTS:
    * in case of using prepared keystore use path to **acris-os-parent/acris.jceks** file
    * for the environment of the operating system (the preferred way):
      * in **Windows**: Start -> Control panel -> System -> Environment variables, put new key MAVEN\_OPTS following values -Djavax.net.ssl.trustStore=c:\path\to\keystore\acris.jceks -Djavax.net.ssl.trustStorePassword=password -Djavax.net.ssl.trustStoreType=jceks
      * in **Linux**: in your home directory create file **.mavenrc** and put htere following value MAVEN\_OPTS="-Djavax.net.ssl.trustStore=/path/to/keystore/acris.jceks -Djavax.net.ssl.trustStorePassword=password -Djavax.net.ssl.trustStoreType=jceks
  * cd sesam/sesam-os-base-parent
  * mvn -DskipTests clean install
  * cd sesam
  * mvn -DskipTests clean install
  * cd corpis
  * mvn -DskipTests clean install
  * cd acris/acris-os-gwt-parent
  * mvn -DskipTests clean install
  * cd acris
  * mvn -DskipTests clean install
  * pray, a lot, for no error messages

## Running from web container (tomcat) ##

Following step are related to acris-binding showcase project (other projects are analogical)

  * cd acris-showcase/acris-showcase-binding
  * mvn clean package
  * deploy on tomcat - copy acris-showcase-binding-1.0.0-SNAPSHOT.war from target directory to your CATALINA\_HOME/webapps
  * start tomcat - CATALINA\_HOME/bin/start.bat

## Running from eclipse ##

Following step are related to acris-binding showcase project (other projects are analogical)

  * install GEP from <a href='http://code.google.com/intl/sk-SK/appengine/docs/java/tools/eclipse.html'><a href='http://code.google.com/intl/sk-SK/appengine/docs/java/tools/eclipse.html'>http://code.google.com/intl/sk-SK/appengine/docs/java/tools/eclipse.html</a></a>
  * cd acris-showcase/acris-showcase-binding
  * mvn eclise:clean eclipse:eclipse and import project into your eclipse workspace
  * right click on Showcase.gwt.xml file (located in acris-showcase-binding\src\main\resources\sk\seges\acris\binding\ directory) and Run as webapplication
  * open following URL in your browser (change port, if you had specified different port):
http://localhost:8888/sk.seges.acris.binding.Showcase/Showcase.html?gwt.codesvr=localhost:9997