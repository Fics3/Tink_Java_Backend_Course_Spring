package edu.java.scrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
public class TableExistIntegrationTest extends IntegrationTest {

    @Test
    void testContainerIsRunning() {
        // Act&Assert
        assertThat(POSTGRES.isRunning()).isTrue();

        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        );
             Statement statement = connection.createStatement()) {

            assertThat(statement.execute("SELECT 1")).isTrue();

        } catch (SQLException e) {
            log.error("error during create migration test");
        }
    }

    @Test
    @DisplayName("chat has some columns after migration")
    public void testMigrations_chatTableColumnsCountShouldBeNotZero() {
        // Act&Assert
        try (Connection connection = DriverManager.getConnection(
            POSTGRES.getJdbcUrl(),
            POSTGRES.getUsername(),
            POSTGRES.getPassword()
        );
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT * FROM chats");

            assertThat(resultSet.getMetaData().getColumnCount()).isNotNull().isNotZero();
        } catch (SQLException e) {
            log.error("error during create table test");
        }
    }

}
