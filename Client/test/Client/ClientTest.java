package Client;

import static Client.Client.buildDefaultFilePath;
import static Client.Client.getServerFilePath;
import static org.junit.jupiter.api.Assertions.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.Cleaner;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;

class ClientTest {
  String sampleURL = "somecharacters.someothercharacters.com";
  String sampleURLWithFile = "somecharacters.someothercharacters.com/folder/file.html";
  String sampleRealURL = "www.geeksforgeeks.org/geeksforgeeks/GFG_Courses_Practice_GeeksforGeeks.html";

  @Test
  void test() throws UnknownHostException {
    assertEquals(InetAddress.getByName("google.com").toString(), "google.com/172.217.165.142");
  }

  @Test
  void testFormatFile() {
    assertEquals(File.separator, "\\");
//    assertEquals(File.separator, "\\");
//    assertEquals(Client.buildFileName(), "c");
  }

  @Test
  void testWriteFile() throws IOException {

    DataOutputStream fileOut = new DataOutputStream(
        new FileOutputStream(new File(buildDefaultFilePath(getServerFilePath(sampleURL)))));
    DataInputStream fileIn = new DataInputStream(new FileInputStream(new File("src/Client/Client.java")));
    Client.writeFile(fileIn, fileOut);
    assertTrue(new File(buildDefaultFilePath(getServerFilePath(sampleURL))).isFile());
    Client.openFile(buildDefaultFilePath(getServerFilePath(sampleURL)));
  }

  @Test
  void testWriteWithPath() throws IOException {
    DataOutputStream fileOut = new DataOutputStream(
        new FileOutputStream(new File(buildDefaultFilePath(getServerFilePath(sampleURLWithFile)))));
    DataInputStream fileIn = new DataInputStream(new FileInputStream(new File("src/Client/Client.java")));
    Client.writeFile(fileIn, fileOut);
    assertTrue(new File(buildDefaultFilePath(getServerFilePath(sampleURLWithFile))).isFile());
    Client.openFile(buildDefaultFilePath(getServerFilePath(sampleURLWithFile)));
  }

  @Test
  void testWriteReal() throws IOException {
    DataOutputStream fileOut = new DataOutputStream(
        new FileOutputStream(new File(buildDefaultFilePath(getServerFilePath(sampleRealURL)))));
    DataInputStream fileIn = new DataInputStream(new FileInputStream(new File("src/Client/Client.java")));
    Client.writeFile(fileIn, fileOut);
    assertTrue(new File(buildDefaultFilePath(getServerFilePath(sampleRealURL))).isFile());
    Client.openFile(buildDefaultFilePath(getServerFilePath(sampleRealURL)));
  }


  @Test
  void testMain()
  {
    Client.main(new String[]{
       sampleRealURL
    });
  }
}