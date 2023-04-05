package domain.models

import play.api.libs.json.{Json, OFormat}

import java.time.LocalDateTime

case class Post (id: Option[Long],
                 author: Long,
                 title: String,
                 content: String,
                 date: LocalDateTime = LocalDateTime.now(),
                 description: Option[String] = None)

object Post {

  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[Post] = Json.format[Post]
}


