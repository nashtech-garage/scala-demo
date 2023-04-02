# Dependency Injection
*Ref: https://www.playframework.com/documentation/2.8.x/ScalaDependencyInjection*

Dependency injection is a widely used design pattern that helps separate your components’ behaviour from dependency resolution.
Play supports both runtime dependency injection based on [JSR 330](https://jcp.org/en/jsr/detail?id=330) 
and [compile time dependency injection](https://www.playframework.com/documentation/2.8.x/ScalaCompileTimeDependencyInjection) in Scala.

Runtime dependency injection is so called because the dependency graph is created, wired and validated at runtime.
If a dependency cannot be found for a particular component, you won’t get an error until you run your application.

Play supports [Guice](https://github.com/google/guice) out of the box, but other `JSR 330` implementations can be plugged in.
The [Guice wiki](https://github.com/google/guice/wiki/) is a great resource for learning more about the features of Guice and DI design patterns in general.

**Note**: *Guice is a Java library and the examples in this documentation use Guice’s built-in Java API. If you prefer a Scala DSL you might wish to use the [scala-guice](https://github.com/codingwell/scala-guice) or [sse-guice](https://github.com/sptz45/sse-guice) library.*

# DI Benefits
Dependency injection achieves several goals:
1. It allows you to easily bind different implementations for the same component. This is useful especially for testing, where you can manually instantiate components using mock dependencies or inject an alternate implementation.
2. It allows you to avoid global static state. While static factories can achieve the first goal, you have to be careful to make sure your state is set up properly. In particular Play’s (now deprecated) static APIs require a running application, which makes testing less flexible. And having more than one instance available at a time makes it possible to run tests in parallel.
