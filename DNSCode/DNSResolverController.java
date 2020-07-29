import java.util.concurrent.*;

public class DNSResolverController {

    // todo: implement recursive and iterative search (root can only do iterative)

    LinkedBlockingQueue<ResourceRecord> ipRoundRobin= new LinkedBlockingQueue<ResourceRecord>();





    public void queueNextTurn() {
        ResourceRecord usedRecord = this.ipRoundRobin.poll();
        ipRoundRobin.add(usedRecord);
    }




    // round robin (ip address list permutation)
    // each address gets a turn and then goes to the back of the line
    // recursive query
    // iterative query
}
