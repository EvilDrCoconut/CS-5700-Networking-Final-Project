import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class WebServer implements WebServerInterface {

  // variables for actual server to handle requests, priority queue to hold data and sequence numbers,
  //        and a thread pool executor to help handle multiple threads for a program.
  private ThreadPoolExecutor exeggutor;
  private ServerSocket server;
  private Socket clientSock;
  private HttpServer httpServer;

  /**
   * Constructor of the WebServer Class, takes no variables and sets up on localhost, through port
   * 2000
   */


  public WebServer(String ip) throws IOException {
    try {
      // can change backlog to queue requests from incoming clients. Shouldn't need to with multithreading,
      //      could be used to help prevent overflow errors.
      server = new ServerSocket(2000, 0);
    } catch (IOException e) {
      System.out.println("Error creating Http Server: Error Code: Served");
    }
    // creates executor thread for running multiple threads
    exeggutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    // socket that the server is listening through

    // function to set the executor for multiple threads
    //this.retServer().setExecutor(this.retExeguttor());
  }

  public static void main(String[] argv) throws IOException {

    // requires us to give an ip in the argv, suggest using -ip 0.0.0.0
    String ip = "192.0.0.0";
    int i = 0;
    for(String each: argv){
      if(each.equalsIgnoreCase("-ip")){
        ip = argv[i+1];
      }
      i++;
    }
    WebServer httpServer = new WebServer(ip);
    System.out.println("Server created successfully on localhost, now listening for requests.");

    while (true) {
      try {
        httpServer.listenAndAccept();
      } catch(IOException e){
      }
    }
  }


  @Override
  public void listenAndAccept() throws IOException {


    while (true) {

      clientSock = server.accept();

      if (clientSock.isBound()) {
        DataInputStream received = new DataInputStream(new BufferedInputStream(clientSock.getInputStream()));
        String request = received.readUTF();
        WebServerHandler helper = new WebServerHandler(request);
        helper.handle();

        EchoGetHeader path = new EchoGetHeader(helper.retPath());
        path.handle();
        packageAndSend(helper.retLang(), helper.retSize(), path.retComp(), path.retFolder(), path.retFile());
      }
    }
  }

  @Override
  public void packageAndSend(String lang, String size, String comp, String folder, String file) throws IOException {

    // creates a string to the desired file's path, also checks language parameter if given
    String path = "";
    switch(lang) {
      case "":
      case "e":
        path = "database" + "/" + "english" + "/" + folder + "/" + file;
        break;
      case "f":
        path = "database" + "/" + "french" + "/" + folder + "/" + file;
        break;
      case "s":
        path = "database" + "/" + "spanish" + "/" + folder + "/" + file;
    }

    System.out.println(path);

    // initiate a fileinputstream to help retrieve all of file
    FileInputStream buf = null;
    File dummyFile = new File(path);
    long sizeCheck = dummyFile.length();
    int error404Check = 0;
    String error404 = "File given is not found, check spelling and try again.";
    try{
      buf = new FileInputStream(dummyFile);
    } catch (FileNotFoundException e){
      System.out.println(error404);
      error404Check = 1;
    }

    // creates a payload byte array of standard size of 4000 bytes, unless specified by GET request
    if(error404Check == 0) {
      byte[] payload;
      if (size.equals("")) {
        payload = new byte[(int) sizeCheck + 1];
      } else {
        payload = new byte[Integer.parseInt(size)];
      }

      buf.read(payload);
      // sends payload back to client
      try {
        OutputStream out = clientSock.getOutputStream();
        out.write(payload);
        clientSock.close();
      } catch (IOException e) {
        System.out.println("Error Code 01");
      }
    } else{
      try {
        System.out.println("Sending 404 message");
        OutputStream out = clientSock.getOutputStream();
        byte[] error = error404.getBytes();
        out.write(error);
        clientSock.close();
      } catch (IOException e) {
        System.out.println("Error Code 01");
      }
    }

  }

  public ThreadPoolExecutor retExeguttor(){
    return this.exeggutor;
  }
  public ServerSocket retServer(){
    return this.server;
  }

}