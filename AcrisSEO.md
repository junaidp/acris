# Introduction #

A problem, that came up with AJAX-based web-sites and still bothers many of the web developers (at least web developers in Seges :) ) is how to make your javascript pages (AJAX based) crawlable and indexable by search engines like google, yahoo or bing. And what exatly is the problem?
Lets imagine AJAX based web site, or better ... lets take existing one: www.synapso.sk. This web site is completly based on AJAX (now I can tell it, it's not a secret - it's based on GWT framework) and with the clicking on the page, you can get URLs like this:
  * http://synapso.sk/#en/templates
  * http://synapso.sk/#en/projekt

If you are take a closer look on the URL you can see that it consists of several pieces:
```
   http://synapso.sk/#en/template
   \__/  \_________/\___________/
    |         |           |
 scheme    authority   fragment
```

You can find complete URI definition in RFC 3986 (Uniform Resource Identifier (URI): Generic Syntax) http://www.ietf.org/rfc/rfc3986.txt

Most important part is the fragment (aka fragment identifier or aka anchor indentifier) which is representing AJAX based token (see the acris web for the details). Everything looks fine till now:
  * each time when browser is requesting URL it DOES NOT send fragment identifier to the web server so he is requesting only http://synapso.sk/ URL each time when you are requesting url http://synapso.sk/#en/template or url http://synapso.sk/#en/projekt
  * search engines cannot distinquish between this 2 URLs. It is still the same URL
  * you are generating whole page content using AJAX so crawlers cannot see any content (crawlers cannot interpret javascript very well, so they cannot index any content on your page)
  * and what about accessibility? When user is using mobile for browsing or at all browser without javascript support? or screen reader? ... hmm?

# Search engines support #

Let me quote one sentence from [googlewebmastercentral.blogspot.com](http://googlewebmastercentral.blogspot.com/2009/10/proposal-for-making-ajax-crawlable.html)
"While AJAX-based websites are popular with users, search engines traditionally are not able to access any of the content on them. The last time we checked, almost 70% of the websites we know about use JavaScript in some form or another. Of course, most of that JavaScript is not AJAX, but the better that search engines could crawl and index AJAX, the more that developers could add richer features to their websites and still show up in search engines."

Hmm, pretty interesting. 70% of the web pages are not fully indexed by search engines because they are using javascript. This number force Google to think and the clever guys from Google create a proposal how you have to make your AJAX based web sites available also for search engines (ehm, still forgetting, there is another world outside the Google and lets guess if Bing or Yahoo shaked hands with Google in order to make common decission about AJAX crawlability. So in future text i'll discuss only google, because I did not yet find any reasonable solution for crawable AJAX in other search engines than google).

Google creates easy solutions:
Just slightly modify the fragment identifiers from
```
http://synapso.sk/#en/template
```
into
```
http://synapso.sk/#!en/template
```
Now google the can distinguish between
```
http://synapso.sk/#!en/template
http://synapso.sk/#!en/projekt
```
and recorginze that these links are different. See the full article on [Google central blog](http://googlewebmastercentral.blogspot.com/2009/10/proposal-for-making-ajax-crawlable.html).

Also take a look on [this presentation](http://docs.google.com/present/view?id=dc75gmks_120cjkt2chf) - it has just 11 slides and look at the slide 7.
![http://acris.googlecode.com/svn/wiki/images/seo_google_crawlability.png](http://acris.googlecode.com/svn/wiki/images/seo_google_crawlability.png)

So the steps are following:
  1. Crawler converts pretty URL (`http://synapso.sk/#!en/template` ... ehm :) pretty? ) to ugly URL (`http://synapso.sk/?query_escaperd_fragment_=en/template`) and requests URL from web server
  1. Web server should produce HTML snapshot (using the headless browser) and provide it to the crawler

Now Google can index your pages, also if they are AJAX based, using the HTML snapshot produced by headless browser.

# The headless browser #

There are few headless browsers which can interpret javascript and can create a HTML snapshot, for example:
  * **[HTMLUnit](http://htmlunit.sourceforge.net/)** - can work with GWT applications (In GWT 2.0 GWTTestCase does not use any SWT or native code so GWT can use pure Java solution for testing GWT application. See the details on [GWT 2.0 Release notes](http://code.google.com/intl/sk/webtoolkit/doc/latest/ReleaseNotes.html#NewFeaturesHtmlUnit) or on [GWT Development guide](http://code.google.com/intl/sk/webtoolkit/doc/latest/DevGuideTestingHtmlUnit.html)
  * **[Golf web application server](http://golf.github.com/)** - provides a way to build and deploy JavaScript driven webapps without sacrificing accessibility to JavaScript-disabled browsers

## HTML snapshot of the GWT web site ##

HTMLUnit sounds like a good solution for our needs, but in one more abstract step back, the GWTTestCase seems like the best solution for grabbing HTML snapshot of the page. For this purpose we created GWT offlinec generator project which is based on GWTTestCase and creates snapshot of the GWT application for all available tokens/niceUrls available in the database for the specified webId (unique identifier for the web site) and specified language.

![http://acris.googlecode.com/svn/wiki/images/seo_offline_generator.png](http://acris.googlecode.com/svn/wiki/images/seo_offline_generator.png)

How does it work?
  1. Offline content generator starts GWTTestCase as indenpendent java process.
  1. GWTTestCase starts GWT application in the headless browser (HtmlUnit) and navigates through the application tokens/niceUrls on by one on
  1. After each navigation to another token/niceUrl HTML content is taken from RootPanel and saved on the file system as the token filename (for niceUrl !en/template is created file **template** under the directory **en** with full offline HTML content)
  1. After HTML content is saved, content post processor is started with:
    * **alters** responsible for modifying HTML tags like:
      * anchors - link <a href='#!en/template'>template</a> is replaced for <a href='www.synapso.sk/en/template'>template</a>
      * description - meta tag (located in head) is replaced with correct page decription, because in GWT navigation description is not updated, we have to alter this meta tag manually by post processor
      * keywords - meta tag (located in head) is replaced with correct page keywords, because in GWT navigation keywords are not updated, we have to alter this meta tag manually by post processor
      * title - tag (located in head) is replaced with correct title, because in GWT navigation title is not updated, we have to alter this tag manually by post processor
      * links - update styles relative paths and images relative paths to the correct one
    * **appenders** - responsible for adding new HTML tags like:
      * Google analytics
      * GWT meta tag properties
    * **annihilators** - responsible for removing existing HTML tags

## Niceurl links vs AJAX based links ##

Applying post processor rules to the offline content we are moving from AJAX based links (`http://synapso.sk/#!en/template`) to the niceurl links (`http://synapso.sk/en/template`). Niceurl link will works without the problem because there is /en/template html file located on the files system. This file is contains complete offline version of the dynamic generated web page (for browsers without javascript support) and nocache GWT script for AJAX based features for the users with browsers with javascript support (thanks to the links post processor alter we are getting ../sk.seges.site.template.nocache.js for the file en/template so the links/paths are correct).

FAQ:
  1. Q: How to avoid links like http://synapso.sk/en/template#!en/projekt when user comes to the page through the link http://synapso.sk/en/template and then clicks on the projekt menu item and AJAX based navigation is added at the end of the URL + fragment hash.
> > A: SWS internally rewrite/restart? rule from http://synapso.sk/en/template into  http://synapso.sk/#en/template. TODO: Provide example and SWS configuration.

  1. Q: So, when are the links in AJAX based format (with fragment) comes into account?
> > A: Anytime when user AJAX based link (like `http://synapso.sk/#!en/template`) put somewhere in the another page or forum - generally said there exists somewhere in the internet backlink to the AJAX based web site. TODO: provide SWS rule with 301 status


![http://acris.googlecode.com/svn/wiki/images/generator_small.png](http://acris.googlecode.com/svn/wiki/images/generator_small.png)

Anchor based rewrite:
http://httpd.apache.org/docs/2.2/rewrite/rewrite_guide.html#redirectanchors

http://www.pgmsemblog.com/?p=9576
http://www.w3.org/DesignIssues/Fragment.html

http://googlewebmastercentral.blogspot.com/2009/10/proposal-for-making-ajax-crawlable.html
http://docs.google.com/present/view?id=dc75gmks_120cjkt2chf

http://googlewebmastercentral.blogspot.com/2007/11/spiders-view-of-web-20.html
http://www.youtube.com/watch?v=GEbS0a2JcAo