import java.io.*;
import java.util.*;

public class FileStorage {
    private static final String DATA_FILE = "data.db";

    public static synchronized void saveToFile(String key, String value) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE, true))) {
            writer.write(key + "=" + value);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void loadFromFile(InMemoryCache cache) {
        File storageFile = new File(DATA_FILE);
        if (!storageFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    if(parts[0].equals("DELETE")) {
                        cache.deleteEntry(parts[1]);
                    }
                    cache.addEntry(parts[0], parts[1]);
                }
            }
            System.out.println("Data loaded from file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
