## The "standard way" ##

Using @SupportedAnnotationTypes annotation _(used mainly to support all annotations using asterix "`*`")_

```
@SupportedAnnotationTypes("sk.seges.sesam.core.annotation.CustomAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SesamProcessor extends AbstractProcessor {
}

```

or using getSupportedAnnotationTypes method _(in order to have type safe code)_

```
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SesamProcessor extends AbstractProcessor {
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotations = new HashSet<String>();
		annotations.add(CustomAnnotation.class.getCanonicalName());
		return annotations;
	}
}
```

## Using configurers ##

```
public class SesamProcessorConfigurer extends DefaultProcessorConfigurer {

	@Override
	protected Type[] getConfigurationElement(DefaultConfigurationElement element) {
		switch (element) {
		case PROCESSING_ANNOTATIONS:
			return new Type[] {
				CustomAnnotation.class
			};
		}
		return new Type[] {};
	}

}

```

```
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SesamProcessor extends MutableAnnotationProcessor {
	@Override
	protected ProcessorConfigurer getConfigurer() {
		return new SesamProcessorConfigurer();
	}	
}

```