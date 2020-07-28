package Client;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Client {

  static public int webPort = 2000;
  static public int packetSize = 1500;

  static void addCacheFolder() throws IOException {
    if (!Files
        .isDirectory(Paths.get(System.getProperty("user.home") + File.separator + "web-cache"))) {
      Files.createDirectory(
          Paths.get(System.getProperty("user.home") + File.separator + "web-cache"));
    }
  }

  static String buildDefaultFilePath(String serverPath) {
    return buildFilePath(serverPath,
        System.getProperty("user.home") + File.separator + "web-cache");
  }

  static String buildFilePath(String serverPath, String folderPath) {
    return folderPath + File.separator + serverPath;
  }

  static String getURL(String input) {
    String url;
    if (input.indexOf('/') != -1) {
      url = input.substring(0, input.indexOf('/'));
    } else {
      url = input;
    }
    return url;
  }

  static String getServerFilePath(String input) {
    String filePath;
    if (input.indexOf('/') != -1) {
      filePath = input.substring(input.indexOf('/'));
    } else {
      filePath = "/index.html";
    }
    return filePath;
  }

  static void writeFile(DataInputStream in, DataOutputStream out) throws IOException {
    // Write the file out from the stream
    // -1 if end of stream
    byte[] writeBytes = new byte[packetSize];
    int response = in.read(writeBytes);
    while (response != -1) {
      out.write(writeBytes);
      writeBytes = new byte[packetSize];
      response = in.read(writeBytes);
    }
  }

  static void openFile(String filePath) throws IOException {
    Desktop.getDesktop().open(new File(filePath));
  }

  /**
   * Parses commandline arguments. First argument is a url, in the format
   * hello.testing.com/folder/file.html. Two other arguments may be provided, prefaced by headers=
   * and save=. Headers is a string for any parameters to be passed with, for instance "headers=GET:
   * { accept: gif, language: english }". headers= is required for parsing. Save is where the files
   * downloaded should be saved. defaults to ~/web-cache if none is provided.
   *
   * @param args Arguments for running the program.
   */
  public static void main(String[] args) {

    if (args.length < 1 || args.length > 3) {
      System.out.println("Must give minimum url argument in order to be a valid instance.");
    } else {

      try {
        String serverFilePath = getServerFilePath(args[0]);
        String filePath = buildDefaultFilePath(serverFilePath);;
        String parameters =  "path: " + serverFilePath;
        if (args.length == 1) {
          addCacheFolder();
        } else if (args.length == 2) {
          if (args[1].toLowerCase().startsWith("headers=")) {

          } else if (args[1].toLowerCase().startsWith("save=")) {

          } else {
            System.out.println("Make sure that each argument corresponds to a valid key.");
          }
        } else if (args.length == 3) {
          if (args[1].toLowerCase().startsWith("headers=") && args[2].toLowerCase()
              .startsWith("save=")
              || args[1].toLowerCase().startsWith("save=") && args[2].toLowerCase()
              .startsWith("header=")) {
            System.out.println(
                "Make sure that each argument corresponds to a valid key and only one argument "
                    + "should have each key.");
          } else {
            if (Files.isDirectory(Paths.get(args[1].toLowerCase().replace("save=", ""))) ||
                Files.isDirectory(Paths.get(args[2].toLowerCase().replace("save=", "")))) {

              // Should have special formatting for header/parameter string
              parameters =
                  args[1].contains("headers=") ? args[1].toLowerCase().replace("headers=", "")
                      : args[2].toLowerCase().replace("headers=", "");

              filePath = args[1].contains("save=") ? buildFilePath(serverFilePath,
                  args[1].toLowerCase().replace("save=", ""))
                  : buildFilePath(serverFilePath, args[2].toLowerCase().replace("save=", ""));
            } else {
              System.out.println("Make sure valid directory path");
            }
          }

        }



        // Arg 0 is the website url.
        // hello.testing.com/folder/file.html
//        InetAddress host = InetAddress.getByName(getURL(args[0]));

        // Localhost is 127.0.0.1
        // TODO ****
        InetAddress host = InetAddress.getByName("127.0.0.1");

        addCacheFolder();

        try (Socket socket = new Socket(host, webPort)) {
          // Instantiate input and output to send and receive from server.
          // These do not need to be closed (closed does nothing according to documentation)
          OutputStream socketOutputStream = socket.getOutputStream();
          InputStream socketInputStream = socket.getInputStream();

          try (DataInputStream dataInputStream = new DataInputStream(socketInputStream);
              DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream)) {
            dataOutputStream.writeUTF(parameters);

            DataOutputStream fileOut = new DataOutputStream(
                new FileOutputStream(new File(filePath)));
            // Get the translated line back from the server.

            writeFile(dataInputStream, fileOut);

            // Open file in default program
            // https://stackoverflow.com/questions/550329/how-to-open-a-file-with-the-default
            //-associated-program
            openFile(filePath);
          }
        }

      } catch (IOException e) {
        System.out.println(e);
        // In future, let user know to run the server first
        System.out.println("Unable to connect to client/server.");
      }
    }
  }

}
