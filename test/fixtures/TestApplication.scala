package fixtures

import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration, Environment}

object TestApplication {

  def config(file: String, environment: Environment): Configuration =
    Configuration.load(environment, Map("config.file" -> file))

  def app(additionalConfiguration: Map[String, Any] = Map.empty): Application =
    GuiceApplicationBuilder(loadConfiguration = e => config("test/resources/application.test.conf", e))
      .in(Environment.simple())
      .configure(additionalConfiguration)
      .build()
}
