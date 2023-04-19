package httpclient

import play.api.libs.json.{Json, OFormat}

case class ErrorMessage(message: String)

object ErrorMessage {
  implicit val errorMessageFormat: OFormat[ErrorMessage] = Json.format[ErrorMessage]
}

case class ErrorResponse(error: List[ErrorMessage])

object ErrorResponse {
  implicit val errorResponseFormat: OFormat[ErrorResponse] = Json.format[ErrorResponse]
}
