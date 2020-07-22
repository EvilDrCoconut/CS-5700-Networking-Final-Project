import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Public class of the Http WebServer
 */
public class WebServer implements WebServerInterface {

  // variables for actual server to handle requests, priority queue to hold data and sequence numbers,
  //        and a thread pool executor to help handle multiple threads for a program.
  private HttpServer server;
  private PriorityQueue<Segments> dataBuffer;
  private ThreadPoolExecutor exeggutor;

  /**
   * Constructor of the WebServer Class, takes no variables and sets up on localhost, through port
   * 2000
   */
  public WebServer() {
    try {
      // can change backlog to queue requests from incoming clients. Shouldn't need to with multithreading,
      //      could be used to help prevent overflow errors.
      server = HttpServer.create(new InetSocketAddress("localhost", 2000), 0);
    } catch (IOException e) {
      System.out.println("Error creating Http Server: Error Code: Served");
    }
    exeggutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    dataBuffer = null;
  }

  @Override
  public void listenAndAccept() {
    this.retServer().createContext("/", new WebServerHandler());
    this.retServer().createContext("/echoHeader", new EchoHeadHandler());
    this.retServer().createContext("/echoGet", new EchoGetHeader());
    this.retServer().createContext("/echoPost", new EchoPostHeader());
    this.retServer().setExecutor(this.retExeguttor());
  }

  public ThreadPoolExecutor retExeguttor(){
    return this.exeggutor;
  }
  public HttpServer retServer(){
    return this.server;
  }


  public static void main(String[] argv) {

    WebServer httpServer = new WebServer();
    System.out.println("Server created successfully on localhost, now listening for requests.");

    while(true){
      httpServer.listenAndAccept();
    }

  }

  // code guide: https://www.codeproject.com/Tips/1040097/Create-a-Simple-Web-Server-in-Java-HTTP-Server
}