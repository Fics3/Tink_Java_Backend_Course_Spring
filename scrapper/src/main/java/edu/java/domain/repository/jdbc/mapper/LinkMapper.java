<<<<<<<< HEAD:scrapper/src/main/java/edu/java/domain/repository/jdbc/mapper/LinkMapper.java
package edu.java.domain.repository.jdbc.mapper;
========
package edu.java.domain.repository.mapper;
>>>>>>>> 1b59763 (hw5 bonus):scrapper/src/main/java/edu/java/domain/repository/mapper/LinkMapper.java

import edu.java.model.LinkModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import static edu.java.util.DateUtil.getTimestampWithTimezone;

public class LinkMapper implements RowMapper<LinkModel> {
    @Override
    public LinkModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new LinkModel(
            UUID.fromString(rs.getString("link_id")),
            rs.getString("link"),
            getTimestampWithTimezone(rs.getTimestamp("last_update")),
            getTimestampWithTimezone(rs.getTimestamp("last_check"))
        );
    }
}
