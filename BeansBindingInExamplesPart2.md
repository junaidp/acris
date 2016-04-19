# Introduction #

Today we will walk through acris-binding showcase where you can learn how to create bound form with validation support. For validation related stuff we will use JSR-303 annotations and [gwt-validation](http://code.google.com/p/gwt-validation) project which implements it.

In three (+1 optional) steps you get a form with validation!

# Setup #

Let's suppose (and we already have them) two POJOs: [SimpleBean](http://acris.googlecode.com/svn/trunk/acris-deployer/src/main/java/sk/seges/acris/binding/client/samples/mocks/SimpleBean.java) and [Company](http://acris.googlecode.com/svn/trunk/acris-deployer/src/main/java/sk/seges/acris/binding/client/samples/mocks/Company.java)

To be able to use them for binding we need to get wrappers for them. Because we are smart and don't want to write boilerplate code we use acris-binding for that purpose.

As a first step we create bean wrappers either by using annotation @BeanWrapper (or any other) or by creating bean wrapper interface by hand. The implementation of the bean wrapper interface is left for BeanWrapperGenerator class.

To be able to use acris-binding with validation we need to extend our module configuration to contain not only **bean wrapper** generator but also a generator that connects **validator** generator and bean wrapper generator together - for that purpose we have **validator delegate** generator. You can find it in ValidatorDelegateGenerator class.

Necessary configuration can be found in Showcase.gwt.xml:
```
	<generate-with
		class="sk.seges.acris.binding.rebind.bean.BeanWrapperGenerator">
		<any>
			<when-type-assignable
				class="sk.seges.acris.binding.client.samples.mocks.SimpleBeanBeanWrapper" />
			<when-type-assignable
				class="sk.seges.acris.binding.client.samples.mocks.CompanyBeanWrapper" />
		</any>
	</generate-with>

	<generate-with
		class="sk.seges.acris.binding.rebind.binding.ValidatorDelegateGenerator">
		<any>
			<when-type-assignable
				class="sk.seges.acris.binding.client.samples.mocks.SimpleBeanBeanWrapper" />
			<when-type-assignable
				class="sk.seges.acris.binding.client.samples.mocks.CompanyBeanWrapper" />
		</any>
	</generate-with>
```

As you can see when we hit e.g. SimpleBeanBeanWrapper class with GWT.create we will generate implementation of the bean wrapper + validator implementation for SimpleBean bean + validation delegate class that binds these two together in one class as a result.

# Extend the form binding #

To let acris-binding know we have bound form we use @BindingFieldsBase annotation. By simply adding two other parameters we get the form extended with validation support:
  * validationStrategy = ValidationStrategy.ON\_SUBMIT
  * (optional) validationHighlighter = ExampleHighlighter.class

Feel free to look into showcase example [SimpleForm](http://acris.googlecode.com/svn/trunk/acris-binding/src/main/java/sk/seges/acris/binding/client/samples/form/SimpleForm.java), it looks like this:

```
@BindingFieldsBase(updateStrategy=UpdateStrategy.READ_WRITE,validationStrategy=ValidationStrategy.ON_SUBMIT, validationHighlighter = ExampleHighlighter.class)
public class SimpleForm extends StandardFormBase implements IBeanBindingHolder<SimpleBean> {
```

# ... in application bind them #

Finally we can validate the form by clicking Submit button. Our click handler looks like this:

```
		submit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(selectedButton != null) {
					selectedButton.setText(simpleForm.getBean().getName());
				}
				
				IValidator<SimpleBean> validator = GWT.create(SimpleBeanBeanWrapper.class);
				Set<InvalidConstraint<SimpleBean>> constraints = validator.validate(simpleForm.getBean());
				ValidationMediator.highlightConstraints(simpleForm, constraints);
			}
		});
```

Here we just receive a bean from the form `simpleForm.getBean()` and execute validation. As a result we get validation constraints which can be highlighted by calling:
`ValidationMediator.highlightConstraints`

# Example highlighter #

Maybe you noticed optional attribute in the form binding annotation. Yes, its purpose is to choose one lovely component responsible for highlighting invalid constraints - usually by changing a color or showing a dialog.

For the purpose of the showcase we have one [example highlighter](http://acris.googlecode.com/svn/trunk/acris-deployer/src/main/java/sk/seges/acris/binding/client/samples/form/ExampleHighlighter.java).