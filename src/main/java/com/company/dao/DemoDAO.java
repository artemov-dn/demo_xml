package com.company.dao;

import com.company.model.Entries;
import com.company.model.Entry;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DemoDAO {
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static RowMapper<Entry> entryMapper =
            (ResultSet rs, int i) -> {
                Entry dto = new Entry();
                dto.setField(rs.getInt("field"));
                return dto;
            };

    public Entries getAllFields() throws DataAccessException  {
        Entries result = new Entries();

        String query = "select field from test";
        List<Entry> list = jdbcTemplate.query(query, entryMapper);
        result.setEntryList(list);

        return result;
    }


    public void insertNFields(int n) throws DataAccessException {
        jdbcTemplate.update("truncate test");

        jdbcTemplate.batchUpdate("insert into public.test (field) values(?)",
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, i + 1);
                    }

                    public int getBatchSize() {
                        return n;
                    }

                });
    }


}
