# Scala Implicit
_Ref: https://docs.scala-lang.org/tour/implicit-parameters.html_

_Ref: https://docs.scala-lang.org/overviews/core/implicit-classes.html_

Scala `implicits` are a powerful language feature that allow for flexible and concise code.
They can be used for a variety of purposes such as adding methods to existing classes, 
providing default values for function arguments, or automatically converting between types.

Java _does not_ have an exact equivalent to Scala `implicits`, 
but there are a few similar language features that can achieve some of the same goals. 
Here are a few examples:

**Static imports** - In Java, you can use static imports to bring methods and fields from a class into scope 
without having to prefix them with the class name. This can make code more concise and readable. 
For example, instead of writing `Math.sqrt(2)`, you can write `import static java.lang.Math.sqrt; sqrt(2)`.

**Default method implementations in interfaces** - Starting from Java 8, interfaces can have default method implementations.
This means that if a class implements an interface that has a default method,
it does not have to provide its own implementation of that method.
This can be useful for providing default behavior or adding methods to existing classes without modifying their source code.

**Type inference** - Java also has some limited type inference capabilities, which can help reduce boilerplate code. 
For example, if you declare a variable with the `var` keyword, Java will infer its type from the value assigned to it.
Similarly, if you use the diamond operator (`<>`) when creating a new instance of a generic class, Java can infer the type arguments based on the context.

While none of these features provide the same level of flexibility and expressiveness as Scala `implicits`,
they can still be used to write more concise and readable code in Java.
