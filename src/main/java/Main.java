
void main() {
    var onedriveService = OneDriveService.of("ms-od");
    var localStorageService = LocalStorageService.of();
    var synologyService = SynologyService.of();
    var jobs = JobProvider.of(onedriveService, localStorageService, synologyService).provide();
    var syncService = SyncService.configure(2, 1, jobs);
    Thread.ofVirtual().start(syncService);
}