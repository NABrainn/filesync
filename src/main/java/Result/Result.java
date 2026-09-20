package Result;

public sealed interface Result<V, E> permits Failure, Success {
    static <V, E> Success<V, E> success(V value) {
        return new Success<V, E>(value);
    }
    static <V, E> Failure<V, E> failure(E error) {
        return new Failure<V, E>(error);
    }
}
