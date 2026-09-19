public class LocalStorageService {
    private LocalStorageService() {

    }
    public static LocalStorageService of()
    {
        return new LocalStorageService();
    }
}
