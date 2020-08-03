import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerTreeNode {
    public DNSServer server;
    public ServerTreeNode parent;
    public ArrayList<ServerTreeNode> children;

    public ServerTreeNode (DNSServer server, ServerTreeNode parent)
    {
        this.server = server;
        this.parent = parent;
        this.children = new ArrayList<ServerTreeNode>();
    }

    DNSServer getServer() {
        return this.server;
    }

    void setServer(final DNSServer server) {
        this.server = server;
    }

    ServerTreeNode getParent() {
        return this.parent;
    }

    void setParent(final ServerTreeNode parent) {
        this.parent = parent;
    }

    ArrayList<ServerTreeNode> getChildren() {
        return this.children;
    }

    void setChildren(final ArrayList<ServerTreeNode> children) {
        this.children = children;
    }

    public void addChild(DNSServer server) {
        ServerTreeNode childNode = new ServerTreeNode(server, this);
        if (this.children != null) {
            this.children.add(childNode);
        }
    }

    public void printNode() {
        if(this.parent == null) {
            System.out.println(this.server.getDnsServerName());
        } else {
            System.out.println(this.server.getDnsServerName());
            for (String key: this.getServer().getCache().getDnsCacheMap().keySet()) {
                ResourceRecord rr = (ResourceRecord) this.getServer().getCache().getRecord(key);
                System.out.println(rr.toString());
           }
        }
    }

    public void printAll(ServerTreeNode node) {
        LinkedBlockingQueue<ServerTreeNode> printQueue = new LinkedBlockingQueue<ServerTreeNode>();
        if (node.parent == null) {
            printQueue.add(node);
        }
        while (!printQueue.isEmpty()) {
            ServerTreeNode printNode = printQueue.poll();
            printNode.printNode();
            if (printNode.getChildren().isEmpty()) {
                continue;
            } else {
                for(ServerTreeNode childNode: printNode.getChildren()) {
                    if(!printQueue.contains(childNode)) {
                        printQueue.add(childNode);
                    }
                }
            }
        }
    }

    public ServerTreeNode serverTreeSearch(String query, String queryType, DNSResolver resolver,
                                           ServerTreeNode node) throws UnknownHostException,
            IOException {
        LinkedBlockingQueue<ServerTreeNode> searchQueue = new LinkedBlockingQueue<ServerTreeNode>();
        if (node.parent == null) {
            searchQueue.add(node);
        }
        while (!searchQueue.isEmpty()) {
            ServerTreeNode searchNode = searchQueue.poll();
            if(resolver.rrLookupByQuery(query, queryType, searchNode.getServer().getCache()) != null) {
                return searchNode;
            }
            else {
                if (searchNode.getChildren().isEmpty()) {
                    continue;
                }
                else {
                    for(ServerTreeNode childNode: searchNode.getChildren()) {
                        if(!searchQueue.contains(childNode)) {
                            searchQueue.add(childNode);
                        }
                    }
                }
            }
        }
        return null;
    }
}
