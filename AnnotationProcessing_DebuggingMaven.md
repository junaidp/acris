# Debugging annotation processor (maven based) #

Have you ever wrote your own annotation processor and don't know how to debug it? If you prefer maven for executing annotation processors (IMHO it is currently the best way, because eclipse compiler still have some bugs or different behaviour comparing to standard java compiler API / processor API), you can setup debug environment really easy in a few seconds.

**Steps**:
  * **Add java debugging options**
```
set MAVEN_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
```
  * **start maven execution**
```
mvn processor:process
```
> _depends on your [maven configuration](AnnotationProcessing_Maven.md)_
> because of `suspend=y` parameter compiler will wait until the debugger is attached
```
Listening for transport dt_socket at address: 8000
```
  * **Attach with eclipse remote debugger**
![http://acris.googlecode.com/svn/wiki/images/eclipse_remote_debugger.png](http://acris.googlecode.com/svn/wiki/images/eclipse_remote_debugger.png)
  * **Place your breakpoints and just debug it**