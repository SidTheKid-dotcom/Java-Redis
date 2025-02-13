package main.storage;

import main.cache.InMemoryCache;
import main.util.Util;

import java.io.*;
import java.util.logging.Logger;

public class FileStorage {

    private static final Logger logger = Logger.getLogger(Util.class.getName());

    public static synchronized void saveToFile(String fileName, String key, String value) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(key + "=" + value);
            writer.newLine();
            logger.info("New line written");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromFile(String fileName, InMemoryCache cache) {
        File storageFile = new File(fileName);
        if (!storageFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    if (parts[0].equals("DELETE")) {
                        cache.deleteEntry(parts[1]);
                        continue;
                    }
                    cache.addEntry(parts[0], parts[1]);
                }
            }
            logger.info("Data loaded from file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
