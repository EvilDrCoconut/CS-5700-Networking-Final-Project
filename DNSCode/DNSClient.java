import com.sun.xml.internal.bind.v2.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class DNSClient {

    // TODO: finish process response, add Registrar code to set up initial server tree

    private DatagramSocket querySocket;
    private static final int RESPONSE_BUFFER = 512; // in bytes
    private static final int LOCAL_DNS_SERVER_PORT = 12345;
    private DNSResolver dnsResolver = new DNSResolver();

    public DNSClient() {

        try {
            this.querySocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public InetAddress browserHostNameQuery(String URL) throws IOException {
        InetAddress queryResponse = null;
        if (this.dnsResolver.validateURL(this.dnsResolver.urlRegex, URL)) {
            String hostName = dnsResolver.retrieveHostNameFromURL(URL);
            queryDNSServer(12345, InetAddress.getLocalHost(), 0, 0,
                    1, 1, hostName);
            queryResponse = handleResponseSegment();

        }
        return queryResponse;
    }

    public void queryDNSServer(int portNum, InetAddress inetAddress, int questionType,
                               int authFlag, int recursionDesiredFlag, int numQuestions,
                               String queryHostName) throws IOException {

        byte[] querySegment = DNSSegment.createQuerySegment(questionType, authFlag,
                recursionDesiredFlag, numQuestions, queryHostName);
        DatagramPacket queryPacket = new DatagramPacket(querySegment, querySegment.length,
                inetAddress, portNum);
        this.querySocket.send(queryPacket);
    }

    public InetAddress handleResponseSegment() {
        DatagramPacket newResponseSegment = new DatagramPacket(new byte[RESPONSE_BUFFER],
                RESPONSE_BUFFER);
        try {
            this.querySocket.receive(newResponseSegment);
        } catch (IOException e) {
            System.err.println("io exception");
        }
        InetAddress siteIP = null; //this.processResponse(newResponseSegment);
        return siteIP;
    }

    /*
    private InetAddress processResponse(DatagramPacket responseSegment) {
        byte[] responseMessage = responseSegment.getData();
        byte[] responseHeader = Arrays.copyOfRange(responseMessage, 0, 12);
        byte[] responsePayload = Arrays.copyOfRange(responseMessage, 12,
                responseMessage.length-1);
        int[] responseHeaderFields = DNSSegment.readread the header fields
        // read the payload and extract the ip address
        // return ip address.


        return null;
    }


    /*

    public static InetAddress lookupIPAddress(String URL, DNSCache dnsCache) throws UnknownHostException {
        DNSResolver dnsResolver = new DNSResolver();
        return dnsResolver.DNSLookupByURL(URL, dnsCache);
    }


   // String textString = new String(payload) converting byte array back into string


    public static void main(String[] args) throws UnknownHostException {

        // create n DNS servers, assign ports, create caches

        DNSCache dnsCache = new DNSCache();
        DNSRecord eduRecord = new DNSRecord("https://www.abc12345.edu", "TLD", "73.169.179.107",
                12345);
        DNSRecord netRecord = new DNSRecord("https://www.hostnameNet.net", "TLD", "149.253.249.247",
                12596);
        DNSRecord comRecord = new DNSRecord("https://www.hostCom.com", "TLD", "149.253.249.247",
                12596);

        dnsCache.getDnsCacheMap().put("https://www.abc12345.edu", eduRecord);
        dnsCache.getDnsCacheMap().put("https://www.hostnameNet.net", netRecord);
        dnsCache.getDnsCacheMap().put("https://www.hostCom.com", comRecord);

        InetAddress myInetAddress = lookupIPAddress("https://www.abc12345.edu", dnsCache);
        System.out.println(myInetAddress.toString());

    }
    */
}
