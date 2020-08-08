package DNS;

import java.io.IOException;

public class ExampleClient {

  public static void main(String[] args) throws IOException {

    int ROOT_SERVER_PORT = 31111;
    int LOCAL_DNS_SERVER_PORT = 11166;
    Registrar registrar = new Registrar();
    DNSResolver dnsResolver = new DNSResolver();

    DNSServer localServer = registrar.createLocalDNSServer(LOCAL_DNS_SERVER_PORT);
    registrar.registerDomainServer(localServer, "com.dnsServer", "walmart.com");
    registrar.registerDomainServer(localServer, "org.dnsServer", "geeksforgeeks.org");


    /* this code would be in the browser client */
    // create a new DNSClient()
    DNSClient client = new DNSClient();
    client.setLocalServer(localServer);

    String walmartAddress = client.browserQueryIPString("http://www.walmart.com");
    System.out.println("Walmart ip address is " + walmartAddress);


  }
}


