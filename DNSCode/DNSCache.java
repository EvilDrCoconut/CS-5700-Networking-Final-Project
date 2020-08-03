import java.util.*;
import java.util.concurrent.*;

public class DNSCache {

    private DNSCacheMap dnsCacheMap = new DNSCacheMap(80);

    DNSCacheMap getDnsCacheMap() {
        return this.dnsCacheMap;
    }

    void setDnsCacheMap(final DNSCacheMap dnsCacheMap) {
        this.dnsCacheMap = dnsCacheMap;
    }

    public void addRecord(ResourceRecord rr) {
        dnsCacheMap.cachePut(rr.getName(), rr);
      /*  Thread purgeThread = new Thread(new Runnable() {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(rr.getTtl() * 1000);
                } catch (InterruptedException ex) {
                }
                purgeExpiredRecords();
            }
        }});
        purgeThread.setDaemon(true);
        purgeThread.start();

       */
    }

    public ResourceRecord getRecord(String hostName) {
        ResourceRecord rr = (ResourceRecord) this.dnsCacheMap.cacheGet(hostName);
        return rr;
    }

    public void addRecords(ArrayList<ResourceRecord> rrs) {
        for (ResourceRecord rr : rrs) {
            addRecord(rr);
        }
    }

    public class DNSCacheMap extends ConcurrentHashMap<String, Object> {
        private int maxCacheSize;

        DNSCacheMap(int maxCacheSize) {
            super(16, (float) 0.75);
            this.maxCacheSize = maxCacheSize;
        }

        void cachePut(String name, Object ResourceRecord) {
            this.put(name, ResourceRecord);
        }
        // getters and setters

        ResourceRecord cacheGet(String key) {
            ResourceRecord rr = (ResourceRecord) this.get(key);
            if (rr == null)
                return null;
            else {
                rr.setLastAccessed(System.currentTimeMillis());
                return rr;
            }
        }

        public void remove(String key) {
            this.remove(key);
        }

        public int size() {
            return this.size();
        }
    }

    //need to add record to the cache
    // need to calc expiration date
    //
    public void purgeExpiredRecords() {
        long now = System.currentTimeMillis();
        ArrayList<String> deleteKey = null;

        Iterator<Map.Entry<String, Object>> cacheIterator = this.dnsCacheMap.entrySet().iterator();
        deleteKey = new ArrayList<String>((this.dnsCacheMap.size() / 2) + 1);
        String key = null;
        ResourceRecord rr = null;

        while (cacheIterator.hasNext()) {
            key = cacheIterator.next().getKey();
            rr = (ResourceRecord) cacheIterator.next().getValue();

            if (rr != null && (now > (rr.getTtl() * 1000 + rr.getLastAccessed()))) {
                deleteKey.add(key);
            }
        }

        for (String delKey : deleteKey) {
            this.dnsCacheMap.remove(delKey);
        }

        Thread.yield();
    }
}

