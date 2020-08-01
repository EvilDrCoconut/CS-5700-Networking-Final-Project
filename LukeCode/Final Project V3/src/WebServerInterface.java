import java.io.IOException;

public interface WebServerInterface {

  void listenAndAccept() throws IOException;

  void packageAndSend(String lang, String size, String comp, String folder, String file) throws IOException;


}
