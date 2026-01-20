package com.api.reports;

import com.aventstack.extentreports.Status;


public class Log {
    private Log() {}

    public static void info(String message) {
        log(Status.INFO, message);
    }

    public static void pass(String message) {
        log(Status.PASS, message);
    }

    public static void fail(String message) {
        log(Status.FAIL, message);
    }

    public static void warn(String message) {
        log(Status.WARNING, message);
    }

    public static void skip(String message){
        log(Status.SKIP,message);
    }

    private static void log(Status status, String message) {
        try {
            ExtentTestManager.getTest().log(status, message);
        } catch (Exception e) {
            // Safe fallback (useful if called outside test context)
            System.out.println("[" + status + "] " + message);
        }
    }
}
