# Introduction #

When you develop and maintain more then one site / application in production you likely want to be notified when it is unavailable. So we developed simple tool - **Asmant** - which checks sites using HTTP requests whether they are still alive.

In case they are not ... at least your mailbox will be filled with e-mails about it :)

### Related artifacts ###

```
<groupId>sk.seges.acris</groupId>
<artifactId>acris-asmant</artifactId>
```

# Configuration #

Asmant is a web application deployable to Google AppEngine. All you need to do is to build / download the WAR file and deploy.

There are several possibilities of what to configure:
  * http://localhost:8888/add-site.html - HTML form for specifying sites to regularly check
  * http://localhost:8888/add-recipient.html - HTML form for adding e-mail recipients of alarms
  * http://localhost:8888/asmant-rest/site/status - provides status of sites in JSON format

When deployed on GAE, it will register a cron job checking defined list of sites every 2 minutes.

Only users specified as developers (in GAE) will be able to access administration interfaces.

You can manage (especially view and delete) the configuration using GAE Data Explorer view.

# Project #

```
	<groupId>sk.seges.acris</groupId>
	<artifactId>acris-asmant</artifactId>
```