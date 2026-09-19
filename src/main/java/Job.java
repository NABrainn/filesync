import java.util.Objects;
import java.util.UUID;

public record Job(UUID uuid, WorkerRunnable runnable) {
    public Job {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(runnable);
    }
    public static Job of(UUID uuid, WorkerRunnable runnable) {
        return new Job(uuid, runnable);
    }
}
