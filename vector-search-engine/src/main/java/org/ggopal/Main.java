package org.ggopal;

import org.ggopal.client.ConsoleSearchClient;
import org.ggopal.client.SearchClient;
import org.ggopal.engine.FileIndexSearch;
import org.ggopal.scan.utilities.AppDirectoryOperations;
import org.ggopal.scan.utilities.FileSearchCache;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        AppDirectoryOperations appDirectoryOperations = new AppDirectoryOperations();
        appDirectoryOperations.initializeFolder();
        appDirectoryOperations.intializeIndexMap();
        SearchClient sc = new ConsoleSearchClient(new FileSearchCache(), new FileIndexSearch());
        try {
            sc.start();
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException("Error starting the search client");
        }
    }
}