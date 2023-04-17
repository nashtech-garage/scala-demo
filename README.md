# Scala REST API Application with Play Framework

### Technology Stack
- Scala 2.13
- Play Framework 2.8.19
- Slick 5.0.0 (DB Access/Evolutions)
- PostgreSQL
- Guice (DI)
- Silhouette (Authn/Authz)
- HTTPClient (Play WS)
- JSON Conversion (Play JSON)
- Logging (Logback)

### Project Structure
```
── scala-demo
   ├── app                                            # The Scala application source code
   │   ├── utils
   │   │   └── auth                                   # Authentication utils
   │   ├── domain
   │   │   ├── tables                                 # Slick tables
   │   │   │   ├── PostTable.scala                    # Represents posts table
   │   │   │   └── UserTable.scala                    # Represents users table
   │   │   ├── models                                 # Contains UserService with its implementation
   │   │   │   ├── Post.scala                         # Post model
   │   │   │   └── User.scala                         # User model
   │   │   └── daos
   │   │       ├── DaoRunner.scala                    # Run Slick database actions by transactions
   │   │       ├── DbExecutionContext.scala           # Custom ExecutionContext for running DB connections
   │   │       ├── PasswordInfoDao.scala              # Password dao
   │   │       ├── PostDao.scala                      # Post dao
   │   │       └── UserDao.scala                      # User dao
   │   ├── system                                     # Play modules
   │   │   └── modules
   │   │       ├── AppModule.scala                    # Bind all application components (Same as Spring @Configuration)
   │   │       └── SilhouetteModule.scala             # Bind silhouette components
   │   └── controllers                                # Application controllers
   │       ├── auth                                   
   │       │   ├── AuthController.scala               # SignUp/SignIn controllers
   │       │   ├── SilhouetteController.scala         # Abstract silhouette controller
   │       │   ├── UnsecuredResourceController.scala  # Example of a un-secured endpoint
   │       └── post                                   
   │           ├── PostController.scala               # Post controllers for CRUD a post
   │           ├── PostControllerComponents.scala     # Post Controller components
   │           ├── PostResource.scala                 # Request/Response Post dto
   │           └── PostRouter.scala                   # Post endpoints routing
   ├── test
   ├── conf
   │   ├── messages                               # Error Messages for messages API
   │   ├── evolutions                             # Play evolutions SQL queries
   │   │   └── default                            # Default database
   │   │       ├── 1.sql                          # Creates schema
   │   │       └── 2.sql                          # Creates db tables
   │   ├── application.conf                       # Play configuration
   │   ├── routes                                 # Play routing
   │   ├── db.conf                                # Database configuration
   │   └── silhouette.conf                        # Silhouette configuration
   ├── project
   ├── build.sbt
   └── target
```

### Getting Started

#### 1. Setup `PostgreSQL` Database
You can install PostgreSQL on your local machine or running the docker compose in the `/docker/database` folder
to get PostgreSQL ready.

#### 2. Run application 
You need to download and install sbt for this application to run.
_Note: I've added the `SBT bin` to this project to build the source code without SBT installation_
Once you have sbt installed, the following at the command prompt will start up Play in development mode:
```bash
./sbt run
```

Play will start up on the HTTP port at <http://localhost:8080/>.   You don't need to deploy or reload anything -- changing any source code while the server is running will automatically recompile and hot-reload the application on the next HTTP request.

#### 3. Run Unit Tests
```bash
./sbt clean test
```

or To generate code coverage report with SCoverage
```bash
./sbt clean coverage test coverageReport
```

#### 4. Run Integration Tests
```bash
./sbt clean integration/test
```

### Usage
_Ref: Postman collection at `postman` folder_

1. Create an User by using `POST /SignUp` endpoint
2. You can access the `GET /Unsecured` endpoint without login
3. Using `POST- /SignIn` endpoint to login with newly created user to get JWT token in `X-Auth` response header
4. Get All Posts via `GET /v1/posts` endpoint -> empty list returned at first
5. Create a new Post by using `POST /v1/posts` endpoint
6. Get All Posts via `GET /v1/posts` endpoint again -> Only one created Post shown
7. Get single Post via `GET /v1/posts/:id`
8. Delete existing Post via `DELETE /v1/posts/:id` endpoint
