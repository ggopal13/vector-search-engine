package org.ggopal.scan.utilities;

import org.ggopal.constants.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Queue;

public class FileSearchCache implements SearchCache {

    private Queue<String> searches = null;
    private final int maxSize = 5;

    public FileSearchCache() {
        String searchCacheFile = Constants.AppDir + Constants.SearchCacheFile;
        Path filePath = Paths.get(searchCacheFile);
        boolean fileExists = Files.exists(filePath);
        if (fileExists) {
            try (ObjectInputStream objStream = new ObjectInputStream(new FileInputStream(searchCacheFile))) {
                searches = (ArrayDeque<String>) objStream.readObject();
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("Cannot load the search cache");
            }
        } else {
            try (ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(searchCacheFile))) {
                searches = new ArrayDeque<>(5);
                objStream.writeObject(searches);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Cannot create the search cache");
            }
        }
    }

    public String[] getRecentSearches() {
        return searches.toArray(new String[0]);
    }

    public boolean addSearchEntry(String dir) {
        if (searches.size() == maxSize)
            searches.remove();
        try (ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(Constants.AppDir + Constants.SearchCacheFile))) {
            searches = new ArrayDeque<>(5);
            objStream.writeObject(searches);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot create the search cache");
        }
        return searches.add(dir);
    }
}
