package com.example.jupiter.util;

public class StringUtil {

    public static boolean isNull(String str) {
        return str == null;
    }

    public static boolean isEmpty(String str) {
        return isNull(str) || "".equals(str);
    }

    public static boolean isOneEmpty(String[] strArr) {
        if (strArr == null || strArr.length == 0) {
            return true;
        }
        for (String str : strArr) {
            if (isEmpty(str)) return true;
        }
        return false;
    }

}
