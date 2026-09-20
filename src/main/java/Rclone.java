import Result.Failure;
import Result.OneDriveError;
import Result.Result;
import Result.Success;

import java.nio.file.Path;
import java.util.List;

public final class Rclone {

    private Rclone() {
    }

    public static Result<Void, OneDriveError> execute(String operation, String command, String... arguments) {
        var invocation = "rclone " + command + " " + String.join(" ", arguments);
        return switch (CLI.execute(invocation)) {
            case Failure(var error) -> Result.failure(new OneDriveError("Failed to " + operation + ": " + error.message()));
            case Success(var lines) when containsCritical(lines) -> Result.failure(new OneDriveError("Failed to " + operation + ": " + String.join("\n", lines)));
            case Success(var ignored) -> Result.success(null);
        };
    }

    public static Result<List<String>, OneDriveError> executeWithOutput(String operation, String command, String... arguments) {
        var invocation = "rclone " + command + " " + String.join(" ", arguments);
        return switch (CLI.execute(invocation)) {
            case Failure(var error) -> Result.failure(new OneDriveError("Failed to " + operation + ": " + error.message()));
            case Success(var lines) when containsCritical(lines) -> Result.failure(new OneDriveError("Failed to " + operation + ": " + String.join("\n", lines)));
            case Success(var lines) -> Result.success(lines);
        };
    }

    public static boolean containsCritical(List<String> lines) {
        return lines.stream().anyMatch(line -> line.contains("CRITICAL"));
    }

    public static String remotePath(String remote, Path path) {
        return Str.quote(remote + ":" + path);
    }

    public static String localPath(Path path) {
        return Str.quote(path);
    }
}
