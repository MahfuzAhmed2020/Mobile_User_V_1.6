package com.selenium.mobileUser;


public class TestLogger {

    public static void step(String msg) {
        System.out.println("\n🔹 STEP: " + msg);
    }

    public static void verify(String msg) {
        System.out.println("✔ VERIFY: " + msg);
    }
}
