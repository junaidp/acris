# Code generation platform #

There was a dream - to generate skelet of the application from the model definition, implement custom or specific bussiness logic, sell the product for a big money and be rich :-). This dream has also the name - it's model driven architecture and there are plenty of the implementations somewhere in the space. This article is kind of evaluation of multiple different approaches reaching the same goal and respecting DRY principles.

First evaluated approach will be Annotation processing tools that comes with Java 1.5 and plugable annotation processing (JSR269) that comes with Java 1.6. These techniques are used to auto generate java classes based on specified annotations. The perfect example is well known framemework - hibernate. The hibernate team is still developing new and new annotation processors and they are building a new platform over the JPA and validation annotations - they are calling it a metamodel. Lets see the example:

If you will annotate your domain class with @javax.persistence.Entity annotation then Jpa metamodel generator/processor starts his hard work. He will generate all meta classes in order to reach typesafe query writing. This stuff is really amazing and with the IDE integration you will get a powerfull way of writing a code - new java classes will be generated right after you are creating your domain class.

```
@Entity
public class Order {
    @Id 
    @GeneratedValue
    Integer id;
    
    @ManyToOne 
    Customer customer;   

    @OneToMany 
    Set<Item> items;
    BigDecimal totalCost;    
    // standard setter/getter methods
}
```

The processor is executed based on the @javax.persistence.Entity annotation and will produce `Order_` class with definitions of the fields which allows you to write your queries without any hardcoded strings and also in a type safe way.

```
@StaticMetamodel(Order.class)
public class Order_ {
    public static volatile SingularAttribute<Order, Integer> id;
    public static volatile SingularAttribute<Order, Customer> customer;
    public static volatile SetAttribute<Order, Item> items;
    public static volatile SingularAttribute<Order, BigDecimal> totalCost;
}
```

Currently hibernate also generates validation metamodel based on validation annotation, but it is the same story. So what to do when you want to write your own processor?

## Writing own annotation (or reuse the existing) ##

```
package sk.seges.corpis.platform.annotation;
 
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
 
@Retention( RetentionPolicy.SOURCE )
@Target( ElementType.TYPE )
public @interface TransferObject {
 
}
```

Annotation name is TransferObject and:
  * it can be defined over the type (ElementType.TYPE) - class, interface or enumeration
  * annotation is visible only in source java file (RetentionPolicy.SOURCE) and will not be the part of the compiled class file (this means that annotation could not be accessed using java reflection API, but the annotation processor can see it)

## Basic annotation processor ##

```
package sk.seges.corpis.core.pap.transfer;
 
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import javax.tools.Diagnostic.Kind;
 
@SupportedAnnotationTypes("*")
@SupportedSourceVersion( SourceVersion.RELEASE_6 )
public class TransferObjectProcessor extends AbstractProcessor {
	
	@Override 
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      		this.processingEnv.getMessager().printMessage(Kind.NOTE, "Beer, beer, beer. I'm going for a beeeer. I like drinking beer, lovely, lovely beer. La la la");
        	return false;
    	}
}
```

Annotation @SupportedAnnotationTypes is describing which annotations are registered with the annotation processor. Then annotation processor is executed when class with this annotation is compiled by java/eclipse compiler. So, why we did not write there @sk.seges.corpis.platform.annotation.TransferObject? The answer is: because it is String and thus it is not refactoring aware and it is more error prone. Preferably you can rather override getSupportedAnnotationTypes method instead
(Why the Sun developers did not use Class<?> attribute instead of String one?)

```
@Override
public Set<String> getSupportedAnnotationTypes() {
	HashSet<String> hashSet = new HashSet<String>();
	hashSet.add(sk.seges.corpis.platform.annotation.TransferObject.class.getCanonicalName());
	return hashSet;
}
```

Basic rule is to implement javax.annotation.processing.Processor interface in order to have valid annotation processor. Or better, you can extends javax.annotation.processing.AbstractProcessor which has very basic implementation of initialiation method (btw. here are annotations like @SupportedAnnotationTypes transformed to their method equivalents). Or even better, you can extend from sk.seges.sesam.core.pap.AbstractConfigurableProcessor, which has really complex possibilities like:
  * loading the configuration properties dedicated to the processor (so developers can configure your annotation processors if you want),
  * better fillers implementation (with autoidentation - isn't it better when you can read generated code and it is idented correctly?)
  * much easier output creation process - this could be very helpful if you are new to JSR269 and want to create basic annotation processor without deep knowledge of the DeclaredTypes, TypeElements, ElementVisitors, etc.

Then process method implementation - this is the main method where your implementation should be located. But firstly you have to know something about invoking the processors, because the process method should be (and will be) invoked more than once on each processor. Why?
Because Java is clever :-)

![http://acris.googlecode.com/svn/wiki/images/pap_eclipse_output.png](http://acris.googlecode.com/svn/wiki/images/pap_eclipse_output.png)

(Processor is invoked twice)

  * first round is executed when compiler parses the sources. Then it collects annotations from the source files, determine the available processors and the executes the processors that are supporting the approriate annotation
  * then process method is executed (1st round). Here is very important what your process method returns (whather true or false). If the return value is true then no further attempt to execute another processor associated with this annotation is done. In this state the processors are claimed and no subsequent annotation processor is asked for processing the annotation. Otherwise, the false value allows other processors to be executed after your processing is finished - the unclaimed state.
  * if next file is generated using the processors, then 1st round is reexecuted on this newly generated files
  * and finally processors are invoked once again in order to cleanup the resources and complete the work (after that, generated files are compiled into the bytecode)

This is what you have to take care about in the process method. You can identify the rounding phase using the RoundEnvironment interface (or to be more precise, you can use roundEnv.processingOver() method).

Set of elements which comes as first parameter holds all the java classes which are annotated with supported annotation (note: if the supportedAnnotationType is asterix `"*"` then the input set is empty and you have to fetch annotated java classes in your own)

## How to run the processor? ##

If you want to use maven, see the [Maven integration](AnnotationProcessing_Maven.md) section.

If you want to use eclipse, see the [Eclipse integration](AnnotationProcessing_Eclipse.md) section.

## Processors possiblities ##

A processor can
  * Examine annotations at compile time
  * Explore the compile-time type system
  * Contribute error messages to compiler output
  * Generate new files

A processor can’t
  * Modify source input or compiler output in any way
  * Read method bodies or local classes

## API you should know ##

Firstly you have understand basics behind the java language API:
  * Type in the java programming language is represented by interface  javax.lang.model.type.TypeMirror and can be primitive type, declared type (class and interface type), array type, type variable, and the null type. Upon this concrete type specific implementation exists, for example PrimitiveType, DeclaredType, ArrayType, etc.
Types should by compared using TypeMirror.getKind().equals(TypeKind) - do not use instanceof methods for checking the concrete implementation
For types manipulation you can use util class javax.lang.model.util.Types, which can be used in order to determine whether one type is assignable to another type, objects unboxing, or boxing the primitive types, etc. Instance of this interface can be obtained by javax.annotation.processing.ProcessingEnvironment.getTypeUtils()

  * Piece of code is defined by javax.lang.model.element.Element and it must not necessarily be the class or interface. It can be a method for example (javax.lang.model.element.ExecutableElement). Analogous to the Type, elements should be compared using ElementKind and for manipulation is javax.lang.model.util.Elements interface used. Instance of this interface can be obtained by javax.annotation.processing.ProcessingEnvironment.getElementUtils()

  * More advanced selectors are implemented in javax.lang.model.util.ElementFilter and are used to select just the elements of interest.

```
import static javax.lang.model.util.ElementFilter.*;

List<ExecutableElement> fs = methodsIn(someClass.getEnclosedElements());
```

## Debugging possibilities ##

Althrought debugging annotation processors is not easy it is possible also directly in eclipse environment and also using maven. See details in:
  * [debugging annotation processors using eclipse](AnnotationProcessing_DebuggingEclipse.md)
  * [debugging annotation processors using maven](AnnotationProcessing_DebuggingMaven.md)

# Code generators #

There shouldn't be the question why to generate code that should be generated, but the question should be how to generate that code and the code should looks like.

Depending on our [portable architecture](PortableObjects.md) we created following annotation processors that should simplifies your life:

  * [Domain object declaration](http://code.google.com/p/acris/wiki/PortableObjects#Domain_object_declaration) [annotation processor](CodeGenerationPlatform_DomainObject.md)
  * [Transfer object declaration](http://code.google.com/p/acris/wiki/PortableObjects#Transfer_object_declaration) [annotation processor](CodeGenerationPlatform_TransferObject.md)
  * Data access object base interface [annotation processor](CodeGenerationPlatform_IDAO.md)
  * Data access object hibernate base implementation [annotation processor](CodeGenerationPlatform_HibernateDAO.md)
  * Data access object app engine base implementation CodeGenerationPlatform\_AppEngineDAO annotation processor]
  * Service base implementation [annotation processor](CodeGenerationPlatform_Service.md)
  * Spring configuration [annotation processor](CodeGenerationPlatform_SpringConfiguration.md)

## Processor pitfalls ##

  * [Be aware of processing rounds](CodeGenerationPlatform_Pitfall_Rounds.md) - it's very important to know what are the processing rounds and what is each round supposed to do. Most importatnt rule is "Do not generate files in the last round!". The last round is used to just for cleaning up the resources and for notification that processing is over.
  * [Be aware of eclipse runtime dependencies](CodeGenerationPlatform_Pitfall_EclipseDep.md) - it's very good practise to pack annotation processors also with eclipse plugin configuration in order to achieve processor debuging possibilites. This requires and eclipse runtime (org.eclipse.core:org.eclipse.core.runtime) dependency and now the troubles begins.
  * You have to restart eclipse in order to reload annotation processor changes (or if you know better solution, just let us know)
  * Invalid recognition of the enum constants - https://bugs.eclipse.org/bugs/show_bug.cgi?id=357494
    * **Workaround** - Do not compare kind of the enum field (for ElementKind.ENUM\_CONSTANT), but compare type of the enum field (ENUM constants should have the same type as the enum itself)
```
//Not so nice solution, but working one
if field.asType().equals(enumType.asType())) {

//Correct, but not working
//if (field.getKind().equals(ElementKind.ENUM_CONSTANT)) {
```