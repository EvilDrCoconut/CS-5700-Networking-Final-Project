import java.io.IOException;
import java.net.SocketException;

public interface WebServerInterface {

  void listenAndAccept() throws IOException;

  void packageAndSend(String lang, String size, String comp, String folder, String file);


}
