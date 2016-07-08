package test

import collection.JavaConversions._
import com.typesafe.config.{ConfigResolveOptions, ConfigFactory, Config}
import java.net.URL

/**
 * Copyright 2013 Tomasz Grobel
 * tgrobel@gmail.com
 * twitter: @tgrobel
 */
trait TestConfiguration {

  val TestConfigurationResource: String = "/test.conf"
  val MongoUriKey: String = "mongodb.uri"
  val MongoDatabaseNameKey: String = "mongodb.db"

  lazy val configuration: Map[String, _] = {
    val config: Config = ConfigFactory.parseURL(configFile).resolve(ConfigResolveOptions.defaults())
    checkDbConfiguration(config)
    config.entrySet().map(e => (e.getKey, e.getValue.unwrapped)).toMap
  }

  private def checkDbConfiguration(config: Config) {
    val databaseConfigurationOverwrittenForTests: Boolean = {
      val wholeDatabaseUriOverwritten: Boolean = config.hasPath(MongoUriKey)
      val databaseNameOverwritten: Boolean = config.hasPath(MongoDatabaseNameKey)
      wholeDatabaseUriOverwritten || databaseNameOverwritten
    }
    if (!databaseConfigurationOverwrittenForTests) {
      throw new IllegalStateException(s"Configuration of test mongodb is missing. Please provide <mongodb.db> or <mongodb.uri> keys in test configuration '$TestConfigurationResource' file!")
    }
  }

  private def configFile: URL = {
    val resource: URL = this.getClass.getResource(TestConfigurationResource)
    Option(resource).getOrElse {
      throw new IllegalStateException(s"Test configuration file '$TestConfigurationResource' is missing in classpath!")
    }
  }

}
