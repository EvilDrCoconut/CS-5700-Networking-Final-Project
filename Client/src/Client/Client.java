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
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Client {

  public static final int WEB_PORT = 2000;
  public static final int PACKET_SIZE = 1500;
  // Localhost is 127.0.0.1
  // TODO ****
  InetAddress host = InetAddress.getByName("127.0.0.1");
  private final String baseUrl;
  private String cacheFolder = System.getProperty("user.home") + File.separator + "web-cache";
  private String optionalParameters = "";
  private String writePath;
  private String requestedFileString;

  Client(String requestedFileString) throws UnknownHostException {
    this.requestedFileString = requestedFileString;
    baseUrl = requestedFileString.substring(0, requestedFileString.lastIndexOf("/") + 1);


  }

//
//  static String getURL(String input) {
//    String url;
//    if (input.indexOf('/') != -1) {
//      url = input.substring(0, input.indexOf('/'));
//    } else {
//      url = input;
//    }
//    return url;
//  }

  static String getServerFilePath(String input) {
    String filePath;
    if (input.indexOf('/') != -1) {
      filePath = input.substring(input.indexOf('/') + 1);
    } else {
      filePath = "index.html";
    }
    return filePath;
  }

  static void writeFile(DataInputStream in, DataOutputStream out) throws IOException {
    // Write the file out from the stream
    // -1 if end of stream
    byte[] writeBytes = new byte[PACKET_SIZE];
    int response = in.read(writeBytes);
    while (response != -1) {
      out.write(writeBytes);
      writeBytes = new byte[PACKET_SIZE];
      response = in.read(writeBytes);
    }
  }

  static void openFile(String filePath) throws IOException {
    Desktop.getDesktop().open(new File(filePath));
  }


  private void downloadFile(String url) throws IOException {

    System.out.println(url + optionalParameters);
    try (Socket socket = new Socket(host, WEB_PORT)) {
      // Instantiate input and output to send and receive from server.
      // These do not need to be closed (closed does nothing according to documentation)
      OutputStream socketOutputStream = socket.getOutputStream();
      InputStream socketInputStream = socket.getInputStream();

      try (DataInputStream dataInputStream = new DataInputStream(socketInputStream);
          DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream)) {
        System.out.println(url + optionalParameters);
//        dataOutputStream.writeUTF("path: " + url + ", " + optionalParameters);
        dataOutputStream.writeUTF("path:-" + url);

        DataOutputStream fileOut = new DataOutputStream(
            new FileOutputStream(new File(writePath)));
        // Get the translated line back from the server.

        writeFile(dataInputStream, fileOut);

      }

    }
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
  public static void main(String[] args) throws IOException {

    if (args.length < 1 || args.length > 3) {
      System.out.println("Must give valid url argument and not too many arguments.");
    } else {

      try {

        Client client = new Client(args[0]);
        if (args.length == 1) {
          client.addDefaultCacheFolder();
        }
        if (args.length == 2) {
          if (client.validCacheFolder(args[1])) {
            client.setCacheFolder(args[1], "");
          } else {
            client.setOptionalParameters(args[1], "");
          }
        } else if (args.length == 3) {
          client.setCacheFolder(args[1], args[2]);
          client.setOptionalParameters(args[1], args[2]);
        }
        client.createDirectories();

        client.downloadFile(client.requestedFileString);

        List<String> otherAssets =
            new HTMLDependencyExtractor(client.writePath).getLinks(client.baseUrl);
        System.out.println(otherAssets);
        String sourceFile = client.writePath;
        for (String link: otherAssets) {
          // TODO Need to make valid write path as well
          client.requestedFileString = link;
          client.createDirectories();
          client.downloadFile(link);
        }

        // Arg 0 is the website url.
        // hello.testing.com/folder/file.html
//        InetAddress host = InetAddress.getByName(getURL(args[0]));

        // Open file in default program
        // https://stackoverflow.com/questions/550329/how-to-open-a-file-with-the-default
        //-associated-program
        client.openFile(sourceFile);
      } finally {

      }
    }


  }

  private void createDirectories() throws IOException {
    String[] folders = requestedFileString.split("/");
    String pathSoFar = cacheFolder;
    System.out.println(pathSoFar);
    for (int i = 0; i < folders.length - 1; i++) {
      pathSoFar = pathSoFar + File.separator + folders[i];

      if (!Files
          .isDirectory(Paths.get(pathSoFar))) {
        Files.createDirectory(
            Paths.get(pathSoFar));
      }
    }
    pathSoFar = pathSoFar + File.separator + folders[folders.length - 1];
    this.writePath = pathSoFar;
  }

  private void addDefaultCacheFolder() throws IOException {
    if (!Files
        .isDirectory(Paths.get(cacheFolder))) {
      Files.createDirectory(
          Paths.get(cacheFolder));
    }
  }

  private void setCacheFolder(String arg1, String arg2) {
    if (validCacheFolder(arg1) ^ validCacheFolder(arg2)) {
      this.cacheFolder = validCacheFolder(arg1) ? arg1.toLowerCase().replace("save=", "") :
          arg2.toLowerCase().replace("save=", "");
    } else {
      throw new IllegalArgumentException("Must provide one valid path");
    }
  }

  private boolean validCacheFolder(String arg) {
    return arg.toLowerCase().startsWith("save=") && Files
        .isDirectory(Paths.get(arg.toLowerCase().replace("save=", "")));
  }

  private void setOptionalParameters(String arg1, String arg2) {
    if (validHeader(arg1) ^ validHeader(arg2)) {
      this.optionalParameters = validHeader(arg1) ? arg1.toLowerCase().replace("header=", "") :
          arg2.toLowerCase().replace("header=", "");
    } else {
      throw new IllegalArgumentException("Must provide one valid path");
    }

  }

  private boolean validHeader(String arg) {
    return arg.toLowerCase().startsWith("header=");
//    &&
  }

}
