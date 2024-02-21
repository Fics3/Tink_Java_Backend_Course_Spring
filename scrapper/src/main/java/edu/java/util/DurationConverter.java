package edu.java.util;

public final class DurationConverter {

    private static final long MILLS_IS_SECOND = 1000L;

    private DurationConverter() {}

    public static long convertToMillis(String durationString) {
        String numericValue = durationString.substring(0, durationString.length() - 1);
        long durationInSeconds = Long.parseLong(numericValue);
        return durationInSeconds * MILLS_IS_SECOND;
    }
}
