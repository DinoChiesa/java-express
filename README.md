

![Java Express Logo](https://preview.ibb.co/c1SWkx/java_express.png)


[![License MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://choosealicense.com/licenses/mit/)

**This project is currently in progress, feel free to [contribute](https://github.com/Simonwep/java-express/graphs/contributors) / [report](https://github.com/Simonwep/java-express/issues) issues! :)**

**[0.0.7-alpha](https://github.com/Simonwep/java-express/releases/tag/0.0.7) is ready, check it out!**

```java
Express app  = new Express();

app.get("/", (req, res) -> {
   res.send("Hello World");
});

app.listen(); // Port 80 is default
```

When you create an new Express instance you can add an additional host name for example, your local network:
```java
// Will bind the server to your ip-adress
Express app = new Express(Utils.getYourIp());
```
Default is localhost, so you can access, without setting the hostname, only from your local pc.

Docs (v0.0.7-alpha):
* [Routing](#routing)
   * [Direct](#direct)
   * [With Router](#with-router)
* [URL Basics](#url-basics)
   * [URL Parameter](#url-parameter)
   * [URL Parameter Listener](#url-parameter-listener)
   * [URL Querys](#url-querys)
   * [Cookies](#cookies)
   * [Form Data](#form-data)
* [HTTP - Main Classes](#http---main-classes)
   * [Response Object](#response-object)
   * [Request Object](#request-object)
* [Middleware](#middleware)
   * [Create own middleware](#create-own-middleware)
* [Using local variables](#local-variables)
* [License](#license)

Every following code can be also found in [this package](https://github.com/Simonwep/java-express/tree/master/src/examples).

# Routing
## Direct
You can add routes (And middlewares) directly to the Express object to handle requests:
```java
Express app = new Express();

// Sample for home routes
app.get("/", (req, res) -> res.send("Hello index!"));
app.get("/home", (req, res) -> res.send("Homepage"));
app.get("/about", (req, res) -> res.send("About"));

// Sample for user
app.get("/user/login", (req, res) -> res.send("Please login!"));
app.get("/user/register", (req, res) -> res.send("Join now!"));

app.listen();
```
It also directly supports directly methods like `POST` `PATCH` `DELETE` and `PUT` others need to be created manually:
```java
Express app = new Express();

// Basic methods
app.get("/user", (req, res) -> res.send("Get an user!"));
app.patch("/user", (req, res) -> res.send("Modify an user!"));
app.delete("/user", (req, res) -> res.send("Delete an user!"));
app.put("/user", (req, res) -> res.send("Add an user!"));

// Example fot the CONNECT method
app.on("/user", "CONNECT", (req, res) -> res.send("Connect!"));

app.listen();
```

## With Router
But it's better to split your code, right? With the `ExpressRouter` you can create routes and add it later to the `Express` object:
```java
Express app = new Express();

// Define router for index sites
ExpressRouter indexRouter = new ExpressRouter();
indexRouter.get("/", (req, res) -> res.send("Hello World!"));
indexRouter.get("/index", (req, res) -> res.send("Index"));
indexRouter.get("/about", (req, res) -> res.send("About"));

// Define router for user pages
ExpressRouter userRouter = new ExpressRouter();
userRouter.get("/", (req, res) -> res.send("User Page"));
userRouter.get("/login", (req, res) ->  res.send("User Login"));
userRouter.get("/register", (req, res) -> res.send("User Register"));
userRouter.get("/:username", (req, res) -> res.send("You want to see: " + req.getParam("username")));

// Add router and set root paths
app.use("/", indexRouter);
app.use("/user", userRouter);

// Start server
app.listen();
```

# URL Basics
Over the express object you can create handler for all [request-methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods) and contexts. Some examples:
```java
app.get("/home", (req, res) -> {
	// Will match every request which uses the 'GET' method and matches the '/home' path
});

app.post("/login", (req, res) -> {
	// Will match every request which uses the 'POST' method and matches the /login' path
});
```

## URL Parameter
Sometime you want to create dynamic URL where some parts of the URL's are not static.

Example request: `GET`  `/posts/john/all`:
```java
app.get("/posts/:user/:description", (req, res) -> {
   String user = req.getParam("user"); // Contains 'john'
   String description = req.getParam("description"); // Contains 'all'
   res.send("User: " + user + ", description: " + description); // Send: "User: john, description: all"
});
```

### URL Parameter Listener
You can also add an event listener when the user called an route which contains an certain parameter:
```java
app.get("/posts/:user/:id", (req, res) -> {
  // Code
});
```
For example, if we want to check every `id` before the associated get post etc. handler will be fired, we can use the `app.onParam([PARAM])` function:
```java
app.onParam("id", (req, res) -> {
  // Do something with the id parameter, eg. check if it's valid.
});
```
Now, this function will be called every time when an context is requested which contains the `id` parameter placeholder.

## URL Querys
If you make an request which contains querys, you can access the querys over `req.getQuery(NAME)`.

Example request: `GET`  `/posts?page=12&from=john`:
```java
app.get("/posts", (req, res) -> {
   String page = req.getQuery("page"); // Contains '12'
   String from = req.getQuery("from"); // Contains 'John'
   res.send("Page: " + page + ", from: " + from); // Send: "Page: 12, from: John"
});
```

## Cookies
With `req.getCookie(NAME)` you can get an cookie by his name, and with `res.setCookie(NAME, VALUE)` you can easily set an cookie.

Example request: `GET`  `/setcookie`:
```java
app.get("/setcookie", (req, res) -> {
   Cookie cookie = new Cookie("username", "john");
   res.setCookie(cookie);
   res.send("Cookie has been set!");
});
```

Example request: `GET`  `/showcookie`:
```java
app.get("/showcookie", (req, res) -> {
   Cookie cookie = req.getCookie("username");
   String username = cookie.getValue();
   res.send("The username is: " + username); // Prints "The username is: john"
});
```

## Form data
Over `req.getFormQuery(NAME)` you receive the values from the input elements of an HTML-Form.
Example HTML-Form:
```html
<form action="http://localhost/register" method="post">
   <input description="text" name="email" placeholder="Your E-Mail">
   <input description="text" name="username" placeholder="Your username">
   <input description="submit">
</form>
```
**Attention: Currently, File-inputs don't work, if there is an File-input the data won't get parsed!**
Now description, for the example below, `john` in username and `john@gmail.com` in the email field.
Java code to handle the post request and access the form elements:
```java
app.post("/register", (req, res) -> {
  String email = req.getFormQuery("email");
  String username = req.getFormQuery("username");
  // Process data

  // Prints "E-Mail: john@gmail.com, Username: john"
  res.send("E-Mail: " + email + ", Username: " + username);
});
```

# HTTP - Main Classes
## Express
This class represents the entire HTTP-Server, the available methods are:
```java
app.get(String context, HttpRequest handler);                   // Add an GET request handler
app.post(String context, HttpRequest handler);                  // Add an POST request handler
app.patch(String context, HttpRequest handler);                 // Add an PATCH request handler
app.put(String context, HttpRequest handler);                   // Add an PUT request handler
app.delete(String context, HttpRequest handler);                // Add an DELETE request handler
app.all(HttpRequest handler);                                   // Add an handler for all methods and contexts
app.all(String context, HttpRequest handler);                   // Add an handler for all methods but for an specific context
app.all(String context, String method, HttpRequest handler);    // Add an handler for an specific method and context
app.use(String context, String method, HttpRequest handler);    // Add an middleware for an specific method and context
app.use(HttpRequest handler);                                   // Add an middleware for all methods but for an specific context
app.use(String context, HttpRequest handler);                   // Add an middleware for all methods and contexts
app.use(String context, ExpressRouter router);                  // Add an router for an specific root context
app.use(ExpressRouter router);                                  // Add an router for the root context (/)
app.onParam(String name, HttpRequest handler);                  // Add an listener for an specific url parameter
app.getParameterListener();                                     // Returns all parameterlistener
app.get(String key);                                            // Get an environment variable
app.set(String key, String val);                                // Set an environment variable
app.isSecure();                                                 // Check if the server uses HTTPS
app.setExecutor(Executor executor);                             // Set an executor service for the request
app.listen();                                                   // Start the async server on port 80
app.listen(ExpressListener onstart);                            // Start the async server on port 80, call the listener after starting
app.listen(int port);                                           // Start the async server on an specific port
app.listen(ExpressListener onstart, int port);                  // Start the async server on an specific port call the listener after starting
app.stop();                                                     // Stop the server and all middleware worker
```

## Response Object
Over the response object, you have serveral possibility like setting cookies, send an file and more. Below is an short explanation what methods exists:
(We assume that `res` is the `Response` object)

```java
res.getContentType();                  // Returns the current content type
res.setContentType(MediaType type);    // Set the content type with enum help
res.setContentType(String type);       // Set the content type
res.isClosed();                        // Check if the response is already closed
res.getHeader(String key);             // Get the value from an header field via key
res.setHeader(String key, String val); // Add an specific response header
res.send(String str);                  // Send an string as response
res.send(Path path);                   // Send an file as response
res.send();                            // Send empty response
res.redirect(String location);         // Redirect the request to another url
res.setCookie(Cookie cookie);          // Add an cookie to the response
res.sendStatus(Status status);         // Set the response status and send an empty response
res.getStatus();                       // Returns the current status
res.setStatus(Status status);          // Set the repose status
```
The response object calls are comments because **you can only call the .send(xy) once each request!**

## Request Object
Over the `Request` Object you have access to serveral request stuff (We assume that `req` is the `Request` object):

```java
req.getAddress();                 // Returns the INET-Adress from the client
req.getMethod();                  // Returns the request method
req.getPath();                    // Returns the request path
req.getQuery(String name);        // Returns the query value by name
req.getHost();                    // Returns the request host
req.getContentLength();           // Returns the content length
req.getContentType();             // Returns the content type
req.getMiddlewareContent(String name); // Returns the content from an middleware by name
req.getFormQuerys();              // Returns all form querys
req.getParams();                  // Returns all params
req.getQuerys();                  // Returns all querys
req.getFormQuery(String name);    // Returns the form value by name
req.getHeader(String key);        // Returns the value from an header field by name
req.getParam(String key);         // Returns the url parameter by name
req.getApp();                     // Returns the related express app
req.getCookie(String name);       // Returns an cookie by his name
req.getCookies();                 // Returns all cookies
req.getIp();                      // Returns the client IP-Address
req.getUserAgent();               // Returns the client user agent
req.getURI();                     // Returns the request URI
req.getAuthorization();           // Returns the request authorization
req.hasAuthorization();           // Check if the request has an authorization
req.pipe(OutputStream stream, int buffersize); // Pipe the request body to an outputstream
req.pipe(Path path, int buffersize);           // Pipe the request body to an file
req.getBody();                    // Returns the request inputstream
```

# Middleware
Middleware are one of the most important functions of JavaExpress, with middleware you can handle a request before it reaches any request handler. To create an own middleware you have serveral interfaces:
* `HttpRequest`  - Is **required** to handle an request.
* `ExpressFilter` - Is **required** to put data on the request listener.
* `ExpressFilterTask` - Can be used for middleware which needs an background thread.

Middlewares work, for you, exact same as request handler.
For example an middleware for all [request-methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods) and contexts:

```java
// Global context, matches every request.
app.use((req, res) -> {
Handle data
});
```
You can also filter by [request-methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods) and contexts:
```java
// Global context, you can also pass an context if you want
app.use("/home", "POST", (req, res) -> {
Handle request by context '/home' and method 'POST'
});
```
In addition to that yo can use `*` which stands for every **context** or **request method**:
```java
// Global context, you can also pass an context if you want
app.use("/home", "*", (req, res) -> {
Handle request which matches the context '/home' and all methods.
});
```
## Create own middleware

Now we take a look how we can create own middlewares. Here we create an simple PortParser which parse / extract the port-number for us:
```java
public class PortMiddleware implements HttpRequest, ExpressFilter {

   /**
    * From interface HttpRequest, to handle the request.
    */
   @Override
   public void handle(Request req, Response res) {
      
      // Get the port
      int port = req.getURI().getPort();
      
      // Add the port to the request middleware map
      req.addMiddlewareContent(this, port);

      /**
       * After that you can use this middleware by call:
       *   app.use(new PortMiddleware());
       *   
       * Than you can get the port with:
       *   int port = (Integer) app.getMiddlewareContent("PortParser");
       */
   }

   /**
    * Defines the middleware.
    *
    * @return The middleware name.
    */
   @Override
   public String getName() {
      return "PortParser";
   }
}
```
No we can, as we learned above, include it with:
```java
// Global context, you can also pass an context if you want
app.use(new PortMiddleware());
```
## Existing Middlewares
There are already some basic middlewares included, you can access these via static methods provided from `Middleware`.

#### Provide static Files
If you want to allocate some files, like js-librarys or css files you can use the [static](https://github.com/Simonwep/java-express/blob/master/src/express/middleware/Middleware.java) middleware. But you can also provide other files like mp4 etc.
Example:
```java
 app.use(Middleware.statics("examplepath\\myfiles"));
```
Now you can access every files in the `test_statics` over the root adress `\`. I'ts also possible to set an configuration for the FileProvider:
```java
FileProviderOptionsoptions = new FileProviderOptions();
options.setExtensions("html", "css", "js"); // By default, all are allowed.

/*
 * Activate the fallbacksearch.
 * E.g. if an request to <code>/js/code.js</code> was made but the
 * requested ressource cannot be found. It will be looked for an file called <code>code</code>
 * and return it.
 *
 *  Default is false
 */
options.setFallBackSearching(true);
options.setHandler((req, res) -> {...});    // Can be used to handle the request before the file will be returned.
options.setLastModified(true);              // Send the Last-Modified header, by default true.
options.setMaxAge(10000);                   // Send the Cache-Control header, by default 0.
options.setDotFiles(DotFiles.DENY);         // Deny access to dot-files. Default is IGNORE.
app.use(Middleware.statics("examplepath\\myfiles", new FileProviderOptions())); // Using with StaticOptions
```
#### Cookie Session
There is also an simple cookie-session implementation:
```java
// You should use an meaningless cookie name for serveral security reasons, here f3v4.
// Also you can specify the maximum age of the cookie from the creation date and the file types wich are actually allowed.
app.use(Middleware.cookieSession("f3v4", 9000));
```
To use a session cookie we need to get the data from the middleware which is actually an `SessionCookie`:
```java
 // Cookie session example
app.get("/session", (req, res) -> {

   /**
   * CookieSession named his data "Session Cookie" which is
   * an SessionCookie so we can Cast it.
   */
   SessionCookie sessionCookie = (SessionCookie) req.getMiddlewareContent("sessioncookie");
   int count;
   
Check if the data is null, we want to implement an simple counter
   if (sessionCookie.getData() == null) {
   
      // Set the default data to 1 (first request with this session cookie)
      count = (Integer) sessionCookie.setData(1);
   
   } else {
      // Now we know that the cookie has an integer as data property, increase it
      count = (Integer) sessionCookie.setData((Integer) sessionCookie.getData() + 1);
   }

Send an info message
   res.send("You take use of your session cookie " + count + " times.");
});
```
## Local Variables
Java-express also supports to save and read local variables over the Express instance:
Example:
```java
app.set("my-data", "Hello World");
app.get("my-data"); // Returns "Hello World"
```
# License

This project is licensed under the MIT License - see the [LICENSE.md](https://choosealicense.com/licenses/mit) file for details