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

    public static void main(String[] args) {
        PubSub<String> pubsub = new PubSub<>();

        Consumer<String> subscriber1 = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("Subscriber 1 received: "+s);
            }
        };
        Consumer<String> subscriber2 = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("Subscriber 2 received: "+s);
            }
        };

        pubsub.subscribe("news", subscriber1);
        pubsub.subscribe("news", subscriber2);

        pubsub.publish("news", "Breaking News: Java is amazing!");

        pubsub.unsubscribe("news", subscriber1);

        pubsub.publish("news", "More News: Pub/Sub works!");
    }
}
