import Result.Failure;
import Result.OneDriveError;
import Result.Result;
import Result.Success;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Rclone {

    private Rclone() {
    }

    public static Result<Void, OneDriveError> execute(String operation, String command, List<String> options, String... arguments) {
        var invocation = invocation(command, options, arguments);
        return switch (CLI.execute(invocation)) {
            case Failure(var error) -> Result.failure(new OneDriveError("Failed to " + operation + ": " + error.message()));
            case Success(var lines) when containsCritical(lines) -> Result.failure(new OneDriveError("Failed to " + operation + ": " + String.join("\n", lines)));
            case Success(var ignored) -> Result.success(null);
        };
    }

    public static Result<List<String>, OneDriveError> executeWithOutput(String operation, String command, List<String> options, String... arguments) {
        Objects.requireNonNull(options, "options cannot be null");
        var outputOption = options.stream().filter(Rclone::writesNonListingOutput).findFirst();
        if (outputOption.isPresent()) {
            return Result.failure(new OneDriveError("Option is not allowed when listing a directory: " + outputOption.get()));
        }

        var invocation = invocation(command, options, arguments);
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

    private static String invocation(String command, List<String> options, String... arguments) {
        Objects.requireNonNull(options, "options cannot be null");
        var tokens = new ArrayList<>(List.of(arguments));
        tokens.addAll(options);
        return "rclone " + command + " " + String.join(" ", tokens);
    }

    private static boolean writesNonListingOutput(String option) {
        return option.equals("-P")
                || option.equals("-i")
                || option.equals("-h")
                || option.matches("-v+")
                || option.equals("--verbose")
                || option.startsWith("--verbose=")
                || option.equals("--progress")
                || option.startsWith("--progress-")
                || option.equals("--stats")
                || option.startsWith("--stats=")
                || option.startsWith("--stats-")
                || option.equals("--log-level")
                || option.startsWith("--log-level=")
                || option.equals("--log-format")
                || option.startsWith("--log-format=")
                || option.equals("--use-json-log")
                || option.equals("--dump")
                || option.startsWith("--dump=")
                || option.startsWith("--dump-")
                || option.equals("--color")
                || option.startsWith("--color=")
                || option.equals("--interactive")
                || option.equals("--help");
    }
}
