package Result;

public record Success<V, E>(V value) implements Result<V, E> {
}
