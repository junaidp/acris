package sk.seges.sesam.core.test.selenium.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SeleniumTestCase {
	
	Class<?>[] suiteRunner();
	
	Class<?> configuration();
	
	String description();
}