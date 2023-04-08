package fixtures

import net.codingwell.scalaguice.ScalaModule
import play.api.db.evolutions.EvolutionsModule
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Application, Configuration, Environment}

object TestApplication {

  def config(file: String, environment: Environment): Configuration =
    Configuration.load(environment, Map("config.file" -> file))

  def app(testConfig: String = "test/resources/application.test.conf",
          additionalConfiguration: Map[String, Any] = Map.empty): Application =
    GuiceApplicationBuilder(loadConfiguration = e => config(testConfig, e))
      .in(Environment.simple())
      .configure(additionalConfiguration)
      .build()

  def appWithOverridesModule(module: ScalaModule,
                             testConfig: String = "test/resources/application.test.conf",
                             additionalConfiguration: Map[String, Any] = Map.empty): Application =
    GuiceApplicationBuilder(loadConfiguration = e => config(testConfig, e))
      .in(Environment.simple())
      .configure(additionalConfiguration)
      .disable[EvolutionsModule]
      .overrides(module)
      .build()
}
