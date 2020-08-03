import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class DNSResolverController {

    // todo: implement recursive and iterative search (root can only do iterative)

    LinkedBlockingQueue<ResourceRecord> ipRoundRobin = new LinkedBlockingQueue<ResourceRecord>();

    public ResourceRecord recursiveSearch(String query,
                                          ServerTreeNode localServerNode,
                                          ServerTreeNode serverTree) throws UnknownHostException,
            IOException {

        DNSResolver resolver = new DNSResolver();
        if (resolver.rrLookupByQuery(query, "A", localServerNode.getServer().getCache()) != null) {
            return resolver.rrLookupByQuery(query, "A", localServerNode.getServer().getCache());
        } else {
            ServerTreeNode searchNode = serverTree.serverTreeSearch(query, "A", resolver,
                    serverTree);
            if (searchNode != null) {
                return resolver.rrLookupByQuery(query, "A", searchNode.getServer().getCache());
            } else {
                return null;
            }
        }
    }

    public ResourceRecord iterativeSearch() {
        return null;
    }

    public void queueNextTurn() {
        ResourceRecord usedRecord = this.ipRoundRobin.poll();
        ipRoundRobin.add(usedRecord);
    }

}












    // round robin (ip address list permutation)
    // each address gets a turn and then goes to the back of the line
    // recursive query
    // iterative query

