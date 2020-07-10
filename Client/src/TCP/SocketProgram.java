package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Class for the program. Contains main that takes arguments for how to run, whether the client or
 * the server, and what the host and port should be. Also contains the methods to run as the client
 * or server.
 */
public class SocketProgram {

  private final Scanner scanner;

  public SocketProgram() {
    scanner = new Scanner(System.in);
  }


  /**
   * Parses commandline arguments. First argument is -c for client or -s for server. Second argument
   * is the port if server, and the host if client. Third argument is only valid for client, the
   * port. Any further arguments will be ignored.
   *
   * @param args Arguments for running the program.
   */
  public static void main(String[] args) {
    SocketProgram program = new SocketProgram();

    if (args.length < 2) {
      System.out.println("Must give minimum 2 arguments in order to be a valid instance.");
    } else {
      try {
        if ("-c".equals(args[0])) {
          String host = args[1];
          int port = Integer.parseInt(args[2]);
          program.client(host, port);
        } else if ("-s".equals(args[0])) {
          int port = Integer.parseInt(args[1]);
          program.server(port);
        } else {
          System.out.println("First argument should be -c for client or -s for server.");
        }
      } catch (NumberFormatException e) {
        System.out.println("Port argument must be a valid integer.");
      } catch (IOException e) {
        // In future, let user know to run the server first
        System.out.println("Unable to connect to client/server.");
      }
    }
  }

  /**
   * Code to run for the client case.
   *
   * @param host Hostname IP/name, eg localhost for locally run, or 136.22.213.2
   * @param port The port to connect to
   * @throws IOException If unable to connect at any stage throw an exception
   */
  private void client(String host, int port) throws IOException {
    // Use nested try's for auto-closeable functionality
    try (Socket socket = new Socket(host, port)) {
      // Instantiate input and output to send and receive from server.
      // These do not need to be closed (closed does nothing according to documentation)
      OutputStream socketOutputStream = socket.getOutputStream();
      InputStream socketInputStream = socket.getInputStream();

      try (DataInputStream dataInputStream = new DataInputStream(socketInputStream);
          DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream)) {

        // Get user input for text
        System.out.println("Enter text: ");

        String message = scanner.nextLine();
        // Support specification of failing after 80 characters.
        if (message.length() > 80) {
          System.out.println("Only strings up to 80 characters in length are supported.");
        } else {
          // Send the line from the scanner to the server
          dataOutputStream.writeUTF(message);

          // Get the translated line back from the server.
          String response = dataInputStream.readUTF();
          // Print translated version
          System.out.println("Response from server: " + response);
        }
      }
    }

  }

  /**
   * Code to run for the server case.
   *
   * @param port The port to connect to
   * @throws IOException If unable to connect at any stage throw an exception
   */
  private void server(int port) throws IOException {
    // Use nested try's for auto-closeable functionality
    try (ServerSocket s = new ServerSocket(port)) {

      // Wait and accept a connection
      try (Socket socket = s.accept()) {
        // Instantiate input and output to send and receive from client.
        // These do not need to be closed (closed does nothing according to documentation)
        OutputStream socketOutputStream = socket.getOutputStream();
        InputStream socketInputStream = socket.getInputStream();

        try (DataInputStream dataInputStream = new DataInputStream(socketInputStream);
            DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream)) {

          // Get the input from the client
          String input = dataInputStream.readUTF();

          // Reverse the input
          StringBuilder reversed = new StringBuilder();
          for (Character character : input.toCharArray()) {
            reversed.insert(0, Character.isUpperCase(character) ? Character.toLowerCase(character)
                : Character.toUpperCase(character));
          }

          // Send the reversed version back.
          dataOutputStream.writeUTF(reversed.toString());
        }

      }
    }
  }
}
