import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class EchoPostHeader implements HttpHandler {
  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    // process request
    Map<String, Object> parameters = new HashMap<String, Object>();
    URI requestedURI = httpExchange.getRequestURI();
    String query = requestedURI.getRawQuery();
    parseQuery(query, parameters);

    // send answer
    String response = "";
    for(String key : parameters.keySet()){
      response += key + " = " + parameters.get(key) + "\n";
      httpExchange.sendResponseHeaders(200, response.length());
      OutputStream os = httpExchange.getResponseBody();
      os.write(response.toString().getBytes());
      os.close();
    }

  }

  public static void parseQuery(String q, Map<String, Object> param){
    String segments[] = q.split(".");

  }


}
