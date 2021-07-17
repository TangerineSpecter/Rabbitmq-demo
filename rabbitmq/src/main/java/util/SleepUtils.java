package util;

public class SleepUtils {

    public static void sleep(int second) {
        try {
            Thread.sleep(1000 * second);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}
