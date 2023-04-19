# Action composition
_Ref: https://www.playframework.com/documentation/2.8.x/ScalaActionsComposition_

## Scala + Play FW

In the context of the Play Framework, a **Scala Action** is a function that processes a request and generates a response.
**Actions** are the building blocks of a Play application and can be used to handle incoming requests from clients.

In the Play Framework, an **Action** is defined as a method that takes a request object as input and returns a result object as output.
The result object can be any of the following:

* A plain text string
* A HTML page
* A JSON object
* A file
* A redirect
* etc.

**Actions** can also be composed together to create more complex behavior, using methods such as `andThen` or `compose`. 
For example, you can create a filter that performs some authentication or authorization check before executing the action.

Here's an example of a basic Scala action in Play Framework:

```scala
import play.api.mvc.Results.Ok
import play.api.mvc.Action

def hello(name: String) = Action {
Ok("Hello " + name)
}
```
In this example, the `hello` method takes a `String` argument name and returns an `Action`
that generates a plain text response with the message `"Hello "` followed by the value of `name`.

#### Here are a few more complex examples of Scala Actions in Play Framework:

1. Action with authentication and authorization:
```scala
import play.api.mvc.Results.{Forbidden, Unauthorized}
import play.api.mvc._

import scala.concurrent.Future

object AuthenticatedAction extends ActionBuilder[Request, AnyContent] {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    val maybeUsername = request.session.get("username")
    maybeUsername.map { username =>
      if (isAuthorized(username)) {
        block(request)
      } else {
        Future.successful(Forbidden("You are not authorized to access this resource"))
      }
    }.getOrElse {
      Future.successful(Unauthorized("You must be logged in to access this resource"))
    }
  }

  private def isAuthorized(username: String): Boolean = {
    // Check if the user is authorized to access the resource
    true
  }
}
```

In this example, we define an `AuthenticatedAction` that requires authentication and authorization for accessing a resource.
The `invokeBlock` method checks if the session contains a `username` and whether that user is authorized to access the resource.
If the user is not authorized, we return a `Forbidden` response. If the user is not logged in, we return an `Unauthorized` response.
Otherwise, we pass the request to the underlying block.

2. Action with JSON parsing and validation:
```scala
import play.api.mvc.Results.Ok
import play.api.libs.json._
import play.api.mvc.Results.BadRequest
import play.api.mvc._
import play.mvc.Controller

import scala.concurrent.Future

case class User(id: Int, name: String, email: String)

object UserController extends Controller {
  def createUser: Action[JsValue] = Action(parse.json).async { request =>
    val userResult = request.body.validate[User]
    userResult.fold(
      errors => {
        Future.successful(BadRequest(Json.obj("status" -> "error", "message" -> JsError.toJson(errors))))
      },
      user => {
        // Save the user to the database
        Future.successful(Ok(Json.obj("status" -> "success", "message" -> "User created")))
      }
    )
  }
}
```
In this example, we define a `createUser` action that parses and validates a JSON request body containing user data.
We use the `parse.json` body parser to parse the request body as JSON, and the `validate` method of the `JsValue` object
to validate the JSON against a case class representing the user data.
If the validation fails, we return a `BadRequest` response with the validation errors.
If the validation succeeds, we save the user to the database and return an `Ok` response with a success message.

3. Action with asynchronous database access:
```scala
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.mvc._
import play.api.libs.json._
import models.User
import play.mvc.Controller
import play.api.mvc.Results.Ok
import reactivemongo.api.Cursor
import reactivemongo.play.json._

object UserController extends Controller {
  def listUsers: Action[AnyContent] = Action.async { implicit request =>
    val query = Json.obj()
    val users = User.collection.find(query).cursor[User]().collect[List]()
    users.map { userList =>
      Ok(Json.toJson(userList))
    }
  }
}
```
In this example, we define a `listUsers` action that retrieves a list of users from a `MongoDB` database.
We use the `reactivemongo.play.json._` package to convert the user data to JSON, and the `async` method of the `Action` object 
to return a `Future[Result]`. We retrieve the users from the database asynchronously using the `collect` method of the `Cursor` object,
and map the result to an `Ok` response with the user data converted to JSON.


## Equivalence in Java + Spring FW

In Java with Spring, the equivalent of a Scala action in Play Framework is a Spring `Controller` method 
that handles HTTP requests and generates HTTP responses.
The Controller in Spring provides a number of annotations to map HTTP requests to Controller methods and to generate responses.

Here's an example of a basic Spring Controller method that handles an HTTP GET request:

```java
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @GetMapping("/hello/{name}")
    @ResponseBody
    public String hello(@PathVariable String name) {
        return "Hello " + name;
    }
}
```

In this example, the `MyController` class defines a `hello` method that takes a `String` argument `name` 
and returns a plain text response with the message `"Hello "` followed by the value of `name`. 
The `@GetMapping` annotation maps the HTTP GET request to the `/hello/{name}` URL to the `hello` method,
and the `@ResponseBody` annotation specifies that the return value of the method should be used as the response body.

**Controller** methods can also use other Spring components such as `HttpServletRequest` and `HttpSession` 
to access information about the incoming request or to store information across requests.
For example, you can create a method that reads a value from the session, performs some processing,
and then stores a new value in the session:

```java
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController {

    @GetMapping("/process")
    @ResponseBody
    public String process(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String oldValue = (String) session.getAttribute("value");
        String newValue = (oldValue == null ? "0" : Integer.toString(Integer.parseInt(oldValue) + 1));
        session.setAttribute("value", newValue);
        return "Old value: " + oldValue + "\nNew value: " + newValue;
    }
}
```

In this example, the `process` method uses the `HttpServletRequest` object to obtain the `HttpSession` object,
reads the value of the `"value"` key from the session, increments it by `1`, and returns a response with the old and new values.
It also stores the new value back in the session for future requests.

Controller methods are one of the fundamental concepts of `Spring MVC` and allow developers to build powerful and flexible web applications with Java.

