package sk.seges.sesam.pap.test.selenium.processor;

import java.io.File;

import javax.annotation.processing.Processor;

import org.junit.Test;

import sk.seges.sesam.core.pap.AnnotationTest;
import sk.seges.sesam.core.pap.model.OutputClass;
import sk.seges.sesam.core.pap.model.api.NamedType;
import sk.seges.sesam.core.test.selenium.runner.MockRunner;
import sk.seges.sesam.core.test.selenium.usecase.MockSelenise;

public class SeleniumTestProcessorTest extends AnnotationTest {

	@Test
	public void testTestCase() {
		assertCompilationSuccessful(compileFiles(MockSelenise.class, MockRunner.class));
		assertOutput(getResourceFile(MockSelenise.class), getOutputFile(MockSelenise.class));
	}

	private File getOutputFile(Class<?> clazz) {
		OutputClass inputClass = new OutputClass(clazz.getPackage().getName(), clazz.getSimpleName());
		NamedType outputClass = SeleniumTestProcessor.getOutputClass(inputClass);
		return new File(OUTPUT_DIRECTORY, toPath(outputClass.getPackageName()) + "/" + outputClass.getSimpleName() + SOURCE_FILE_SUFFIX);
	}

	@Override
	protected Processor[] getProcessors() {
		return new Processor[] {
			new SeleniumTestProcessor()
		};
	}
}