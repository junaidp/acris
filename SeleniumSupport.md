In the following few lines we would like to introduce you to a maintainable and easy Selenium test writing. We will start with setting up a brand new project and finishing with a nice test case.

# Creating new project with selenium support #

**Ensure** that you use sesam-os-base-parent as your parent project (note: or another project that has sesam-os-base-parent as the parent project)
```
<project>
	...
	<parent>
		<groupId>sk.seges.sesam</groupId>
		<artifactId>sesam-os-base-parent</artifactId>
		<version>1.2.1-SNAPSHOT</version>
		<relativePath/>
	</parent>
</project>
```

**Create** following files in the root directory (where is your pom.xml located):
  * .pap - this enables maven profile responsible for annotation processor (JSR269) plugins and dependencies, configure required plugins and dependencies
  * .selenium - this provides all dependencies to the selenium and configures reporting plugins

Store test source code in:
  * `src/test/java`

But create also empty:
  * `src/main/java`


# Eclipse integration #

You can run annotation processors also in the eclipse, eclipse:eclipse command prepares all required settings for the eclipse. You can check the settings in the project properties (alt + enter).

![http://acris.googlecode.com/svn/wiki/images/pap_eclipse_integration.png](http://acris.googlecode.com/svn/wiki/images/pap_eclipse_integration.png)

Annotation processor is executed immediately after the java source is saved and generated files are located in the target/generated directory (this should be part of the build path)

# Run Forest #

The very first step of new test project is to create basic triplet of objects:
  * configuration
  * suite
  * test case

```
@Selenium(testURL = "http://synapso.sk", testURI = "/", browser = Browsers.FIREFOX)
public interface DevelConfiguration {}
```

```
@SeleniumSuite
public interface DevelSuite {}
```

```
@SeleniumTestCase(configuration = DevelConfiguration.class, suiteRunner = DevelSuite.class, description = "Basic runner")
public class SiteTest extends AbstractSeleniumTest {
}
```

After you create SiteTest and annotate it, processor in the back will generate configuration class. You have to implement required abstract method and use the class inside:

```
@SeleniumTestCase(configuration = DevelConfiguration.class, suiteRunner = DevelSuite.class, description = "Basic runner")
public class SiteTest extends AbstractSeleniumTest {

	@Override
	protected CoreSeleniumSettingsProvider getSettings() {
		return new SiteTestConfiguration();
	}
}
```

Running such test will lead to an error because there are no test methods yet. However there is one small difference between standard JUnit test and Selenium one:

you must use **@SeleniumTest** annotation instead of **@Test**:

```
	@SeleniumTest(description = "Javascript hides message fields if applied correctly", issue = @Issue(tracker = IssueTracker.MANTIS, value = "42"))
	public void testJavascriptFormDisplayedCorrectly() throws Exception {
	}
```

You might notice that the test method has the possibility to define to which issue is it related. Details of what you can do with it will be described in later chapters.

# Writing a test #

Whenever you write a Selenium test case it is good to inherit from **AbstractSeleniumTest** class so you have all the necessary objects at the hand.

Imagine there is a webpage with a simple web form. The form performs input validation so a user is immediately informed about incorrect input. A status message is involved in the process. The message should be hidden prior to form submition. And this is what we want to test in the beginning...

With AcrIS' Selenium support we decided to design a way to write as maintainable tests as possible. However the process is still open and also here are several/different approaches. We will start with the most "java-ish" one.

## #1 - define the page ##

From the following example it should be clear to you what you can with fluent API in WebDriver (Selenium 2.0) and your bit of fluentness in the class do:

```
public class RegistrationPage extends AbstractWebDriverPage {
	public RegistrationPage(WebDriver webDriver) {
		super(webDriver);
	}

	public RegistrationForm registrationForm() {
		return new RegistrationForm(webDriver);
	}

	public class RegistrationForm extends AbstractWebDriverPage {		
		public RegistrationForm(WebDriver webDriver) {
			super(webDriver);
		}
		
		public WebElement thisElement() {
			return webDriver.findElement(ById.id("registration-form"));
		}
		
		public WebElement errorMessage() {
			return thisElement().findElement(ByClassName.className("error-message"));
		}
		
		public WebElement webIdField() {
			return thisElement().findElement(ByName.name("webId"));
		}
	}
}
```

Just to summarize what happened there:
  * we've created a class representing the page
  * we've defined a class representing the form within the page
  * we've provided public methods accessing specific web elements
  * we've used Java methods of the WebDriver to access those elements (but there are other ways as well e.g. XPath selectors)

## #2 - assert it ##

And the following simple block closes your first AcrIS Selenium test case:

```
RegistrationPage page = new RegistrationPage(webDriver);
		
assertEquals("none", page.registrationForm().errorMessage().getCssValue("display"));
assertNotEquals("none", page.registrationForm().webIdField().getCssValue("display"));
```

# Conclusion #

We have shown you how you can easily create a test for your web application in 3 easy steps. In the following chapters we will describe other (more complex) possibilities to write maintainable tests and to provide comfortable reporting facilities.

So stay tuned!

# Maven APT Plugin #

While implementing the Selenium support we come across various issues. One result of it is fixed Maven APT plugin. It means if you want to not encounter problems we did, extend from **sesam-os-base-parent** where correct version (residing in our repository) is defined.

# Resolved issues #

  * ~~APT maven plugin creates factory path entries with trailing whitespace~~ - this is problem only on linux environment (see reported issue) - http://jira.codehaus.org/browse/MOJO-1609 - **SOLVED** with 1.0-alpha-5-SNAPSHOT version of the apt-maven-plugin
  * ~~Chicken-and-egg problem - if your project has compilation errors there is no easy way how to import project into the eclipse (because processors execution is part of the eclipse:eclipse goal and if it fails no eclipse project file is generated)~~ - **SOLVED** processors are no more bound to the generate-source (generate-test-source) phase therefore you can create eclipse project and run processors separatelly