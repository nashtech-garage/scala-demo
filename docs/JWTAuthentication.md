# JWT Authentication
_Ref: https://sysgears.com/articles/how-to-create-restful-api-with-scala-play-silhouette-and-slick/_

One of the most common things on any web application is authentication. 
Almost every web app needs authentication. 
If you want to have users then you’ll need to implement authentication. 
And there are many different ways to implement it today. 
From the classic user / password, to login with other social accounts (Google, Facebook, GitHub, etc),
using [JWT tokens](https://jwt.io/), or even by using external services like [auth0](https://auth0.com/).

In this section, We will go through how to implement JWT Authentication with Play Framework.

**JSON Web Token (JWT)** is a compact, self-contained which means securely transfer the information between two parties.
It can be sent via Post request or inside the HTTP header. This information can be verified and trusted because it is digitally signed.
JWTs can be signed using a secret or a public/private key pair using RSA.
It consists three parts separated by the dot(.)  i.e Header, Payload and Signature.
The header consists type of the token and hashing algorithm, the payload contains the claims 
and the claims in a JWT are encoded as a JSON object that is used as the payload or as the plaintext,
the signature part is used to verify that the sender of the JWT.

Here, we are using JWT for authentication with Play Framework, This is a most common way of using JWT. 
Once the user is logged in, we need to include JWT with each request that allows the user to access the routes,
service and resources which authenticate the token in order to permit the request.

Unfortunately, There is no built-in JWT support in Play or Akka HTTP.
There are a few open source solutions to handle authentication on Play Framework:

* [Play2-auth](https://github.com/t2v/play2-auth) - last commit 7 years ago
* [SecureSocial](https://github.com/jaliss/securesocial) - last commit 5 years ago
* [Authentikat-jwt](https://github.com/jasongoodwin/authentikat-jwt) - last commit 4 years ago
* [Play-Pac4j](https://github.com/pac4j/play-pac4j) - Most parts are written in Java
* [Silhouette](https://github.com/honeycomb-cheesecake/play-silhouette) - Pure Scala library, seems to be a reliable solution

