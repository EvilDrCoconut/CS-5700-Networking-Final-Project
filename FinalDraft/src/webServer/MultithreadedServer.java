package webServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * class for the Multithreaded server that handles multiple client requests
 */
public class MultithreadedServer extends Thread {

//  int port;
//  String ip;
//  public MultithreadedServer(int portNum, String ipNum){
//    port = portNum;
//    ip = ipNum;
//  }

  /**
   * Main method that starts the overall server, helps to create a smaller webserver that parses
   *        and handles the requests given from the clients.
   * @throws IOException if Server socket can not be created
   */
  public static void main(String[] argv) throws IOException {

    String port = "";

    // requires us to give an ip in the argv, suggest using -ip 0.0.0.0,  this code
    //      is hashed out as it was intended for the GNS3 emulation, in which errors arose.
    String ip = "192.0.110.0";

    int i = 0;
    // parses the argv to find the given ip address, otherwise a standard ip is given
    for(String each: argv){
      if(each.equalsIgnoreCase("-ip")){
        ip = argv[i+1];
      } else if(each.equalsIgnoreCase("-port")){
        port = argv[i+1];
      }
      i++;
    }

    try {
      int portNum = Integer.parseInt(port);

      ServerSocket multiServe = new ServerSocket(portNum, 0);


      // counter to help make sure server is correctly handling multiple clients
      int counter = 0;
      while(true) {
        counter ++;
        System.out.println("Current requests made: " + counter);

        // socket created from the multi threaded server
        Socket clientSocket = multiServe.accept();

        // web server is created by passing in its ip address and clientSocket, then starts
        //      listening to clients request.
        WebServer httpServer = new WebServer(ip, clientSocket);
        System.out.println("Server created successfully on localhost, now listening for requests.");
        httpServer.listenAndAccept();
      }
    } catch (NumberFormatException e) {
      System.out.println("Must provide valid port");
    }

  }

}
