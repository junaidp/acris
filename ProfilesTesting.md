you have to have use sesam-os-base-parent as your parent project
```
<parent>
	<groupId>sk.seges.sesam</groupId>
	<artifactId>sesam-os-base-parent</artifactId>
</parent>
```

# Testable profile #

| **Profile** | **Activation** | **Purpose** |
|:------------|:---------------|:------------|
| **testable** | file activation - **.testable** | Basic test libs - JUnit & JMockit |
| **jee-testable** | file activation - **.jee-testable** | Common test libs for J2EE environmet - MockEJB, MockRunner, GreenMail, spring-test,... |