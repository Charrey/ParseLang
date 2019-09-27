package parselang.filereader;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    public String readStringFromFile(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public String readStringFromFile(URL url) throws IOException {
        return new String(Files.readAllBytes(Paths.get(url.getPath())));
    }

    public String readRootFile(Path rootPath) throws IOException {
        return new String(Files.readAllBytes(rootPath.resolve("highlevel.plang")));
    }
}
