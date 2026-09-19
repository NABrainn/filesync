import java.util.List;

public class JobProvider {

    private final OneDriveService oneDriveService;
    private final LocalStorageService localStorageService;
    private final SynologyService synologyService;

    private JobProvider(OneDriveService oneDriveService, LocalStorageService localStorageService, SynologyService synologyService) {
        this.oneDriveService = oneDriveService;
        this.localStorageService = localStorageService;
        this.synologyService = synologyService;
    }

    public List<Job> provide() {
        return List.of();
        //scan
        //for each scanned file create and implement job to run
    }

    public static JobProvider of(OneDriveService onedriveService, LocalStorageService localStorageService, SynologyService synologyService) {
        return new JobProvider(onedriveService, localStorageService, synologyService);
    }
}
