import Result.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CLI {
    public static Result<List<String>, ExecuteError> execute(String command) {
        var tokens = command.split(" ");
        var result = new ArrayList<String>();
        try {
            var process = new ProcessBuilder(tokens).start();
            var inputStream =  process.getInputStream();
            var errorStream = process.getErrorStream();
            try(
                var inputReader = new BufferedReader(new InputStreamReader(inputStream));
                var errorReader = new BufferedReader(new InputStreamReader(errorStream));
            ) {
                String line;
                while((line = inputReader.readLine()) != null) {
                    result.add(line);
                }
                while ((line = errorReader.readLine()) != null) {
                    result.add(line);
                }
            }

            int isFinished = process.waitFor();

            if(isFinished == 0) {
                process.destroyForcibly();
            }

            return Result.success(result);
        } catch (IOException | InterruptedException e) {
            return Result.failure(new ExecuteError(e.getMessage()));
        }
    }
}
