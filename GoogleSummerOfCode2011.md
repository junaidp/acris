[GSoC 2011](http://www.google-melange.com/gsoc/program/home/google/gsoc2011)

Thank you for your interest in Acris project suite. These draft ideas define the future scope of the whole project suite. If any idea fits your area of interest, describe the proposal implementation, unfold the whole scope and provide your concept that leads to fulfilling the project scope.


---


# Communication data formats in the object world #

**Project:** Acris JSON, Acris XML

**Contact/Mentor:** Peter Simun (simun@seges.sk), Ladislav Gazo (gazo@seges.sk)

**Difficulty:** Easy

**Platform:** Java (1.5+), Google Web Toolkit

**Required skills:** JSON, XML

**Description and motivation:** While GWT is more and more popular, then more and more devices/platforms can be interconnected with GWT frontend. It is not always possible to use GWT "native" communication channel (RPC) in order to communicate with backend services and alternative data formats should be also supported - like XML and JSON. Because communication layer is transparent, it will be easy to support various data formats while the code remains the same.

**Current state:** Acris JSON project is able to convert JSON response into Java POJO, but the other way is not supported (from POJO to JSON). Also XML support is not currently implemented.


---


# Complete basic web BPM set #

**Project:** Acris BPM

**Contact/Mentor:** Ladislav Gazo (gazo@seges.sk), Peter Simun (simun@seges.sk)

**Difficulty:** Easy

**Platform:** Java (1.5+), Google Web Toolkit

**Required skills:** BPM solution, BPMN 2.0 basics

**Description and motivation:** In the current architectural design there is a strong pressure to create reusable and clearly separated implementation that can be maintained over the long period of time where customers, business analytics and developers can keep an eye on the solution and understand it. A lot of business applications use a BPM solution on the back-end side but there are situations where client side can benefit from abstracting the logic out of the front-end code.

**Current status:** Acris BPM solution contains not-yet complete migration of Activiti BPM engine into GWT. The solution contains implementation suitable for creating basic "wizard-like" process.

**Goal:**
Acris BPM solution on the client must fulfill following requirements:
  * keep its JavaScript size at the minimum
  * complete the support of BPMN 2.0 set available in Activiti
  * implement basic activity nodes and tools to build usable process: injecting components/beans into process, accessing GWT-RPC / JSON /... service with just a simple configuration, displaying form input


---


# Improve GWT widgets/components #

**Project:** Acris widgets

**Contact/Mentor:** Peter Simun (simun@seges.sk), Ladislav Gazo (gazo@seges.sk)

**Difficulty:** Medium

**Platform:** Java (1.5+), Google Web Toolkit (2.1+), HTML, CSS (2.1+, 3.x)

**Required skills:** algorithmization, object oriented development, punctual graphical feeling

**Description and motivation:**
The goal is to provide native support for extending GWT widgets in order to achieve new look & feel (L&F) of the core widgets. L&F should be defined in the gwt.xml configuration file and can be then changed without changing the Java code. We don't want to develop new full-featured widgets as it is done in GXT or SmartGWT framework. The scope of the project is to easily provide a new visual theme for standard GWT widgets so the theme can be used in the application built on top of the GWT framework.

**Current state:**
We did a research and prototype solution and we were able to switch among different themes. Solution had some performance issues and it wasn't perfect at all. We are sure we can reach much stable and effective solution with the GWT 2.x.

<table cellpadding='10'>
<tr>
<td align='center'><img src='http://acris.googlecode.com/svn/wiki/images/gsoc_widgets_login_pure.png' /></td>
<td align='center'><img src='http://acris.googlecode.com/svn/wiki/images/gsoc_widgets_login_helix.png' /></td>
<td align='center'><img src='http://acris.googlecode.com/svn/wiki/images/gsoc_widgets_login_metal.png' /></td>
</tr>
<tr>
<td align='center'><b>Core GWT panel - No styles</b></td>
<td align='center'><b>Helix theme</b></td>
<td align='center'><b>Metal theme</b></td>
</tr>
</table>

**Goal:**

Whilst the problem seems pretty straightforward, there are also additional steps needed to be done:
  * provide at least two different color themes in order to verify switching between them without touching the code,
  * prepare showcase that will present all GWT widgets properly styled using selected color theme,
  * make widgets easy to use for various applications, well documented and perfectly prepared.


---


# Generic user interface for security maintenance #

**Project:** Acris security

**Contact/Mentor:** Peter Simun (simun@seges.sk), Ladislav Gazo (gazo@seges.sk)

**Difficulty:** Medium

**Platform:** Java (1.5+), Google Web Toolkit

**Required skills:** Security background, JEE security projects (Spring Security, JAAS, CAS), secured session management, front-end design

**Description and motivation:** Many GWT applications use a kind of user or group maintenance. The solution is done again and again from the scratch because of missing security standards in the GWT framework. Because there is infinite variability in defining user interface presentation, it is also not possible to solve the problem once and for all. On the other hand it is possible to create easy-to-use components for developers where only specific logic would be implemented.

**Current status:** Acris-security project offers possibility to switch between various back-end implementations and current version is backed by Spring security (in JEE environment and also on Appengine). Focus is therefore on building the reusable UI screens.

**Goal:**
The goal is to provide:
  * reusable users/groups/roles maintenance front-end widgets built on top of Google Web Toolkit
  * with back-end implementation including logical and physical data models.

Because there is variety of back-end solutions that effectively work with the security (including ACLs), solution should not reinvent the wheel, but should:
  * reuse these solutions instead
  * offer plugability for different back-end implementations (Spring security, CAS, JAAS, ...)
  * while the front-end screen retain still the same.


---


# Reporting solution in the cloud #

**Project:** Acris reporting

**Contact/Mentor:** Ladislav Gazo (gazo@seges.sk), Peter Simun (simun@seges.sk)

**Difficulty:** Hard

**Platform:** Java (1.5+), Google Web Toolkit (2.1+), Google Appengine, Data persistence - ORM (JPA 2.0, Hibernate) and Twig persists, portable and enterprise architecture.

**Required skills:** Object oriented development, strong architectural skills (mentor support will be provided), distributed data storage, complex data processing and optimization.

**Description and motivation:** Cloud environment has great potential for massive data processing and currently there are some solutions offering reporting features but only few of them can be used in the cloud. Unfortunately, none of these solutions can be hosted on Appengine. Many production ready applications require useful reporting solution so why not to create one? The goal is to carefully design, plan and implement whole reporting solution that runs also on the cloud (Appengine) and also in the JEE (ORM based) environment.

**Current state:** Acris-reporting offers the user interface for managing and viewing the reports built on top of the GWT without dependency on the reporting solution itself (server side). Currently we are able to provide reports based on the Jasper Server/Jasper reports/iReport suite, but unfortunately there is no way to serve reports in the cloud environment like Appengine.

**Goal:**
There are more steps necessary to complete Acris reporting module to a sufficient level:
  * implement reporting solution runable on Appengine
  * support working with common reporting formats - at least JasperReports' JRXML
  * provide export functionality to different formats - HTML, Excel, PDF
  * integrate chart engine into reports
  * keep reporting service and management independent of the reporting solution
  * fine-tune common interface to reporting services from the client perspective


---


# Text to speech in the Rich internet applications (RIA) #

**Project:** Acris voices (with combination of Acris recorder & player)

**Contact/Mentor:** Peter Simun (simun@seges.sk), Ladislav Gazo (gazo@seges.sk)

**Difficulty:** Hard

**Platform:** Java (1.5+), Google Web Toolkit

**Required skills:** algorithmization, synthesis and voice manipulation, HTML 5

**Description and motivation:** Provide HTML 5 native support (and flash as a backup solution) for text-to-speech in the RIA application. The motivation is to record user operations, combine them with the audio "tracks" and reach perfect video manuals/tutorials. Whilst the audio track is generated from the text, that can be localized, application will be able to provide multiple language translations without recording them again and again.

**Current state:** Acris recorder is able to record all user actions which were executed on the page/application and store them in the database in compressed form (1 user event = 2 Bytes). We are also able to replay recorded events but the audio track is still missing.

**Goal:**
Steps may vary based on the internet research that has to be done. Purpose of the research is to identify all open source text-to-speech (TTS) solutions with primary focus on English language, but also additional languages will be required later. Once this is done, there can be various results:
  * no appropriate solution (with open source license) does exist and text-to-speech synthesis has to be implemented,
  * there is a sufficient TTS implementation and can be used as part of the overall solution.

The goal is to mix "video track" (replayed user events) and audio track in one coherent unit. When TTS is provided as third party solution, code should be strictly and clearly separated so TTS implementation can be easily replaced.