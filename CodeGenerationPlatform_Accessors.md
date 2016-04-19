# Introduction #

AnnotationAccessors represents the way of encapsulation and reusing the logic related to the specific annotation and should be the correct way of accessing values of the annotations.

It's extremly usefull in the situations when you have complex annotations, like:
```
@Copy(annotations = { 
	@Annotations(accessor = PropertyAccessor.PROPERTY, packageOf = Size.class),
	@Annotations(accessor = PropertyAccessor.FIELD, typeOf = { Size.class, NotNull.class })})
```

When the annotation processing logic isn't held in one java class (called AnnotationAccessor) it can be maintained only with the pain in your ass (anyway this is very good practice not only in the PAP world but in the whole software development world and it's called DRY principle, so no new information so far :-) ).

Based on this annotation you should create a CopyAccessor that will provide all necessary functionality you require, like:
```
public class CopyAccessor extends AnnotationAccessor {

	/**
	* Element that holds the @Copy annotation definition
	*/
	public CopyAccessor(Element element, MutableProcessingEnvironment processingEnv) {
		//...
	}

	public List<AnnotationMirror> getSupportedAnnotations(Element element) { 
		//parses annotations and returns supported annotations that are defined on 
		//element - ExecutableElement (method) or TypeElement (class, interface, ...)
	}
}
```

or

```
public class CopyAccessor extends AnnotationAccessor {

	public boolean isSupportedAnnotation(AnnotationMirror annotation, ElementKind kind) { 
		//...
	}
	public boolean isSupportedAnnotation(Class<? extends Annotation> annotation, ElementKind kind) { 
		//...
	}
}
```

and now it's very easy to use:
```
Type element = ...;
new CopyAccessor(element).isSupportedAnnotation(Size.class, PropertyAccessor.FIELD);
```

I can only recommend it to you and it's also very usefull for a simple annotation because you never know how complex it'll be after couple of months or one year. This happends to me. We had a very simple annotation so it wasn't reasonable to create annotation accessor for working with the annotation. After the time new parameters were introduced to the annotation and now the logic is comminuted into 12 different locations (in the various source files) and it's almost impossible to change anything in there.

# The problem and the salvation #

To motivate you to create annotation accessors we've created an abstract AnnotationAccessor, that also solves few issues related to annotations, like:
  * if you access the annotation
```
@Annotations(accessor = PropertyAccessor.FIELD, typeOf = { Size.class, NotNull.class })
```
> > in a standard way
```
Annotations annotations = element.getAnnotation(Annotations.class);
```
> > you'll see the bitter reality:
```
Class<?>[] typesOf = annotations.typeOf(); //returns array with only 1 item!! instead of two that were defined in the annoation
```
> > This is pretty serious bug if you want to pass array of the classes (**solution?** use **AnnotationMirror**)

  * if you access the annotation in a standard way, you can't access class based values directly:
```
@Annotations(accessor = PropertyAccessor.FIELD, packageOf = Size.class)
...
Annotations annotations = element.getAnnotation(Annotations.class);
annotations.packageOf(); //throws MirroredTypeException
```
> > this throws MirroredTypeException and you have to catch this exception and work with TypeMirror later in your code (this is something that AnnotationClassPropertyHarvester can handle, but it is still not very comfortable)

  * if you access the annotation in a "alternative" way using annotation mirrors
```
List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
for (AnnotationMirror annotationMirror: annotationMirrors) {
	if (annotationMirror.getAnnotationType().toString().equals(Annotations.class.getName())) {
		for (Entry<? extends ExecutableElement, ? extends AnnotationValue> annotationValue: annotationMirror.getElementValues().entrySet()) {
			if (annotationValue.getKey().getSimpleName().toString().equals("typeOf")) {
				Object classes = annotationValue.getValue().getValue();
			}
		}
	}
}
```
> > you only can:
    * access annotation values in very "dramatic" and complex way (no helper methods are offered by default in the PAP and the easy operation has to be done in a several lines of code - instead of one or two)
    * use String representatives for the value names, like ` ...equals("typeOf")... ` and that can lead to the serious problems after annotations refactoring or modification.

So, what about the solution that can offer advantages from both principles and can still solve drawbacks of both ways? Maybe you already know that the solution has name **AnnotationAccessor** :-).

## Annotation accessor ##

Annotation accessor creates a proxy (or cglib enhancer to be more precise) over the AnnotationMirror and pretends like annotation instance. So, if you access annotation using:
```
Copy copyAnnotation = getAnnotation(element, Copy.class);
```

the result is Copy annotation with the AnnotationMirror behind that.

This approach solves all drawbacks listed above:
  * you can access class references without any problems:
```
Class<?> packageOf = copyAnnotation.annotations[0].packageOf();
```
  * and you don't have to catch strange TypeMirrorExceptions and
  * you can also work with the class type array
```
Class<?>[] typesOf = copyAnnotation.annotations[0].typeOf();
```

## Steps ##

  * If you have your own annotation, for example @MetaModel, create an AnnotationAccessor for that (**!Please, do not create "fluent" implementation on top of that!**):
```
import javax.lang.model.element.Element;

import sk.seges.sesam.core.pap.accessor.AnnotationAccessor;
import sk.seges.sesam.model.metadata.annotation.MetaModel;

public class MetaModelAccessor extends AnnotationAccessor {

	private final MetaModel metaModelAnnotation;
	
	public MetaModelAccessor(Element element) {
		this.metaModelAnnotation = this.getAnnotation(element, MetaModel.class);
	}
	
	@Override
	public boolean isValid() {
		return metaModelAnnotation != null;
	}
}
```
  * add necessary methods you want to be used outside of the class
  * make sure, that MetaModel annotation isn't accessed in any other class (all other classes should use your accessor only) - this is the only place where should be processing located
```
TypeElement metaModelHolder = ...;
MetaModelAccessor accessor = new MetaModelAccessor(metaModelHolder);
accessor.get...();
```


Happy coding :-)