import java.util.*;

public class DNSCache {

    static DNSCacheMap dnsCacheMap = new DNSCacheMap(80);

    DNSCacheMap getDnsCacheMap() {
        return this.dnsCacheMap;
    }

    void setDnsCacheMap(final DNSCacheMap dnsCacheMap) {
        this.dnsCacheMap = dnsCacheMap;
    }

    public void addRecord(ResourceRecord rr) {
        dnsCacheMap.cachePut(rr.getName(), rr);
        Thread purgeThread = new Thread(new Runnable() {
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
    }

    public void addRecords(ArrayList<ResourceRecord> rrs) {
        for (ResourceRecord rr : rrs) {
            addRecord(rr);
        }
    }

    static class DNSCacheMap extends LinkedHashMap<String, Object> {
        private int maxCacheSize;

        DNSCacheMap(int maxCacheSize) {
            super(16, (float) 0.75, true);
            this.maxCacheSize = maxCacheSize;
        }

        void cachePut(String name, Object ResourceRecord) {
            synchronized (this) {
                this.put(name, ResourceRecord);
            }
        }
        // getters and setters

        public ResourceRecord get(String key) {
            synchronized (this) {
                ResourceRecord rr = (ResourceRecord) dnsCacheMap.get(key);

                if (rr == null)
                    return null;
                else {
                    rr.setLastAccessed(System.currentTimeMillis());
                    return rr;
                }
            }
        }

        public void remove(String key) {
            synchronized (this) {
                this.remove(key);
            }
        }

        public int size() {
            synchronized (this) {
                return this.size();
            }
        }
    }

    //need to add record to the cache
    // need to calc expiration date
    //
    public void purgeExpiredRecords() {
        long now = System.currentTimeMillis();
        ArrayList<String> deleteKey = null;

        synchronized (dnsCacheMap) {
            Iterator<Map.Entry<String, Object>> cacheIterator = dnsCacheMap.entrySet().iterator();
            deleteKey = new ArrayList<String>((dnsCacheMap.size() / 2) + 1);
            String key = null;
            ResourceRecord rr = null;

            while (cacheIterator.hasNext()) {
                key = cacheIterator.next().getKey();
                rr = (ResourceRecord) cacheIterator.next().getValue();

                if (rr != null && (now > (rr.getTtl() * 1000 + rr.getLastAccessed()))) {
                    deleteKey.add(key);
                }
            }
        }

        for (String key : deleteKey) {
            synchronized (dnsCacheMap) {
                dnsCacheMap.remove(key);
            }

            Thread.yield();
        }
    }
}
