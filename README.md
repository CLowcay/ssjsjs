## SSJSJS - Super Simple JSON Serialization for JAVA

This package provides a simple annotation based API for serializing JAVA objects to JSON, and deserializing them in a safe and controlled way.

### Rationale

Modern JSON serialization frameworks for JAVA such as Google's Gson are designed for maximum flexibility and convenience for the programmer.  However, convenience is not always desirable.  A framework that can deserialize arbitrary JAVA objects is essentially a mini scripting language embedded in your application that could be misused in ways that are difficult to predict.

SSJSJS is a different point in the design space for JSON serialization libraries.  It very intentionally does NOT attempt to serialize/deserialize arbitrary JAVA objects.  Objects are required to meet several conditions before they can be serialized/deserialized.  In particular they must have an annotated constructor which doubles as a kind of informal schema.  The deserializer will call this constructor and no other methods.  So long as the constructor is written in a safe manner (i.e. without any side effects or any possibility of side effects) then the deserialization will be safe.
