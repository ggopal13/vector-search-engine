package org.ggopal.engine;

import org.ggopal.entity.IndexEntryNode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public interface IndexSearch {

    void resetIndex();

    void loadIndex(String dir);

    void loadIndex(Path dir);

    Iterator<IndexEntryNode> getSearchResults(String ...keyword);

    void indexDirectory(String dir)  throws IOException;

    void indexDirectory(Path dir) throws IOException;

    public boolean doesIndexExist(Path dir);

}
