# Google Web Toolkit client profile #

you have to have use acris-os-gwt-parent as your parent project
```
<parent>
	<groupId>sk.seges.acris</groupId>
	<artifactId>acris-os-gwt-parent</artifactId>
</parent>
```

| **Profile** | **Activation** | **Purpose** |
|:------------|:---------------|:------------|
| **gwt-client** | file activation - **.gwt-client** | GWT configuration for maven and eclipse |
| **gwt-project** | file activatiob - **.gwt-project** | Maven and eclipse configuration for GWT projects that are not web applications |