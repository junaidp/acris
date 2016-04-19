# Debugging annotation processor (eclipse based) - Why? #

When you are developing annotation processors more frequently it will be probably usefull to debug them directly in eclipse environment. There are at least 2 reasons why you should do that:
  * eclipse compiler has little bit different behaviour than java compiler, so sometimes you will get different results - and you probably wants to know why
  * whilst you are developing in eclipse, you probably want to stay in eclipse without switching to maven command line and refreshing the files (yes, this can be solved using m2clipse plugin, but you still have to refresh the sources)

# How? #

This tooks me almost 8 hours in order to get everything working correctly. Many times I though that I'm the only one who wants to debug annotation processors in the eclipse environment. I was searching almost everywhere - read many [tutorials](http://javadude.com/articles/annotations/index.html) (they are almost everyone about old APT which comes with Java 1.5 but I wanted to use new annotation processing API that comes with Java 1.5), many sample projects (I finally founded one [great material](http://www.eclipsecon.org/2007/index.php?page=sub/&id=3618) from BEA Systems Inc. guys: Walter Harley, Jess Garms, Gary Horen), many forums, [eclipse help pages](http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.jdt.doc.isv/reference/extension-points/org_eclipse_jdt_apt_core_annotationProcessorFactory.html) and more [eclipse pages](http://help.eclipse.org/helios/basic/tocView.jsp?toc=/org.eclipse.jdt.doc.isv/toc.xml), many [video materials](http://vimeo.com/8875862) and [etc](http://vimeo.com/8876702). After little experimenting with eclipse plugins I finally found the way how to debug them, but that was just the beginning.

# Step 1 - Create an eclipse plugin #

Yes! This is the only way (correct me if I'm wrong) how to debug annotation processors. This decission is made by design and the java + eclipse teams designed the processors to be located in a separated JAR than your code in order to gets them running and to be located in a separated eclipse environments in order to debug them (and it make sence).

Creating an eclipse plugin is really easy stuff. Press right mouse button on your project in the eclipse and choose Configure -> Convert to Plug-in Projects...

![http://acris.googlecode.com/svn/wiki/images/eclipse_plugin.png](http://acris.googlecode.com/svn/wiki/images/eclipse_plugin.png)

New file named MANIFEST.MF should occur in your META-INF directory. This is the manifest descriptor of your plugin. You will be able to modify overview information of your plug-in, plug-in dependencies, visible packages which can be used by other plugins and plugin extension point after double clicking on the MANIFEST.MF file.

## Plug-in overview ##

2 important things can be specified here:
  * plug-in name (it is good practice to put there a normal name - another than Untitled1, etc. :-) )
  * plug-in activator - how and when will be your plugin activated

![http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_overview.png](http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_overview.png)

This is the most basic plug-in activator, with no extra functionality. You have to place correct plug-in id (TODO need to determine why :-) )

```
package sk.seges.corpis.platform.annotation.plugin.activator;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "sk.seges.corpis.core.pap.transfer";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
```

## Plug-in dependencies ##

You are lucky if your annotation processor does not have any third pary dependencies. Then only two dependecies are:
  * org.eclipse.core.runtime - Eclipse core functionality
  * org.eclipse.jdt.apt.core - Annotation processing support

![http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_dependency.png](http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_dependency.png)

"The "OSGI way" seems to be to make all your plug-in dependencies available as OSGI bundles, either wrapping the original JAR or just inserting the MANIFEST.MF into the JAR.  All local modules would be there directly in your workspace, and 3rd party dependencies in the PDE target platform."

This complicates things quite a lot. You cannot specify plugin JAR dependencies just like in a maven style, but you have to create a plug-in JAR wrapper. I found very good and descriptive [article](http://www.vogella.de/articles/EclipseJarToPlugin/article.html) about creating Eclipse plugins from jars - Mr. Lars Vogel thank you :-)

## Plug-in packages visibility ##

Just choose the packages that are used by your annotation processor or should be visible to another plug-ins. What to say more? :-)

![http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_export.png](http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_export.png)

## Plug-in extension points ##

When I hade annotation processors running and I was able to debug them I was not change them at the runtime. This was because annotation processor was loaded from JAR file instead of from the plug-in.
This was the most tricky part. I found out that in order to achieve this I need to define an extension point. The [documentation](http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.jdt.doc.isv/reference/extension-points/index.html) says it clearly: there is only one extension point for APT interface and his is:
  * org.eclipse.jdt.apt.core.annotationProcessorFactory

After searching the web I found tons of examples how to define this extension point but only with the old APT (JSR-175) - here you have to define AnnotationProcessorFactory, which instantiates and initializes processor and ensures that it will be executed. I can not use AnnotationProcessorFactory (because it can work only with JSR-175 not with JSR-269) and the answer was hidden (for me :-) ) in the [eclipse documentation](http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.jdt.doc.isv/reference/extension-points/org_eclipse_jdt_apt_core_annotationProcessorFactory.html). org.eclipse.jdt.apt.core.annotationProcessorFactory extension point has 2 child nodes:

```
<!ELEMENT extension (factories? , java6processors?)>
<!ATTLIST extension
point CDATA #REQUIRED
id    CDATA #IMPLIED
name  CDATA #IMPLIED
>
```

And the java6processors attribute was the answer! So you have to specify and extension point in this way:

```
<?xml version="1.0" encoding="UTF-8"?>
<?eclipse version="3.4"?>
<plugin>
   <extension
         point="org.eclipse.jdt.apt.core.annotationProcessorFactory">
         <java6processors enableDefault="true">
         	<java6processor class="sk.seges.corpis.core.pap.transfer.TransferObjectProcessor"/>
         </java6processors>
   </extension>

</plugin>
```

![http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_extension.png](http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_extension.png)

This was all from the plugin definition point of view. If you are a hacker and don't want use the nice dialogs for plugin definition, you can define a MANIFEST.MF in your own. It should looks like this:

```
Manifest-Version: 1.0
Bundle-ManifestVersion: 2
Bundle-Name: Java 6 Annotation Processor Plug-in
Bundle-SymbolicName: sk.seges.corpis.core.pap.transfer;singleton:=true
Bundle-Version: 1.0.0
Bundle-Activator: sk.seges.corpis.platform.annotation.plugin.activator.Activator
Bundle-Vendor: Seges
Require-Bundle: org.eclipse.core.runtime,
 org.eclipse.jdt.apt.core,
 sk.seges.sesam.core.pap;bundle-version="1.0.0"
Bundle-ActivationPolicy: lazy
Export-Package: sk.seges.corpis.core.pap.transfer,
 sk.seges.corpis.platform.annotation
Bundle-RequiredExecutionEnvironment: JavaSE-1.6 
Import-Package: sk.seges.corpis.platform.annotation
```

# Step two - executing the plugin #

Now the easier part :-). Right click on the project a choose Debug As -> Eclipse Application. This ensures that annother instance of the eclipse will be started with your plug-in on there.

![http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_debug.png](http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_debug.png)

Check your configuration in your Project Properties -> Java Compiler -> Annotation Processing -> Factory path. Here should be your plugin listed and after pressing Advanced button on it, your annotation processor has to be visible in the container. If not, check your error log view. This setting ensures you that if you change your annotation processing (in first eclise environment), changes are applied without restarting the second environment, you can debug it and tune it up. Now it's up to you.

![http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_factory.png](http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_factory.png)

One more thing. All output messages (INFO, WARNING, ERROR, NOTE) will be visible only in Error Log view (Window -> Show View -> Error Log). So if you are developing annotation processor, be sure that you have always this view opened!

![http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_messages.png](http://acris.googlecode.com/svn/wiki/images/eclipse_plugin_messages.png)

Need more info? Watch the following video:

<a href='http://www.youtube.com/watch?feature=player_embedded&v=PjUaHkUsgzo' target='_blank'><img src='http://img.youtube.com/vi/PjUaHkUsgzo/0.jpg' width='425' height=344 /></a>

Additional links:
  1. [perfect summary of the JSR 269 by Angelika Langer - Training & Consulting](http://www.angelikalanger.com/Conferences/Slides/JavaAnnotationProcessing-JSpring-2008.pdf)
  1. [nice artice about annotations including video materials by Scott Stanchfield](http://javadude.com/articles/annotations/index.html)