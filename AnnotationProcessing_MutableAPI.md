# Purpose/goal #

Mutable API is used to:
  * represent types that is going to be generated (so does not exists on the classpath yet)
  * easier modification of the existing types
  * manipulate with the objects instead of string representations while generating classes
  * handle complex situations when more yet non-existing types should be interconnected

## "Mutable" API provided by JSR 269 by default ##

Although TypeMirrors and Elements are not mutable (no setter are available) you still can create types using methods from javax.lang.model.util.Types class:
  * ` WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound); ` allows you to create "custom" wildcard type like: ? extends Serializable
  * ` DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs); ` allows you to use declared type with specified type variablest, like Map<? extends Serializable, String>

## So, what's the problem? ##

Problem is when you reach the world outside of the simple processors and want to handle more complex situations:
  * create type variable with defined variable name, like ` <T extends Serializable> ` - you simply can't represent variable 'T' using the standard API
  * use type variables with the declared types like ` PagedResult<T extends List<User>> ` - you can't also use it in the types
  * work with classes that does not exists yet because they are going to be generated

## PAP types ##

![http://acris.googlecode.com/svn/wiki/images/pap_types_api.png](http://acris.googlecode.com/svn/wiki/images/pap_types_api.png)

## Mutable types ##

Green types from from standard PAP types are mutable.

![http://acris.googlecode.com/svn/wiki/images/mutable_api.png](http://acris.googlecode.com/svn/wiki/images/mutable_api.png)