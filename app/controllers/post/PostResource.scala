package controllers.post

import domain.models.Post
import play.api.libs.json.{Format, Json, OFormat}

import java.time.LocalDateTime

/**
 * DTO for displaying post information.
 */
case class PostResource(id: Long,
                        author: Long,
                        title: String,
                        content: String,
                        description: Option[String],
                        createdDate: LocalDateTime)

object PostResource {

  /**
   * Mapping to read/write a PostResource out as a JSON value.
   */
  implicit val format: OFormat[PostResource] = Json.format[PostResource]

  def fromPost(post: Post): PostResource =
    PostResource(post.id.getOrElse(-1), post.author, post.title, post.content, post.description, post.date)
}
