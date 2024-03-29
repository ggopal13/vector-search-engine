package org.ggopal.client;

import org.ggopal.console.utilities.ConsoleReader;
import org.ggopal.engine.IndexSearch;
import org.ggopal.entity.IndexEntryNode;
import org.ggopal.file.utilities.FileHelper;
import org.ggopal.scan.utilities.SearchCache;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;

public class ConsoleSearchClient implements SearchClient {

    private int exitOption = 9;
    private SearchCache searchCache;
    private String choosenDir;
    private int currentChoice = -1;
    private FileHelper fileHelper;
    private IndexSearch indexSearch;

    public ConsoleSearchClient(SearchCache searchCache, IndexSearch indexSearch) {
        this.searchCache = searchCache;
        fileHelper = new FileHelper();
        this.indexSearch = indexSearch;
    }

    @Override
    public void start() throws IOException {
        ConsoleReader reader = new ConsoleReader();
        do {
            if (choosenDir == null) {
                System.out.println("Hi! Please enter the directory you want to search!");
                boolean recents = giveRecentSearched();
                giveExitOption();
                String userInput = reader.next();
                currentChoice = parseChoice(userInput);
                if (currentChoice == exitOption)
                    continue;
                if (recents && (currentChoice > 100 && currentChoice <= 105)) {
                    choosenDir = searchCache.getRecentSearches()[currentChoice - 101];
                } else {
                    choosenDir = userInput;
                }
                if (!fileHelper.doesDirExist(choosenDir)) {
                    System.out.println("No such directory exists");
                    choosenDir = null;
                } else {
                    Path dirPath = Paths.get(choosenDir);
                    searchCache.addSearchEntry(choosenDir);
                    if (indexSearch.doesIndexExist(dirPath)) {
                        System.out.println("Index already exists");
                        System.out.println("Press [0] to re index the contents or [1] to continue");
                        while (true) {
                            int choice = reader.nextInt();
                            if (choice == 0)
                                indexSearch.indexDirectory(choosenDir);
                            else if (choice == 1)
                                break;
                            else
                                System.out.println("Please enter valid choice");
                        }
                    } else {
                        System.out.println("Indexing the contents");
                        indexSearch.indexDirectory(choosenDir);
                    }
                }
            } else {
                System.out.println("Please enter your search term");
                giveExitOption();
                String[] keywords = reader.nextLine().split("\\s+");
                if (parseChoice(keywords[0]) == exitOption) {
                    currentChoice = exitOption;
                    break;
                }
                System.out.println(Arrays.toString(keywords));
                Iterator<IndexEntryNode> occurences = indexSearch.getSearchResults(keywords);
                System.out.println("The search results are...");
                while (true) {
                    boolean runOut = false;
                    for (int i = 0; i < 10; i++) {
                        if (!occurences.hasNext()) {
                            runOut = true;
                            break;
                        } else {
                            System.out.println(occurences.next());
                        }
                    }
                    if (runOut) {
                        System.out.println("That's all!");
                        break;
                    }
                    if (occurences.hasNext()) {
                        System.out.println("Press [1] to see more results");
                        giveExitOption();
                        int choice = reader.nextInt();
                        if (choice == 9) {
                            currentChoice = exitOption;
                            break;
                        } else if (choice != 1)
                            System.out.println("Please enter valid choice");
                    }
                }
            }
        }
        while (currentChoice != exitOption);
    }

    private int parseChoice(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -999;
        }
    }

    private boolean giveRecentSearched() {
        String[] searches = searchCache.getRecentSearches();
        if (searches.length == 0) return false;
        System.out.println("Your recent choices are ");
        for (int i = 0; i < searches.length; i++) {
            System.out.println("[" + (100 + 1) + "] " + searches[i]);
        }
        return true;
    }

    private void giveExitOption() {
        System.out.println("Press [" + exitOption + "] to exit");
    }

}
