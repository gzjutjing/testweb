package com.test.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Created by admin on 2016/9/3.
 */
public class DateUtil {
    public static final String DEFAULT_FORMAT = "yyyy-MM-hh HH:mm:ss";

    /**
     * 格式化日期
     * DateTimeFormatter线程安全
     *
     * @param date
     * @return
     */
    public static String defaultFormat(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DEFAULT_FORMAT, Locale.getDefault());
        return formatter.format(localDateTime);
    }
}
