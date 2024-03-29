package org.ggopal.scan.utilities;

import org.ggopal.constants.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class AppDirectoryOperations {

    public boolean initializeFolder() throws IOException {
        Path dir = Paths.get(Constants.AppDir);
        boolean folderExists = Files.exists(dir);
        if (!folderExists)
            Files.createDirectory(dir);
        return true;
    }

    public boolean intializeIndexMap() throws IOException {
        Path file = Paths.get(Constants.cacheFileIndex);
        if (Files.notExists(file))
            Files.createFile(file);
        try (ObjectOutputStream objStream = new ObjectOutputStream(new FileOutputStream(Constants.cacheFileIndex))) {
            objStream.writeObject(new HashMap<String, String>());
        }
        return true;
    }
}
