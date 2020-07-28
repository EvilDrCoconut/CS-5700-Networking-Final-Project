import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WebServer implements WebServerInterface {

  // variables for actual server to handle requests, priority queue to hold data and sequence numbers,
  //        and a thread pool executor to help handle multiple threads for a program.
  private ThreadPoolExecutor exeggutor;
  private ServerSocket server;
  private Socket serverSock;
  private OutputStreamWriter sending;

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
//    serverSock = new Socket(ip, 2000);

    // function to set the executor for multiple threads
    //this.retServer().setExecutor(this.retExeguttor());
  }





  @Override
  public void listenAndAccept() throws IOException {
    System.out.println("dsfa");


    while (true) {

      serverSock = server.accept();

        DataInputStream received = new DataInputStream(new BufferedInputStream(serverSock.getInputStream()));
        String request = received.readUTF();
        System.out.println(request);
        WebServerHandler helper = new WebServerHandler(request);
        helper.handle();

        EchoGetHeader path = new EchoGetHeader(helper.retPath());
        path.handle();
        packageAndSend(helper.retLang(), helper.retSize(), path.retComp(), path.retFolder(), path.retFile());
    }
  }

  @Override
  public void packageAndSend(String lang, String size, String comp, String folder, String file) {

    String path = comp + "/" + folder + "/" + file;

    BufferedReader buf = null;
    try{
      buf = new BufferedReader(new FileReader(path));
    } catch (FileNotFoundException e){
      System.out.println("File given is not found, check spelling and try again.");
    }

    StringBuilder entireData = new StringBuilder();
    String message = "";

    try{
     // sets up a Stringbuilder to insert all text from file, whether it is on one line or multiple
      while ((message = buf.readLine()) != null) {
        entireData.append(message);
      }
    } catch(IOException e){System.out.println("ErrorCode 00"); return;}

    try {
      String fullMessage = entireData.toString();
      String encoded = URLEncoder.encode(fullMessage, "UTF-8");

      ByteArrayOutputStream buff = new ByteArrayOutputStream(512);
      ObjectOutputStream buffer = new ObjectOutputStream(buff);
      buffer.writeObject(encoded);
      byte[] payload = buff.toByteArray();
      OutputStream out = serverSock.getOutputStream();

      out.write(payload);

    } catch(IOException e){System.out.println("Error Code 01");}

  }

  public ThreadPoolExecutor retExeguttor(){
    return this.exeggutor;
  }
  public ServerSocket retServer(){
    return this.server;
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

}