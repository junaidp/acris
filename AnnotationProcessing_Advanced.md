Collect the common PAP cases here:

## Obtain all fields defined in the element ##
```
TypeElement typeElement = ...;

for (TypeMirror field: ElementFilter.fieldsIn(type.getEnclosedElements()) {
    //... process field
}

```

## Converting type to the mutable type ##

```
MutableProcessingEnvironment processingEnv = ...; //defined in the MutableAnnotationProcessor
TypeMirror type = ...;
MutableTypeMirror mutableType = processingEnv.getTypeUtils().toMutable(type);
```

## Specifying super class of the generated class ##

```
MutableProcessingEnvironment processingEnv = ...;
MutableTypeMirror mutableType = ...;

mutableType.setSuperClass(processingEnv.getTypeUtils().toMutable(BufferedReader.class));
```

## Specifying types super class of the generated class ##

```
MutableProcessingEnvironment processingEnv = ...;
MutableTypeMirror mutableType = ...;

mutableType.setSuperClass(processingEnv.getTypeUtils().toMutable(List.class));
```