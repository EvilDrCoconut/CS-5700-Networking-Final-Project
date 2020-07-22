import java.net.SocketException;

public interface WebServerInterface {

  void listenAndAccept() throws SocketException;

  void packageAndSend();


}
