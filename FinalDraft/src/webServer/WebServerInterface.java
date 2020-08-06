package webServer;

import java.io.IOException;

/**
 *  interface for how a web server should act in general
 */
public interface WebServerInterface {

  // method to listen and accept incoming connections
  void listenAndAccept() throws IOException;

  // method to pull requested file and send it to the client
  void packageAndSend(String lang, String size, String comp, String folder, String file) throws IOException;


}
