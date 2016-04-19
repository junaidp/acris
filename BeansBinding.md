# Important information #

**(!!!)** As of next release (1.1.2) the binding part of AcrIS will switch to the maintenance mode and no new features will be implemented. We recommend using [GWT's Editor framework](http://code.google.com/intl/sk-SK/webtoolkit/doc/latest/DevGuideUiEditors.html) instead. All required features will be implemented there in case of a need.

# UI widgets binding #

Project acris-binding represents extension to the gwt-beansbinding library and is focused to binding Beans with GWT UI widgets.

Currently we are supporting 3 types of bindings:
  * **One-to-One binding** - the most simplest type used to bind simple value to the widget that can hold only simple value (e.g. `TextBox`, `CheckBox`, ...)

Sample:

```
@BindingField(User.NAME)
protected TextBox username = GWT.create(TextBox.class);
```

or accessing nested fields (User [this](this.md) -> Address [address](address.md) -> String [street](street.md)):

```
@BindingField(User.ADDRESS + "." + Address.STREET)
protected TextBox userStreet = GWT.create(TextBox.class);
```

  * **One-to-Many binding** - more complex binding type used to bind object with the list of objects represented in `ListBox` component.

Sample:

```
@BindingField(User.ROLE + "." + Role.NAME)
protected ListBox role = GWT.create(ListBox.class);
```

In the sample above we bind role from User with ListBox which holds all available roles. We are matching object through name attribute (in ListBox are displayed role names - in String representation). In order to have complete working example you have to also define data loader, which provides data to the ListBox. By default is defined EmptyLoaderCreator which provides no data. You can define your own data loader which can load data from database (using RPC) or load some predefined data.

```
@BindingField(User.ROLE + "." + Role.NAME)
@BindingSpecLoader(CustomDataLoader.class)
protected ListBox role = GWT.create(ListBox.class);
```

Now CustomDataLoader is called when ListBox is initialized. See FieldSpecLoader for more details.

  * **Many-to-Many binding** - used to specify binding of List of values to ListBox with multiselect functionality. Also should be used for binding to the table, but currently it is only in experimental state

## One To One binding ##

Currently we are supporting binding java primites (and their object alternatives) with:
  * `TextBox` (using `TextBoxBaseAdapterProvider`)
  * `TextArea` (using `TextBoxBaseAdapterProvider`)
  * `DateBox` (using `DateBoxAdapterProvider`)
  * `CheckBox` (using `CheckBoxAdapterProvider`)

Typical binding scenario is that you want to bind simple bean property (lets say **name** property) with one of the widget listed above.
In our example we are binding name property with textbox.

http://acris.googlecode.com/svn/wiki/images/bindingsimple.PNG

Binding mechanism (using GWT generators) will create BeanWrapper which represents middle-point between BindingBean and TextBox. This BeanWrapper implements HasProperty interfaces which is main prerequisite for beans-binding (see <a href='http://code.google.com/p/gwt-beans-binding/'>GWT bean binding projects</a>).
BeanWrapper wraps existing bean and ensures that we can provide dynamic accessing of the properties via introspector. All property changes also fires propertyChange listeners. See IObservableObject for further details.

http://acris.googlecode.com/svn/wiki/images/bindingsimpledetailed.PNG

## One To Many binding ##

Provides binding property with list of the values represented by:
  * `ListBox` (using `ListBoxAutoAdapterProvider`)

Using on-to-many is very easy, but from implementation point of view, it is little bit more complicated. Preconditions are:
  * Load all possible values to the list box - this values are **`ProxyBeans`** and are provided by DataLoader concrete implementation
  * Indentify binding property in list of `ProxyBeans` objects and select it in the widget (e.g. `Listbox`) - see matching algorithm chapter
  * When selection value is changed (item in listbox is selected) matching proxy value has to be found and selected back to the binding bean

In our example we are binding Role object to the ListBox which contains all available Role oibjects (`ProxyBeans`). We are binding role through name property.

http://acris.googlecode.com/svn/wiki/images/bindingcomplex.PNG

As in the previous example, we are generating middle point between binding property and UI binding widget. In this case we are generating `BeanProxyWrapper`, which holds binding bean and `List` of `ProxyBeans`. Matching beans (via matching property) is provided transparently in setBoundPropertyValue and getBoundPropertyValue methods.

http://acris.googlecode.com/svn/wiki/images/bindingcomplexdetailed.PNG

### Data loaders ###

Data loader can be specified using BindingSpecLoader annotation.
```
@BindingField(User.ROLE + "." + Role.NAME)
@BindingSpecLoader(CustomDataLoader.class)
protected ListBox role = GWT.create(ListBox.class);
```

Data loader provides data to the x-to-many binding UI widget and have to implement `IAsyncDataLoader` interface and implement method:
```
void load(Page page, ICallback<PagedResult<T>> callback);
```

There are many posibilities how data can be obtained, using:
  * **GWT RPC**

```
import sk.seges.acris.rpc.CallbackAdapter;
import sk.seges.sesam.dao.IAsyncDataLoader;
import sk.seges.sesam.dao.ICallback;
import sk.seges.sesam.dao.Page;
import sk.seges.sesam.dao.PagedResult;

public class CompanyDataLoader implements IAsyncDataLoader<List<Company>> {
	
	private ICompanyServiceAsync companyService = GWT.create(ICompanyService.class);
   
        public CompanyDataLoader() {
	    //service initialization
	}

	@Override
	public void load(Page page, ICallback<PagedResult<List<Company>>> callback) {
		companyService .findAll(page, new CallbackAdapter<PagedResult<List<Company>>>(callback));
	}
}
```

  * **static defined data (data mocks)**

```
import sk.seges.acris.binding.samples.mocks.Company;
import sk.seges.sesam.dao.IAsyncDataLoader;
import sk.seges.sesam.dao.ICallback;
import sk.seges.sesam.dao.Page;
import sk.seges.sesam.dao.PagedResult;

public class CompanyDataLoader implements IAsyncDataLoader<List<Company>> {

	@Override
	public void load(Page page, ICallback<PagedResult<List<Company>>> callback) {
		PagedResult<List<Company>> pagedResult = new PagedResult<List<Company>>();
		
		List<Company> companies = new ArrayList<Company>();

		Company seges = new Company();
		seges.setName("Seges s.r.o.");
		companies.add(seges);

		Company zettaflops = new Company();
		zettaflops.setName("Zettaflops s.r.o");
		companies.add(zettaflops);

		pagedResult.setResult(companies);
		pagedResult.setPage(Page.ALL_RESULTS_PAGE);

		callback.onSuccess(pagedResult);
	}
}
```

### Adapter providers ###

When you are binding model with UI components, you have to register adapter providers in order to bound values to/from UI components. For example, if you are going to bind String value with TextBox, you have to register TextBoxBaseAdapterProvider which obtain value from TextBox component and set it into String value and also provides binding in another direction - get String value and sets it into UI component after each value change.

So, the manual registration should looks like this:
```
BeanAdapterFactory.addProvider(new TextFieldAdapterProvider());
```

You have also another posibility - register all available adapter providers in one step:
```
AdaptersRegistration registration = GWT.create(AdaptersRegistration.class);
registration.registerAllAdapters();
```

In this way, you don't have to register every adapter provider manually, but it should have a performance loss in case of many adapater providers.