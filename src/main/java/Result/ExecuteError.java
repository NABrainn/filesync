package Result;

import java.util.Objects;

public record ExecuteError(String message, Throwable cause) implements AbstractError {
    public ExecuteError{
        Objects.requireNonNull(message);
    }
}
