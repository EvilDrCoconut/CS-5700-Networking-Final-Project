package DNS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class Registrar {

  /*need to be able to input a domain name and names and ip addresses of primary and secondary
  authoritative DNS servers

  For each of the authoritative DNS servers, the registrar would make sure that a type NS and
  a Type A record are entered into the TLD.com servers. Also have to make sure that the TypeA
  resource record for your web server and the type mx resource for your mail server are
  entered into your authoritative DNS. */
  private static final String BASE_OCTET1 = "168";
  private static final String BASE_OCTET2 = "198";
  private static final String COM_OCTET3 = "110";
  private static final String EDU_OCTET3 = "120";
  private static final String ORG_OCTET3 = "130";
  private static final int DEFAULT_TTL = 0; // turning off the cache for now
  // private static final int DEFAULT_TTL = 40; // in seconds
  private static final int TTL_NEVER_EXPIRES = 0;
  private ConcurrentHashMap<String, String> registeredDomains =
      new ConcurrentHashMap<String, String>();

  public void registerDomain(DNSServer parentServer, String hostName,
      String ipAddress, String domainName, String dnsServerName) {
    if (!registeredDomains.keySet().contains(hostName)) {
      ResourceRecord aRecord = createARecord(hostName, ipAddress);
      ResourceRecord nsRecord = createNSRecord(dnsServerName, domainName);
      parentServer.getCache().put(aRecord.getName(), aRecord);
      parentServer.getCache().put(nsRecord.getName(), nsRecord);
      registeredDomains.put(hostName, dnsServerName);
    }
  }

  public void registerDomainServer(DNSServer parentServer, String dnsServerName,
      String domainName) {
    ResourceRecord nsRecord = createNSRecord(dnsServerName, domainName);
    parentServer.getCache().put(nsRecord.getName(), nsRecord);
  }

  public void registerTLDServer(DNSServer rootServer, DNSServer tldServer) {
    String tldIP = tldServer.selectIP(tldServer);
    ResourceRecord tldRecord = createTLDRecord(tldServer.getDnsServerName(),
        tldIP, tldServer.getPortNum());
    System.out.println("tld get name " + tldRecord.getName());
    rootServer.getCache().put(tldRecord.getName(), tldRecord);
  }

  public void registerAuthServer(DNSServer tldServer, DNSServer authServer) {
    String authIP = authServer.selectIP(authServer);
    ResourceRecord glueRecord = createGlueRecord(authServer.getDnsServerName(),
        authIP, authServer.getPortNum());
    tldServer.getCache().put(glueRecord.getName(), glueRecord);
  }

  public void registerRootServer(DNSServer localServer, DNSServer rootServer) {
    ResourceRecord rootRecord = createRootRecord(rootServer.getDnsServerName(),
        rootServer.getIpAddress().toString(), rootServer.getPortNum());
    localServer.getCache().put(rootRecord.getName(), rootRecord);
  }

  public DNSServer createAuthServer(String hostname, int portNum) {
    DNSServer authServer = new DNSServer("A", hostname + ".dnsServer", portNum);
    return authServer;
  }

  public DNSServer createTLDServer(String extension, int portNum) {
    DNSServer tldServer = new DNSServer("TLD", extension + ".dnsServer", portNum);
    return tldServer;
  }

  public DNSServer createLocalDNSServer(int portNum) {
    DNSServer localServer = new DNSServer("Local", "local.dnsServer", portNum);
    return localServer;
  }

  public DNSServer createRootServer(int portNum) {
    DNSServer rootServer = new DNSServer("Root", "Root.dnsServer", portNum);
    return rootServer;
  }

  public ResourceRecord createARecord(String hostName, String ipAddress) {
    ResourceRecord aRecord = new ResourceRecord(hostName, ipAddress, "A", DEFAULT_TTL);
    return aRecord;
  }

  public ResourceRecord createTLDRecord(String tldName, String ipAddress, int portNum) {
    ResourceRecord tldRecord = new ResourceRecord(tldName, ipAddress, "TLD",
        TTL_NEVER_EXPIRES);
    tldRecord.setPortNum(portNum);
    return tldRecord;
  }

  public ResourceRecord createNSRecord(String dnsServerName, String domainName) {
    ResourceRecord nsRecord = new ResourceRecord(dnsServerName, domainName, "NS", DEFAULT_TTL);
    return nsRecord;
  }

  public ResourceRecord createGlueRecord(String authServerName, String ipAddress, int portNum) {
    ResourceRecord glueRecord = new ResourceRecord(authServerName, ipAddress, "G",
        TTL_NEVER_EXPIRES);
    glueRecord.setPortNum(portNum);
    return glueRecord;
  }

  public ResourceRecord createRootRecord(String rootName, String ipAddress, int portNum) {
    ResourceRecord rootRecord = new ResourceRecord(rootName, ipAddress, "R",
        TTL_NEVER_EXPIRES);
    rootRecord.setPortNum(portNum);
    return rootRecord;

  }

  public ResourceRecord createErrorRecord(String error, String query) {
    ResourceRecord errorRecord = new ResourceRecord(error, query, "E", DEFAULT_TTL);
    return errorRecord;
  }

  public void assignIPs(String tld, ArrayList<DNSServer> dnsServers, int perServerIps) {
    String octet3 = null;
    if (tld.equals("com")) {
      octet3 = COM_OCTET3;
    } else if (tld.equals("org")) {
      octet3 = ORG_OCTET3;
    } else if (tld.equals("edu")) {
      octet3 = EDU_OCTET3;
    }
    String[] ipArray = DNSReceiver.generateIPs(BASE_OCTET1, BASE_OCTET2, octet3,
        dnsServers.size() * perServerIps);
    int offset = 0;
    for (DNSServer dnsServer : dnsServers) {
      String[] serverIPArray = Arrays.copyOfRange(ipArray, offset, offset + perServerIps);
      dnsServer.getIpRoundRobin().addAll(Arrays.asList(serverIPArray));
      offset += perServerIps;
    }
  }
}


