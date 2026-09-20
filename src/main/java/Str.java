import java.nio.file.Path;

public class Str {
    public static String quote(String str) {
        return "\"" + str + "\"";
    }
    public static String quote(Path str) {
        return "\"" + str.toString() + "\"";
    }
}
