import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;

public class AppPaths {
    public static String resources(String subdir) {
        Objects.requireNonNull(subdir);
        if(!subdir.startsWith("/")) {
            subdir = "/" + subdir;
        }
        var path = Path.of(appPath() + URI.create("/src/main/resources" + subdir));
        var exists = Files.exists(path);
        if(!exists) {
            throw new NoSuchElementException("Directory not found");
        }
        return path.toString();
    }
    public static Path resources() {
        var path = Path.of(appPath() + "/src/main/resources");
        var exists = Files.exists(path);
        if(!exists) {
            throw new NoSuchElementException("Directory not found");
        }
        return path;
    }
    public static String appPath() {
        return Path.of("")
                .toAbsolutePath()
                .normalize()
                .toString();
    }
}
