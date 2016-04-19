It tooks me quite a long time to fully understand the compiler API and annotation processing API. After tons of coffe and many pernoctations I was well prepared and I thought that I can write a good annotation processor that will be reusable and reduces writing of repeatable code. But I was wrong, I missed one very important thing. In time of writing my first annotation processor I did not realize that generating code in the last processor round (when `roundEnv.processingOver() == true`) is very bad practise. So, how did my first annotation processor looks like?

```
package sk.seges.sesam.core.pap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import sk.seges.sesam.core.annotation.BadHierarchy;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class BadAnnotationProcessor extends AbstractProcessor {

	private static final String DEFAULT_SUFFIX  = "Generated";
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotationTypes = new HashSet<String>();
		annotationTypes.add(BadHierarchy.class.getName());
		return annotationTypes;
	}
	
	private String getGeneratedFileSuffix() {
		return DEFAULT_SUFFIX;
	}
	
	private List<Element> processingElements = new ArrayList<Element>();
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			for (Element annotatedElement: processingElements) {
				OutputStream fileStream = null;
				
				String elementPackage = processingEnv.getElementUtils().getPackageOf(annotatedElement).
					getQualifiedName().toString();
				
				try {
					JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(elementPackage + "." + 
							annotatedElement.getSimpleName().toString() + getGeneratedFileSuffix(), annotatedElement);
					fileStream = sourceFile.openOutputStream();
				} catch (IOException e) {
					processingEnv.getMessager().printMessage(Kind.ERROR, "Unable to generate " + 
						"target class for elmenet [reason: " + e.toString() + "]",
							annotatedElement);
				}
				PrintWriter pw = new PrintWriter(fileStream);
				pw.println("package " + elementPackage + ";");
				pw.println();
				pw.println("public class " + annotatedElement.getSimpleName().toString() + getGeneratedFileSuffix() + "{");
				pw.println("}");
				pw.flush();
				pw.close();
			}
			processingEnv.getMessager().printMessage(Kind.NOTE, "Processing finished");
		} else {
			for (String annotationType: getSupportedAnnotationTypes()) {
				Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(
					processingEnv.getElementUtils().getTypeElement(annotationType));
				for (Element annotatedElement: annotatedElements) { 
					processingElements.add(annotatedElement);
				}
			}
		}
		return false;
	}
}
```

Notice, that the generating of the new java classes is done in roundEnv.processingOver() equals true condition ... And, what's wrong with this approach?
  * Firstly, the last processing round is not dedicated to process of generation phase. It's used to make a final resource cleanup and nothing more.
  * Thanks to the maven fundamentals, there are no problems with the maven integration - At first the maven plugin is executed and generates all java classes using annotation processors, then compiler plugin is executed and all available classes are compiled. This strict process separation ensures that it does not matter in which processor round you are producing a new source classes.
  * But there is problem with eclipse integration. Eclipse compiler will simply do not compile java files that are produced in the last round even if they exists in the eclipse build path.

I found at least 2 more people that had problem as I had, but they did not find the solution (only one very time consuming work-arround, but anyway I was happy that I'm not alone :-) ):
  * http://stackoverflow.com/questions/3580564/eclipse-3-5-annotation-processor-generated-classes-cannot-be-imported
  * https://forum.hibernate.org/viewtopic.php?f=1&t=1004784

But after a couple of hours I finally found the root of all evil. I rewrote the annotation processor and it starts perfectly working also with eclipse and maven together. So, what I've changed in the implementation?

```
package sk.seges.sesam.core.pap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import sk.seges.sesam.core.annotation.Hierarchy;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SimpleAnnotationProcessor extends AbstractProcessor {

	private static final String DEFAULT_SUFFIX  = "Generated";
	
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotationTypes = new HashSet<String>();
		annotationTypes.add(Hierarchy.class.getName());
		return annotationTypes;
	}
	
	private String getGeneratedFileSuffix() {
		return DEFAULT_SUFFIX;
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			for (String annotationType: getSupportedAnnotationTypes()) {
				Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(
					processingEnv.getElementUtils().getTypeElement(annotationType));
				for (Element annotatedElement: annotatedElements) { 
					OutputStream fileStream = null;
					
					String elementPackage = processingEnv.getElementUtils().getPackageOf(annotatedElement).
						getQualifiedName().toString();
					
					try {
						JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(elementPackage + "." +
							annotatedElement.getSimpleName().toString() + getGeneratedFileSuffix(), annotatedElement);
						fileStream = sourceFile.openOutputStream();
					} catch (IOException e) {
						processingEnv.getMessager().printMessage(Kind.ERROR, "Unable to generate" + 
							" target class for elmenet [reason: " + e.toString() + "]",
								annotatedElement);
					}
					PrintWriter pw = new PrintWriter(fileStream);
					pw.println("package " + elementPackage + ";");
					pw.println();
					pw.println("public class " + annotatedElement.getSimpleName().toString() + getGeneratedFileSuffix() + "{");
					pw.println("}");
					pw.flush();
					pw.close();
				}
			}
		} else {
			processingEnv.getMessager().printMessage(Kind.NOTE, "Processing finished");
		}
		return false;
	}
}
```

Hopefully this saves you a lot of time when you make a stupid mistake as I did.