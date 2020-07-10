package TransportProtocol;

import TransportProtocol.Data.DataSegment;
import TransportProtocol.Data.Header;
import TransportProtocol.Data.Sequence;
import TransportProtocol.Data.Type;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Represents the sender of data in transport protocol. Only sends data segments and receives ACKs.
 */
public class Sender {

  private final int serverPort;
  private final InetAddress host;
  private final File file;
  private final List<DataSegment> bufferedSegments = new ArrayList<>();
  private final int windowSize;
  private int receiverWindowSize = 1;
  private int inTransmission = 0;
  private Sequence currentSegment = new Sequence();
  private FileInputStream fileBytes;
  private DatagramSocket datagramSocket;
  private boolean finalTransmission;
  private Header lastReceived;


  public Sender(int serverPort, int windowSize, InetAddress host, File file) {
    this.serverPort = serverPort;
    this.windowSize = windowSize;
    this.host = host;
    this.file = file;
  }

  /**
   * Instantiate, connect, open file, send the file over, and receive acknowledgements. Close when
   * last segment sent is acknowledged.
   *
   * @param args <destination_DNS_name> <destination_port_number> <window_size> <input_file>
   */
  public static void main(String[] args) {

    // Validate args
    if (args.length != 4) {
      System.out.println("Incorrect number of arguments");
      return;
    }
    Sender sender;
    try {
      // Args 0 is an IP
      InetAddress host = InetAddress.getByName(args[0]);
      // Arg 1 is a port
      int serverPort = Integer.parseInt(args[1]);
      // Arg 2 is the window_size
      int windowSize = Integer.parseInt(args[2]);
      if (windowSize < 1 || windowSize > 7 || serverPort < 1 || serverPort > 65000) {
        System.out
            .println("Window size must be between 1 and 7 and port number between 1 and 65000");
        return;
      }
      // Arg 3 is the file
      File file = new File(args[3]);
      sender = new Sender(serverPort, windowSize, host, file);
    } catch (NumberFormatException e) {
      System.out.println("Port number and Window size arguments must be valid integers.");
      return;
    } catch (UnknownHostException e) {
      System.out.println("Destination IP must be valid.");
      return;
    }

    // Transfer file
    try {
      // Open the file and socket connection
      sender.openConnections();

      // Keep looping as long as more data and unresolved transmissions
      while (!(sender.finalTransmission && sender.inTransmission == 0)) {
        // In try catch since a UDP timeout is an exception in java
        try {
          // Sets max window size to min of receiver and sender window size
          while (sender.receiverWindowSize > 0
              && sender.inTransmission < sender.windowSize
              && sender.inTransmission < sender.receiverWindowSize
              && !sender.finalTransmission) {
            sender.sendMessage();
          }
          sender.receiveMessage();
          System.out.println("Number in transmission: " + sender.inTransmission);
        } catch (SocketTimeoutException e) {
          sender.resendAll();
          // Assume that one resend is good enough if last transmission
          if (sender.finalTransmission) {
            sender.inTransmission = 0;
          }
        }
      }
    } catch (IOException e) {
      System.out.println("Port number and Window size arguments must be valid integers.");
    } finally {
      sender.closeConnections();
    }
  }

  /**
   * Open file and socket connection, set socket timeout to 500 ms.
   *
   * @throws IOException Issue with opening
   */
  void openConnections() throws IOException {
    if (!file.isFile()) {
      System.out.println("Invalid file.");
      return;
    }
    this.fileBytes = new FileInputStream(file);
    this.datagramSocket = new DatagramSocket();
    // half second timeout
    datagramSocket.setSoTimeout(500);
  }

  /**
   * Close the datagram and file output.
   */
  void closeConnections() {
    datagramSocket.close();
    try {
      fileBytes.close();
    } catch (IOException e) {
      System.out.println("File issues on closing.");
    }
  }


  /**
   * Send one packet to the receiver, reading the data from the file and storing the packet in a
   * buffer in case it gets lost in transmission, and needs to be resent. Increments current segment
   * and inTransmission. Has random loss to simulate network.
   *
   * @throws IOException lost connection
   */
  void sendMessage() throws IOException {
    byte[] data = new byte[DataSegment.MAX_PAYLOAD];
    // FileInputStream.read will read and output bytes into the byte array. Returns total
    // number of bytes read, should be 512 as passed in. If lower, then it's last read
    int length = this.fileBytes.read(data);
    if (length < DataSegment.MAX_PAYLOAD) {
      finalTransmission = true;
    }
    // If file is multiple of 512 bytes, need to send last empty data segment.
    if (length == -1) {
      length = 0;
    }

    // Generate the appropriate data header
    Header header = new Header(windowSize, length, Type.DATA, currentSegment);
    DataSegment outgoingSegment = new DataSegment(header, data);
    bufferedSegments.add(outgoingSegment);
    byte[] message = outgoingSegment.toBytes();

    DatagramPacket request = new DatagramPacket(message, message.length, host, serverPort);

    // Introduce droppage, 30% to lose outgoing
    if ((new Random().nextInt(10) > 3) || finalTransmission) {
      datagramSocket.send(request);
    }

    inTransmission += 1;
    // Current segment always ends as the next one to be sent
    currentSegment = currentSegment.increment();
  }

  /**
   * Receive one message. If it is an ACK that is ahead in sequence from the first buffered, it will
   * act as a cumulative acknowledgement, and all packets up to that ACK (-1) will be removed from
   * the buffer. If it is not a valid ACK, then it is ignored and all the packets are resent.
   *
   * @throws IOException connection error
   */
  void receiveMessage() throws IOException {


    // Allocate 4 bytes for the response
    byte[] buffer = new byte[Header.SIZE];
    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
    datagramSocket.receive(reply);

    lastReceived = new Header(Arrays.copyOfRange(buffer, 0, 4));
    receiverWindowSize = lastReceived.getWindow().getSize();

    // How many segments are being ack'd,
    // how much greater is the current sequence than the first buffered?
    int numACKd =
        lastReceived.getSequence().getNum() - bufferedSegments.get(0).getHeader().getSequence()
            .getNum();
    System.out.println("Number acknowledged by receiver: " + numACKd);

    // Should be more than 1 ACK'd but needs to be a valid number and type ack to be valid
    boolean isValidACK = numACKd > 0 && numACKd <= inTransmission && lastReceived.getType() == Type.ACK;
    // Cumulative acknowledgement, acknowledge up to a certain value.
    // If receiver receives out of order packet, doesn't save, all are resent
    if (isValidACK) {
      System.out.println("Received valid ACK at " + new Date().getTime());
      for (int i = 0; i < numACKd; i++) {
        inTransmission -= 1;
        bufferedSegments.remove(0);
      }
    } else {
      resendAll();
    }
  }

  /**
   * Resend all valid messages if invalid ACK or timeout.
   *
   * @throws IOException connection issues
   */
  void resendAll() throws IOException {
    for (DataSegment outgoingSegment : bufferedSegments) {
      byte[] message = outgoingSegment.toBytes();

      DatagramPacket request = new DatagramPacket(message, message.length, host, serverPort);

      datagramSocket.send(request);
    }
  }
}
