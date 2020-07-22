import java.io.IOException;

public class WebServerHandler {

  public String[] handle(String URL, int ip) throws IOException {
        return URL.split(".|/");
  }
}
