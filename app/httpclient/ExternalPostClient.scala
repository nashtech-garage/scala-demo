package httpclient

import org.apache.commons.lang3.StringUtils
import play.api.libs.json.{JsResultException, JsValue, Reads}
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.{Configuration, Logging}
import play.api.libs.ws.{WSAuthScheme, WSClient, WSRequestFilter, WSResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import java.io.IOException

@Singleton
class ExternalPostClient @Inject()(ws: WSClient, config: Configuration)(implicit ec: ExecutionContext)
  extends AbstractHttpClient(ws) with Logging {

  def baseUrl: String = config.get[String]("external.posts.auth.basic.url")

  def username: String = config.get[String]("external.posts.auth.basic.username")

  def password: String = config.get[String]("external.posts.auth.basic.password")

  override protected def provideAuth: (String, String, WSAuthScheme) = (username, password, WSAuthScheme.BASIC)

  override protected def provideRequestFilters: Seq[WSRequestFilter] = Seq(AhcCurlRequestLogger())

  /**
   * Perform GET request
   *
   * @param apiPath          ApiPath of request
   * @param params           The Query parameters
   * @param extraHeaders     List of extra headers
   * @tparam A Return type
   * @return Future of A
   */
  def get[A <: Object](apiPath: String,
                       params: Seq[(String, String)] = Nil,
                       extraHeaders: Seq[(String, String)] = Nil)(implicit reads: Reads[A]): Future[A] =
      handleResponseEntity(super.get(buildRequestURL(apiPath), params, extraHeaders))

  /**
   * Perform PUT request
   *
   * @param apiPath          ApiPath of request
   * @param requestBody      The object, will be mapped to JSON according to the implicit writes (optional)
   * @param params           The Query parameters
   * @param extraHeaders     List of extra headers
   * @tparam A Return type
   * @return Future of A
   */
  def put[A <: Object](apiPath: String,
                       requestBody: Option[JsValue] = None,
                       params: Seq[(String, String)] = Nil,
                       extraHeaders: Seq[(String, String)] = Nil)(implicit reads: Reads[A]): Future[A] =
      handleResponseEntity(super.put(buildRequestURL(apiPath), params, requestBody, extraHeaders))

  /**
   * Perform POST request
   *
   * @param apiPath          ApiPath of request
   * @param requestBody      The object, will be mapped to JSON according to the implicit writes (optional)
   * @param params           The Query parameters
   * @param extraHeaders     List of extra headers
   * @tparam A Return type
   * @return Future of A
   */
  def post[A <: Object](apiPath: String,
                        requestBody: Option[JsValue] = None,
                        params: Seq[(String, String)] = Nil,
                        extraHeaders: Seq[(String, String)] = Nil)(implicit reads: Reads[A]): Future[A] =
    handleResponseEntity(super.post(buildRequestURL(apiPath), requestBody, params, extraHeaders))

  /**
   * Perform DELETE request
   *
   * @param apiPath          ApiPath of request
   * @param params           The Query parameters
   * @param extraHeaders     List of extra headers
   * @tparam A Return type
   * @return Future of A
   */
  def delete[A <: Object](apiPath: String,
                          params: Seq[(String, String)] = Nil,
                          extraHeaders: Seq[(String, String)] = Nil)(implicit reads: Reads[A]): Future[A] =
    handleResponseEntity(super.delete(buildRequestURL(apiPath), params, extraHeaders))

  private def handleResponseEntity[A](response: Future[WSResponse])(implicit reads: Reads[A]): Future[A] = {
    response.map { response =>

      // application level error
      if (response.status >= 400) {
        throw new ExternalServiceException(response.status,
          "External Service Exception",
          Option(response.body[JsValue].as[ErrorResponse])
        )
      } else {
        response.body[JsValue].as[A]
      }
    } transform {
      case Success(value) => Try(value)
      case Failure(exception) => throw handleError(exception)
    }
  }

  private def handleError(exception: Throwable): Throwable = exception match {
    case exception: ExternalServiceException =>
      logger.error(s"ExternalServiceException statusCode: ${exception.statusCode} message: ${exception.message}")
      exception
    case parsingError: JsResultException =>
      logger.error(s"Unable to parse response body: $parsingError")
      new ExternalServiceException(500, "JsonParser error occurred", None, Some(parsingError))
    case networkingError: IOException =>
      logger.error(s"Networking error: $networkingError")
      new ExternalServiceException(500, "Networking error occurred", None, Some(networkingError))
    case _ =>
      logger.error("ex: ", exception)
      logger.error(s"External Service Exception: ${exception.getMessage}")
      new ExternalServiceException(500, "Other exception occurred", None, Some(exception))
  }

  private def buildRequestURL(apiPath: String): String =
    StringUtils.removeEnd(baseUrl, "/")
      .concat("/")
      .concat(StringUtils.removeStart(apiPath, "/"))
}
