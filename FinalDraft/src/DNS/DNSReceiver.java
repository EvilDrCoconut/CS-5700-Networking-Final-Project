import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class DNSReceiver {

    public static void main(String[] args) throws IOException {
        int ROOT_SERVER_PORT = 31111;
        int LOCAL_DNS_SERVER_PORT = 11166;
        Registrar registrar = new Registrar();
        DNSServerConfig config = new DNSServerConfig(registrar);
        DNSClient client = new DNSClient();
        DNSServer rootServer = registrar.createRootServer(ROOT_SERVER_PORT);

        ArrayList<DNSServer> comServers = new ArrayList<DNSServer>();
        ArrayList<DNSServer> orgServers = new ArrayList<DNSServer>();
        ArrayList<DNSServer> eduServers = new ArrayList<DNSServer>();
        ArrayList<DNSServer> dnsServers = new ArrayList<DNSServer>();

        DNSServer comServer = registrar.createTLDServer("com", 32222);
        DNSServer walmartServer = registrar.createAuthServer("walmart.com", 35555);
        DNSServer lowesServer = registrar.createAuthServer("lowes.com", 36668);

        comServers.add(comServer);
        comServers.add(walmartServer);
        comServers.add(lowesServer);
        registrar.assignIPs("com", comServers, 10);

        DNSServer orgServer = registrar.createTLDServer("org", 32333);
        DNSServer gforgServer = registrar.createAuthServer("geeksforgeeks.org", 36666);
        DNSServer pbsServer = registrar.createAuthServer("pbs.org", 37777);
        DNSServer wikiServer = registrar.createAuthServer("wikipedia.org", 38888);

        orgServers.add(orgServer);
        orgServers.add(gforgServer);
        orgServers.add(pbsServer);
        orgServers.add(wikiServer);
        registrar.assignIPs("org", orgServers, 10);

        DNSServer eduServer = registrar.createTLDServer("edu", 34444);
        DNSServer northeasternServer = registrar.createAuthServer("northeastern.edu", 36667);

        eduServers.add(eduServer);
        eduServers.add(northeasternServer);
        registrar.assignIPs("edu", eduServers, 10);

        registrar.registerTLDServer(rootServer, comServer);
        registrar.registerTLDServer(rootServer, orgServer);
        registrar.registerTLDServer(rootServer, eduServer);
        registrar.registerAuthServer(comServer, walmartServer);
        registrar.registerAuthServer(comServer, lowesServer);
        registrar.registerAuthServer(orgServer, gforgServer);
        registrar.registerAuthServer(orgServer, pbsServer);
        registrar.registerAuthServer(orgServer, wikiServer);
        registrar.registerAuthServer(eduServer, northeasternServer);

        registrar.registerDomain(walmartServer, "walmart.com",
                walmartServer.selectIP(walmartServer), "walmart.com",
                walmartServer.getDnsServerName());
        registrar.registerDomain(lowesServer, "lowes.com",
                lowesServer.selectIP(lowesServer), "lowesServer.com",
                        lowesServer.getDnsServerName());
        registrar.registerDomain(gforgServer, "geeksforgeeks.org",
                gforgServer.selectIP(gforgServer), "geeksforgeeks.org",
                gforgServer.getDnsServerName());
        registrar.registerDomain(pbsServer, "pbs.org",
                pbsServer.selectIP(pbsServer), "pbs.org",
                pbsServer.getDnsServerName());
        registrar.registerDomain(wikiServer, "wikipedia.org",
                wikiServer.selectIP(wikiServer), "wikipedia.org",
                wikiServer.getDnsServerName());
        registrar.registerDomain(northeasternServer, "northeastern.edu",
                northeasternServer.selectIP(northeasternServer), "northeastern.edu",
                northeasternServer.getDnsServerName());

        dnsServers.add(rootServer);
        dnsServers.addAll(comServers);
        dnsServers.addAll(orgServers);
        dnsServers.addAll(eduServers);
        launchServers(dnsServers);
    }


    public static String[] generateIPs(String octet1, String octet2, String octet3,
                                       int numIPs) {
        String[] ipArray = new String[numIPs];
        for(int i = 0; i < numIPs; i++) {
            StringBuilder ipBuilder = new StringBuilder();
            ipBuilder.append(octet1).append(octet2).append(octet3).append(i);
            ipArray[i] = ipBuilder.toString();
        }
        return ipArray;
    }
    public static void launchServers(ArrayList<DNSServer> dnsServers) {
        ExecutorService dnsExecutor = Executors.newFixedThreadPool(dnsServers.size()); {
            for (DNSServer dnsServer : dnsServers) {
                Runnable serverLaunch = new MyRunnable(dnsServer);
                System.out.println("About to launch " + dnsServer.getDnsServerName());
                dnsExecutor.execute(serverLaunch);
            }
            //dnsExecutor.shutdown();
            dnsExecutor.shutdown();
            while(!dnsExecutor.isTerminated()) {

            }
            System.out.println("All servers are done");
        }
    }

    public static class MyRunnable implements Runnable {
        final DNSServer dnsServer;
        MyRunnable(DNSServer dnsServer) {
            this.dnsServer = dnsServer;
        }

        @Override
        public void run() {
            System.out.println("in the UDPServer process...");
            System.out.println("cachemap size " + this.dnsServer.getCache().size());

            try {
                this.dnsServer.handleQuerySegment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}