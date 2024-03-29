package org.ggopal.engine;

import org.ggopal.constants.Constants;
import org.ggopal.entity.IndexEntryNode;
import org.ggopal.entity.InvertedIndex;
import org.ggopal.entity.InvertedIndexBasicImpl;
import org.ggopal.file.utilities.FileHelper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FileIndexSearch implements IndexSearch {

    private InvertedIndex index;
    private Map<String, String> indexMap;
    private FileHelper fileHelper = new FileHelper();

    public FileIndexSearch() {
        index = new InvertedIndexBasicImpl();
        indexMap = null;
    }

    @Override
    public void resetIndex() {
        index = new InvertedIndexBasicImpl();
    }

    @Override
    public void loadIndex(String dir) {
        loadIndex(Paths.get(dir));
    }

    @Override
    public void loadIndex(Path dir) {
        Path absPath = dir.toAbsolutePath().normalize();
        try (Stream<Path> pathStream = Files.walk(absPath)) {
            pathStream
                    .filter(Files::isDirectory)
                    .forEach(path -> {
                        Path dirIndexFile = Paths.get(indexMap.get(path.toAbsolutePath().toString()));
                        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(dirIndexFile.toAbsolutePath().toString()))) {
                            InvertedIndex invertedIndex = (InvertedIndex) inputStream.readObject();
                            ((InvertedIndexBasicImpl) index).addIndices((InvertedIndexBasicImpl) invertedIndex);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
            Path dirIndexFile = Paths.get(indexMap.get(dir.toAbsolutePath().toString()));
            try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(dirIndexFile.toAbsolutePath().toString()))) {
                InvertedIndex invertedIndex = (InvertedIndex) inputStream.readObject();
                ((InvertedIndexBasicImpl) index).addIndices((InvertedIndexBasicImpl) invertedIndex);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterator<IndexEntryNode> getSearchResults(String... keyword) {
        return index.getOccurrences(keyword);
    }

    public void indexDirectory(Path dir) throws IOException {
        Path absPath = dir.toAbsolutePath().normalize();
        UUID uuid = UUID.randomUUID();
        Path directoryIndexFile = Paths.get(Constants.AppDir + File.separator + uuid.toString() + ".dat");
        InvertedIndex dirIndex = new InvertedIndexBasicImpl();
        try (Stream<Path> pathStream = Files.list(absPath)) {
            pathStream.forEach(path -> {
                if (Files.isDirectory(path)) {
                    try {
                        indexDirectory(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if (Constants.acceptedFileFormats.contains(fileHelper.getExtensionByStringHandling(path.getFileName().toString()).orElse("$$$"))) {
                        LinkedList<IndexEntryNode> nodes = indexFileContents(path);
                        dirIndex.addEntries(nodes);
                        index.addEntries(nodes);
                    }
                }
            });
            if (Files.notExists(directoryIndexFile))
                Files.createFile(directoryIndexFile);
            try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(directoryIndexFile.toAbsolutePath().toString()))) {
                outputStream.writeObject(dirIndex);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            indexMap.put(dir.toAbsolutePath().toString(), directoryIndexFile.toAbsolutePath().toString());
            System.out.println(indexMap);
            writeIndexMap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void indexDirectory(String dir) throws IOException {
        Path absPath = Paths.get(dir);
        indexDirectory(absPath);
    }

    private LinkedList<IndexEntryNode> indexFileContents(Path filePath) {
        LinkedList<IndexEntryNode> fileIndex = new LinkedList<>();
        try (Stream<String> stream = Files.lines(filePath, Charset.defaultCharset())) {
            AtomicInteger lineNo = new AtomicInteger(1);
            stream.forEachOrdered(line -> {
                String[] words = line.split("\\s+");
                Stream.of(words)
                        .map(String::toLowerCase)
                        .filter(word -> !Constants.grammarWords.contains(word))
                        .forEach(word -> {
                            IndexEntryNode node = IndexEntryNode.builder()
                                    .keyword(word)
                                    .documentName(filePath.getFileName().toString())
                                    .lineNo(lineNo.get())
                                    .fullPath(filePath.toAbsolutePath().toString())
                                    .build();
                            System.out.println("Indexing " + node);
                            fileIndex.add(node);
                        });
                lineNo.addAndGet(1);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileIndex;
    }

    public boolean doesIndexExist(Path dir) {
        if (indexMap == null) {
            indexMap = new HashMap<>();
            loadIndexMap();
        }
        System.out.println(indexMap);
        return indexMap.containsKey(dir.toAbsolutePath().toString());
    }

    private void loadIndexMap() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(Constants.cacheFileIndex))) {
            indexMap = (Map<String, String>) inputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeIndexMap() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(Constants.cacheFileIndex))) {
            outputStream.writeObject(indexMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
