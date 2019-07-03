package parselang.filereader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class FileReader {

    public String readStringFromFile(String fileName) throws IOException {
        String data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }
 


    public Path seekRoot(String filename) throws IOException {
        Path toCheck = Paths.get(filename).getParent();
        while (!containsHighLevel(toCheck)) {
            toCheck = toCheck.getParent();
            if (toCheck == null) {
                throw new IOException("Project root not found");
            }
        }

        return toCheck;
    }

    private boolean containsHighLevel(Path toCheck) {
        List<File> files = Arrays.asList(toCheck.toFile().listFiles());
        return files.stream().anyMatch(file -> file.getName().equals("highlevel.plang"));
    }

    public String readRootFile(Path rootPath) throws IOException {
        String data = new String(Files.readAllBytes(rootPath.resolve("highlevel.plang")));
        return data;
    }
}
