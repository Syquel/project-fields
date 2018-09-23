package com.mrozekma.atlassian.bitbucket.projectFields.ao;

public class DBHelper {
    private DBHelper() {}

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }
}
