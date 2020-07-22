import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class WebServerHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
          String response = "<h1>Server started, request received.</h1>";
          httpExchange.sendResponseHeaders(200, response.length());
          OutputStream os = httpExchange.getResponseBody();
          os.write(response.getBytes());
          os.close();
  }

}
