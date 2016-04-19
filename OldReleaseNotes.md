# Release 1.2.2 #

## Features ##
Sesam:
  * DTO processors improvements

Corpis:
  * New generic domain classes introduced - price conditions
  * VUB payment

Acris:
  * Security and ACL fixes
  * OpenID integration with Google Apps introduced
  * Image reader with support for RGB, BGR, CMYK and many other image formats
  * Tomcat OpenMQ startup listener

# Release 1.2.1 #

## Features ##
  * Annotation processors for generating DTOs from domain entities
    * Offering automated and explicit DTO configuration
    * Used for generating converters for transforming domain entity to DTO and vice versa
    * Support for hibernate and transaction propagation
    * Added support for scaffold model definitions
    * Introduced new way of extending generated classes and override default behavior
  * Annotation processors for generating DAO and base classes from scaffold model definition
  * Selenium suport stabilization and more powerful HTML report


# Release notes 1.2.0 #

## Features ##
  * introduced [profiles](ProjectQuickStart.md) to allow you to quickly start with Processors, GWT client, AppEngine project development
  * writing Java Annotation Processors from now on will be a piece of cake with AcrIS
    * we implemented [Mutable API](AnnotationProcessing_MutableAPI.md) to be able to easily transform types and write annotation processors in clear way
    * delegated configuration might help you when you don't want to polute existing (or even new) objects with processor-specific annotations
    * there are several prepared annotation processors:
      * for generating portable domain interfaces
      * [generating async](CodeGenerationPlatform_Async.md) version of the remote service
      * [generating DTOs](CodeGenerationPlatform_DTO.md) and convertion layers between server and client layer
      * first sketch of appconstructor - scaffolding platform using annotations
      * GWT service layer processors
  * [JSON data migration](JSONDataMigration.md) helps you to transform your data between versions
  * your sites and applications in production environment should be constantly monitored with [Asmant monitoring tool](AsmantMonitoring.md) deployable to AppEngine
  * two new widgets to support dynamic layouts:
    * DynamicUiBinder alternate to UiBinder and [Micro template](MicroTemplate.md) panel for complete dynamics of data representation
  * testing became integral part of AcrIS framework so we developed [supporting layer for Selenium](SeleniumSupport.md)

## Announcements ##

  * current development was performed on the branch 1.1.x and for future versions will be moved back to the trunk. Current trunk will be moved to 1.0.x branch.
  * beans binding part of AcrIS is discontinued and will be maintained for the purpose of old projects only - we recommend to use GWT's Editor framework.


# Release notes 1.1.2 #

## Features ##
  * Added shadow theme + new components into the widgets projects (ComboBox, CheckBox based on images
  * New showcases available
  * Added test project for testing cardpay/tatrapay payment methods
  * New APIs/core for annotation processors
  * Added complex selenium/webDriver support with configuration possibilities (with many fixes for doubleclick, etc.)

## Improvements ##
  * 3rd party libraries upgrade - we are now using
    * GWT 2.3.0
    * gwt-log 3.1.2
    * gwt-query 1.0.0
    * Gilead 1.3.3 for GWT 2.3.0
  * More profiles added for easier plugin/depedency configuration(.gwt for GWT based projects, .pap profiles for plugable annotation processor based projects)

# Release notes 1.1.1 #

## Features ##
  * Added annotation processing support - part of the sesam & corpis suite. Read more in CodeGenerationPlatform
  * Added more and more showcases
  * Portable architecture introduced and code is continuosuly migrated

## Improvements ##
  * Offline content generator improved, code cleanup and polishing

# Release notes 1.1.0 #

## Features ##
  * Reporting module integrated with JasperServer
  * Offline generator capable of creating a version of the web searchable by any web crawler

## Improvements ##
  * 3rd party libraries upgrade - Spring 3.0.4.Release, Hibernate 3.6.0-Final, GWT 2.0.4, Gilead 1.3.2
  * from now on AcrIS is using GWT 2.1
  * first integration with OpenID in our acris-security module
  * and of course lot of bugfixes against production code

# Release notes 1.0.1 #

## Features ##
  * security generalized - it is possible to have AcrIS security running on top of Hibernate, Spring, Gilead, AppEngine,... and all dependencies are clearly separated
  * bean wrapper reworked. It is not necessary to register beans into introspector and bean wrappers should extends any superclass. This allows you to bind beanwrappers with any UI library like smartGWT or ExtGWT without modifying your domain model (this is also related to a BeanWrapperAdapterProvider)
  * registering all adapter providers in a one step
  * type safe beans binding using annotations (also nested binding is supported in a completly typesafe way)
  * moved Maven repository to Sonatype

## Bug fixes ##
  * Fixes in beans binding (more stable code)

## Improvements ##
  * Improved list box binding (selection in list change handled properly)
  * Gilead dependency from UserService removed