
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class JobQueue {

    private final Queue<Job> queue;

    private JobQueue(List<Job> jobs) {
        this.queue = new ArrayBlockingQueue<>(jobs.size());
        this.queue.addAll(jobs);
    }

    public static JobQueue of(List<Job> jobs) {
        return new JobQueue(jobs);
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }

    public Job dequeue() {
        return queue.poll();
    }

    public long size() {
        return queue.size();
    }
}
