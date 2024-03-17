package edu.java.domain.repository.mapper;

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
