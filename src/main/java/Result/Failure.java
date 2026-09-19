package Result;

public record Failure<V, E>(E error) implements Result<V, E> {
}
