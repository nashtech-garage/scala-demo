package httpclient

import play.api.http.HttpVerbs._
import play.api.libs.json._
import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future}

abstract class AbstractHttpClient(ws: WSClient) (implicit ec: ExecutionContext) {

  protected def provideAuth: (String, String, WSAuthScheme)

  protected def provideRequestFilters: Seq[WSRequestFilter]

  /**
   * Do GET request
   *
   * @param url          The URL
   * @param params       The Parameters
   * @param extraHeaders The Extra Headers
   */
  def get(url: String,
          params: Seq[(String, String)],
          extraHeaders: Seq[(String, String)]): Future[WSResponse] =
    execute(url = url, method = GET, params = params, requestBody = None, extraHeaders = extraHeaders)

  /**
   * Do PUT request
   *
   * @param url          The URL
   * @param params       The Parameters
   * @param requestBody  The Request Body
   * @param extraHeaders The Extra Headers
   */
  def put(url: String,
          params: Seq[(String, String)],
          requestBody: Option[JsValue],
          extraHeaders: Seq[(String, String)]): Future[WSResponse] =
    execute(url = url, method = PUT, params = params, requestBody = requestBody, extraHeaders = extraHeaders)

  /**
   * Do POST request
   *
   * @param url          The URL
   * @param requestBody  The Request Body
   * @param params       The Parameters
   * @param extraHeaders The Extra Headers
   */
  def post(url: String,
           requestBody: Option[JsValue],
           params: Seq[(String, String)],
           extraHeaders: Seq[(String, String)]): Future[WSResponse] =
    execute(url = url, method = POST, params = params, requestBody = requestBody, extraHeaders = extraHeaders)

  /**
   * Do DELETE request
   *
   * @param url          The URL
   * @param params       The Parameters
   * @param extraHeaders The Extra Headers
   */
  def delete(url: String,
             params: Seq[(String, String)],
             extraHeaders: Seq[(String, String)]): Future[WSResponse] =
    execute(url = url, method = DELETE, params = params, requestBody = None, extraHeaders = extraHeaders)

  /**
   * Build WSRequest to perform
   *
   * @param url          The URL
   * @param method       The Method
   * @param params       The Request params
   * @param requestBody  The Request body
   * @param extraHeaders The Extra Headers
   */
  private def buildRequest(url: String,
                           method: String,
                           params: Seq[(String, String)],
                           requestBody: Option[JsValue],
                           extraHeaders: Seq[(String, String)]): WSRequest = {

    // Set up URL and request method
    var wsRequest = ws.url(url).withMethod(method)
    // Include request params?
    params.foreach(p => wsRequest = wsRequest.withQueryStringParameters(p))
    // Include request body?
    requestBody.foreach(body => wsRequest = wsRequest.withBody(body))
    // Include additional headers?
    extraHeaders.foreach(h => wsRequest = wsRequest.addHttpHeaders(h))

    val (username, password, authScheme) = provideAuth
    wsRequest = wsRequest.withAuth(username, password, authScheme)
    provideRequestFilters.foreach(filter => wsRequest = wsRequest.withRequestFilter(filter))
    wsRequest
  }

  private def execute(url: String,
                      method: String,
                      params: Seq[(String, String)],
                      requestBody: Option[JsValue],
                      extraHeaders: Seq[(String, String)]): Future[WSResponse] =
    buildRequest(url, method, params, requestBody, extraHeaders).execute()

}
