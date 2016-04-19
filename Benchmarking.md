# Introduction #

### Related artifacts ###

```
<groupId>sk.seges.acris</groupId>
<artifactId>acris-benchmark</artifactId>
```


# Benchmarking GWT.create #

Have you ever been interested how much time GWT.create calls consume time? We were also and therefore we create simple benchmark utility which you can find in **acris-benchmark** project.

This utility class will override default GWT bridge implementation and measure time of each and every call to GWT.create. The results are written to HTML table in your default temporary directory (on Unix it is /tmp). Each file has prefix //gwt-create-//. To collect statistical informations you can import the table to OpenOffice or Excel and e.g. filter calls based on the class - the only parameter to GWT.create. The table also contains information about stack trace from which point the call to GWT.create was made.

Running the utility is useful in development mode where you can influence the order of classpath entries easily. It is also possible to integrate it to your build process if you want but there is only one rule you have to fullfil:
  * the project (the dependency itself either in form of project or JAR or...) **must be placed before any GWT library** on the classpath