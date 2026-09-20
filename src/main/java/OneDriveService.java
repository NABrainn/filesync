import Result.Failure;
import Result.Result;
import Result.Success;
import Result.OneDriveError;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class OneDriveService {

    private final String remote;

    private  OneDriveService(String remote) {
        Objects.requireNonNull(remote, "remote cannot be null");
        if (remote.isBlank()) {
            throw new IllegalArgumentException("remote cannot be blank");
        }

        var rcloneResult = CLI.execute("rclone --help");
        switch (rcloneResult) {
            case Failure(var error) -> throw new RuntimeException("Failed to run [rclone] command: " + error.message());
            case Success(var rcloneValue) -> {
                if(Rclone.containsCritical(rcloneValue)) {
                    throw new RuntimeException("Failed to run [rclone] command: " + rcloneValue.getFirst());
                }
                var rcloneAboutRemoteResult = CLI.execute("rclone about " + remote + ":");
                switch (rcloneAboutRemoteResult) {
                    case Failure(var error) -> throw new RuntimeException("Failed to run [rclone about remote:] command: " + error.message(), error.cause());
                    case Success(var rcloneAboutRemoteValue) -> {
                        if(Rclone.containsCritical(rcloneAboutRemoteValue)) {
                            throw new RuntimeException("Failed to run [rclone about remote:] command: " + rcloneAboutRemoteValue.getFirst());
                        }

                        this.remote = remote;
                    }
                }
            }
        }
    }

    public Result<Integer, OneDriveError> downloadDirectory(Path sourcePath, Path destinationPath) {
        Objects.requireNonNull(sourcePath);
        Objects.requireNonNull(destinationPath);
        return switch (Rclone.execute("download directory", "copy", Rclone.remotePath(remote, sourcePath), Rclone.localPath(destinationPath))) {
            case Failure(var error) -> Result.failure(error);
            case Success(var ignored) -> {
                var contents = destinationPath.toFile().list();
                if (contents == null) {
                    yield Result.failure(new OneDriveError("Failed to download directory: destination is not a readable directory: " + destinationPath));
                }
                var count = contents.length;
                yield Result.success(count);
            }
        };
    }

    public Result<Void, OneDriveError> downloadFile(Path sourcePath, Path destinationPath) {
        Objects.requireNonNull(sourcePath);
        Objects.requireNonNull(destinationPath);
        return Rclone.execute("download file", "copyto", Rclone.remotePath(remote, sourcePath), Rclone.localPath(destinationPath));
    }

    public Result<Void, OneDriveError> uploadFile(Path sourcePath, Path destinationPath) {
        Objects.requireNonNull(sourcePath);
        Objects.requireNonNull(destinationPath);
        return Rclone.execute("upload file", "copyto", Rclone.localPath(sourcePath), Rclone.remotePath(remote, destinationPath));
    }

    public Result<Void, OneDriveError> uploadDirectory(Path sourcePath, Path destinationPath) {
        Objects.requireNonNull(sourcePath);
        Objects.requireNonNull(destinationPath);
        return Rclone.execute("upload directory", "copy", Rclone.localPath(sourcePath), Rclone.remotePath(remote, destinationPath));
    }

    public Result<List<Path>, OneDriveError> listDirectory(Path path) {
        Objects.requireNonNull(path);
        return switch (Rclone.executeWithOutput("list directory", "lsf", Rclone.remotePath(remote, path))) {
            case Failure(var error) -> Result.failure(error);
            case Success(var entries) -> Result.success(entries.stream()
                    .filter(entry -> !entry.isBlank())
                    .map(Path::of)
                    .toList());
        };
    }

    public Result<Void, OneDriveError> createDirectory(Path path) {
        Objects.requireNonNull(path);
        return Rclone.execute("create directory", "mkdir", Rclone.remotePath(remote, path));
    }

    public Result<Void, OneDriveError> deleteFile(Path path) {
        Objects.requireNonNull(path);
        return Rclone.execute("delete file", "deletefile", Rclone.remotePath(remote, path));
    }

    public Result<Void, OneDriveError> deleteDirectory(Path path) {
        Objects.requireNonNull(path);
        return Rclone.execute("delete directory", "purge", Rclone.remotePath(remote, path));
    }

    public Result<Void, OneDriveError> moveFile(Path sourcePath, Path destinationPath) {
        Objects.requireNonNull(sourcePath);
        Objects.requireNonNull(destinationPath);
        return Rclone.execute("move file", "moveto", Rclone.remotePath(remote, sourcePath), Rclone.remotePath(remote, destinationPath));
    }

    public Result<Void, OneDriveError> moveDirectory(Path sourcePath, Path destinationPath) {
        Objects.requireNonNull(sourcePath);
        Objects.requireNonNull(destinationPath);
        return Rclone.execute("move directory", "move", Rclone.remotePath(remote, sourcePath), Rclone.remotePath(remote, destinationPath));
    }

    public static OneDriveService of(String remote) {
        return new OneDriveService(remote);
    }
}
