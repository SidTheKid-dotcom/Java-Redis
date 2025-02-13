import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class Server {
    private static final int PORT = 8100;
    private static final InMemoryCache cache = new InMemoryCache();
    private static final PubSub pubSub = new PubSub();

    // Handle client requests
    public static void handleClientConnection(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String input;
            while ((input = in.readLine()) != null) {
                String[] parts = input.split(" ", 3);
                String command = parts[0].toUpperCase();

                switch (command) {
                    case "SET":
                        if (parts.length == 3) {
                            String key = parts[1];
                            String value = parts[2];
                            cache.addEntry(key, value);
                            out.println("SET command successful.");
                        } else {
                            out.println("Invalid SET command. Usage: SET <key> <value>");
                        }
                        break;

                    case "GET":
                        if (parts.length == 2) {
                            String key = parts[1];
                            String value = cache.getEntry(key) == null ? null : cache.getEntry(key).toString();
                            out.println(value != null ? value : "Key not found.");
                        } else {
                            out.println("Invalid GET command. Usage: GET <key>");
                        }
                        break;

                    case "SUB":
                        if (parts.length == 2) {
                            String topic = parts[1];
                            Consumer<String> subscriber = msg -> out.println("Subscriber received: " + msg);
                            pubSub.subscribe(topic, subscriber);
                            out.println("Subscribed to topic: " + topic);
                        } else {
                            out.println("Invalid SUBSCRIBE command. Usage: SUBSCRIBE <topic>");
                        }
                        break;

                    case "PUB":
                        if (parts.length == 3) {
                            String topic = parts[1];
                            String message = parts[2];
                            pubSub.publish(topic, message);
                            out.println("Message published to topic: " + topic);
                        } else {
                            out.println("Invalid PUBLISH command. Usage: PUBLISH <topic> <message>");
                        }
                        break;

                    default:
                        out.println("Invalid Command");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start the server
    public static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming connections
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                handleClientConnection(clientSocket);
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main entry point
    public static void main(String[] args) {
        startServer();
    }
}
