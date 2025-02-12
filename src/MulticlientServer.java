import java.util.*;
import java.io.*;
import java.net.*;
import java.util.function.Consumer;

public class MulticlientServer {

    private static final int PORT = 8100;
    private static final InMemoryCache cache = new InMemoryCache();
    private static final PubSub pubSub = new PubSub();

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run()
        {
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
                                if(key.equals("DELETE")) {
                                    out.println("Invalid key name");
                                }
                                FileStorage.saveToFile(key, value);
                                cache.addEntry(key, value);
                                out.println("SET command successful.");
                            } else {
                                out.println("Invalid SET command. Usage: SET <key> <value>");
                            }
                            break;

                        case "GET":
                            if (parts.length == 2) {
                                String key = parts[1];
                                Object value = cache.getEntry(key);
                                out.println(value != null ? value.toString() : "Key not found.");
                            } else {
                                out.println("Invalid GET command. Usage: GET <key>");
                            }
                            break;

                        case "DEL":
                            if (parts.length == 2) {
                                String key = parts[1];
                                System.out.println(key);
                                FileStorage.saveToFile("DELETE", key);
                                // Attempt to remove the key and check if it existed
                                if (cache.deleteEntry(key)) {
                                    out.println("Deleted key: " + key);
                                } else {
                                    out.println("Key not found.");
                                }
                            } else {
                                out.println("Invalid DEL command. Usage: DEL <key>");
                            }
                            break;

                        case "PRINT_CACHE":
                            cache.display(out);
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
            } catch (SocketException e) {
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            }
            catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void startServer() {
        FileStorage.loadFromFile(cache);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startServer();
    }
}
