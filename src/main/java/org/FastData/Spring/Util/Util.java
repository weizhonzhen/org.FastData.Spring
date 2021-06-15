package org.FastData.Spring.Util;

public final class Util {
    public static boolean isNullOrEmpty(Object value) {
        return value == null || value.toString().equals("");
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.toString().equals("");
    }
}
