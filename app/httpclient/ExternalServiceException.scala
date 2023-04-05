package httpclient

class ExternalServiceException(val statusCode: Int,
                               val message: String = "",
                               val error: Option[ErrorResponse] = None,
                               val exception: Option[Throwable] = None)
  extends Exception(message) {
}
