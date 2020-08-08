import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class DNSServer {

    private static final int RESPONSE_BUFFER = 512; // in bytes
    String type; // local, root, TLD, authoritative
    String domainName;
    String ipAddress = "localhost";
    String dnsServerName;
    int portNum;
    DCache cache;
    DNSResolver resolver;
    DatagramSocket responseSocket;
    LinkedBlockingQueue<String> ipRoundRobin = new LinkedBlockingQueue<String>();
    DNSClient client = new DNSClient(new Registrar());

    public DNSServer(String type, String dnsServerName, int portNum) {
        this.type = type; // TLD root auth
        this.domainName = domainName;
        this.dnsServerName = dnsServerName;
        this.portNum = portNum; // make it a random portNum within a range?
        this.cache = new DCache(40, 10, 80); // default max size?
        this.resolver = new DNSResolver();
        try {
            this.responseSocket = new DatagramSocket(portNum);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    String getType() {
        return this.type;
    }

    void setType(final String type) {
        this.type = type;
    }

    int getPortNum() {
        return this.portNum;
    }

    void setPortNum(final int portNum) {
        this.portNum = portNum;
    }

    String getDomainName() {
        return this.domainName;
    }

    void setDomainName(final String domainName) {
        this.domainName = domainName;
    }

    String getDnsServerName() {
        return this.dnsServerName;
    }

    void setDnsServerName(final String dnsServerName) {
        this.dnsServerName = dnsServerName;
    }

    DCache getCache() {
            return this.cache;
    }

    LinkedBlockingQueue<String> getIpRoundRobin() {
        return this.ipRoundRobin;
    }

    void setIpRoundRobin(final LinkedBlockingQueue<String> ipRoundRobin) {
        this.ipRoundRobin = ipRoundRobin;
    }

    void setCache(final DCache cache) {
        this.cache = cache;
    }

    InetAddress getIpAddress() {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(this.ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return inetAddress;
    }

    public void handleQuerySegment() throws IOException {
        ArrayList<ResourceRecord> responseRecords = new ArrayList<ResourceRecord>();
        DatagramPacket newQuerySegment = new DatagramPacket(new byte[RESPONSE_BUFFER],
                RESPONSE_BUFFER);
        System.out.println("in the handle query segment");
        try {
            this.responseSocket.receive(newQuerySegment);
        } catch (IOException e) {
            System.err.println("io exception");
        }
        InetAddress inetAddress = newQuerySegment.getAddress();
        int portNum = newQuerySegment.getPort();
        byte[] queryMessage = newQuerySegment.getData();
        byte[] qHeaderArray = Arrays.copyOfRange(queryMessage, 0, 12);
        int[] qHeaderFields =
                DNSSegment.readHeaderFields(DNSSegment.readSegmentHeader(queryMessage));
        byte[] qPayload = Arrays.copyOfRange(queryMessage, 12, queryMessage.length);
        ArrayList<String> queryAndqType = DNSSegment.readQuestionPayload(qPayload);
        String payloadQuery = queryAndqType.get(0);
        String questionType = queryAndqType.get(1);

        // if recursion is requested and the server is not a root server
      //  if((qHeaderFields[3] == 1) && !this.getType().equals("Root")){ // if recursion desired
       //     System.out.println("recursion is desired");
            // execute recursive query
         //   responseRecords = client.recursiveSearch(payloadQuery,
         //           questionType, this);
       // } else {
       responseRecords = client.iterativeSearch(payloadQuery, questionType,
                    this);
       /// }
        byte[] response = DNSSegment.createResponseSegment(queryMessage, responseRecords);
        DatagramPacket responsePacket = new DatagramPacket(response, response.length,
                inetAddress, portNum);
        this.responseSocket.send(responsePacket);
    }

    public String selectIP(DNSServer dnsServer) {
        String usedIP = dnsServer.getIpRoundRobin().poll();
        dnsServer.getIpRoundRobin().add(usedIP);
        return usedIP;
    }
}
