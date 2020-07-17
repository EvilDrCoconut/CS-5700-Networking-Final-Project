import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EchoHeadHandler implements HttpHandler {


  @Override
  public void handle(HttpExchange httpExchange) throws IOException {

        Headers head = httpExchange.getRequestHeaders();
        Set<Map.Entry<String, List<String>>> entries = head.entrySet();
        String response = "";
        for(Map.Entry<String, List<String>> entry : entries){
            response = entry.toString() + "\n";
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
  }
}
