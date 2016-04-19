# Eclipse configuration #

Running annotation processors in the eclipse can be done in a few very easy steps. Annotation processor and the usage of the annotation should be divided into 2 separate projects (there is a possiblity how support also single project architecture, but the processors was designed to lay in separate jar - so let's follow this rule).

There are plenty of ways, how to enable annotation processing in eclipse. First one is to register eclipse plugin (in this way you can also debug annotation processor) or you can activate native support in the eclipse settings.
This requires processor registration in _META-INF/services/javax.annotation.processing.Processor_

![http://acris.googlecode.com/svn/wiki/images/pap_eclipse_service_registration.png](http://acris.googlecode.com/svn/wiki/images/pap_eclipse_service_registration.png)

Next step is to enable annotation processing in the project **Preferences -> Java compiler -> Annotation processing**. Check all the checkboxes :-)

![http://acris.googlecode.com/svn/wiki/images/pap_eclipse_apt.png](http://acris.googlecode.com/svn/wiki/images/pap_eclipse_apt.png)

Now, the most important step. You have to configure a classpath for the processor (aka Factory path). In our example there are no more dependencies, just the library with the processor itself (do not forget another libraries if they are needed for running the processor. Otherwise it won't starts)

![http://acris.googlecode.com/svn/wiki/images/pap_eclipse_factory_path.png](http://acris.googlecode.com/svn/wiki/images/pap_eclipse_factory_path.png)

Done. After rebuilding the application, or saving the java class with the appropriate annotation, procesor runs and should log some NOTE messages. But where they are?

![http://acris.googlecode.com/svn/wiki/images/pap_eclipse_editing.png](http://acris.googlecode.com/svn/wiki/images/pap_eclipse_editing.png)

Show log error view from **Window -> Show view -> Error log** and here are located all output messages from the annotation processors.

![http://acris.googlecode.com/svn/wiki/images/pap_eclipse_output.png](http://acris.googlecode.com/svn/wiki/images/pap_eclipse_output.png)

We are done.

Happy coding :-)

## Eclipse compiler pitfalls ##

You probably know that eclipse has own compiler in order to reach inceremental build. This custom compilation process is perfectly fine but it has some drawbacks. They have to reach the same behavious as the java compiler, otherwise it's fail. An currently it is little bit fail, because there are some cases that are not working in eclipse but in standard java compiler does. For example, lets try to gain all classes from the specified package:
  * With the java compiler it is not a problem with following code:
```
Elements elements = processingEnv.getElementUtils();
TypeElement typeElement = Elements.getTypeElement(SomeClass.class.getCanonicalName());			
List<? extends Element> enclosedElements = Elements.getPackageOf(typeElement).getEnclosedElements();
```
It returns all classes in the package
  * but with the eclipse compiler it is a problem, it returns an empty list