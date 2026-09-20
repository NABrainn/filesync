

import Result.ExecuteError;
import Result.Result;


import java.io.IOException;

import java.util.*;

public class CLI {
    public static Result<List<String>, ExecuteError> execute(String command) {
        var words = Arrays
                .stream(command.split(" "))
                .filter(word -> !word.isEmpty())
                .toList();
        var tokens = tokenize(command);
        var result = new ArrayList<String>();
        try {
            var process = new ProcessBuilder(tokens)
                    .redirectErrorStream(true)
                    .start();
            try(var inputReader = process.inputReader()) {
                String line;
                while((line = inputReader.readLine()) != null) {
                    result.add(line);
                }
            }
            int exitCode = process.waitFor();

            return exitCode == 0
                    ? Result.success(result)
                    : Result.failure(new ExecuteError("Process exited with code " + "\n" + String.join("\n", result), null));
        } catch (IOException | InterruptedException e) {
            return Result.failure(new ExecuteError(e.getMessage(), e.getCause()));
        }
    }
    public static Map<String, String> readArgs(String... args) {
        var map = new HashMap<String, String>();
        for(var arg : args) {
            var splitArg = arg.split("=");
            if(splitArg.length != 2) {
                throw new RuntimeException("Invalid argument format");
            }
            map.put(splitArg[0], splitArg[1]);
        }
        return map;
    }

    private static List<String> tokenize(String command) {
        var tokens = new ArrayList<String>();
        var current = new StringBuilder();

        boolean quoted = false;

        for (int i = 0; i < command.length(); i++) {
            char character = command.charAt(i);

            if (character == '"') {
                quoted = !quoted;
                continue;
            }

            if (Character.isWhitespace(character) && !quoted) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(character);
            }
        }

        if (quoted) {
            throw new IllegalArgumentException("Unclosed double quote");
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        return tokens;
    }
}
