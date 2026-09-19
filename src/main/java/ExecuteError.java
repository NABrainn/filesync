import java.util.Objects;

public record ExecuteError(String message) {
    public ExecuteError{
        Objects.requireNonNull(message);
    }
}
