package Client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.junit.jupiter.api.Test;
import webServer.MultithreadedServer;

class ClientTest {

  String sampleURL = "somecharacters.someothercharacters.com";
  String sampleURLWithFile = "somecharacters.someothercharacters.com/folder/file.html";
  String sampleRealURL = "www.geeksforgeeks.org/geeksforgeeks/GFG_Courses_Practice_GeeksforGeeks"
      + ".html";

  @Test
  void test() throws UnknownHostException {
    assertEquals(InetAddress.getByName("google.com").toString(), "google.com/172.217.165.142");
  }

  @Test
  void testFormatFile() {
    assertEquals("www.dsaf.com/folder/file.html".substring(0, "www.dsaf.com/folder/file.html".indexOf("/")), "www.dsaf.com");
//    assertEquals(File.separator, "\\");
//    assertEquals(File.separator, "\\");
//    assertEquals(Client.buildFileName(), "c");
  }

  @Test
  void testWriteFile() throws IOException {

  }

  @Test
  void testWriteWithPath() throws IOException {
  }

  @Test
  void testWriteReal() throws IOException {
  }

  @Test
  void testGeeksForGeeks() throws IOException, InterruptedException {
    MultithreadedServer.main(new String[]{});
    Thread.sleep(3000);
    Client.main(new String[]{"www.geeksforgeeks.org/geeksforgeeks/GeeksforGeeks_Home.html"});
  }

  @Test
  void testMain() {
    Client.main(new String[]{
        sampleRealURL
    });
  }
}