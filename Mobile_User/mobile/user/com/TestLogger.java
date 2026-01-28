package mobile.user.com;


public class TestLogger {

    public static void step(String msg) {
        System.out.println("\n🔹 STEP: " + msg);
    }

    public static void request(String msg) {
        System.out.println("➡ REQUEST: " + msg);
    }

    public static void response(String msg) {
        System.out.println("⬅ RESPONSE:");
        System.out.println(msg);
    }

    public static void assertCheck(String msg) {
        System.out.println("✔ ASSERT: " + msg);
    }
}
