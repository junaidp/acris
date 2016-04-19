# History #

When Google starts creating a new project for translating Java code into JavaScript I was little bit sceptic. I've always knew that Google has very inovative approaches for solving the problems and they are still comming with a something epochal, but this goal was something different. Something that I wasn't fully identified with. I thought that JavaScript and Java principles were so different that is not possible to create working port from one language to another at least for:
  * **threads** - how they will handle java multithread code in a single thread JavaScript?
  * **filesystem** - almost whole java is tightly bound to **io** package which is not possible to use in the javascript
  * **reflection** - how they will use reflection (in a static JavaScript) which is the most used technique in each bigger java framework?
This were only few ideas that comes into my mind and there is for sure a lot of more. But the time was moving and framework gets bigger and bigger and then I just wanted to try it if is already doing what Google promises.

It has to be a **rocket sience** to achieve translating java language into javascript - _I thought_ - but it will be a huge step forward for web 2.0 development (and also a wet and unspoken dream of each java developer). I've just wanted to be a part of this and started with first experiments with **GWT**. Year 2008 was written and GWT actual version was 1.4 and after short time I was completly addicted to Google Web Toolkit. Any other web platform seem to me as a non effective way of developing the web application (of course there exists many other perfect platforms which I was using before - mostly JSF & IceFaces - but GWT completly fits my needs and the way of developing with GWT was completly natural for me).

Three years of development with GWT in a daily basis are gone and there are few big applications in a production, created by me and by the team I was part of. After 3 years it brings fruits to me and it's time to collect the fruits in a form of the best practices, which can be used by every Google Web Toolkit developer.

# Best practices #

OK, stop talking, just tell us the best practices so we can decide if they are usefull or not.
In a short answer I have to say that I will completly follow up the principles proposed by **Ray Ryan** on **Google I/O 2009** and add something new to his proposals - and whats more, I have and working examples for you, not only the theory, so do no hesitate to start with them.

Just to sum up what Ryan was saying:
  * use **MVP** (**Model-View-Presenter**) pattern for preciselly spliting your code into three parts (data which are going to be displayed - **model** - forms and the screens with the UI widgets - **view** - and the logic somewhere between them - **presenter**)
  * use **event bus** in order to to interconnect these components and as a communication channel between presenters.
  * use **command pattern** and **dispatch service** as midlayer between client and server
  * use **dependency injection** for loose coupling between your presenters and application parts
You probably know the diagrams presented by Ray identifying the process of loosely coupled elements. Looks cool, isn't it? :) Yes, of course, in a theory, but what about the reality?

![http://acris.googlecode.com/svn/wiki/images/mvp_coupling_small.png](http://acris.googlecode.com/svn/wiki/images/mvp_coupling_small.png)

I dig into the MVP pattern paradigm and found few implementations for Google Web Toolkit. I'm not going to discuss here which MVP implementation is the best ([mvp4g](http://code.google.com/p/mvp4g/), [gwt-presenter](http://code.google.com/p/gwt-presenter/) or [gwt-platform](http://code.google.com/p/gwt-platform/)) this was well done by [Andreas Borglin pages](http://borglin.net/gwt-project/?page_id=10) and for my projects and for the presentation purposes I've choose the [gwt-platform](http://code.google.com/p/gwt-platform/) created by Philippe Beaudoin and  Christian Goudreau (thank you guys for the great work on this project). Note that I'm not saying that other MVP implementations are bad and you can choose another one if you one, it's still the MVP and this article is about the additional value of the MVP, so the concrete implementation really does not matter.

# So, how is the reality looks like? #

Let's firstly take a look on MVP pattern.
![http://acris.googlecode.com/svn/wiki/images/mvp_small.png](http://acris.googlecode.com/svn/wiki/images/mvp_small.png)

Core of the each application is the model which represents application specific domain data and differs between the applications. But there are some parts of the domain area which you can find in each bigger application. I think it's not surprise to you that I'm talking about **users**, so I will explain you the model part of the MVP pattern on the users which I took from acris-security project.

![http://acris.googlecode.com/svn/wiki/images/user_model_lr_small.png](http://acris.googlecode.com/svn/wiki/images/user_model_lr_small.png)

The colors in the diagram are not there beacause it is fancy and super cool, but they helps us to identify which classes are used on **client (green)** part of the application, which are used on the **server (yellow)** side and which are the **common (red)** classes. As you can see from the diagram we will use the DTO's as lightweight version of the persistent entities, but both of the implements the same interface so we can all the time working the interfaces to have the code clear. So for example, service layer will works only with UserData interface:
```
@GenDispatch
public class PersistUser {
  @In(1) UserData user;
  @Out(1) UserData user;
}

public UserData persist(UserData entity);

```
Service method input is the transparently converted from !GenericUserDTO into TwigGenericUser and return method is converted in oposite direction, from TwigGenericUser into !GenericUserDTO automaticly using dozer. Ok, so far, so good - this is our domain model, now the question is how acris is extending the model?

## Model extension ##

Now if you have many of your DTO's used in your client application, you probably want to:
  * display them somewhere in UI :)
  * use beans binding as a inteligent solution for data propagation
  * validate data that comes from UI

The plan is fine, but there are some issues that should be solved to have all this tasks working:
  * your domain model is pretty dependend on your UI library, because for example SmartGWT can work with Record interface, ExtGWT (GXT) can work with ModelData interface and etc. so you have to ensure that your model will implements this interfaces and that, whole application is highly dependend on your UI library, which is definitely bad idea. This is the reason for the **[bean wrappers](http://code.google.com/p/acris/wiki/BeanWrappers)**, which wrapps your model classes and add the specific behaviour (like setAttribute method) to them in a **completly transparent** way. In this way you can achieve that your model will not be dependend on UI library and you can switch the UI library without touching your model classes.
  * probably you know great gwt-beansbinding library which allows you to use beans binding in GWT applications. If yes, then you will be maybe interested into [acris-binding](http://code.google.com/p/acris/wiki/BeansBinding) which allows you the same binding using the annotations. Only problem with beansbinding is that you have to implement HasPropertyChangeSupport interface in your model and properly fires the property change events. This complicates situation little bit because it's pretty hard to maintain such things (and the number of interfaces in your model is still growing) and on the other hand, they should be **generated** so it is not necessary to write them. Second reasond for [bean wrappers](http://code.google.com/p/acris/wiki/BeanWrappers).
  * and the third reason consist in validation itself. Here comes into place next great project [gwt-validation](http://code.google.com/p/gwt-validation/) which requires to have implemented IValidatable interface in your model. It is still possible to do it manually or it should be done in a fully automatic and transparent way using the bean wrappers.

So the bean wrappers wraps your model and added specific support to it, like ModelData, IValidation, HasPropertyChangeSupport, etc. This process is ilustrated on the following diagram in model column.

![http://acris.googlecode.com/svn/wiki/images/mvp_acris_model_small.png](http://acris.googlecode.com/svn/wiki/images/mvp_acris_model_small.png)

### Creating bean wrapper ###

You have few possibilities how to create bean wrapper for your bean (model data class):
  * annotate your model class with @BeanWrapper annotation
```
@BeanWrapper
public class GenericUserDTO implements UserData {
...
}
```
  * implement marker interface !ITransferableObject or domain interface !IDomainObject
```
public class GenericUserDTO implements UserData, ITransferableObject {
...
}
```
  * create external configuration with all classes listed in getClasses method
```
public class CustomBeanWrapperConfiguration extends DefaultBeanWrapperConfiguration {
	@Override
	public ClassDescriptor[] getClasses() {
		return new ClassDescriptor[] {
				new ClassResourceDescriptor(GenericUserDTO.class)
		};
	}
}
```
> and change override default configuration in META-INF/bean-wrapper.properties file:
```
configuration=sk.seges.acris.binding.jsr269.CustomBeanWrapperConfiguration
```

By fulfilling one of these steps above you can use generated bean wrapper interface. Following code snippet shows integration with smartGWT

```
UserData user = ...; //fetched user from service
BeanWrapper userWrapper = GWT.create(GenericUserDTOBeanWrapper.class);
userWrapper.setBeanWrapperContent(user);
ListGrid grid = ...; //grid initialization
grid.getDataAsRecordList().add((ListGridRecord)record);
```

**TODO add ext gwt sample here**

This allows you to display data in **smartGWT grid** without extending the record class. The  logic should be **carefully splitted** into model, view and presenter parts and code snippet is used just like an example.

## Presenter extension ##

Presenter is used to:
  * handle whole business logic of your application
  * contacting the server using dispatch servlet
  * interact with views

Communication between presenters is realized **strictly** through the **eventbus** by firing the **events** and communication between presenter and view is used by **registering the handlers** into view (through local eventbuses - GWT handler manager). Using this approach
you can achieve completly **loose coupling strategy** between code parts.

### Interaction between presenter & view ###
```
public class LoginPresenter extends PresenterImpl<LoginDisplay, LoginProxy> implements LoginEventHandler {

        ...

	public interface LoginDisplay extends View {

		HandlerRegistration addLoginHandler(LoginEventHandler handler);

		void showValidationErrors(Set<InvalidConstraint<UserData>> constraints);
	}

	@Override
	protected void onBind() {
		super.onBind();
		registerHandler(getView().addLoginHandler(this));
	}

	@Override
	public void onLogin(LoginEvent loginEvent) {
               ...
        }
}
```

You are registering your LoginEventHandler into the view in the onBind method (and handler is deregistered in onUnbind method, so we are preventing any memory leaks) and handles login event in your presenter code. This event is fired in the view (probably after submit button). This code splitting is very important and allows you to have clean code, which is maintanable, reusable and testable - this is important factor in agile way of development.

Do you remember the bean wrappers? They can perfectly helps us with the bean validation process (following JSR303 specification).

```
IBeanWrapper<UserData> userWrapper = loginEvent.getResult();
Set<InvalidConstraint<UserData>> constraints = ((IValidator<UserData>)userWrapper).validate(userWrapper);
if (constraints.size() > 0) {
   getView().showValidationErrors(constraints);
} else {
   //do login
}
```

**TODO add validation rules here**

![http://acris.googlecode.com/svn/wiki/images/mvp_acris_presenter_small.png](http://acris.googlecode.com/svn/wiki/images/mvp_acris_presenter_small.png)

**TODO Cancel support**

## View extension ##

Now comes the most interesting part - **the View** - where you can finally prosper from the acris benefits. Maybe you noticed that acris is primarily focused on annotations a can perfectly reuse them in view for:
  * binding beans with the UI components
  * securing the UI components

Let's see the implementation of the binding and the most simplest example is our login form.

```
@BindingFieldsBase(updateStrategy = UpdateStrategy.READ_WRITE, validationStrategy = ValidationStrategy.ON_SUBMIT)
public class LoginSmartForm extends TwoColumnSmartForm<UserData> {

	@BindingField(UserDataBeanWrapper.USERNAME)
	protected final TextItem username = GWT.create(TextItem.class);

	@BindingField(UserDataBeanWrapper.PASSWORD)
	protected final PasswordItem password = GWT.create(PasswordItem.class);

	public LoginSmartForm() {
		setWidth(400);
	}

	@Override
	protected void prepareFields() {
		addFormRow(true, withLabel("username", username));
		addFormRow(true, withLabel("description", password));
	}
}
```

For this small form you saved maybe only the few lines of code, but think about the binding with the much more complicated form. And what are another benefits from binding using annotations?

  * **much less error prone code writing** - using generated metadata which allows you to bind to properties which reflects the reality (not hand written constants). Did you noteced that UserDataBeanWrapper class with USERNAME and PASSWORD constants? They are generated from UserData class (which has setter/getter for these 2 properties) and are regenerated after each change in UserData so it will warn you that you have compile error in the code whe you change the UserData class property which is used in beans binding. This allows you to write changes propagation from beans into UI component and back from UI component into bean which is completly safe on 1 line of code.
  * **cleaner code** containing only the important LOCs without the code which does not have any business value. Accepting this way of coding you will write maintainable code which can easily extended anytime in the future without any complications.
  * **maintain the code** without touching the code - this, little bit strange affirmation, is talking about that kind of situation where you want to for example convert values before they are displayed in the UI - e.g. using the converters. You will just change the annotation and your code remains the same. I'm pretty sure that you experienced situations when you wanted to change only small think in your code and you accidentally introduced a new bug there. This way should prevent you to do that.

Now creating an login event, mentioned above is pretty easy. Just 2 lines of code:

```
submit.addClickHandler(new ClickHandler() {
	
	@Override
	public void onClick(ClickEvent event) {
		LoginEvent loginEvent = new LoginEvent(loginForm.getBean());
		fireEvent(loginEvent);
	}
});
```

Event is fired, presenter will catch it, validates the input and if everything goes without the problems, send request to the server with your credentials in order to log the user into the application.

Maybe the final think that you are missing is how will presenter listen to the events and how will be registered in the view. Again the simple answer:
```
@Override
public HandlerRegistration addLoginHandler(LoginEventHandler handler) {
	return addHandler(handler, LoginEvent.getType());
}
```

This will registers presenter's handler into the view's local eventbus and after the LoginEvent is fired, presenter's handler is notified.

If you look on the image below, you will see one more thing there: the security integration with view. This the most fency stuff because just using the annotation acris allows you to keep your server side security in a consistent way with your client view. This means if the user is not allowed to maintain other users, he will be not able to:
  * fetch users from server, delete users, modify users, create users (server side security)
  * see all screens related to user maintenance (client side security)
Both securities goes hand-by-hand because if there will be no client side security there will be the empty screens without any data (because server side security does not allows user to fetch the data from the server, but the screen will be displayed) so each professional application should count also with the security propagation into client/view. And this everything is done using acris-security.
```
...TODO...
```

![http://acris.googlecode.com/svn/wiki/images/mvp_acris_view_small.png](http://acris.googlecode.com/svn/wiki/images/mvp_acris_view_small.png)]

# Overall view #

![http://acris.googlecode.com/svn/wiki/images/mvp_acris_small.png](http://acris.googlecode.com/svn/wiki/images/mvp_acris_small.png)

![http://acris.googlecode.com/svn/wiki/images/acris_binding_small.png](http://acris.googlecode.com/svn/wiki/images/acris_binding_small.png)

![http://acris.googlecode.com/svn/wiki/images/security_steps_small.png](http://acris.googlecode.com/svn/wiki/images/security_steps_small.png)

# Demo application #

![http://acris.googlecode.com/svn/wiki/images/mvp_usecase.png](http://acris.googlecode.com/svn/wiki/images/mvp_usecase.png)

![http://acris.googlecode.com/svn/wiki/images/mvp_user_maint_small.png](http://acris.googlecode.com/svn/wiki/images/mvp_user_maint_small.png)

# Hosting on appengine #

# Migrating possibilities #

## Migrate to RDBMS system ##

## Migrate to different UI library ##