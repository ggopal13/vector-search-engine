package org.ggopal.entity;

import java.util.*;

public class InvertedIndexBasicImpl implements InvertedIndex {

    private Map<String, LinkedList<IndexEntryNode>> index;

    public InvertedIndexBasicImpl() {
        this.index = new HashMap<>();
    }

    @Override
    public boolean addEntry(IndexEntryNode s) {
        index.putIfAbsent(s.getKeyword(), new LinkedList<>());
        return index.get(s.getKeyword()).add(s);
    }

    @Override
    public void addEntries(Collection<IndexEntryNode> coll) {
        for (IndexEntryNode node : coll) {
            addEntry(node);
        }
    }

    @Override
    public void removeEntry(IndexEntryNode node) {
        LinkedList<IndexEntryNode> list = index.get(node.getKeyword());
        if (list == null)
            throw new NoSuchElementException("Given keyword is not present");
        list.remove(node);
    }

    @Override
    public Iterator<IndexEntryNode> getOccurrences(String... keywords) {
        Set<IndexEntryNode> nodes = new HashSet<>();
        nodes.addAll(index.getOrDefault(keywords[0].toLowerCase(), new LinkedList<>()));
        for (String keyword : keywords) {
            LinkedList<IndexEntryNode> list = index.getOrDefault(keyword.toLowerCase(), new LinkedList<>());
            nodes.retainAll(list);
        }
        return nodes.iterator();
    }

    public void addIndices(InvertedIndexBasicImpl invertedIndex) {
        invertedIndex.index.forEach((k, v) -> {
            index.putIfAbsent(k, new LinkedList<>());
            v.forEach(node -> {
                index.get(node.getKeyword()).add(node);
            });
        });
    }
}
