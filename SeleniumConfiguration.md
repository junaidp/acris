# Why annotation processors ? #

Because they are sexy and they rocks :-) ... no, seriously, because of the configuration that can be achieved in very simple way (annotation processors aren't the only way how to achieve this result, but annotation really fits to this situation).
  * Default configurations for the test cases are held in one file - it's called SuiteRunner and it looks in the following way:
```
@SeleniumSuite
@Selenium (
		testURL = "http://localhost/",
		testURI = "web/test",
		browser = Browsers.FIREFOX
)
@Mail (
		host = "smtp.googlemail.com",
		mail = "test@example.com",
		password = "password",
		provider = Provider.IMAPS
)
@Credentials(username = "admin", password = "password")
@Report(screenshot = @Support(enabled = true))
public class SuiteRunner {}
```

  * When you create test case (for example LoginTestCase) and defines @SeleniumTest annotation (with specific SuiteRunner), all configurations are automatically inherited from the SuiteRunner - this allows you to define global settings in one single place.
```
@SeleniumTest(suiteRunner = SuiteRunner.class)
public class LoginTestCase extends AbstractSynapsoTest {
	
	@Override
	protected SuiteRunnerSettingsProvider getSettings() {
		return new LoginTestCaseConfiguration();
	}	
	
	@Test
	public void testLogin() {
             //...test body
        }
}
```

  * If you want to override global settings only in hte specific test, you can do it easily - using annotations
```
@Credentials(username = "new_user", password = "other_password")
@SeleniumTest(suiteRunner = SuiteRunner.class)
public class LoginTestCase extends AbstractSeleniumTest {
	
	@Override
	protected SuiteRunnerSettingsProvider getSettings() {
		return new LoginTestCaseConfiguration();
	}	
	
	@Test
	public void testLogin() {
             //...test body
        }
}
```
  * In that case, all parameters will be inherited from suite runner except the credentials.
  * If you want to redefine configurations without touching the code, nothing is easier - just use the java variable, like:
```
mvn test -Dtest.username=production_user -Dtest.password=secred_password
```
  * Now, parameters from the command line will be used
  * This allows you to handle situations like:
    * run tests on different platforms just using different parameters (still without touching the code), like executing tests on firefox ` mvn test -Dtest.testBrowser=firefox ` or executing tests on IE ` mvn test -Dtest.testBrowser=iexplore `
    * run tests on the test environment with test user and also run non invazive tests on the production environemnt with production user just by changing users from command line

## Default configurations ##

### @Selenium ###

| **Name** | **Description** | **Command line parameter** | **Default value** | **Required** |
|:---------|:----------------|:---------------------------|:------------------|:-------------|
| seleniumServer | Defines host name where the selenium server is located | test.seleniumHost          | localhost         | no           |
| seleniumPort | Defines port name where the selenium server is located | test.seleniumPort          | 4444              | no           |
| seleniumRemote | Bindings connect to the remote server instance | test.testRemote            | false             | no           |
| bromineServer | Defines host name where the bromine server is located | test.bromineHost           | localhost         | no           |
| brominePort | Defines port name where the bromine server is located | test.brominePort           | 8080              | no           |
| bromine  | Enables/disables support for bromine | test.bromineEnabled        | false             | no           |
| testURL  | Defines root URL of the testing site | test.testHost              | _NON DEFINED_     | no           |
| testURI  | Defines relative URI of the testing home page | test.testUri               | _NON DEFINED_     | no           |
| browser  | Defines browser the tests are executed with | test.testBrowser           | FIREFOX           | no           |

### @Credentials ###

| **Name** | **Description** | **Command line parameter** | **Default value** | **Required** |
|:---------|:----------------|:---------------------------|:------------------|:-------------|
| username | Username used to login to the system | test.username              | _NON DEFINED_     | yes          |
| password | Password used to login to the system | test.password              | _NON DEFINED_     | yes          |

### @Mail ###

| **Name** | **Description** | **Command line parameter** | **Default value** | **Required** |
|:---------|:----------------|:---------------------------|:------------------|:-------------|
| mail     | E-mail recepient settings | mail.address               | _NON DEFINED_     | yes          |
| password | E-mail password settings | mail.password              | _NON DEFINED_     | yes          |
| provider | E-mail provider | mail.provider              | IMAP              | no           |

### @Report ###

| **Name** | **Description** | **Command line parameter** | **Default value** | **Required** |
|:---------|:----------------|:---------------------------|:------------------|:-------------|
| screenshot.enabled | Screenshots enabled/disabled | report.screenshot.enabled  | false             | no           |
| screenshot.directory | Output directory for the screenshots | report.screenshot.directory | _NON DEFINED_     | no           |
| html.support.enabled | HTML report enabled/disabled | report.html.enabled        | false             | no           |
| html.support.directory | Output directory for the HTML reports | report.html.directory      | _NON DEFINED_     | no           |
| html.support.templatePath | Defines path to the used template | report.html.template.path  | _NON DEFINED_     | no           |

You can use all of these settings in each selenium (to be more precise: sesam-selenium) based project, but now comes the real advantage of the annotation processors - you can define own configuration settings using annotations and use them exactly in the same way as in the annotation described above! Maybe you don't understand what exaclty I'm talking (ehm writing :-) ) about so maybe the better way will the way with the examples:

Let's imagine that your application has openID/oauth based login and you want to define settings for that. So the firstly:
  * create a custom annotation for openID (or oauth)
```
public @interface OpenID {

	public enum Provider {
		GOOGLE("google"), ...;
		
		private String name;
		
		Provider(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}

	@Parameter(name = "openid.used", description = "OpenID user")
	String user();

	
	@Parameter(name = "openid.pass", description = "OpenID password")
	String pass(); 
	
	@Parameter(name = "openid.provider", description = "OpenID provider")
	Provider provider() default Provider.GOOGLE;
```
  * define default settings in your SuiteRunner
```
@OpenID(user = "test@gmail.com", pass = "secred_pass", provider = Provider.GOOGLE)
public class SuiteRunner {}
```
  * you can access these settings in your test like
```
@SeleniumTest(suiteRunner = SuiteRunner.class)
public class OpenIDLoginTestCase extends AbstractSynapsoTest {
	@Override
	protected SuiteRunnerSettingsProvider getSettings() {
		return new OpenIDLoginTestCaseConfiguration();
	}

	@Test
	public void testLogin() {
                getSettings().getOpenID().getUser(); //... etc
        }
}
```
  * and you have all the possibilities that sesam-selenium offers, like: redefine default settings using ` -Dopen.user=production@gmail.com ` or redefine global settings using annotation on the test case directly