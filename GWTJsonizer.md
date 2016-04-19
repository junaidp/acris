# Introduction #

### Related artifacts ###

```
<groupId>sk.seges.acris</groupId>
<artifactId>acris-json</artifactId>
```

# Project motivation #

Firstly, let's talk in a few sentences about the motivation for creating this project and about core AcrIS ideas behind this project. Those, who is motivated enough, can skip this short intro and continue with reading the next chapter.
I'm not going to talk about what [JSON](http://www.json.org/) is but in a short sentence JSON are data in specific format like this:
```
{
  'feed': {
    'entry': [
      {'title': {'type': 'text', '$t': 'Some Text'}},
      {'title': {'type': 'text', '$t': 'Some More Text'}}
    ]
  }
}
```
Now, if you are GWT developer (and if you are not, then go away :) ) you probably want to deserialize JSON data representation with piece of code like this (I did not try to compile it, but it should be in this way):
Firstly we should define some data structure for holding the data.
```
public class Feed {

       public static class Entry {

              public static Class TextConstruct {
                     public String type;
                     public String value;
              }

              public TextConstruct title;
       }

       public List<Entry> entries;
}
```

```
JSONValue value = JSONParser.parse(jsonString);
if (value.isObject() != null) {
    JSONValue feed = value.isObject().get("feed");

    if (feed == null) {
        //Invalid JSON string
        return null;
    }

    Feed feed = new Feed();

    JSONValue entries = feed.get("entry");

    if (entries.isArray() != null) {
        //Invalid JSON string
        return null;
    }

    feed.entries = new ArrayList<TextConstruct>();
    for (int i = 0; i < entries.isArray().size(); i++) {
         JSONValue titleTextConstruct = entries.isArray().get(i).get("title");
         if (titleTextConstruct == null || titleTextConstruct.isObject() == null) {
             //Invalid JSON string
             return null;
         }
         JSONValue titleValue = titleTextConstruct.isObject().get("$t");

         if (titleValue == null || titleValue.isObject() == null) {
             return null;
         }

         TextConstruct textConstruct = new TextConstruct();
         JSONValue title = titleValue.isObject().get("$t");

         if (title == null || title.isString() == null) {
             return null;
         }
         textConstruct.value = title.isString().stringValue();
         feed.entries.add(textConstruct);
    }

    return result;
}

return null;
```

Pretty easy, isn't it ? :) Just "few" lines of code which are quite a lot of errorprone and we are completly in conflict with DRY & KISS principles. Lets try to make it little bit easier and more maintenable. In a first step, add JSON annotation into a data structure in order to define mapping fields.

```
@JsonObject
public class Feed {

       @JsonObject
       public static class Entry {

              @JsonObject
              public static Class TextConstruct {
                     @Field       public String type;
                     @Field("$t") public String value;
              }

              @Field
              public TextConstruct title;
       }

       @Field("entry")
       public List<Entry> entries;
}
```

Now create jsonizer for deserialize json data and fill-up the required data instance. You don't have to create class instances in your own, but you have to keep in mind that your POJOs have to be defaultly instantiated or you have to provide custom POJO instantiator in order to create new class instance.

```
IJsonizer jsonnizer = new JsonizerBuilder().create();
Feed feed = jsonnizer.fromJson(jsonString, "feed", Feed.class);
```

You can compare the result of both approaches but compare it with respect that we have really easy data structure but we were able produce much less lines of code with higher maintenability just with adding annotations and using AcrIS jsonizer. Still don't sleep or not gone away? :) So then, continue reading or just play little bit with a <a href='http://acris-gwt.appspot.com/sk.seges.acris.demo.Json/Json.html'>JSON demo</a> deployed on appengine.

## POJO annotation ##

GWT jsonization process will take into account only classes with `@JSONObject` annotation (because of many reasons :) and one of the most important is missing reflection in JS/GWT so jsonizer have to preprocess classes using GWT generators). So if you want to define POJO which will be automatically deserialized just annotate it with this annotation.
```
@JsonObject
public class Feed {
       ///standard java bean with properties
}
```

Jsonization process will automatically recognize type of the field/property, find the appropriate setter, will instantiate the instance but in a specific situations you have to specify a way how will be data deserialized:
  * every json-aware field should have @Field annotation
  * when you want to deserialize DateTime object, just specify the pattern, using @DateTimePattern("y-M-d'T'H:m:s.SSSZ") annotation
  * when you want to deserialize Number object, just specify the pattern, using @NumberPatter("00,0###") pattern

```
@JsonObject
public class SourceState {

	@Field
	public String id;

	@Field
	@DateTimePattern("y-M-d'T'H:m:s.SSSZ")
	public DateTime updated;

	@Field
	public Long itemsCount;

	@Field
        @NumberPatter("00,0##")
	public int age;
}
```


## Project setup ##

In order to reach complex code generator capabilities, you have to setup [Annotation processors aka JSR 269](http://code.google.com/p/acris/wiki/AnnotationProcessing) properly. Annotation processors will generate code from @JSONObject annotation, so you don't have to write it. Of course, you have also another possibility - to write the code by your hand.

For example annotation processor will generate following code for SourceState class.

```
package ...;

public interface SourceStateJsonizer extends sk.seges.acris.json.client.data.IJsonObject<SourceState> {
}
```

Class wiil be located in the same package as original class in the /target/generated directory. So, if you don't want to use annotation processor, just write this class by yourself.

## Custom deserializer ##

Many deserializers are registered by default in json builder and are automatically provided to the jsonization process. Of course, you can define your own deserializer in case when you cannot add @JSONObject annotation to the specific objects.

Let's see the example and imagine that we have an Source object and we cannot add an annotation here. Source object has SourceState (it should be interface, class, whatever) field/property.

```
public class Source  {

       public ISourceState sourceState;
}

@JsonObject
public class SourceState implements ISourceState {

       @Filed
       public String state;
}
```

Source class cannot be deserialized because it is not a JSON object so we can create deserializer

```
public class SourceDeserializer extends JsonDeserializer<Source, JSONValue> {

	@Override
	public Source deserialize(JSONValue s, DeserializationContext context) {
                Source source = new Source();
                if (s == null || s.isObject() == null) {
                    return source;
                }

		source.sourceState = new SourceState();

                JSonObject jsonObject = s.isObject();
		IJsonizer sourceStateJsonizer = context.getJsonizer();
		sourceStateJsonizer.fromJson(jsonObject.get("source"), source.sourceState);
		return source;
	}
}
```

Now you have to register your custom deserializer into Jsonizer builder in this way.

```
JsonizerBuilder jsonizerBuilder = new JsonizerBuilder();
jsonizerBuilder.registerDeserializer(Source.class, new SourceDeserializer());
IJsonizer jsonnizer = jsonizerBuilder.create();
jsonnizer.fromJson(...);
```

## Custom instantiators ##

So far, so good, hopefully :) we can deserialize json data into object automatically, or we can help to the deserialization process with some custom deserializators, but what if the target class is not defaultly instantiable? The jsonizer is not able to create an isntance of the target field and will leave the field empty. This is common case for:
  * lists or sets ... or in one word collections
  * maps
  * objects created using factories or builders ...

So if you have a class with list of entries, like in Feed class was, then instantiator have to create an instance of the collection.

```
@JsonObject
public class Feed {

       @JsonObject
       public static class Entry {
             //some not interesting stuff
       }

       @Field("entry")
       public List<Entry> entries;
}
```

Fortunatelly, acris-json has set of instantiators (ufff), for example ListInstanceCreator.
```
public class ListInstanceCreator implements InstanceCreator<List<?>> {

	@Override
	public List<?> createInstance(Class<List<?>> type) {
		return new ArrayList<Object>();
	}

}
```

This will create an ArrayList as a reference instance for List. So if you want to create different instance for Lists just create your own instance creator. And do NOT forget to register it in jsonizer builder.

```
JsonizerBuilder jsonizerBuilder = new JsonizerBuilder();
jsonizerBuilder.registerInstanceCreator(List.class, new ListInstanceCreator());
IJsonizer jsonnizer = jsonizerBuilder.create();
jsonnizer.fromJson(...);
```

Cool ... now we are much closer from manual robotic-like programmer to the clever programmer who writes nice code in easy and efficent way. Just imagine that you want to deserialize Google API JSON data with hundreds of data structures in manual way? Huh, sounds interesting? I believe that someone will do it or just did it, but I don't ... I will use acris-json project and it will save me a thoundsands of lines and when I will modify my code after one year I will be able to maintain it without breaking the existing functionality. What about you?

## Handling complex data structures into simple fields ##

Sometimes it's usefull to deserialize JSON string into simpliers data structures than original JSON data structures. Sounds little bit confusing but it's not. If you have JSON input from our example above and if you are not interested in whole Title (represents as TextConstruct) object but you just need a value of the title you can simplify your data structures like this.
```
{
  'feed': {
    'entry': [
      {'title': {'type': 'text', '$t': 'Some Text'}},
      {'title': {'type': 'text', '$t': 'Some More Text'}}
    ]
  }
}
```
```
@JsonObject
public class Feed {

       @JsonObject
       public static class Entry {

              @ComplexField({
                         @Field("title"), 
                         @Field("$t")})
              public String value
       }

       @Field("entry")
       public List<Entry> entries;
}
```

You see the difference? ComplexField groups more simple fields and maps $t value from title object into String field value; In other words it can be translated like:
```
value = jsonEntryObject.get("title").get("$t");
```

ComplexField allows you to group more that 2 @Fields so you can simplify your data structures in case you don't need to map full data structure as it is defined.

## Extensible points & extensions ##

Still haven't enough information about using acris-json project? You will get more and more but not now, it's friday and I'm going to be little bit thirsty :)