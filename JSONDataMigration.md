# Introduction #

JSON data format is simple and still powerful. Nowadays you can encounter more and more web applications communicating using JSON format then couple of years ago. It is simple for a developer to read the format, it is effective for web browser to parse the format and there are databases using it as its primary data format.

But what happens when the data structure changes? You need to **migrate**.

And that is where **acris-json-migration** might help you!

# Maven integration #

```
		<dependency>
			<groupId>sk.seges.acris</groupId>
			<artifactId>acris-json-migration</artifactId>
			<version>1.1.3-SNAPSHOT</version>
		</dependency>

```

# Example situation #

Let's shed a light into it and assume we have a data like this:

```
{
	"firstName":"John"
	"secondName":"Doe"
	"street":"Over the rainbow"
	"streetNr":21
}
```

Such data can be represented by following Java domain object:
```
public class Person {
	String firstName;
	String secondName;
	String street;
	Integer streetNr;

	// ... and getters and setters...
}
```

Well, this seems like data about a person named John Doe. We stored it in database and you can clearly see, that _secondName_ is probably not the field name we really like to have. But a developer made a mistake and in second version of our domain model we are going to fix it:

```
{
	"firstName":"John"
	"surname":"Doe"
	"street":"Over the rainbow"
	"streetNr":21
}
```

Now you can see the point - thousands of data stored in the format defined by Person class in its version #1 but our program communicating in version #2 with changed _secondName_ to _surname_ in Person class. Clients can wonder why the don't see surnames, can't they? ;)

One thing to remember (for the following context) - the class Person changed and there is only Person class in version #2.

## Simple migration script ##

In this situation I would like to write a script:

```
public class PersonV1toV2Script extends JacksonTransformationScript<ObjectNode> {

	@Override
	public void process(ObjectNode node) {
		rename(node, "secondName", "surname");
	}

}
```

From the above example it is clear that the script will do the job. And you can do pretty anything with the whole tree of JSON data - adding new nodes, removing existing ones, transforming here and there - all thanks to [Jackson's tree model](http://wiki.fasterxml.com/JacksonTreeModel).

## How can I execute it? ##

There is a `Transformer` abstract class representing a transofmer responsible for passing JSON data to a script and writing it back.

Currently there are tow kinds of transformers:
  * [Jackson-based](http://jackson.codehaus.org/)
  * [JSONT-based](http://goessner.net/articles/jsont/)

Jackson-based is the preferred one and is more developed then JSONT-based.

So to execute a transformation on a data set you have to specify only two lines of code:

```
JacksonTransformer t = new JacksonTransformer(input, output);
t.transform(PersonV1toV2Script.class.getName());
```

... where _input_ and _output_ represent directories. In the input directory all files are treated as files containing JSON data and are transformed and written to the output directory. For a detailed test you can look into [TransformerTest in the project](http://code.google.com/p/acris/source/browse/branches/1.1.0/acris-json-migration/src/test/java/sk/seges/acris/json/server/migrate/TransformerTest.java).

## Example conclusion ##

The script's helper API is evolving and provides you with nice methods like **removeIfExists** or **addNonExistent** methods. We would like to hear about your use-cases which are not handled by **acris-json-migration** yet so the project can generally serve the purpose of JSON data migration.

# Details of the concept #

Questions to answer:
  * why not use field constants?
    * (A) because the constant might not be there in the future version and the script should reflect the state of transition in that time

TBD.

SchemaExporter - helper utility