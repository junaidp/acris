# Example #

```
Binding.alter(boundPanel).enable(panel.perilLevelBox).when(ForestBeanWrapper.TIGERS_IN_VICINITY)
				.isNotNull();
```

# Concept of operation #

![http://acris.googlecode.com/svn/wiki/images/conditional_binding_concept.png](http://acris.googlecode.com/svn/wiki/images/conditional_binding_concept.png)

# Possibilities #

There are possibilities to control:
  * visibility
  * enable

by calling methods on BindingActions object available from **alter** method. You can set up the condition against a field of a bean either by using predefined methods:
  * isNotNull
  * isNotEmpty
  * equalTo
  * ...

or by implementing custom condition applied through calling **matches** method.
