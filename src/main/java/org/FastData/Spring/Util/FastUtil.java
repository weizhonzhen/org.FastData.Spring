package org.FastData.Spring.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FastUtil {
    public static boolean isNullOrEmpty(Object value) {
        return value == null || value.toString().equals("");
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.toString().equals("");
    }

    public static boolean isZhString(String str) {
        if (isNullOrEmpty(str))
            return false;
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find())
            return true;
        else
            return false;
    }

    public static String getDateTime(String pattern){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }
}
