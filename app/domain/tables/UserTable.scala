package domain.tables

import domain.models.User
import slick.jdbc.PostgresProfile.api._

class UserTable(tag: Tag) extends Table[User](tag, Some("scalademo"), "users") {

  /** The ID column, which is the primary key, and auto incremented */
  def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc, O.Unique)

  /** The name column */
  def name = column[String]("name")

  /** The email column */
  def email = column[String]("email", O.Unique)

  /** The role column */
  def role = column[String]("role")

  /** The last name column */
  def lastName = column[String]("lastname")

  /** The password column */
  def password = column[Option[String]]("password")

  /**
   * This is the table's default "projection".
   * It defines how the columns are converted to and from the User object.
   * In this case, we are simply passing the id, name, email and password parameters to the User case classes
   * apply and unapply methods.
   */
  def * = (id, email, role, name, lastName, password) <> ((User.apply _).tupled, User.unapply)
}
