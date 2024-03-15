package edu.java.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.TimeZone;

public final class DateUtil {

    private DateUtil() {

    }

    public static OffsetDateTime getTimestampWithTimezone(Timestamp rs) {

        LocalDateTime localDateTime = rs.toLocalDateTime();
        ZoneId zoneId = TimeZone.getDefault().toZoneId();
        int offsetSeconds = zoneId.getRules().getOffset(localDateTime).getTotalSeconds();

        return OffsetDateTime.of(localDateTime, ZoneOffset.ofTotalSeconds(offsetSeconds));
    }

}
