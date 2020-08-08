import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class DNSClient {

    private DatagramSocket querySocket;
    int ROOT_SERVER_PORT = 31111;
    private static final int RESPONSE_BUFFER = 512; // in bytes
    private DNSServer localServer;
    private Registrar registrar = new Registrar();
    private DNSResolver dnsResolver= new DNSResolver();

    public DNSClient() {
        try {
            this.querySocket = new DatagramSocket();

        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public DNSClient(Registrar registrar) {
        this.registrar = registrar;

    }

    DNSServer getLocalServer() {
        return this.localServer;
    }

    void setLocalServer(final DNSServer localServer) {
        this.localServer = localServer;
    }

    public InetAddress browserHostNameQueryIP(String URL) throws IOException {
        InetAddress queryResponse = null;
        ArrayList<ResourceRecord> responseRecords = new ArrayList<ResourceRecord>();
        if (DNSResolver.validateURL(DNSResolver.URL_REGEX, URL)) {
            String hostName = DNSResolver.retrieveHostNameFromURL(URL);
            queryDNSServer(this.localServer.getPortNum(),
                    this.localServer.getIpAddress(), 0, 0,
                    1, 1, hostName);
            responseRecords = handleResponseSegment();
            for(ResourceRecord rr: responseRecords) {
                if(rr.getName().equals(hostName)) {
                    queryResponse = InetAddress.getByName(rr.getValue());
                    break;
                }
            }
        }

        return queryResponse;
    }

    public String browserQueryIPString(String URL) throws IOException {
        String ipAddress = null;
        ArrayList<ResourceRecord> responseRecords = new ArrayList<ResourceRecord>();
        if (DNSResolver.validateURL(DNSResolver.URL_REGEX, URL)) {
            String hostName = DNSResolver.retrieveHostNameFromURL(URL);
            System.out.println("hostname " + hostName);
            queryDNSServer(this.localServer.getPortNum(),
                    this.localServer.getIpAddress(), 0, 0,
                    1, 1, hostName);
            responseRecords = this.handleResponseSegment();
            for(ResourceRecord rr: responseRecords) {
                if(rr.getName().equals(hostName)) {
                    ipAddress = rr.getValue();
                    break;
                }
                // todo: need to add ":portNum" to the String
        }

    }
        return ipAddress;
    }

    void queryDNSServer(int portNum, InetAddress inetAddress, int questionType,
                        int authFlag, int recursionDesiredFlag, int numQuestions,
                        String queryHostName) throws IOException {
        byte[] querySegment = DNSSegment.createQuerySegment(questionType, authFlag,
                recursionDesiredFlag, numQuestions, queryHostName);
        System.out.println("created segment");
        DatagramPacket queryPacket = new DatagramPacket(querySegment, querySegment.length,
                inetAddress, portNum);
        sendQuerySegment(querySegment, inetAddress, portNum);
    }

    void sendQuerySegment(byte[] querySegment, InetAddress hostAddress, int portNum) throws IOException {
        DatagramPacket queryPacket = new DatagramPacket (querySegment, querySegment.length,
                hostAddress, portNum);
        System.out.println("about to send the packet");
        this.querySocket.send (queryPacket); // send the query
       }

    ArrayList<ResourceRecord> handleResponseSegment() throws IOException {
        System.out.println("in the handle repoonse segment, about to process");
        ArrayList<ResourceRecord> rrs = new ArrayList<>();

        DatagramPacket newResponseSegment = new DatagramPacket(new byte[RESPONSE_BUFFER],
                RESPONSE_BUFFER);
        try {
            this.querySocket.receive(newResponseSegment);
        } catch (IOException e) {
            System.err.println("io exception");
        }
        rrs = processResponse(newResponseSegment);
        return rrs;
    }

    public static ArrayList<ResourceRecord> processResponse(DatagramPacket responseSegment) throws IOException {
        System.out.println("in the process response");
        byte[] responseMessage = responseSegment.getData();
        byte[] responseHeader = Arrays.copyOfRange(responseMessage, 0, 12);
        int[] responseHeaderFields =
                DNSSegment.readHeaderFields(DNSSegment.readSegmentHeader(responseHeader));
        System.out.println("response header fields");
        byte[] responsePayload = Arrays.copyOfRange(responseMessage, 12,
                responseMessage.length);
        ArrayList<ResourceRecord> responseRecords =
                (ArrayList<ResourceRecord>) DNSSegment.readResponsePayload(responsePayload);
        return responseRecords;
    }


    public ArrayList<ResourceRecord> recursiveSearch(String query, String questionType,
                                                         DNSServer server)
                throws UnknownHostException, IOException {

            ArrayList<ResourceRecord> rrSet = new ArrayList<>();
            ArrayList<ResourceRecord> aRecordSet = new ArrayList<>();
            ArrayList<ResourceRecord> errorSet = new ArrayList<>();
            // if can't get the exact address from the first server cache
            rrSet = iterativeSearch(query, "A", server);

            if (!rrSet.isEmpty() && rrSet != null) {
                return rrSet;
            } else {
                // go to root and get the tld/name server
                String tldQuery = DNSResolver.retrieveTLDFromURL(query);
                queryDNSServer(this.localServer.getCache().getCacheMap().get("Root").record.getPortNum(),
                        InetAddress.getByName(this.localServer.getCache().getCacheMap().get("Root").record.getValue()),
                        5, 0, 0, 1, tldQuery);
                rrSet = handleResponseSegment();
                if (!rrSet.isEmpty() && rrSet != null) {
                    for (ResourceRecord rr : rrSet) {
                       queryDNSServer(rr.getPortNum(), InetAddress.getByName(rr.getValue()),
                                1, 0, 1, 1, query);

                        aRecordSet = handleResponseSegment();
                    }
                    if (!rrSet.isEmpty() && rrSet != null) {
                        return aRecordSet;
                    } else {
                        ResourceRecord errorRecord = this.registrar.createErrorRecord("NO_DOMAIN_FOUND",
                                query);
                        errorSet.add(errorRecord);
                        return errorSet;
                    }

                } else {
                    ResourceRecord tldErrorRecord = this.registrar.createErrorRecord("NO_TLD_FOUND",
                            query);
                    errorSet.add(tldErrorRecord);
                    return errorSet;
                }
            }
        }

        public ArrayList<ResourceRecord> iterativeSearch(String query,
                                                         String questionType, DNSServer server) throws IOException {
            ArrayList<ResourceRecord> rrSet = new ArrayList<ResourceRecord>();
            ConcurrentHashMap<String, DCache.CachedObject> filteredRecords =
                    dnsResolver.rrQuery(query,
                    questionType, server.getCache());
            System.out.println(filteredRecords.keySet());
            if(filteredRecords.isEmpty()) {
                rrSet.add(this.registrar.createErrorRecord("DOMAIN NOT FOUND", query));
            } else {
                for (String key: filteredRecords.keySet()) {
                    rrSet.add(filteredRecords.get(key).record);
                }
            }
            System.out.println(rrSet.size());
            return rrSet;

        }

    }



