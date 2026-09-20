import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class LocalStorageService {

    private final Path tempDirectory;

    private LocalStorageService(String tempDirectoryName) {
        Objects.requireNonNull(tempDirectoryName, "tempDirectoryName cannot be null");
        try {
            tempDirectory = Files.createTempDirectory(AppPaths.resources(), tempDirectoryName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp directory: " + e);
        }
    }

    public Path tempDirectory() {
        return tempDirectory;
    }

    public void deleteTempDirectory() {
        try {
            Files.delete(tempDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create delete directory: " + e);
        }
    }

    public static LocalStorageService of(String tempDirectoryName) {
        return new LocalStorageService(tempDirectoryName);
    }
}
