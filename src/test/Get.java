package test;

import express.Express;
import express.http.cookie.Cookie;

import java.io.IOException;
import java.util.HashMap;

public class Get {

  public static void main(String[] args) throws IOException {
    Express app = new Express();

    // Test case for url
    app.get("/", (req, res) -> res.send("Called /"));

    // Test case for url
    app.get("/user", (req, res) -> res.send("Called /user"));

    // Test case for url
    app.get("/user/bob", (req, res) -> res.send("Called /user/bob"));

    // Test case for url querying
    app.get("/getposts", (req, res) -> {
      String age = req.getQuery("age");
      String from = req.getQuery("from");
      res.send("Age: " + age + "\nFrom: " + from);
    });

    // Test case for param placeholder
    app.get("/hello/:username", (req, res) -> {
      String username = req.getParam("username");
      res.send("User " + username + " sad hello!");
    });

    // Test case for multiple param placeholder
    app.get("/hello/:username/:count", (req, res) -> {
      String username = req.getParam("username");
      String count = req.getParam("count");
      res.send("User " + username + " want to say " + count + " times hello!");
    });

    // Test case for cookie setting & multiple param placeholder
    app.get("/cookie/:name/:val", (req, res) -> {
      String name = req.getParam("name");
      String val = req.getParam("val");
      Cookie cookie = new Cookie(name, val);
      res.setCookie(cookie);
      res.send("ok");
    });

    // Test case for cookie reading
    app.get("/showcookies", (req, res) -> {
      HashMap<String, Cookie> cookies = req.getCookies();
      StringBuffer buffer = new StringBuffer();
      cookies.forEach((s, cookie) -> buffer.append(s).append(": ").append(cookie));
      res.send(buffer.toString());
    });

    app.listen(() -> System.out.println("Express is listening!"));
  }

}
