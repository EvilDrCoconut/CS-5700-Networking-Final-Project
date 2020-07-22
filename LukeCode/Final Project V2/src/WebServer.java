import com.sun.net.httpserver.HttpServer;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.PriorityQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class WebServer implements WebServerInterface {

  // variables for actual server to handle requests, priority queue to hold data and sequence numbers,
  //        and a thread pool executor to help handle multiple threads for a program.
  private HttpServer server;
  private PriorityQueue<Segment> dataBuffer;
  private ThreadPoolExecutor exeggutor;
  private DatagramSocket serverSock;
  private byte[] receiver;
  private DatagramPacket received;
  private String message;
  private int[] seqNums;
  private Segment[] data;

  /**
   * Constructor of the WebServer Class, takes no variables and sets up on localhost, through port
   * 2000
   */
  public WebServer() throws SocketException {
    try {
      // can change backlog to queue requests from incoming clients. Shouldn't need to with multithreading,
      //      could be used to help prevent overflow errors.
      server = HttpServer.create(new InetSocketAddress("localhost", 2000), 0);
    } catch (IOException e) {
      System.out.println("Error creating Http Server: Error Code: Served");
    }
    exeggutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(100);
    dataBuffer = null;

    serverSock = new DatagramSocket(5000);
    receiver = new byte[560];
    message = "ACK";
    int ackSeqNum = 0;
    seqNums = new int[255];
    for(int pop = 0; pop < 255; pop ++){
      seqNums[pop] = 0;
    }
    data = new Segment[255];
    this.retServer().setExecutor(this.retExeguttor());
  }





  @Override
  public void listenAndAccept() throws SocketException {


    while (true) {

      received = new DatagramPacket(receiver, receiver.length);

      try {
        serverSock.receive(received);
        ByteArrayInputStream bytesRec = new ByteArrayInputStream(receiver);
        ObjectInputStream segReceived = new ObjectInputStream(new BufferedInputStream(bytesRec));
        Segment sentSeg = (Segment) segReceived.readObject();

        seqNums[sentSeg.retSeqnum()] = sentSeg.retSeqnum();
        data[sentSeg.retSeqnum()] = sentSeg;

        String url = sentSeg.retPayload();
        int ip = sentSeg.retIP();

        WebServerHandler wsh = new WebServerHandler();
        String[] urlSplit = wsh.handle(url, ip);




      } catch (IOException | ClassNotFoundException e) {

      }
    }
  }

  @Override
  public void packageAndSend() {

  }

  public ThreadPoolExecutor retExeguttor(){
    return this.exeggutor;
  }
  public HttpServer retServer(){
    return this.server;
  }


  public static void main(String[] argv) throws SocketException {

    WebServer httpServer = new WebServer();
    System.out.println("Server created successfully on localhost, now listening for requests.");

    while (true) {
      httpServer.listenAndAccept();
    }
  }

}