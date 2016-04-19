# Introduction #

Probably all of you know the chapter in GWT documentation about Declarative Layout with UiBinder. It describes a way how to bind individual components within a panel to its UI representation - written in a HTML-like markup language. It allows you to define the UI layout and compile it, so you don't have to do it in the code of the panel directly and are able to switch the UI anytime with different one.

But what if you need to switch the layout while the application is already running?

That is the point where Dynamic UI binder comes. It allows you to specify the binding information in HTML and GWT panel the same way you do in GWT's UiBinder. The only difference is that you don't need to have the layout template in that time. You can load it later using a service or read it/modify it in the code dynamically.

So let's take a look how you would do that.

# Showcase #

**For those impatient:** You can find a showcase of this in [acris-showcase-widgets](http://code.google.com/p/acris/source/browse/#svn%2Fbranches%2F1.1.0%2Facris-showcase%2Facris-showcase-widgets) project.

Let's suppose we are going to create a simple calculator of how many ships our space fleet has. We have to "leaders" responsible for their ships. So we will have two text boxes to enter the number and a button to calculate everything:

```
<div class="dyn-panel">
	<div ui:field="message" class="dyn-message">Status messages are shown here</div>
	<div class="fleet-container">
		<div ui:field="description" class="dyn-description"></div>
		<input type="text" ui:field="ashtarShips" />
		<input type="text" ui:field="ptahShips" />
		<button ui:field="recalculateFleet">Recalculate fleet</button>
	</div>
</div>
```

As you can see we will output status/error messages and have a link pointing to a description (you may wonder why it is a "div" tag, explanation will follow).

OK, let's construct the panel. We start with binder definition:

```

public class DynamicallyBoundPanel extends Composite {
        interface DynamicallyBoundPanelUiBinder extends DynamicUiBinder<Widget, DynamicallyBoundPanel> {}

        private static final DynamicallyBoundPanelUiBinder binder = GWT.create(DynamicallyBoundPanelUiBinder.class);

	...
}

```

As you can see, the syntax is similar to GWT's one, instead of **UiBinder** we use **DynamicUiBinder**.

And the fields will follow:

```
	@UiField
        protected Label message;

        // this is acris-widget Hyperlink because GWT's misses wrap method!
        @UiField
        protected Hyperlink description;

        @UiField
        protected TextBox ashtarShips;

        @UiField
        protected TextBox ptahShips;

        @UiField
        protected Button recalculateFleet;
```

Hmm, so now the only piece missing is the glue between the template and the binding:

```
	public DynamicallyBoundPanel() {
		// load the template e.g. from service ...
                String htmlTemplate = "<div class=\"dyn-panel\"><div ui:field=\"message\" class=\"dyn-message\">Status messages are shown here</div><div class=\"fleet-container\"><div ui:field=\"description\" class=\"dyn-description\"></div><input type=\"text\" ui:field=\"ashtarShips\" /><input type=\"text\" ui:field=\"ptahShips\" /><button ui:field=\"recalculateFleet\">Recalculate fleet</button</div></div>";
                binder.setViewTemplate(htmlTemplate);

                // known from GWT UiBinder - initialize
                initWidget(binder.createAndBindUi(this));
	
		...
	}
```

For the purposes of the explanation the HTML template is directly put into the variable "htmlTemplate". Usually you will read it from service.

Every tag that has to be matched to a field-widget in the panel must have corresponding name in the attribute **ui:field**. The value is the same as a name of the field.

The main difference between GWT's UiBinder and DynamicUiBinder is the template setup. Before you execute **`binder.createAndBindUi(this)`**, the binder needs to have it. You can consider the binder as a factory of panels, so consecutive calls to the pair of methods setViewTemplate/createAndBindUi can lead to different results (if the template changes). That way you can keep one such binder factory initialized and provide only the template based on businness logic requirements.

With bound fields you can perform ordinary GWT coding, e.g. setting the value of the message:

```
	message.setText("You accessed " + event.getValue() + ". Thank you for your interest in AcrIS. For more information, please visit http://acris.googlecode.com");
```

And that's it for the showcase...

# Advanced component wrapping #

In the showcase you can notice a specific **Hyperlink** class with a comment above:

```
        // this is acris-widget Hyperlink because GWT's misses wrap method!
        @UiField
        protected Hyperlink description;
```

And after closer examination it is indeed acris-widget's [Hyperlink](http://code.google.com/p/acris/source/browse/branches/1.1.0/acris-widgets/src/main/java/sk/seges/acris/widget/client/Hyperlink.java).

The Hyperlink is an example of how to extend existing widgets to be able to bind them with DynamicUiBinder.

The binder always reads type of a field and calls a `wrap(Element)` method on it. The element matches a tag in the template. Some widgets don't have `wrap` method, e.g. GWT's Hyperlink and therefore you need to provide it in the extended class.

There is also an alternative seen in UiBinder - use `@UiFactory` method to construct the widget for an element:

```
	@UiField
	protected MyWidget widget;

	// name of the method doesn't matter here, only return type
	@UiFactory
	protected MyWidget createMyMagicWidget(Element element) {
		...	
	}
```

DynamicUiBinder will use the `createMyMagicWidget` method instead of the default `wrap` one to construct the binding between `widget` and HTML tag counterpart.

Based on the observation all form-related widgets (Label, TextBox, Button,...) contain `wrap` method and in case you need to assign your own widget, you can find an inspiration of how to do that there.

And why the Hyperlink's counterpart is `div` tag? GWT's implementation by default is to wrap anchor tag into a div. Keep in mind that in order to change the text in Hyperlink you have to use the method `setText`