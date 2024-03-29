package org.ggopal.file.utilities;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FileHelper {

    public boolean doesDirExist(String dir) {
        Path path = Paths.get(dir);
        path = path.toAbsolutePath().normalize();
//        System.out.println(path);
        return Files.exists(path);
    }

    public Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }
}
