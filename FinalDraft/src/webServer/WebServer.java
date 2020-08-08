package webServer;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * class for the web server that handles the listens for clients and handles their requests
 */
public class WebServer extends Thread implements WebServerInterface {

  private Socket clientSock;
  private int serverAllowanceCheck;

  /**
   * Constructor of the webServer.WebServer Class, takes no variables and sets up
   *        on localhost, through port 2000
   */
  public WebServer(String ip, Socket inSocket)  {

      clientSock = inSocket;
      String[] ipNums = ip.split("\\.");
      serverAllowanceCheck = Integer.parseInt(ipNums[2]);

  }


  /**
   * method to listen and accept client connections
   * @throws IOException if data stream can't be created due to socket not be found
   */
  @Override
  public void listenAndAccept() throws IOException {

    // if socket from multithreaded server is bound, then creates an input to read request
      if (clientSock.isBound()) {
        DataInputStream received = new DataInputStream(new BufferedInputStream(clientSock.getInputStream()));
        String request = received.readUTF();
        System.out.println(request);
        // creates web server handler and parses request header
        WebServerHandler helper = new WebServerHandler(request);
        helper.handle();

        // creates echo get header and parses requested url
        EchoGetHeader path = new EchoGetHeader(helper.retPath());
        System.out.println(helper.retPath());
        path.handle();

        // receives requested file and
        packageAndSend(helper.retLang(), helper.retSize(), path.retEndCap(), path.retFolder(), path.retFile());
    }
  }

  /**
   * Method to find the requested url file, package it, and send it back to the client
   * @param lang the requested language, if not given, assumed English
   * @param size the requested size of the file returned, or segmented into smaller sections
   * @param endCap the type of site requested, verifies web server can reach it
   * @param folder the folder path to the file
   * @param file the file specifically requested
   * @throws IOException exception if unable to send byte array back to client
   */
  @Override
  public void packageAndSend(String lang, String size, String endCap, String folder, String file) throws IOException {

    // creates a string to the desired file's path, also checks language parameter if given
    int error404Check = 1;
    String endCapGiven = "";

    // checks if current web server can reach requested files
    if(endCap.equals("com") && serverAllowanceCheck == 110){
      error404Check = 0;
      endCapGiven = endCap;
    } else if(endCap.equals("org") && serverAllowanceCheck == 130){
      error404Check = 0;
      endCapGiven = endCap;
    } else if(endCap.equals("edu") && serverAllowanceCheck == 120){
      error404Check = 0;
      endCapGiven = endCap;
    } else{
      System.out.println("Internal database error.");
    }

    // creates final path based on language requested
    String path = "";
    switch(lang) {
      case "":
      case "e":
        path = "database" + "/" + "english" + "/" + endCapGiven +  folder + "/" + file;
        break;
      case "f":
        path = "database" + "/" + "french" + "/" + endCapGiven + folder + "/" + file;
        break;
      case "s":
        path = "database" + "/" + "spanish" + "/" + endCapGiven + folder + "/" + file;
    }

    System.out.println(path);

    // initiate a fileinputstream to help retrieve all of file, if file not found,
    //      404 error to client.
    FileInputStream buf = null;
    File dummyFile = new File(path);
    long sizeCheck = dummyFile.length();
    String error404 = "File given is not found, check spelling and try again.";
    try{
      buf = new FileInputStream(dummyFile);
    } catch (FileNotFoundException e){
      System.out.println(error404);
      error404Check = 1;
    }

    // if file requested can be reached, then a payload byte array is created and the file
    //      is read into it.  then sent back to the client.
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
        out.close();
      } catch (IOException e) {
        System.out.println("Error Code 01");
      }
    } else{
      // if file was not found, then send back a 404 error message to client
      try {
        System.out.println("Sending 404 message");
        OutputStream out = clientSock.getOutputStream();
        byte[] error = error404.getBytes();
        out.write(error);
        out.close();
      } catch (IOException e) {
        System.out.println("Error Code 01");
      }
    }
  }

}