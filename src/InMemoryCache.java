import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCache {

    private static final int MAX_CACHE_SIZE = 100;
    private final ConcurrentHashMap<String, CacheEntry> cache;
    private final LinkedHashMap<String, Long> accessOrder;

    static class CacheEntry
    {
        Object value;
        int frequency;
        long lastAccessTime;

        public CacheEntry(Object value)
        {
            this.value = value;
            this.frequency = 1;
            this.lastAccessTime = System.currentTimeMillis();
        }

        public void access()
        {
            this.frequency++;
            this.lastAccessTime = System.currentTimeMillis();
        }
    }

    public InMemoryCache()
    {
        this.cache = new ConcurrentHashMap<>();
        this.accessOrder = new LinkedHashMap<>();
    }

    void addEntry(String key, Object value)
    {
        if(cache.size() == MAX_CACHE_SIZE)
        {
            evict();
        }
        CacheEntry entry = new CacheEntry(value);
        cache.put(key, entry);
        accessOrder.put(key, entry.lastAccessTime);
    }

    void updateEntry(String key, Object value)
    {
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            entry.value = value;
            entry.access();
        } else {
            addEntry(key, value);
        }
    }

    boolean deleteEntry(String key)
    {
        return cache.remove(key) != null;
    }

    Object getEntry(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            entry.access();
            accessOrder.put(key, entry.lastAccessTime);
            return entry.value;
        }
        return null;
    }

    private void evict() {
        // For LRU: Evict the least recently accessed entry
        String lruKey = null;
        long oldestTime = Long.MAX_VALUE;

        for (Map.Entry<String, Long> entry : accessOrder.entrySet()) {
            if (entry.getValue() < oldestTime) {
                oldestTime = entry.getValue();
                lruKey = entry.getKey();
            }
        }

        if (lruKey != null) {
            cache.remove(lruKey);
            accessOrder.remove(lruKey);
            System.out.println("Evicted LRU key: " + lruKey);
        }
    }

    void display(PrintWriter out)
    {
        for(ConcurrentHashMap.Entry<String, CacheEntry> entry : cache.entrySet())
        {
            out.println(entry.getKey()+" "+entry.getValue().value.toString());
        }
    }
}
