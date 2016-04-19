# Introduction #

From now on we will be providing you with a series of examples where we will show you how to use AcrIS Binding.

Let's get started with a simple form...

# Simple form + Model-View-Presenter (MVP) Example #

In this example we will show you how to bind a bean with a form without using a lot of unnecessary binding code. We will use the MVP pattern to separate logic from the view and to demonstrate where is the displaying part and where is the back-end part.

Because it is the first example in the row we will also inform you about the necessary steps required to run binding code (Maven stuff, talks around, ...)

## Model ##

Let's have a domain object of type **Customer** fields which we want to display in a standard form. Our domain object has the following structure (we pick only one particularly interesting field):

```
@Entity
@Table
public class Customer extends LightEntity implements IDomainObject<Integer> {
	...

	public static final String COMPANY = "company";

	...
	
	@Embedded
	private CompanyName company;

	...
}
```

```
@Embeddable
public class CompanyName extends LightEntity implements Serializable {
	
	public static final String NAME = "name";
	
	private static final byte NAME_LENGTH = 50;
	
	@Column(length = NAME_LENGTH)
	private String name;

	...
}
```

This means we would like to bind a form to the nested "company" field. To start working with binding check [quick-start](BeansBindingQuickStart.md).

## Presenter ##

This is an example presenter responsible for providing **CustomerDisplay** with the data. Implementation of CustomerDisplay, as you can expect, will be our standard form. Focus on the onBind method ...

```
public class OrderCustomerPresenter extends BasePresenter<CustomerDisplay> {
	private final ValueHolder<Order> order;
	
	public OrderCustomerPresenter(CustomerDisplay display, ChocolateContext eventBus, final ValueHolder<Order> order) {
		super(display, eventBus);
		this.order = order;
	}

	@Override
	protected void onBind() {
		Order value = order.getValue();
		if(value == null) {
			throw new RuntimeException("There is no order presented");
		}

		// get the bean from the order
		Customer customer = value.getCustomer();
		// create a wrapper for the bean
		BeanWrapper<Customer> customerWrapper = GWT.create(Customer.class);
		// feed the wrapper
		customerWrapper.setContent(customer);
		// feed the display with wrapper containing the bean
		display.setValue((Customer) customerWrapper);
	}
	
	@Override
	protected void onUnbind() {
	}
}
```

When you are working with AcrIS binding, your object in the binding must be able to send change events to the world around and also allow others to listen to changes. As you can see, our Customer object is not that kind of guy. But don't be bothered, we have a mechanism for that - **BeanWrapper**.

BeanWrapper is an interface but using BeanWrapperGenerator you are able to create a wrapper instance around your bean with all the "listening" stuff. Because our entities are all extending a common interface we will define a rule:

```
<generate-with class="sk.seges.acris.rebind.bind.BeanWrapperGenerator">
	<any>
		<when-type-assignable class="net.sf.gilead.pojo.base.ILightEntity"/>
	</any>
</generate-with>
```

Now it is complete and we are prepared to create the visuals...

## View ##

Our CustomerDisplay interface is currently very simple:

```
public interface CustomerDisplay extends Display, HasValue<Customer> {
}
```

It has just the ability to be fed by the bean - Customer.

The interesting part is the implementation of the interface. We will call it OrderCustomerPanel because it displays the "customer"-part of an order. Depending on your MVP implementation (common displays, presenters,...) there will be methods to implement. For this example we will show only relevant parts of the binding. It is also recommended to read AcrIS binding [documentation](BeansBinding.md):

```
@BindingFieldsBase(updateStrategy=UpdateStrategy.READ_WRITE, validationStrategy=ValidationStrategy.ON_SUBMIT)
public class OrderCustomerPanel extends StandardForm implements CustomerDisplay, IBeanBindingHolder<Customer> {

	// just annotate the text box and everything is bound
	@BindingField(Customer.COMPANY + "." + CompanyName.NAME)
	protected TextBox tbName = GWT.create(TextBox.class);
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasValue#getValue()
	 */
	@Override
	public Customer getValue() {
		// not important now to return a value, but if you want...
		return null;
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
	 */
	@Override
	public void setValue(Customer value, boolean fireEvents) {
		// just delegate to setBean method
		setBean(value);
		if(fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	@Override
	public Customer getBean() {
		// all code here will be replaced by generator
		return null;
	}

	@Override
	public void setBean(Customer bean) {
		// all code here will be replaced by generator
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		// putting the text box to the form
		getContainerWidget().addWidget("Company", tbName);
	}
}
```

Our panel must extend the StandardForm, IBeanBindingHolder and also the CustomerDisplay interfaces. After that you just need to annotate the text box representing the value of the customer's company name and put the text box to the form...

## Final word ##

We have scratched the surface of the bindings world and we will continue with other examples. AcrIS binding is evolving quickly and just in this very moment we see places were we can get rid of boilerplate code... stay in touch and feel free to ask questions in the discussion group.