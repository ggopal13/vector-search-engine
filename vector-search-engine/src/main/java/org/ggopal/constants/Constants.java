package org.ggopal.constants;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Constants {

    private Constants() {
        throw new AssertionError("Should not be instantiated");
    }

    private static final String sep = File.separator;

    public static final String AppDir = System.getProperty("user.home") + sep + ".vector";

    public static final String SearchCacheFile = sep + "search-cache.dat";

    public static final String cacheFileIndex = AppDir + sep + "index-map.txt";

    public static final Set<String> grammarWords;

    static {
        grammarWords = new HashSet<>(
                Arrays.asList("a", "an", "and", "the", "by", "or", "to", "for", "in", "on"));
    }

    public static final Set<String> acceptedFileFormats;

    static {
        acceptedFileFormats = new HashSet<>(
                Arrays.asList("txt", "doc", "docx", "html", "css", "js", "java")
        );
    }
}
