import java.net.*;
import java.util.regex.*;

public class DNSServer {

    // edu-servers.net
    // com-servers.net
    // org-servers.net
    // root can only do iterative


    String urlRegex = "^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*." +
            "(com|net|edu)$";

    String type; // local, root, TLD, authoritative
    String domainName;
    String ipAddress = "localhost";
    String dnsServerName;
    int portNum;
    DNSCache cache;

    public DNSServer(String type, String dnsServerName, int portNum) {
        this.type = type; // TLD root auth
        this.domainName = domainName;
        this.dnsServerName = dnsServerName;
        this.portNum = portNum; // make it a random portNum within a range?
        this.cache = new DNSCache(); // default max size?
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

    DNSCache getCache() {
        return this.cache;
    }

    void setCache(final DNSCache cache) {
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

    // has local, authoritative, TLD
    // port number for local is 12345
    // port number for authoritative is 23456
    // port number for TLDs is 78910

  //  private static class DNSResponse() {


   // }
}
