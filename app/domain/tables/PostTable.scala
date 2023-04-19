package domain.tables

import domain.models.Post
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime

class PostTable(tag: Tag) extends Table[Post](tag, Some("scalademo"), "posts") {

  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  /** The author column */
  def author = column[Long]("author")

  /** The title column */
  def title = column[String]("title")

  /** The content column */
  def content = column[String]("content")

  /** The date column */
  def date = column[LocalDateTime]("date")

  /** The description column */
  def description = column[Option[String]]("description")

  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the Post object.
   */
  def * = (id, author, title, content, date, description) <> ((Post.apply _).tupled, Post.unapply)
}
