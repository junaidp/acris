# Sesam projects refactoring #
  * sesam-annotations-support renamed into sesam-annotation-core-support
  * sesam-model-metadata renamed into sesam-model-metadata-processor

# New world of PAP #

**!!WARNING!!** Old API was immutable (all setters returned a new instance), new API is completly mutable and no new instances are created. Use clone method if you want to create a new cloned instance!!

  * ~~sk.seges.sesam.core.pap.model.api.HasTypeParameters~~ is replaced by sk.seges.sesam.core.pap.model.mutable.api.MutableDeclaredType
  * ~~sk.seges.sesam.core.pap.builder.NameTypeUtils~~ is replace by sk.seges.sesam.core.pap.model.mutable.utils.MutableTypes and can be obtained using sk.seges.sesam.core.pap.model.mutable.utils.MutableProcessingEnvironment that is hold by sk.seges.sesam.core.pap.processor.MutableAnnotationProcessor (so, your processor should extends this class)
  * ~~sk.seges.sesam.core.pap.model.api.TypeParameter~~ replaced by sk.seges.sesam.core.pap.model.mutable.api.MutableTypeVariable or sk.seges.sesam.core.pap.model.mutable.api.MutableWildcardType, depends which type variable you want to use
  * ~~sk.seges.sesam.core.pap.model.TypedClassBuilder~~ removed, now the mutable types are created using sk.seges.sesam.core.pap.model.mutable.utils.MutableTypes using methods getDeclaredType, etc.
  * ~~sk.seges.sesam.core.pap.AbstractConfigurableProcessor~~ is no more exist - it was replace by multiple abstract processors you can extend:
    * sk.seges.sesam.core.pap.processor.PlugableAnnotationProcessor - very basic processor with output formatting purposes
    * sk.seges.sesam.core.pap.processor.ConfigurableAnnotationProcessor - annotation processor that can be configured using configurers and delegated configurers
    * sk.seges.sesam.core.pap.processor.MutableAnnotationProcessor- annotation processor that can manipulate with mutable types and offers you advanced features
  * method ~~protected Type[.md](.md) getOutputDefinition(OutputDefinition type, TypeElement typeElement)~~ from AbstractConfigurableProcessor was replaced by protected MutableDeclaredType[.md](.md) getOutputClasses(RoundContext context) in the MutableAnnotationProcessor - in this method you should specify whole output class with all the implemented interfaces, element kind and/or superclass. You should define interfaces implemented by output class directly in the MutableDeclaredType using setInterfaces method
  * method ~~protected NamedType[.md](.md) getTargetClassNames(ImmutableType immutableType)~~ from AbstractConfigurableProcessor was removed and replaced by getOutputClasses (see paragraph above)
  * method ~~protected void processElement(TypeElement element, NamedType outputName, RoundEnvironment roundEnv, FormattedPrintWriter pw)~~ from AbstractConfigurableProcessor was changed into: protected void processElement(ProcessorContext context). Method arguments now can be obtained in the following way:
    * TypeElement element = context.getTypeElement();
    * ~~NamedType~~ MutableDeclaredType outputName = context.getOutputType();
    * FormattedPrintWriter  pw = context.getPrintWriter()
    * RoundEnvironment roundEnv is local class field of the MutableAnnotationProcessor, so: this.roundEnv
  * ~~sk.seges.sesam.core.pap.model.api.ArrayNamedType~~ renamed into sk.seges.sesam.core.pap.model.mutable.api.MutableArrayType
  * ~~sk.seges.sesam.core.pap.model.api.TypeVariable~~ was removed and has no replacement in the new API (this class is no more required)
  * you can't now override protected boolean processElement(Element element, RoundEnvironment roundEnv) method in AbstractConfigurableProcessor (this case was very common in order to check if annotation processor should be executed on the element or not) -> for these purposes was protected boolean checkPreconditions(ProcessorContext context, boolean alreadyExists) introduced
  * method ~~protected void writeClassAnnotations(Element el, NamedType outputName, PrintWriter pw)~~ from ~~AbstractConfigurableProcessor~~ changed into protected void printAnnotations(ProcessorContext context). Method arguments can be obtained from the context itself:
    * Element el = context.getTypeElement();
    * ~~NamedType~~ MutableDeclaredType outputName = context.getMutableType();
    * FormattedPrintWriter pw = context.getPrintWriter()
  * getConfigurationElement in the class sk.seges.sesam.core.pap.configuration.DefaultProcessorConfigurer is now abstract so you have implement this method in your processor configurer. Also can't call return super.getConfigurationElement now, istead of that use return new Type[.md](.md) {};
  * method ~~protected ElementKind getElementKind()~~ from ~~AbstractConfigurableProcessor~~ was removed. Now you should define kind of the output type directly in the MutableDeclaredType using setKind method
  * ~~protected boolean isSupportedAnnotation(AnnotationMirror annotationMirror)~~ method is removed from ~~AbstractConfigurableProcessor~~. Use protected boolean checkPreconditions(ProcessorContext context, boolean alreadyExists) instead and get annotation from type using context.getTypeElement().getAnnotation(XYZ.class)

## Migrate branches to trunk ##

For your working copy follow this procedure:
  * if you have local modifications, **backup** the version before doing a switch !!!
  * go to sesam branch 1.1.0 and execute `svn switch https://acris.googlecode.com/svn/sesam/trunk`
  * go to corpis branch 1.1.0 and execute `svn switch https://acris.googlecode.com/svn/corpis/trunk`
  * go to acris branch 1.1.0 and execute `svn switch https://acris.googlecode.com/svn/trunk`