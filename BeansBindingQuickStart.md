# Quick-start #

  * [showcase](http://acris.googlecode.com/svn/trunk/acris-deployer)
  * [Setup project](ProjectQuickStart.md)

The fast way:

  * Import required Maven artifacts:

```
<dependency>
	<groupId>sk.seges.acris</groupId>
	<artifactId>acris-binding</artifactId>
	<classifier>sources</classifier>
	<version>${acris-os.version}</version>
</dependency>
<dependency>
	<groupId>sk.seges.acris</groupId>
	<artifactId>acris-binding</artifactId>
	<version>${acris-os.version}</version>
</dependency>
```

  * inherit binding in your GWT module

```
<inherits name='sk.seges.acris.Binding' />
```

  * define generators for [bean wrappers](BeanWrappers.md)

```
	<generate-with class="sk.seges.acris.binding.rebind.bean.BeanWrapperGenerator">
		<any>
			<when-type-assignable class="sk.seges.acris.binding.client.wrappers.BeanWrapper"/>
	    </any>
	</generate-with>

	<!-- optional if you want to use bean validation also -->
	<generate-with
		class="sk.seges.acris.binding.rebind.binding.ValidatorDelegateGenerator">
		<any>
			<when-type-assignable class="sk.seges.acris.binding.client.wrappers.BeanWrapper"/>
		</any>
	</generate-with>
```

  * create a bean and a wrapper - [lazy coders](BeanWrappers.md) can use generator for that

```
	public class BlogPost {
	... fields, getters and setters ...
	}
	
	public interface BlogPostBeanWrapper
		extends
			sk.seges.acris.binding.client.wrappers.BeanWrapper<your.package.BlogPost> {
	}
```

  * bind it like in the [example](BeansBindingInExamplesPart1.md)