# Goals #

  * TDD
  * effective way of debugging annotation processors in eclipse and maven

# Background #
There is little bit magic behind the annotation processor tests :-) and we would thank to [Andrew Phillips](http://code.google.com/p/aphillips/) which helped us to achieve this goal. Andrew wrote great article about [testing the annotation processor](http://blog.xebia.com/2009/07/21/testing-annotation-processors/) and open sourced http://aphillips.googlecode.com/svn/commons-test-support/trunk/src/main/java/com/qrmedia/commons/test/annotation/processing/AbstractAnnotationProcessorTest.java AbstractAnnotationProcessorTest] that can be used in order to run the test.

The idea behind the test implementation is very simple (but powerful) - just run the javac task with specific processor over the defined classes. This ensures that annotation processor will be executed and additionaly if you are running your test in debug mode you will be able to debug also annotation processor. IMHO this is the most powerfull way of debugging the annotation processors on one side and also writing the tests on the other side :-)

Unfortunatelly we were not able to easily extend the AbstractAnnotationProcessorTest (we wanted to make a lot of changes in the abstract classes and also in private methods) so created own [sk.seges.sesam.core.pap.AnnotationTest](http://acris.googlecode.com/svn/sesam/branches/1.1.0/sesam-annotations-support/src/test/java/sk/seges/sesam/core/pap/AnnotationTest.java) which is inspired by Andrews test.

# Steps #

So steps are:
  1. **extend** sk.seges.sesam.core.pap.AnnotationTest in your test case.
  1. **define particular annotation processors** in getProcessors() method
```
@Override
protected Processor[] getProcessors() {
	return new Processor[] {
		new MetaModelProcessor()
	};
}
```
  1. **create a test** using @Test annotation. The test should have specified which clasees (or whole packages) will be compiled by java compiler and will be subjects to particular annotation processors
```
@Test
public void testMockEntityDao() {
	assertCompilationSuccessful(compileTestCase(MockEntity.class));
}
```

This will compile MockEntity class and will set MetaModelProcessor to a java compiler.

You can also specify output directory for the generated files by setting compiler option (CompilerOptions.GENERATED\_SOURCES\_DIRECTORY):

```
@Override
protected String[] getCompilerOptions() {
	return CompilerOptions.GENERATED_SOURCES_DIRECTORY.getOption("<DIRECTORY_IN_ABSOLUTE_PATH>");
}
```

## Example ##

More complex example also with testing the results:

```
package ...;

import java.io.File;
import javax.annotation.processing.Processor;
import org.junit.Test;
import sk.seges.sesam.core.pap.AnnotationTest;
import sk.seges.sesam.core.pap.model.InputClass.OutputClass;
import sk.seges.sesam.core.pap.model.api.NamedType;
import sk.seges.sesam.core.pap.structure.DefaultPackageValidatorProvider;
import sk.seges.sesam.shared.model.mock.MockEntity;

public class MetaModelProcessorTest extends AnnotationTest {

	@Test
	public void testMockEntityDao() {
		assertCompilationSuccessful(compileTestCase(MockEntity.class));
		assertOutput(getResourceFile(MockEntity.class), getOutputFile(MockEntity.class));
	}

	private String toPath(Package packageName) {
		return toPath(packageName.getName());
	}

	private String toPath(String packageName) { 
		return packageName.replace(".", "/");
	}

	private File getOutputFile(Class<?> clazz) {
		OutputClass inputClass = new OutputClass(clazz.getPackage().getName(), clazz.getSimpleName());
		NamedType outputClass = MetaModelProcessor.getOutputClass(inputClass, new DefaultPackageValidatorProvider());
		return new File(OUTPUT_DIRECTORY, toPath(outputClass.getPackageName()) + "/" + outputClass.getSimpleName() + SOURCE_FILE_SUFFIX);
	}

	private File getResourceFile(Class<?> clazz) {
		return new File(getClass().getResource("/" + toPath(clazz.getPackage()) + "/" + 
				clazz.getSimpleName() + ".output").getFile());
	}

	@Override
	protected Processor[] getProcessors() {
		return new Processor[] {
			new MetaModelProcessor()
		};
	}

	private static final String OUTPUT_DIRECTORY = "target/generated-test";
	
	protected File ensureOutputDirectory() {
		File file = new File(OUTPUT_DIRECTORY);
		if (!file.exists()) {
			file.mkdirs();
		}
		
		return file;
	}
	
	@Override
	protected String[] getCompilerOptions() {
		return CompilerOptions.GENERATED_SOURCES_DIRECTORY.getOption(ensureOutputDirectory().getAbsolutePath());
	}
}

```

## Multiple compilers support ##

### Java compiler ###

Running test against javac:
```
...
@Test
public void testLocalService() {
	assertCompilationSuccessful(compileFiles(MockRemoteService.class));
	assertOutput(getResourceFile(MockRemoteService.class), getOutputFile(MockRemoteService.class));
}
```

or

```
...
@Test
public void testLocalService() {
	assertCompilationSuccessful(compileFiles(Compiler.JAVAC, MockRemoteService.class));
	assertOutput(getResourceFile(MockRemoteService.class), getOutputFile(MockRemoteService.class));
}
```

**Remark**: Compiler.JAVAC argument in the compileFiles method

### Eclipse compiler ###

```
...
@Test
public void testLocalServiceInEclipse() {
	assertCompilationSuccessful(compileFiles(Compiler.ECLIPSE, MockRemoteService.class));
	assertOutput(getResourceFile(MockRemoteService.class), getOutputFile(MockRemoteService.class));
}
```

**Remark**: Compiler.ECLIPSE argument in the compileFiles method