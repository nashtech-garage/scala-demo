package system.modules

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import play.api.{Configuration, Environment}

import javax.inject._

/**
 * Sets up custom components for Play.
 * By default, Play will load any class called modules.Module that is defined in the root package (the “app” directory)
 * or you can define them explicitly inside the reference.conf or the application.conf:
 * play.modules.enabled += "modules.MyModule"
 *
 * https://www.playframework.com/documentation/latest/ScalaDependencyInjection
 * https://www.javadoc.io/doc/net.codingwell/scala-guice_2.11/4.2.7/net/codingwell/scalaguice/InternalModule.html
 */
class AppModule(environment: Environment, configuration: Configuration)
  extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
//    bind[PostRepository].annotatedWith(Names.named("postRepository")).to[PostRepositoryImpl].in[Singleton]()
  }
}
