import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Registrar {

     /*need to be able to input a domain name and names and ip addresses of primary and secondary
     authoritative DNS servers

     For each of the authoritative DNS servers, the registrar would make sure that a type NS and
     a Type A record are entered into the TLD.com servers. Also have to make sure that the TypeA
     resource record for your web server and the type mx resource for your mail server are
     entered into your authoritative DNS. */

    private static final int DEFAULT_TTL = 40; // in seconds
    private ConcurrentHashMap<String, String> registeredDomains =
            new ConcurrentHashMap<String, String>();
    private ServerTreeNode serverTree;

    public void registerDomain(ServerTreeNode parentServerNode, String hostName,
                               String ipAddress, String domainName, String dnsServerName) {
        if (!registeredDomains.keySet().contains(hostName)) {
            ResourceRecord aRecord = createARecord(hostName, ipAddress);
            ResourceRecord nsRecord = createNSRecord(dnsServerName, domainName);
            parentServerNode.getServer().getCache().addRecord(aRecord);
            parentServerNode.getParent().getServer().getCache().addRecord(nsRecord);
            registeredDomains.put(hostName, dnsServerName);
        }
    }

    public void registerDomainServer(DNSServer parentServer, String dnsServerName,
                                     String dnsServerIP) {
        ResourceRecord nsRecord = createNSRecord(dnsServerName, dnsServerIP);
        parentServer.getCache().addRecord(nsRecord);
    }

    public DNSServer createAuthServer(String hostname, int portNum) {
        DNSServer authServer = new DNSServer("auth", hostname + ".dnsServer", portNum);
        return authServer;
    }

    public DNSServer createTLDServer(String extension) {
        DNSServer tldServer = new DNSServer("TLD", extension + ".dnsServer", 12359);
        return tldServer;
    }

    public DNSServer createLocalDNSServer() {
        DNSServer localServer = new DNSServer("Local", "Local Server", 12345);
        return localServer;
    }

    public DNSServer createRootServer() {
        DNSServer rootServer = new DNSServer("Root", "Root server", 15666);
        return rootServer;
    }

    public ResourceRecord createARecord(String hostName, String ipAddress) {
        ResourceRecord aRecord = new ResourceRecord(hostName, ipAddress, "A", DEFAULT_TTL);
        return aRecord;
    }

    public ResourceRecord createNSRecord(String dnsServerName, String domainName) {
        ResourceRecord nsRecord = new ResourceRecord(dnsServerName, domainName, "NS", DEFAULT_TTL);
        return nsRecord;
    }

    public ServerTreeNode createServerTree(DNSServer rootServer, ArrayList<DNSServer> tldServers,
                                           ArrayList<DNSServer> authServers) {
        ServerTreeNode serverTree = new ServerTreeNode(rootServer, null);
        for (DNSServer tldServer : tldServers) {
            serverTree.addChild(tldServer);
        }
        for (int i = 0; i < serverTree.getChildren().size(); i++) {
            serverTree.getChildren().get(i).addChild(authServers.get(i));
        }
        return serverTree;
    }
}

    /* type: A, then Name is a hostname and Value is the IP address for the hostname (relay1.bar
    .foo.com, 145.37.93.126, A) is a type A record
    // type: NS (Name is a domain, such as foo.com, and value is the hostname of an authoritative
    // DNS server that knows how to obtain the IP addresses for hosts in the domain. E.g., {foo
    // .com, dns.foo.com, NS)
    // port number for local is 12345
    // port number for authoritative is 23456
    // port number for TLDs is 78910
    public void createMXRecord() {

    }

     */

