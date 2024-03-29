package org.ggopal.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

public interface InvertedIndex extends Serializable {

    boolean addEntry(IndexEntryNode s);

    void addEntries(Collection<IndexEntryNode> coll);

    void removeEntry(IndexEntryNode node);

    Iterator<IndexEntryNode> getOccurrences(String... keywords);

}
