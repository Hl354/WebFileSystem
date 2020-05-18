package com.example.jupiter.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    private static final String strFormat = "yyyy-MM-dd hh:mm:ss";

    public static String nowDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(strFormat);
        return simpleDateFormat.format(new Date());
    }

}
