import sun.security.x509.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class FinalProjectMain {
    public static void main(String[] args) throws IOException {

        DNSClient newClient = new DNSClient();
        InetAddress walmartAddress = newClient.browserHostNameQuery("http://www.walmart.com");
        System.out.println("Walmart ip address is " + walmartAddress);

        InetAddress gforgAddress = newClient.browserHostNameQuery("http://www.geeksforgeeks.com");
        System.out.println("Geeks for geeks ip address is " + gforgAddress);

        InetAddress pbsAddress = newClient.browserHostNameQuery("http://www.pbs.org");
        System.out.println("pbs ip address is " + pbsAddress);

        InetAddress wikiAddress = newClient.browserHostNameQuery("http://www.wikipedia.org");
        System.out.println("wikipedia ip address is " + wikiAddress);

        InetAddress nuAddress = newClient.browserHostNameQuery("http://www.northeastern.edu");
        System.out.println("northeastern ip address is " + nuAddress);

        InetAddress tuftsAddress = newClient.browserHostNameQuery("http://www.tufts.edu");
        System.out.println("tufts ip address is " + tuftsAddress);

    }
}
        /*
        String DNSName = "https://www.abc12345.edu";
        // find the info between the two dots
        int firstDotIndex = DNSName.indexOf('.');
        int lastDotIndex = DNSName.lastIndexOf('.');
        String hostName = DNSName.substring(firstDotIndex + 1, lastDotIndex);
        System.out.println(hostName);
        String TLD = DNSName.substring(lastDotIndex, DNSName.length());
        System.out.println(TLD);

        DNSCache dnsCache = new DNSCache();

        DNSRecord eduRecord = new DNSRecord("https://www.abc12345.edu", "TLD", "73.169.179.107",
                12345);
        DNSRecord netRecord = new DNSRecord("https://www.hostnameNet.net", "TLD", "149.253.249.247",
                12596);
        DNSRecord comRecord = new DNSRecord("https://www.hostCom.com", "TLD", "149.253.249.247",
                12596);

        DNSCache.DNSCacheMap dnsCacheMap = dnsCache.getDnsCacheMap();
        dnsCacheMap.put("https://www.abc12345.edu", eduRecord);
        dnsCacheMap.put("https://www.hostnameNet.net", netRecord);
        dnsCacheMap.put("https://www.hostCom.com", comRecord);
*/


