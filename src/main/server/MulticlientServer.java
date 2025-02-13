package main.server;

import main.cache.InMemoryCache;
import java.util.logging.Logger;
import main.pubsub.PubSub;
import main.storage.FileStorage;
import main.util.Util;

import java.io.*;
import java.net.*;

public class MulticlientServer {
    private static final int PORT = 8100;
    private static final InMemoryCache cache = new InMemoryCache();
    private static final PubSub<String> pubSub = new PubSub<>();
    private static final Logger logger = Logger.getLogger(MulticlientServer.class.getName());

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String input;
                while ((input = in.readLine()) != null) {
                    String[] parts = input.split(" ", 3);
                    String command = parts[0].toUpperCase();

                    switch (command) {
                        case "SET" -> Util.handleSetCommand(parts, out, cache);
                        case "GET" -> Util.handleGetCommand(parts, out, cache);
                        case "DEL" -> Util.handleDelCommand(parts, out, cache);
                        case "PRINT_CACHE" -> cache.display(out);
                        case "PUB" -> Util.handlePublishCommand(parts, out, pubSub);
                        case "SUB" -> Util.handleSubscribeCommand(parts, out, pubSub);
                        default -> out.println("Invalid Command");
                    }
                }
            } catch (SocketException e) {
                logger.warning("Client disconnected: " + clientSocket.getInetAddress());
            } catch (Exception e) {
                logger.severe("Error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    logger.severe("Error closing socket: " + e.getMessage());
                }
            }
        }
    }

    public static void startServer() {
        FileStorage.loadFromFile("src/main/data.db", cache);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port: " + PORT);
            logger.info("Server is running on port: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client connected: " + clientSocket.getInetAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            logger.severe("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startServer();
    }
}
