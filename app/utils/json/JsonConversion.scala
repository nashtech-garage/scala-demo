package utils.json

import play.api.libs.json.{Format, JsString, JsValue}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object JsonConversion {

  val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS"
  val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat)

  implicit val formatsLocalDateTime: Format[LocalDateTime] = Format(
    (json: JsValue) => json.validate[String].map(dtString => LocalDateTime.parse(dtString, timeFormatter)),
    (date: LocalDateTime) => JsString(date.toString)
  )
}
