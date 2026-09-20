package Result;

public sealed interface AbstractError permits ExecuteError, OneDriveError {
    String message();
}
