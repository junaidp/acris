# Introduction #
<table>
<tr>
<td>
When we were evaluating the frameworks for automated tests, the <a href='http://seleniumhq.org/'>Selenium</a> was the best choice for us that perfectly fits our needs. Except the client (java) library, there is a whole bunch of another tools available, like:<br>
<ul><li><b>Selenium IDE</b> for recording the tests<br>
</li><li><b>Selenium server</b> for executing automated tests remotely<br>
</li><li><b>Selenium client</b> for writing automated tests in the specific language like java, c#, ruby and python<br>
</li><li><b>Selenium Grid</b> for distributed selenium tests execution<br>
</li><li>and many many <b>plugins</b> that could make your life easier ... or not :-)</li></ul>

We were using selenium few years ago and we are still using selenium now and all I can say is: the selenium is cool! ... but:<br>
<ul><li>we are <b>not</b> using selenium IDE for recording the tests,<br>
</li><li>we are <b>not</b> using selenium server for executing tests remotelly<br>
</li><li>we are <b>not</b> using selenium grid<br>
</li><li>and we are <b>not</b> using selenium plugins that are officialy available</li></ul>

and here is why not.<br>
</td>
<td>
<img src='http://www.buzzle.com/img/articleImages/268534-2911-45.jpg' />
</td>
</tr>
</table>

# Why not? #

## Selenium IDE ##

Selenium IDE allows you to record user actions easily whilst you are **clicking on the page**. This is great tool when you are just beginning with the selenium or if you want to show the advantages of the selenium to your management during the presentation :-). But there are plenty of problems we have faced with:
  * xpaths are not recorded in the **optimal way** (for the elements that does not have IDs - to be honest, the only way how to write them correctly is to use human brain) therefore the tests were hard to maintain,
  * **reuse problem** - when there is a common functionality, like login, that has to be performed before each test, it should be in the separated in the individual test and should be included in all other automated tests - this is impossible to do using selenium IDE,
  * tests cannot be **configured from outside** of the automated test - like using one user in order to login for the test environment and using different user in order to login for the production environment.

This reasons lead us to not use selenium IDE, but it helps us a lot in the **very first phases**.

## Selenium plugins ##

What we were missing in the selenium is the **support for HTML reports** that will tell/provide us with:
  * which commands were executed,
  * which command failed,
  * what was the reason of the failure,
  * screenshot before the action and screenshot after the action so we can see how exactly the web page was looking.

This is what we've expected from the selenium plugins, because selenium itself does not have mechanism for reporting results, but selenium has a sophisticated solution for creating such reports (for selenium 1.x the [Selenium logging](http://loggingselenium.sourceforge.net/) framework can be used).

Next think we were looking for was the **possibility to configure** the tests from the command line (= **without changing the code**), for example:
  * specify the browser that the tests are running with,
  * change user that is going to be used for the tests,
  * to enable/disable or specify reporting possibilities (e.g. if I want to have report with the screenshots or not, ...),
  * to specify email account - for the tests that are integrated with mailbox and are validating also the mail arrivals and contents,

and all of this have to be achieved in easy way.

This is what a bromine should be used for - but it does not!

## Selenium Grid & Selenium Server ##

  * Selenium Grid is a great way how to run tests in **parallel**. But you probably want to run tests using continuous integration tools - like jenkins and that's exactly what **jenkins does - runs multiple jobs in a parallel**, in a multiple nodes simultaneously or in a multiple nodes in a parallel - that perfectly fits the needs, so we never had a real reason to use selenium grid,
  * and the same with the **selenium server** - we never had a reason to use selenium server/RC - jenkins has a **slave nodes** for that purpose. Just run a slave node on a machine where selenium server should be executed and run tests directly on that node.

Pretty bad! There are plenty of selenium tools, but **what makes selenium great is selenium client libraries**.

# Why yes? #

With the new selenium releases (2.x versions) web driver was introduced. This made a **revolution** in the selenium and whole approach was completely reworked from scratch, but with the **bright future**. Client libraries allows us to achieve:
  * **writing reusable tests** - common functionality is located in the common class/test and when the common functionality is changed we just change it in a single place,
  * **integrate tests with outside world** like emails - in a few web applications/sites email is an integral part that represents core functionality - like how you will test a newsletter? How you can check in your automated test if email is working and if was delivered in the correct format with proper content? How you can parse the email whether contains correct activation link? - this can be easily achieved using java selenium client libraries,
  * **integrate test with maven**. This means:
    * run tests in easy way: using ` mvn test ` command,
    * run tests using CI like jenkins,
    * easily upgrade selenium libraries - there is new selenium release (with a new minor version) available each week and maven allows us to upgrade selenium in a seconds,
    * and few more - I'll explain this later on

# Let's play with sample project #

Want a try maven based selenium project in a 3 minutes?
  1. download sample project from [SVN repository](http://acris.googlecode.com/svn/sesam/branches/selenium/)
  1. download and run [maven](http://maven.apache.org/)
  1. run command ` mvn test ` in the directory where sample project is located

Behold! Firefox should be opened with some prerecorded actions in it (If you have installed Firefox :-) )

## Eclipse configuration ##

  1. run command ` mvn eclipse:eclipse `
  1. import project into the eclipse
  1. run test as junit tests (4 easy steps described on images below)

<table border='none'>
<tr>
<td>
<a href='http://acris.googlecode.com/svn/wiki/images/selenium_1_eclipse.png'><img src='http://acris.googlecode.com/svn/wiki/images/selenium_1_eclipse_small.png' /></a>
</td>
<td>
<a href='http://acris.googlecode.com/svn/wiki/images/selenium_2_import.png'><img src='http://acris.googlecode.com/svn/wiki/images/selenium_2_import_small.png' /></a>
</td>
<td>
<a href='http://acris.googlecode.com/svn/wiki/images/selenium_3_project.png'><img src='http://acris.googlecode.com/svn/wiki/images/selenium_3_project_small.png' /></a>
</td>
</tr>
<tr>
<td align='center'>
<font color='#DDDDDD' size='5'><b>Select import</b></font>
</td>
<td align='center'>
<font color='#DDDDDD' size='5'><b>Existing project</b></font>
</td>
<td align='center'>
<font color='#DDDDDD' size='5'><b>Choose directory</b></font>
</td>
</tr>
<tr>
<td></td>
<td>
<a href='http://acris.googlecode.com/svn/wiki/images/selenium_4_final.png'><img src='http://acris.googlecode.com/svn/wiki/images/selenium_4_final_small.png' /></a>
</td>
<td></td>
</tr>
<tr>
<td></td>
<td align='center'>
<font color='#DDDDDD' size='5'><b>Project imported</b></font>
</td>
<td></td>
</tr>
</table>

## Scope ##

Search test consists of just 3 lines (except the method definition):

```
@Test
public void searchTest() {
	GooglePage googlePage = new GooglePage();
	webDriver.findElement(googlePage.getSearchBoxLocator()).sendKeys("seges\n");
	webDriver.findElement(googlePage.getResultLink());
}
```

  * ` GooglePage googlePage = new GooglePage(); ` creates the page descriptor that holds all locators used on the page. It's very good practice to separate xpatch/css/id locators from the test code - you can easily change them in the future because they are in one place - this is most critical part of the automated tests because many times the locators are changed in the application lifecycle. At least 9/10 selenium users recommends this :-) - selenium has an integral support the [page object](http://code.google.com/p/selenium/wiki/PageObjects),
  * ` webDriver.findElement(googlePage.getSearchBoxLocator()).sendKeys("seges\n"); ` this finds the search text box on the google page and writes "seges" text in there. The test does not care how text box is identified, that's the purpose of the page object, the test itself should just do the test logic. Everything other than logic should be outside of the test,
  * ` webDriver.findElement(googlePage.getResultLink()); ` command will wait until the results are displayed on the page. Default wait timeout is 60 seconds.

Now, maybe you are asking:
  * why the www.google.com page is opened? Where it is defined?
  * why it is opened in the firefox?

a the answer is: it's the **configuration** mentioned above and it is located in the SuiteRunner class:

```
@SeleniumSuite
@Selenium(testURL = "http://www.google.com",  testURI="/", browser = Browsers.FIREFOX)
public class SuiteRunner {}
```

Now if you change the browser settings from ` browser = Browsers.FIREFOX ` into ` browser = Browsers.IE ` your tests will be executed in the Internet Explorer (**!!you have to clean eclipse project after configuration property change!!** - so the configuration can be regenerated)