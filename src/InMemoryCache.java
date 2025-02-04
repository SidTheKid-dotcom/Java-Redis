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

    void deleteEntry(String key)
    {
        cache.remove(key);
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

    void display()
    {
        for(ConcurrentHashMap.Entry<String, CacheEntry> entry : cache.entrySet())
        {
            System.out.println(entry.getKey()+" "+entry.getValue().value.toString());
        }
    }

    public static void main(String[] args) {
        InMemoryCache cache = new InMemoryCache();

        cache.addEntry("stringKey", "Hello, World!");
        cache.addEntry("integerArray", new int[]{1, 2, 3, 4, 5});
        cache.addEntry("arrayList", new ArrayList<>(Arrays.asList("Apple", "Banana", "Cherry")));
        cache.addEntry("hashMap", new HashMap<>(Map.of("A", 1, "B", 2)));
        cache.addEntry("treeSet", new TreeSet<>(Set.of(10, 5, 20, 15)));

        // Display stored data
        cache.display();

        // Retrieve and cast data
        String strValue = (String) cache.getEntry("stringKey");
        System.out.println("\nRetrieved String: " + strValue);

        int[] intArray = (int[]) cache.getEntry("integerArray");
        System.out.println("Retrieved Array: " + Arrays.toString(intArray));

        ArrayList<String> arrayList = (ArrayList<String>) cache.getEntry("arrayList");
        System.out.println("Retrieved ArrayList: " + arrayList);

        HashMap<String, Integer> hashMap = (HashMap<String, Integer>) cache.getEntry("hashMap");
        System.out.println("Retrieved HashMap: " + hashMap);

        TreeSet<Integer> treeSet = (TreeSet<Integer>) cache.getEntry("treeSet");
        System.out.println("Retrieved TreeSet: " + treeSet);
    }
}
