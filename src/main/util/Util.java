package main.util;

import main.cache.InMemoryCache;
import main.pubsub.PubSub;
import main.storage.FileStorage;

import java.io.PrintWriter;

public class Util {

    public static void handleSetCommand(String[] parts, PrintWriter out, InMemoryCache cache) {
        if (parts.length < 3) {
            out.println("Usage: SET <key> <value>");
            return;
        }
        FileStorage.saveToFile("src/main/data.db", parts[1], parts[2]);
        cache.addEntry(parts[1], parts[2]);
        out.println("SET Command Successful");
    }

    public static void handleGetCommand(String[] parts, PrintWriter out, InMemoryCache cache) {
        if (parts.length < 2) {
            out.println("Usage: GET <key>");
            return;
        }
        Object value = cache.getEntry(parts[1]);
        if (value != null) {
            out.println(value.toString());
        } else {
            out.println("(nil)");
        }
    }

    public static void handleDelCommand(String[] parts, PrintWriter out, InMemoryCache cache) {
        if (parts.length < 2) {
            out.println("Usage: DEL <key>");
            return;
        }
        FileStorage.saveToFile("src/main/data.db", "DELETE", parts[1]);
        if (cache.deleteEntry(parts[1])) {
            out.println("Deleted key " + parts[1]);
        } else {
            out.println("(nil)");
        }
    }

    public static void handlePublishCommand(String[] parts, PrintWriter out, PubSub<String> pubSub) {
        if (parts.length < 3) {
            out.println("Usage: PUB <topic> <message>");
            return;
        }
        String topic = parts[1];
        String message = parts[2];
        pubSub.publish(topic, message);
        out.println("Message published to " + topic);
    }

    public static void handleSubscribeCommand(String[] parts, PrintWriter out, PubSub<String> pubSub) {
        if (parts.length < 2) {
            out.println("Usage: SUB <topic>");
            return;
        }
        String topic = parts[1];
        pubSub.subscribe(topic, message -> out.println("Message on " + topic + ": " + message));
        out.println("Subscribed to " + topic);
    }
}
