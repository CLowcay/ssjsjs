## SSJSJS - A library for encoding and decoding Java objects as JSON

This package provides a simple annotation based API for encoding Java objects
as JSON, and decoding them in a safe and controlled way.

### Rationale

Modern JSON serialization frameworks for Java such as Google's Gson are
designed for maximum flexibility and convenience for the programmer.  However,
convenience is not always desirable.  A framework that can deserialize
arbitrary Java objects is essentially a mini scripting language embedded in
your application that could be misused in ways that are difficult to predict.

SSJSJS does not aim to serialize/deserialize arbitrary Java objects to JSON.
Nor does it require a formal schema.  Instead, the programmer adds annotations
to a regular Java class, and the SSJSJS library uses those annotations (through
reflection) to encode instances of the class to JSON, and to decode JSON objects
to instances of the class.

### Build

This library is modularized for Java 9, so make sure you have a Java 9 JDK
installed on the path.  Then use `./gradlew test` to build the library and run
the test suite.

Note that this repository uses git submodules, so make sure you update the
submodules before attempting a build.

### How to use it

To encode objects of a Java class to JSON, first implement the `JSONable`
interface.  This interface has no methods, it is used to create a compile
time distinction between classes that are intended to be encoded to JSON, and
classes that are not.  Next, create a constructor with a parameter for each
field that should appear in the JSON object.  This constructor is annotated with
the `@JSON` annotation.  The fields must be annotated with `@Field` annotations.
Each `@Field` annotation has a string parameter which specifies the name of the
field that supplies the values for this parameter.  Here's a simple example:

```Java
public class Example implements JSONable {
	private final int someField;

	@JSON
	public Example(
		@Field("someField") final int someField
	) {
		this.someField = someField;
	}
}
```

It can be encoded to JSON with something like this:

```Java
Example exampleObject = ...;
SSJSJS.encode(exampleObject);
```

We can also decode a JSON object to an instance of the Example class like so:

```Java
Example exampleObject = SSJSJS.decode(someJSON, Example.class);
```

#### Aliases

The JSON object will have an element for every `@Field` annotation in the
`@JSON` constructor.  The name of the element in the JSON object is usually the
same as the parameter to the `@Field` annotation (which is linked to the name of
an actual field in the Java class).  However, you can customize the name of the
element in the JSON object with the `@As` annotation:

```Java
public class Example implements JSONable {
	private final int someField;

	@JSON
	public Example(
		@Field("someField")@As("customFieldName") final int someField
	) {
		this.someField = someField;
	}
}
```

Now, when an instance of Example is encoded to JSON, it will have one element
called 'customFieldName' which will contain the value extracted from the
'someField' field of the Example class.  When this JSON object is decoded back
to a Java object, SSJSJS will match up the 'customFieldName' element with the
'someField' parameter of the `@JSON` constructor.

#### Optional fields

SSJSJS will throw an exception if the value of any of the annotated fields are
null.  During encoding, SSJSJS throws an exception if any of the annotated
fields are null.  During decoding, if any fields are missing from the input JSON
object, then SSJSJS throws an exception.  To handle values that may or may not
be present, there are two options.  The preferred option is to use Java's
Optional class:

```Java
public class Example implements JSONable {
	private final Optional<Integer> someField;

	@JSON
	public Example(
		@Field("someField") final Optional<Integer> someField
	) {
		this.someField = someField;
	}
}
```

When SSJSJS encodes a `Optional.empty` value, it simply omits that element from
the output JSON.  When decoding an `Optional` field, if the element is missing
from the input JSON then SSJSJS decodes it as `Optional.empty`.

If you really do require null values (not recommended) then you can mark fields
as `@Nullable`.  Then you get the same semantics as with `Optional` fields
except that `null` is substituted for `Optional.empty`.

#### Passing extra fields to a constructor

If a constructor requires additional information besides what is in the JSON
object you are trying to decode, then you can declare these fields with the
`@Implicit` annotation.  The `@Implicit` annotation has a single string
parameter which declares a lookup key for the parameter value.  The
`SSJSJS.decode` method can take an extra parameter of type `Map<String, Object>`
which maps keys to values for `@Implicit` parameters.  This mechanism cannot be
checked by the compiler so extreme caution should be exercised.  Use of
`@Implicit` fields should be minimized, and constants rather than literal
strings should be used for the keys.

### Encodable types

The follow types are valid for `@Field`s in `JSONable` objects:

* Java primitives
* Boxed versions of Java primitives (i.e. Integer, Float, Double, ...)
* String
* `enum` types
* Arrays of any of the types listed in this section, or any `JSONable` class
* The follow generic types: `Optional`, `Collection`, `List`, `Set`, `Map`.  The
	type parameter can be any of the types listed in this section, or any
	`JSONable` class

### Missing features are future work

1. Compile time annotation processing.  It would be nice to verify at compile
	 time that the annotations are used correctly.  For example, verifying that
	 all `JSONable` classes have a constructor with the `@JSON` annotation and
	 that all `@Field` annotations refer to an existing field.  Compile time
	 generation of encode/decode methods is also possible, but less important.
2. A mechanism for migration.  When you add a new field to a `JSONable` class,
	 you might still want to be able to decode objects from before the new field
	 was added.  There are various ways this might be acheived.

