# Introduction #

AcrIS is Maven-based project but that doesn't mean it is impossible to use Ant or other build tools... but following examples will use [Maven](http://maven.apache.org)

# Where is it available #

Stable releases are available in **Maven Central Repository**.

## AcrIS ##

```
	<parent>
		<groupId>sk.seges.acris</groupId>
		<artifactId>acris-os-gwt-parent</artifactId>
		<version>${acris-os.version}</version>
	</parent>
```


## Sesam ##

```
	<parent>
		<groupId>sk.seges.sesam</groupId>
		<artifactId>sesam-os-base-parent</artifactId>
		<version>${sesam-os.version}</version>
	</parent>
```

## CorpIS ##

```
		<groupId>sk.seges.corpis</groupId>
		<artifactId>a-corpis-project</artifactId>
		<version>${corpis-os.version}</version>
```


# Profiles #

We try to simplify the process of creating new projects so we started to heavily use Maven Profiles. When you use a profile most of the relevant configuration is already present in the profile and you don't have to maintain it on your own.

Pick a specific type of how to configure AcrIS project from the following list (or menu on the left):

  * [Google Web Toolkit profiles](ProfilesWebToolkit.md)
  * [Google AppEngine profiles](ProfilesAppEngine.md)
  * [SeSAM Annotation Processing profiles](ProfilesAnnotationProcessing.md)
  * [Customized quick-start](QuickStartCustomized.md)

Profiles are activated by placing `-P` argument to `mvn` command but to simplify the process again, you usually need to **place profile's dot-file** only to the root directory.

Chapters about profiles provide more detailed information.

## How-to use profiles ##

There are only 3 steps:

Create project with POM file inheriting from:

```
	<parent>
		<groupId>sk.seges.acris</groupId>
		<artifactId>acris-os-gwt-parent</artifactId>
		<version>${acris-os.version}</version>
	</parent>
```

Define which version of AcrIS you would like to use, e.g:

```
<properties>
	<acris-os.version>1.2.0</acris-os.version>
</properties>
```

Place desired dot-file (empty file with specific name - e.g. `.gwt` ) to the same directory where POM file is.

That's it!