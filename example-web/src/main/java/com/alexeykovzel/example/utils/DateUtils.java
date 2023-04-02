package com.alexeykovzel.example.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DateUtils {
    public static Date parse(String date, String format) {
        try {
            if (date == null || format == null) return null;
            return new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            log.error("Failed to parse date: format={}, error={}", format, e.getMessage());
            return null;
        }
    }

    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date shift(Date date, TimeUnit timeUnit, int duration) {
        return new Date(date.getTime() + timeUnit.toMillis(duration));
    }

    public static long between(Date d1, Date d2, TimeUnit timeUnit) {
        long timeDifference = Math.abs(d2.getTime() - d1.getTime());
        long seconds = timeDifference / TimeUnit.SECONDS.toMillis(1);
        switch (timeUnit) {
            case DAYS:
                return TimeUnit.SECONDS.toDays(seconds);
            case HOURS:
                return TimeUnit.SECONDS.toHours(seconds);
            case MINUTES:
                return TimeUnit.SECONDS.toMinutes(seconds);
            case SECONDS:
                return seconds;
            default:
                return -1;
        }
    }
}
