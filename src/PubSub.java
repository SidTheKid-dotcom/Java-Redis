import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class PubSub<T> {
    private final Map<String, List<Consumer<T>>> registry = new ConcurrentHashMap<>();

    public void subscribe(String topic, Consumer<T> subscriber) {
        registry.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(subscriber);
    }

    public void unsubscribe(String topic, Consumer<T> subscriber) {
        registry.getOrDefault(topic, Collections.emptyList()).remove(subscriber);
    }

    public void publish(String topic, T message) {
        List<Consumer<T>> subscribers = registry.get(topic);
        if (subscribers != null) {
            subscribers.forEach(subscriber -> subscriber.accept(message));
        }
    }
}
