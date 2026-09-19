import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.AtomicLong;

public class SyncService implements Runnable {

    private final int syncWorkers;
    private final int cleanupWorkers;
    private final JobQueue queue;

    private SyncService(int syncWorkers, int cleanupWorkers, List<Job> jobs) {
        this.syncWorkers = syncWorkers;
        this.cleanupWorkers = cleanupWorkers;
        this.queue = JobQueue.of(jobs);
    }

    public void run() {
        try(var scope = StructuredTaskScope.open()) {

            var requestedJobs = new AtomicLong(queue.size());
            var completedJobs = new AtomicLong(0);


            for(var i = 0; i < syncWorkers; i++) {
                scope.fork(() -> {
                    while(queue.hasNext()) {
                        var nextJob = queue.dequeue();
                        var success = nextJob.runnable().run();
                        if(success) {
                            var jobId = completedJobs.incrementAndGet();
                            IO.println("Job completed successfully: " + jobId);
                        }
                    }
                });
            }

            for(var i = 0; i < cleanupWorkers; i++) {
                scope.fork(() -> {
                    while(completedJobs.get() != requestedJobs.get()) {
                        IO.println("Waiting for all the jobs to complete...");
                    }
                    IO.println("All jobs completed, cleaning up...");
                });
            }

            try {
                scope.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static SyncService configure(int syncWorkers, int cleanupWorkers, List<Job> jobs) {
        return new SyncService(syncWorkers, cleanupWorkers, jobs);
    }
}
