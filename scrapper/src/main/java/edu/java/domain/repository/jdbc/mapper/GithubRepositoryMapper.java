package edu.java.domain.repository.jdbc.mapper;

import edu.java.model.GithubRepositoryModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

public class GithubRepositoryMapper implements RowMapper<GithubRepositoryModel> {
    @Override
    public GithubRepositoryModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new GithubRepositoryModel(
            rs.findColumn("repository_id"),
            UUID.fromString(rs.getString(("link_id"))),
            rs.getInt("subscribers_count")
        );
    }
}
