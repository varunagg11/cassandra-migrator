package com.scylladb.migrator

import cats.implicits._
<<<<<<< HEAD
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Paths }
import java.util

import com.typesafe.config.{ Config, ConfigFactory }
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.yaml._
import io.circe.yaml.syntax._
//import io.circe.parser._
//import play.api.libs.json._
=======
import com.datastax.spark.connector.rdd.partitioner.dht.{ BigIntToken, LongToken, Token }
import io.circe._
import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.yaml._
import io.circe.yaml.syntax._
>>>>>>> 7686a5e7e775b1c6965e8897c7299299c86f3021

case class MigratorConfig(source: SourceSettings,
                          target: TargetSettings,
                          preserveTimestamps: Boolean,
                          renames: List[Rename],
                          savepoints: Savepoints,
                          skipTokenRanges: Set[(Token[_], Token[_])]) {
  def render: String = this.asJson.asYaml.spaces2
}

object MigratorConfig {
  implicit val tokenEncoder: Encoder[Token[_]] = Encoder.instance {
    case LongToken(value)   => Json.obj("type" := "long", "value"   := value)
    case BigIntToken(value) => Json.obj("type" := "bigint", "value" := value)
  }

  implicit val tokenDecoder: Decoder[Token[_]] = Decoder.instance { cursor =>
    for {
      tpe <- cursor.get[String]("type").right
      result <- tpe match {
                 case "long"    => cursor.get[Long]("value").right.map(LongToken(_))
                 case "bigint"  => cursor.get[BigInt]("value").right.map(BigIntToken(_))
                 case otherwise => Left(DecodingFailure(s"Unknown token type '$otherwise'", Nil))
               }
    } yield result
  }

  implicit val migratorConfigDecoder: Decoder[MigratorConfig] = deriveDecoder[MigratorConfig]
  implicit val migratorConfigEncoder: Encoder[MigratorConfig] = deriveEncoder[MigratorConfig]

  def loadFrom(path: String): MigratorConfig = {
    val configData = scala.io.Source.fromFile(path).mkString

    io.circe.yaml.parser
      .parse(configData)
      .leftWiden[Error]
      .flatMap(_.as[MigratorConfig])
      .valueOr(throw _)
  }

  def load(config: Config): MigratorConfig =
//    val source = config.getConfig("source")
//    val target = config.getConfig("target")
//    val preserveTimestamps = config.getBoolean("preserveTimestamps")
//    val savepoints = config.getConfig("savepoints")
//    val renames = config.getConfig("renames")
//    val skipTokenRanges = config.getConfig("skipTokenRanges")
//
//    var migCong =  ConfigFactory.empty
//
//    migCong = migCong.withValue("source",source)
//    migCong = migCong.withValue("target",target)
//    migCong = migCong.withValue("preserveTimestamps",preserveTimestamps)
//    migCong = migCong.withValue("savepoints",savepoints)
//    migCong = migCong.withValue("renames",renames)
//    migCong = migCong.withValue("source",source)
//
//
//
//
//    migCong = migCong.withFallback(source)
//    migCong = migCong.withFallback(target)
//    migCong = migCong.withFallback(preserveTimestamps)
//    migCong = migCong.withFallback(savepoints)
//    migCong = migCong.withFallback(renames)
//    migCong = migCong.withFallback(skipTokenRanges)

//    val configs = config.toString.substring(26,config.toString.length-2)
//
//    val sourceConf = config.getConfig("source").toString.substring(26,config.getConfig("source").toString.length-2)
//    val targetConf = config.getConfig("target").toString.substring(26,config.getConfig("target").toString.length-2)
//    val savepointConf = config.getConfig("savepoints").toString.substring(26,config.getConfig("savepoints").toString.length-2)
//
//    val sourceSettings = io.circe.parser.parse(sourceConf)
//      .leftWiden[Error]
//      .flatMap(_.as[SourceSettings])
//      .valueOr(throw _)
//
//    val targetSettings = io.circe.parser.parse(targetConf)
//      .leftWiden[Error]
//      .flatMap(_.as[TargetSettings])
//      .valueOr(throw _)
//
//    val savepoints = io.circe.parser.parse(savepointConf)
//      .leftWiden[Error]
//      .flatMap(_.as[Savepoints])
//      .valueOr(throw _)
    //val json: JsValue = play.api.libs.json.Json.parse(configs)
    io.circe.parser
      .parse(config.getString("migratorConfig"))
      .leftWiden[Error]
      .flatMap(_.as[MigratorConfig])
      .valueOr(throw _)
}

case class Credentials(username: String, password: String)
object Credentials {
  implicit val encoder: Encoder[Credentials] = deriveEncoder
  implicit val decoder: Decoder[Credentials] = deriveDecoder
}

case class SourceSettings(host: String,
                          port: Int,
                          credentials: Option[Credentials],
                          keyspace: String,
                          table: String,
                          splitCount: Option[Int],
                          connections: Option[Int],
                          fetchSize: Int)
object SourceSettings {
  implicit val encoder: Encoder[SourceSettings] = deriveEncoder
  implicit val decoder: Decoder[SourceSettings] = deriveDecoder
}

case class TargetSettings(host: String,
                          port: Int,
                          credentials: Option[Credentials],
                          keyspace: String,
                          table: String,
                          connections: Option[Int])
object TargetSettings {
  implicit val encoder: Encoder[TargetSettings] = deriveEncoder
  implicit val decoder: Decoder[TargetSettings] = deriveDecoder
}

case class Rename(from: String, to: String)
object Rename {
  implicit val encoder: Encoder[Rename] = deriveEncoder
  implicit val decoder: Decoder[Rename] = deriveDecoder
}

case class Savepoints(intervalSeconds: Int, path: String)
object Savepoints {
  implicit val encoder: Encoder[Savepoints] = deriveEncoder
  implicit val decoder: Decoder[Savepoints] = deriveDecoder
}
