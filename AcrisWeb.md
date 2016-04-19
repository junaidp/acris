

# What's this all about ? #
AcrisWeb is a project exactly matching the summary above. You may be like: "What? Of course it's for websites. It's a web technology..." Well, true, but... GWT is an application framework. You may think of it as "the Swing for your browser". And there's the catch (NullPointerException lameJokeFail :( ). Ever tried to create a website with Swing ? Not that there would be any particular reason for it, but imagining it, you will get the picture of where I am heading with this. GWT, as stated, is an application framework. It's great for creating RIA applications, that are most commonly placed within a page of a website or published as stand-alone applications.

## Website vs. Web application ##
What's the difference ?
A website is meant solely for information purposes. It consists of a mostly static skelet (header, footer, 2/3 column design, ...) and variable content pages, that are put into the skelet. Of course, the skelet may not be entirely static, but the point is, you have some text, images, flash videos, links and other fancy stuff and what you see will change according to where you click.
A web application is an another name for a web frontend of a multitier application. It's purpose is to present the user some data, receive his input from various forms and other GUI stuff, pass it to the backend for evaluation and again, present the results. In terms of implementation, it is a hardcoded bunch of code, most commonly flawless and bugfree ;) . Mostly, you want to combine these two. You have to write a web application, but you have to put it into a website. This is where AcrisWeb comes in very handy.

# Websites in GWT #
With GWT you can implement your web desing/your website easily. You take all the nice panels, wire them together, put in some tables, links and images, desing it all with CSS, test it on all available browsers and Internet Explorer and voila, you have a website.

## Problems ? ##
Several...
  * what happens if your design changes? Suddenly your customer realizes, that he actually doesn't like the 2 column desing, but rather he would the the content to be placed in 3 columns. No problem. You take the code, rearrange the panels, compile it, pack it, test it, deploy it and voila, there's the fix. But... what's up with the other 3 websites you have to create ? You probably got some experience already with the first one, so creating the next should not take you more than...a month ?
  * have you ever looked at the source of your website/application in your browser ? Because that's exactly how the search engines look at it. Being an AJAX technology, GWT is pure Javascript. Therefore there is not much to see in the HTML file unless you put something in there.
  * imagine a situation, where you are asked to add a page to the existing website, that looks exactly like this (let your imagination loose - put some crazy looking page here). Again. Hardcoding, compiling, packing, testing, deploying...
Sure you can image a whole set of other problems.
You can:
  1. forget about GWT as being your platform to implement the website part with and pick some other technology or
  1. accept the problems and hardcode a website for every webapp you build or
  1. use AcrisWeb.

# AcrisWeb #
AcrisWeb takes care of all the content pages and webflow you may ever need. It is a GWT application meant to build and display dynamic content. In other words, it is a universal GWT website.

## Content entity ##
As with the most applications, AcrisWeb uses a database to store important data in. The most important entity is the Content. Within the content data is stored, that is linked with a particullar part of the website. Contents are linked with each other via a parent-child relationship building a tree which resembles the website's internal structure. A Content object may represent a column, the header, footer or some text part of a page. Among other things, that the Content contains, are: SEO parameters, niceURL, title (for web pages) and additional positioning data. A very important piece of information is the reference to a specific panel the content is linked to.

## Automatic layouting panels ##
In all Acris we have a crush on chocolates. So, there is this ChocolatePanel i.e. ChocolatePanel is a GWT panel linked to a Content from the database and, as was mentioned, is responsible for displaying a part of the website (header, footer, etc.). The relationship between a Content and a ChocolatePanel is described on the picture below: (TODO - add picture here)
A LayoutChocolatePanel is a ChocolatePanel that contains a container to put other panels to and layouting algorithms, that take care of the actual composition of ChocolatePanels. Being able to automatically compose and display panels according to a tree-like database structure allows us to display a page exactly as it's meant to be displayed. One more thing we need is the ability to switch some panels with another ones upon some navigation event (link clicked i.e.). The tree shown on figure above is not a whole website. It is merely a skelet of a simple website with no text content pages. However, in a website, you will have plenty of text pages, where many may be placed on the same spot and are switched upon a navigation event - i.e. you have a menu with some links and clicking on them will switch the content of the center column. The tree structure of Contents in the database therefor contains all possible Contents and Content branches (web pages) that may be displayed in the website.

## AND&OR - together forever ##
So, there are exactly 2 main activities, you want your website to be able to do:
  1. you want it to be able to display a couple of parts at a moment, previously called as the static or fixed skelet (i.e. header AND center part divided into 2 columns AND the footer are always visible)
  1. you want it to be able to switch the content of a specific part ("Home" page OR "About us" page OR "Projects" page OR another page is visible at a moment in a specified part of the site - i.e. the middle column of the center part)

For these activities, there are the two main layouting panels:

  1. the FlowLayoutChocolatePanel - which, when visible, displays all it's child ChocolatePanels (the main panel always displays the header, center part and the footer)
  1. the DeckLayoutChocolatePanel - which, when visible, displays only one from it's children at a time (the center part switches between it's child ChocolatePanels, that represent text content pages like "Home", "About us", "Projects", etc.)

Combining these two, you can create ANY web layout you are told to. For support, you have the MenuLayoutChocolatePanel with the all known menu functionality and the HTMLLayoutChocolatePanel, which displays all the actual content (the HTML text part) you want to display within the panel. All you have to do is create a valid structure of Contents stored within the database. AcrisWeb takes care of everything else. Adding a new page to the website simply boils down to adding a new Content to the database. When done correctly, except for some CSS needed to properly display the page, you don't have to do anything else.

# Advantages #
Besides having a pure GWT website, which itself sounds cool, you gain numerous advantages:
  * speed - when switching between site's pages, the data traffic between the client and the server is significantly reduced. You only ask and only get the data, you actually want to display. Everything else which is already displayed, remains untouched. When using modern browsers (firefox 3.5, opera 10.5) or Internet Explorer, you also gain control over the browsers internal cache. This means, the you will make exactly ONE server request per page EVER. All the page's content (the HTML text part) will be stored directly in your browser, so next time you visit the page, the content will be fetched directly from your browser, instead asking the server for it.
  * extensibility - mentioned HTMLLayoutChocolatePanel and MenuLayoutChocolatePanel serve only AcrisWeb's needs. In other words, they are responsible for displaying some informative web pages. However, you can extend the basic AbstractLayoutChocolatePanel and put in some stuff from your existing GWT web application. This seamless integration makes it incredibly easy and straightforward to combine your web application with some nice web design without the need to code the design by your self. The options are limitless.
  * a happy server and a green planet - this is about the server's load. When serving one website, most of the time, the server has got less work to do, than a PHP powered Apache. When started, first client connected requests all structural contents from the server. This is a matter of one database select from the content table. The select fetches all Content entities without their HTML text part - only data like: title, niceURL, keywords, etc. is fetched. If you have only one website, the select simply fetches all content data from the table. No restrictions, no big deal. The results can be stored in Hibernate's second level cache, so no simillar selects will ever be made again. After that, the client sends request, when it wants to display a certain page. This is actually a request for some Content's HTML text part. As mentioned earlier, after receiving this part, the client, if it is a modern browser like Firefox 3.5, Opera 10.10 or IE8, stores the HTML in it's own cache, so in the future, the client will never again request this data from the server, unless the cache is cleared or the Content changes. Summarized, the server's load is minimal, so the (client count/server count) ratio is rissen significiantly. And the green planet ? Well, less servers = less polution ;)

# So...what about the search engines then ? #
We all know that web crawlers can't do much about Javascript. Since AcrisWeb is a GWT application, it is pure Javascript including all the texts, images and everything it may contain. There is a solution however. It is called AcrisGenerator - a project designed to create static HTML content from GWT applications. You can fing more info about it here (TODO - add link).