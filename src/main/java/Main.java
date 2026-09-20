import Result.Failure;
import Result.Success;

void main(String... args) {
    var argMap = CLI.readArgs(args);
    var remote = argMap.get("remote");
    var tempDirectoryName = "temp";
    var localStorageService = LocalStorageService.of(tempDirectoryName);
    var onedriveService = OneDriveService.of(remote);
    var synologyService = SynologyService.of();
    var source = Path.of("LMPAY-SA/CC/Nagrania SharePoint/Zofia Sawicka Nagrania SharePoint");
    var destination = localStorageService.tempDirectory();
    var result = onedriveService.downloadDirectory(source, destination);
    switch (result) {
        case Failure(var error) -> {
            IO.println(error.message());
        }
        case Success(var count) -> {
            IO.println(count);
        }
    }
    var jobs = JobProvider.of(onedriveService, localStorageService, synologyService).provide();
    var syncService = SyncService.configure(2, 1, jobs);
    Thread.ofVirtual().start(syncService);
}