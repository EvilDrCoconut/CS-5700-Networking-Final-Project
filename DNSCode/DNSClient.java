import com.sun.xml.internal.bind.v2.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class DNSClient {

    // TODO: finish process response, add Registrar code to set up initial server tree

    private DatagramSocket querySocket;
    private DatagramSocket responseSocket;
    private static final int RESPONSE_BUFFER = 512; // in bytes
    private static final int LOCAL_DNS_SERVER_PORT = 12345;

    DNSServer getLocalServer() {
        return this.localServer;
    }

    void setLocalServer(final DNSServer localServer) {
        this.localServer = localServer;
    }

    private DNSServer localServer;
    private DNSResolver dnsResolver = new DNSResolver();
    private DNSResolverController dnsResolverController = new DNSResolverController();
    private Registrar registrar = new Registrar();
    private ServerTreeNode serverTree = null;
    private ServerTreeNode localServerNode;


    ServerTreeNode getLocalServerNode() {
        return this.localServerNode;
    }

    void setLocalServerNode(final ServerTreeNode localServerNode) {
        this.localServerNode = localServerNode;
    }

    /*public DNSClient() {

            try {
                this.querySocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

         */
    public DNSClient() {
        try {
            this.querySocket = new DatagramSocket();
            this.responseSocket = new DatagramSocket(LOCAL_DNS_SERVER_PORT);

        } catch (SocketException e) {
            e.printStackTrace();
        }

        this.localServer = this.registrar.createLocalDNSServer();
        DNSServer rootServer = this.registrar.createRootServer();
        DNSServer comServer = this.registrar.createTLDServer("com");
        DNSServer orgServer = this.registrar.createTLDServer("org");
        DNSServer eduServer = this.registrar.createTLDServer("edu");
        ArrayList<DNSServer> tldServers = new ArrayList<DNSServer>();
        tldServers.add(comServer);
        tldServers.add(orgServer);
        tldServers.add(eduServer);

        DNSServer walmartServer = this.registrar.createAuthServer("walmart", 78956);
        DNSServer gforgServer = this.registrar.createAuthServer("geeksforgeeks", 78957);
        DNSServer northeasternServer = this.registrar.createAuthServer("northeastern", 78958);
        DNSServer tuftsServer = this.registrar.createAuthServer("tufts", 78959);
        DNSServer pbsServer = this.registrar.createAuthServer("pbs", 79959);
        DNSServer wikiServer = this.registrar.createAuthServer("wikipedia", 79659);

        ServerTreeNode rootNode = new ServerTreeNode(rootServer, null);
        rootNode.addChild(comServer);
        rootNode.addChild(orgServer);
        rootNode.addChild(eduServer);
        ServerTreeNode comNode = rootNode.getChildren().get(0);
        ServerTreeNode orgNode = rootNode.getChildren().get(1);
        ServerTreeNode eduNode = rootNode.getChildren().get(2);
        comNode.addChild(walmartServer);
        comNode.addChild(gforgServer);
        orgNode.addChild(pbsServer);
        orgNode.addChild(wikiServer);
        eduNode.addChild(northeasternServer);
        eduNode.addChild(tuftsServer);

        ServerTreeNode walmartNode = comNode.getChildren().get(0);
        ServerTreeNode gforgNode = comNode.getChildren().get(1);

        ServerTreeNode pbsNode = orgNode.getChildren().get(0);
        ServerTreeNode wikiNode = orgNode.getChildren().get(1);

        ServerTreeNode northNode = eduNode.getChildren().get(0);
        ServerTreeNode tuftsNode = eduNode.getChildren().get(1);

        this.registrar.registerDomain(walmartNode, "walmart.com",
                "83.255.110.100", "walmart.com", walmartNode.getServer().getDnsServerName());
        this.registrar.registerDomain(gforgNode, "geeksforgeeks.com",
                "83.255.110.101", "geeksforgeeks.com", gforgNode.getServer().getDnsServerName());

        this.registrar.registerDomain(pbsNode, "pbs.org",
                "83.255.120.100", "pbs.org", pbsNode.getServer().getDnsServerName());
        this.registrar.registerDomain(wikiNode, "wikipedia.org",
                "83.255.120.101", "wikipedia.org", wikiNode.getServer().getDnsServerName());

        this.registrar.registerDomain(northNode, "northeastern.edu",
                "83.255.130.100", "northeastern.edu", northNode.getServer().getDnsServerName());
        this.registrar.registerDomain(tuftsNode, "tufts.edu",
                "83.255.130.101", "tufts.edu", tuftsNode.getServer().getDnsServerName());

        ResourceRecord localARecordEdu = this.registrar.createARecord("northeastern.edu","83.255" +
                ".130.100");
        ResourceRecord localARecordCom = this.registrar.createARecord("geeksforgeeks.com", "83" +
                ".255.110.101");
        ResourceRecord localNSRecordEdu = this.registrar.createNSRecord("edu.dnsServer",
                "northeastern.edu");

        this.localServer.getCache().addRecord(localNSRecordEdu);
        this.localServer.getCache().addRecord(localARecordCom);
        this.localServerNode = new ServerTreeNode(this.localServer, null);

        this.serverTree = rootNode;
    }

    ServerTreeNode getServerTree() {
        return this.serverTree;
    }

    void setServerTree(final ServerTreeNode serverTree) {
        this.serverTree = serverTree;
    }

    public InetAddress browserHostNameQuery(String URL) throws IOException {
        InetAddress queryResponse = null;
        if (this.dnsResolver.validateURL(this.dnsResolver.urlRegex, URL)) {
            String hostName = dnsResolver.retrieveHostNameFromURL(URL);
            queryDNSServer(LOCAL_DNS_SERVER_PORT, InetAddress.getLocalHost(), 0, 0,
                    1, 1, hostName);
            queryResponse = this.handleResponseSegment();

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
        sendQuerySegment(querySegment, inetAddress, portNum);
        this.handleQuerySegment();
    }

    void sendQuerySegment (byte[] querySegment, InetAddress hostAddress, int portNum) throws IOException {
        DatagramPacket queryPacket = new DatagramPacket (querySegment, querySegment.length,
                hostAddress, portNum);
        this.querySocket.send (queryPacket); // send the query
       }

    public void handleQuerySegment() throws IOException {
        DatagramPacket newQuerySegment = new DatagramPacket(new byte[RESPONSE_BUFFER],
                RESPONSE_BUFFER);
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
        String payloadQuery = DNSSegment.readQuestionPayload(qPayload).get(0);
        ResourceRecord rr = this.dnsResolverController.recursiveSearch(payloadQuery,
                this.localServerNode, this.serverTree);
        byte[] response = DNSSegment.createResponseSegment(queryMessage, rr);
        DatagramPacket responsePacket = new DatagramPacket (response, response.length,
                inetAddress, portNum);
        this.responseSocket.send(responsePacket);
    }

    public InetAddress handleResponseSegment() throws IOException {
        DatagramPacket newResponseSegment = new DatagramPacket(new byte[RESPONSE_BUFFER],
                RESPONSE_BUFFER);
        try {
            this.querySocket.receive(newResponseSegment);
        } catch (IOException e) {
            System.err.println("io exception");
        }
        InetAddress siteIP = this.processResponse(newResponseSegment);
        return siteIP;
    }

    public InetAddress processResponse(DatagramPacket responseSegment) throws IOException {
        byte[] responseMessage = responseSegment.getData();
        byte[] responseHeader = Arrays.copyOfRange(responseMessage, 0, 12);
        int[] responseHeaderFields =
                DNSSegment.readHeaderFields(DNSSegment.readSegmentHeader(responseHeader));
        byte[] responsePayload = Arrays.copyOfRange(responseMessage, 12,
                responseMessage.length);
       // int[] responseHeaderFields = DNSSegment.readread the header fields
        ResourceRecord responseRecord =
                (ResourceRecord) DNSSegment.readResponsePayload(responsePayload);
        InetAddress responseIP = null;
        try {
             responseIP = InetAddress.getByName(responseRecord.getValue());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return responseIP;
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
