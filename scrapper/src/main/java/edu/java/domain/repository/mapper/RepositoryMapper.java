package edu.java.domain.repository.mapper;

import edu.java.model.RepositoryModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class RepositoryMapper implements RowMapper<RepositoryModel> {
    @Override
    public RepositoryModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RepositoryModel(
            rs.findColumn("repository_id"),
            UUID.fromString(rs.getString(("link_id"))),
            rs.getInt("subscribers_count")
        );
    }
}
