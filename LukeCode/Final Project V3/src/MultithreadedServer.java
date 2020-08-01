import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultithreadedServer extends Thread {

  public static void main(String[] argv) throws IOException {

    ServerSocket multiServe = new ServerSocket(2000, 0);

    // requires us to give an ip in the argv, suggest using -ip 0.0.0.0
    String ip = "192.0.120.0";
    int i = 0;
    for(String each: argv){
      if(each.equalsIgnoreCase("-ip")){
        ip = argv[i+1];
      }
      i++;
    }

    int counter = 0;
    while(true) {
      counter ++;
      System.out.println("Current requests made: " + counter);

      Socket clientSocket = multiServe.accept();

      WebServer httpServer = new WebServer(ip, clientSocket);
      System.out.println("Server created successfully on localhost, now listening for requests.");
      httpServer.listenAndAccept();

    }
  }

}
