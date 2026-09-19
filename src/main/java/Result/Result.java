package Result;

public sealed interface Result<V, E> permits Failure, Success {
    static <V, E> Result<V, E> success(V value) {
        return new Success<V, E>(value);
    }
    static <V, E> Result<V, E> failure(E error) {
        return new Failure<V, E>(error);
    }
}
