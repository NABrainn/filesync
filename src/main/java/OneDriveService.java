import Result.Failure;
import Result.Success;

import java.util.List;
import java.util.Objects;

public class OneDriveService {

    private  OneDriveService(String remote) {
        var rcloneResult = CLI.execute("rclone");
        switch (rcloneResult) {
            case Failure(var error) -> throw new RuntimeException("Failed to run [rclone] command: " + error.message());
            case Success(var rcloneValue) -> {
                if(containsCritical(rcloneValue)) {
                    throw new RuntimeException("Failed to run [rclone] command: " + rcloneValue.getFirst());
                }
                Objects.requireNonNull(remote);
                var remoteFormatted = remote + ":";
                var rcloneAboutRemoteResult = CLI.execute("rclone about " + remoteFormatted);
                switch (rcloneAboutRemoteResult) {
                    case Failure(var error) -> throw new RuntimeException("Failed to run [rclone about remote:] command: " + error.message());
                    case Success(var rcloneAboutRemoteValue) -> {
                        if(containsCritical(rcloneAboutRemoteValue)) {
                            throw new RuntimeException("Failed to run [rclone about remote:] command: " + rcloneAboutRemoteValue.getFirst());
                        }
                    }
                }
            }
        }
    }

    private boolean containsCritical(List<String> list) {
        var CRITICAL = "CRITICAL";
        if(!list.isEmpty()) {
            var first = list.getFirst();
            return first.contains(CRITICAL);
        }
        return false;
    }



    public static OneDriveService of(String remote) {
        return new OneDriveService(remote);
    }
}
