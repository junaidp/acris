# SeSAM Annotation Processing profile #

you have to have use sesam-os-base-parent as your parent project
```
<parent>
	<groupId>sk.seges.sesam</groupId>
	<artifactId>sesam-os-base-parent</artifactId>
</parent>
```

| **Profile** | **Activation** | **Purpose** |
|:------------|:---------------|:------------|
| **pap**     | file activation - **.pap** | annotation processor configuration for maven and eclipse |
| **selenium** | file activation - **.selenium** | selenium based project - with all selenium dependencies |
| **generate-site** | property activation - **generateSite=true** | generates documentation before release |
| **server**  | file activation - **.server** | spring based server side project configuration |
| selenium-api | file activation - .selenium-api | **Deprecated** |