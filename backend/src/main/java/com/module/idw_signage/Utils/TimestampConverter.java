package com.module.idw_signage.Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimestampConverter {


    public static LocalDateTime longToLocalDateTime(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }

    public static long localDateTimeToLong(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static Date longToDate(long epochMilli) {
        return new Date(epochMilli);
    }

    public static long dateToLong(Date date) {
        return date.getTime();
    }
}
