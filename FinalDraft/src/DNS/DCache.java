
import java.util.*;
import java.util.concurrent.*;


public class DCache {

    private long timeToLiveInMillis;

    private ConcurrentHashMap<String, CachedObject> cacheMap;
    ConcurrentHashMap<String, CachedObject> getCacheMap() {
        return this.cacheMap;
    }

    void setCacheMap(final ConcurrentHashMap<String, CachedObject> cacheMap) {
        this.cacheMap = cacheMap;
    }

    class CachedObject {
        public long lastAccessed = System.currentTimeMillis();
        public ResourceRecord record;
        CachedObject(ResourceRecord record) {

            this.record = record;
        }

        public void setLastAccessed(long lastAccessed) {

            this.lastAccessed = lastAccessed;
        }
    }

    public DCache(long timeToLiveInSeconds, final long timerIntervalInSeconds,
                 int maxItems) {
        this.timeToLiveInMillis = timeToLiveInSeconds * 1000;

        this.cacheMap = new ConcurrentHashMap<String, CachedObject>(maxItems);

        if (timeToLiveInMillis > 0 && timerIntervalInSeconds > 0) {

            Thread t = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(timerIntervalInSeconds * 1000);
                        }
                        catch (InterruptedException ex) {
                        }

                        cleanup();
                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    public void put(String key, ResourceRecord record) {
        synchronized (this.cacheMap) {
            ResourceRecord rr = record;
            CachedObject c = new CachedObject(rr);
            if(rr.getTtl() == 0) { // 0 means never expires
                c.lastAccessed = 0;
            }
            this.cacheMap.put(key, c);
        }
    }

    public ResourceRecord get(String key) {
        synchronized (this.cacheMap) {
            CachedObject c = (CachedObject) this.cacheMap.get(key);
            if (c == null)
                return null;
            else {
                c.lastAccessed = System.currentTimeMillis();
                return c.record;
            }
        }
    }

    public void remove(String key) {
        synchronized (this.cacheMap) {
            this.cacheMap.remove(key);
        }
    }

    public int size() {
        synchronized (this.cacheMap) {
            return this.cacheMap.size();
        }
    }

    public void cleanup() {

        System.out.println("in the cleanup");

        long now = System.currentTimeMillis();
        ArrayList<String> keysToDelete = null;

        synchronized (this.cacheMap) {
            Iterator<Map.Entry<String, CachedObject>> itr = this.cacheMap.entrySet().iterator();

            keysToDelete = new ArrayList<String>((this.cacheMap.size() / 2) + 1);
            String key = null;
            CachedObject c = null;

            while (itr.hasNext()) {
                Map.Entry<String, CachedObject> entry = itr.next();
                key = entry.getKey();
                c = entry.getValue();

                if (c != null && c.lastAccessed != 0 && (now > (timeToLiveInMillis + c.lastAccessed))) {
                    keysToDelete.add(key);
                }
            }
        }

        for (String key : keysToDelete) {
            synchronized (this.cacheMap) {
                System.out.println("about to remove " + key);
                this.cacheMap.remove(key);
            }

            Thread.yield();
        }
    }
}
