package Client;

import DNS.DNSClient;
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

/**
 * Top level class for internet system client, sends get request to download files, parses first
 * html to get dependent assets and gets those as well.
 */
public class Client {

  public int webPort = 2000;
  public static final int PACKET_SIZE = 1500;
  private final String baseUrl;
  private InetAddress host = InetAddress.getByName("127.0.0.1"); // Default to localhost.
  private String cacheFolder = System.getProperty("user.home") + File.separator + "web-cache";
  private String optionalParameters = "";
  private String writePath;
  private String requestedFileString;

  /**
   * Constructs a client to receive and write out files. Has the www.site.com/folder/file.html
   * format. Creates the base URL sent over with every request and also sets the first file to get.
   *
   * @param requestedFileString
   * @throws UnknownHostException
   */
  Client(String requestedFileString) throws UnknownHostException {
    this.requestedFileString = requestedFileString;
    baseUrl = requestedFileString.substring(0, requestedFileString.lastIndexOf("/") + 1);

//    try {
////       TODO: get host from DNS.
//      String dnsResponse = new DNSClient()
//          .browserQueryIPString(requestedFileString.substring(0, requestedFileString.indexOf("/")));
//      webPort = Integer.parseInt(dnsResponse);
//    } catch (IOException e) {
//      System.out.println("Trouble resolving DNS, defaulting to localhost.");
//    }


  }

  /**
   * Writes out the file, one packet received at a time. Uses 1480 byte arrays since that is TCP
   * body size.
   *
   * @param in
   * @param out
   * @throws IOException
   */
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

  /**
   * Opens the HTML file in the default browser. Open file in default program
   * https://stackoverflow.com/questions/550329/how-to-open-a-file-with-the-default-associated
   * -program
   *
   * @param filePath
   * @throws IOException
   */
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
      System.out.println("Must give valid url argument and not too many arguments.");
    } else {

      try {
        Client client = new Client(args[0]);
        client.addDefaultCacheFolder();
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
        String sourceFile = client.writePath;
        for (String link : otherAssets) {
          client.requestedFileString = link;
          client.createDirectories();
          client.downloadFile(link);
        }
        openFile(sourceFile);
      } catch (IOException e) {
        System.out.println("Issue occurred in file transmission.");
      } catch (IllegalArgumentException e) {
        System.out.println(e.getLocalizedMessage());
      }
    }
  }

  /**
   * Connects to server, sends information over and gets files back which are written out.
   *
   * @param url String of the URL getting sent over.
   * @throws IOException
   */
  private void downloadFile(String url) throws IOException {

    try (Socket socket = new Socket(host, webPort)) {
      // Instantiate input and output to send and receive from server.
      // These do not need to be closed (closed does nothing according to documentation)
      OutputStream socketOutputStream = socket.getOutputStream();
      InputStream socketInputStream = socket.getInputStream();

      try (DataInputStream dataInputStream = new DataInputStream(socketInputStream);
          DataOutputStream dataOutputStream = new DataOutputStream(socketOutputStream)) {
        dataOutputStream.writeUTF("path:" + url + ":" + optionalParameters);
        DataOutputStream fileOut = new DataOutputStream(new FileOutputStream(new File(writePath)));
        writeFile(dataInputStream, fileOut);
      }
    }
  }

  /**
   * Creates the write directory based on user input of where to save and the URL. If no save
   * directory is given, saves to ~/web-cache/site/folder/file.html.
   *
   * @throws IOException
   */
  private void createDirectories() throws IOException {
    String[] folders = requestedFileString.split("/");
    String pathSoFar = cacheFolder;
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

  /**
   * Creates ~/web-cache as a directory to save into.
   *
   * @throws IOException
   */
  private void addDefaultCacheFolder() throws IOException {
    if (!Files.isDirectory(Paths.get(cacheFolder))) {
      Files.createDirectory(Paths.get(cacheFolder));
    }
  }

  /**
   * Sets a valid string to the cache folder, or throws exception.
   *
   * @param arg1
   * @param arg2
   */
  private void setCacheFolder(String arg1, String arg2) {
    if (validCacheFolder(arg1) ^ validCacheFolder(arg2)) {
      this.cacheFolder = validCacheFolder(arg1) ? arg1.toLowerCase().replace("save=", "") :
          arg2.toLowerCase().replace("save=", "");
    } else {
      throw new IllegalArgumentException("Must provide one valid path");
    }
  }

  /**
   * Checks if a valid cache folder.
   *
   * @param arg
   * @return
   */
  private boolean validCacheFolder(String arg) {
    return arg.toLowerCase().startsWith("save=") && Files
        .isDirectory(Paths.get(arg.toLowerCase().replace("save=", "")));
  }

  /**
   * Sets valid optional parameters, such as size and language, or throws exception if invalid.
   *
   * @param arg1
   * @param arg2
   */
  private void setOptionalParameters(String arg1, String arg2) {
    if (validHeader(arg1) ^ validHeader(arg2)) {
      this.optionalParameters = validHeader(arg1) ? arg1.toLowerCase().replace("header=", "") :
          arg2.toLowerCase().replace("header=", "");
    } else {
      throw new IllegalArgumentException("Must provide valid header");
    }
  }

  /**
   * Checks for optional paramet/header validity. Needs to contain size, language, or both
   * arguments.
   *
   * @param arg
   * @return
   */
  private boolean validHeader(String arg) {
    return arg.toLowerCase().startsWith("header=") && (arg.toLowerCase().contains("size:") || arg
        .toLowerCase().contains("language:"));
  }
}
