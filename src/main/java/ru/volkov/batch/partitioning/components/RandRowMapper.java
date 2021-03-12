package ru.volkov.batch.partitioning.components;

import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class RandRowMapper implements RowMapper<Rand> {

    @Override
    public Rand mapRow(ResultSet resultSet, int i) throws SQLException {
        Rand rand = new Rand();
        rand.setId(resultSet.getInt("id"));
        rand.setRandomText(resultSet.getString("random_text"));
        rand.setRandomData(toLocalDateTime(resultSet.getDate("random_data")));
        return rand;
    }

    LocalDateTime toLocalDateTime(Date dateToConvert) {
        return new java.sql.Timestamp(dateToConvert.getTime()).toLocalDateTime();
    }
}
