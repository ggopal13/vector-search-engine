package org.ggopal.scan.utilities;

public interface SearchCache {

    String[] getRecentSearches();

    boolean addSearchEntry(String dir);
}
