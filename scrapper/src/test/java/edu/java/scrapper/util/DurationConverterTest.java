package edu.java.scrapper.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static edu.java.util.DurationConverter.convertToMillis;
import static org.assertj.core.api.Assertions.assertThat;

public class DurationConverterTest {
    @Test
    @DisplayName("should convert string in second to mills")
    public void testConvertToMillis() {
        // Arrange
        String durationString = "10s";

        // Act
        long result = convertToMillis(durationString);

        // Assert
        assertThat(result).isEqualTo(10000L);
    }
}
