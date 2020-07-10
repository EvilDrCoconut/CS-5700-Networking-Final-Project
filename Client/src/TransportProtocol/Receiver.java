package TransportProtocol;

import TransportProtocol.Data.DataSegment;
import TransportProtocol.Data.Header;
import TransportProtocol.Data.Sequence;
import TransportProtocol.Data.Type;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents the receiver of data in transport protocol. Only sends ACK segments and receives
 * Data.
 */
public class Receiver {

  private final int serverPort;
  private final File file;
  private final int windowSize;
  private boolean lastSegment = false;
  private Sequence lastSent = new Sequence();
  private FileOutputStream writeFile;
  private DatagramSocket datagramSocket;
  private int responsePort;
  private InetAddress responseAddress;

  public Receiver(int serverPort, File file, int windowSize) {
    this.serverPort = serverPort;
    this.file = file;
    this.windowSize = windowSize;
  }


  /**
   * Instantiate, connect, open new file, receive file, write file out, and send acknowledgements.
   * Close when receive a length < 512 segment (indicating end of file).
   *
   * @param args <listening_port_number> <window_size> <output_file>
   */
  public static void main(String[] args) {

    // Validate args
    if (args.length != 3) {
      System.out.println("Incorrect number of arguments");
      return;
    }
    Receiver receiver;
    try {
      // Arg 0 is a port
      int serverPort = Integer.parseInt(args[0]);
      // Arg 1 is the window_size
      int windowSize = Integer.parseInt(args[1]);
      if (windowSize < 1 || windowSize > 7 || serverPort < 1 || serverPort > 65000) {
        System.out
            .println("Window size must be between 1 and 7 and port number between 1 and 65000");
        return;
      }
      // Arg 2 is the file
      File file = new File(args[2]);
      receiver = new Receiver(serverPort, file, windowSize);
    } catch (NumberFormatException e) {
      System.out.println("Port number and Window size arguments must be valid integers.");
      return;
    }

    // Receive File
    try {
      // Open the file and socket connection
      receiver.openConnections();

      // Receive messages and send a response
      while (!receiver.lastSegment) {
        // Introduce random droppage, 30% chance to drop packet incoming or outgoing.
        receiver.receiveMessage();

        receiver.sendMessage();
      }

    } catch (IOException e) {
      System.out.println("Error with connection.");
    } finally {
      receiver.closeConnections();
    }
  }

  /**
   * Open IO connections
   *
   * @throws IOException error opening
   */
  void openConnections() throws IOException {
    this.writeFile = new FileOutputStream(file);
    this.datagramSocket = new DatagramSocket(serverPort);
  }

  /**
   * Close IO connections
   */
  void closeConnections() {
    datagramSocket.close();
    try {
      writeFile.close();
    } catch (IOException e) {
      System.out.println("File issues on closing.");
    }
  }

  /**
   * Receive and process incoming message, write payload to file if valid header, then prepare to
   * respond.
   *
   * @throws IOException connection issues
   */
  void receiveMessage() throws IOException {
    // create data buffer to receive message, max packet size (516)
    byte[] buffer = new byte[DataSegment.MAX_PAYLOAD + Header.SIZE];

    DatagramPacket request = new DatagramPacket(buffer, buffer.length);

    datagramSocket.receive(request);

    // Need to check that data and write and increment only if in right order ****
    // Get the header bytes as new header.
    Header header = new Header(Arrays.copyOfRange(buffer, 0, Header.SIZE));

    // Check if valid type and if received sequence is next in order
    if (header.getType() == Type.DATA && header.getSequence().equals(lastSent)) {
      writeFile.write(Arrays.copyOfRange(buffer, Header.SIZE, buffer.length));

      lastSent = header.getSequence().increment();

      if (header.getLength().lastSegment()) {
        lastSegment = true;
      }
    }
    responseAddress = request.getAddress();
    responsePort = request.getPort();
  }

  /**
   * Reply to incoming message. Sends ACK of packet. Has delay and random loss to simulate network
   * layer.
   *
   * @throws IOException connection issue
   */
  void sendMessage() throws IOException {
    Header header = new Header(windowSize, 0, Type.ACK, lastSent);
    byte[] message = Utils.bytesFromBinary(header.toBinary());

    DatagramPacket response = new DatagramPacket(message, message.length, responseAddress,
        responsePort);

    // Introduce random delay, < 200 ms
    try {
      Thread.sleep(new Random().nextInt(200));
    } catch (InterruptedException e) {
    }
    if (new Random().nextInt(10) > 3) {
      datagramSocket.send(response);
    }
  }

}
