# Introduction #

To generate new annotation processor, just execute the following (replace correct SeSAM version):

```
mvn archetype:generate -DarchetypeGroupId=sk.seges.sesam -DarchetypeArtifactId=sesam-annotation-archetype -DarchetypeVersion=1.1.4-SNAPSHOT
```


# Result #

Every generated processor consists of 3 Maven projects:
  * parent
  * API - contains annotations used by processor and other public interfaces. Projects using your newly created processor should have dependency on the API project only!
  * processor - contains implementation of the processor itself and helper classes. Processor should not be declared as compile time dependency in projects using it.

```
<dependency>
      <groupId>sk.seges.sesam</groupId>
      <artifactId>sesam-model-metadata-api</artifactId>
</dependency>
<dependency>
      <groupId>sk.seges.sesam</groupId>
      <artifactId>sesam-model-metadata-processor</artifactId>
      <scope>provided</scope>
</dependency>
```

You might notice there are two strange dependencies: sesam-annotation-archetype-api-base and sesam-annotation-archetype-processor-base. Both of them contain core dependencies required to easily write PAP. You do not have to bother about providing transitive dependencies for processors, all new features developed in scope of annotation processing will be available to you without any hastle.