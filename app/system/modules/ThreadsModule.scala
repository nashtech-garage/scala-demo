package system.modules

import akka.actor.ActorSystem
import com.google.inject.name.Named
import com.google.inject.{AbstractModule, Provides}
import net.codingwell.scalaguice.ScalaModule

import javax.inject.Singleton
import scala.concurrent.ExecutionContext

class ThreadsModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {}

//  @Provides
//  @Singleton
//  @Named("dbExContext")
//  def dbExContext(akka: ActorSystem): ExecutionContext = akka.dispatchers.lookup("threads.db.dispatcher")
}
