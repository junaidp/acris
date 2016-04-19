Acris security is well designed with respect on exnsibility and plugability so it is very easy to change data provider in the overall concept. Image below shows overall  architecture view over the acris security a 2 different implementations:
  * JPA implementation
  * Appengine implementation - using twig persists framework

Swithching the implementation of the domain model and DAO provider you can migrate from RDBMS system into distributed one without touching the acris security concept. Whole concept uses primarily interfaces so the implementation really does not change the overall concept.

![https://acris.googlecode.com/svn/wiki/images/overall-security.png](https://acris.googlecode.com/svn/wiki/images/overall-security.png)

//TODO add DAOs here