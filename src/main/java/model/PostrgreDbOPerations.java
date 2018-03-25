package model;

import model.exceptions.MyDbException;
import model.utils.ResourcesUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Created by stas on 10/16/17.
 */
@Component
@Scope("prototype")
public class PostrgreDbOPerations implements DbOperations {

    private JdbcTemplate jdbcTemplate;

    private static final String DEFAULT_PROP_RESOURCE = "postGreConnection.properties";

    @Override
    public DbOperations connect(Properties connectionProperties) {
        try {
            Properties urlProp = new Properties();
            urlProp.load(ResourcesUtils.getResourceAsInputStream(DEFAULT_PROP_RESOURCE));

            jdbcTemplate = new JdbcTemplate(new SimpleDriverDataSource(
                DriverManager.getDrivers().nextElement(),
                format(urlProp.getProperty("url") + "%s",
                    connectionProperties.getProperty("database")), connectionProperties));

        } catch (IOException e) {
            throw new MyDbException("Problems with Connection", e);
        }
        return this;
    }

    @Override
    public List<String> getTables() {
        return jdbcTemplate.query("SELECT table_schema || '.' || table_name FROM information_schema.tables " +
            "WHERE table_type = 'BASE TABLE' " +
            "AND table_schema NOT IN ('pg_catalog', 'information_schema')", (rs, rowNum) -> rs.getString(1));
    }

    @Override
    public void clearTable(String tableName) {
        jdbcTemplate.execute("DELETE FROM" + tableName);
    }

    @Override
    public void dropTable(String tableName) {
       jdbcTemplate.execute("DROP TABLE " + tableName);
    }

    @Override
    public void create(String tableName, List<String> columns) {
        jdbcTemplate.execute(format("CREATE TABLE %s", tableName) +
            " ( " + columns.stream().collect(joining(" text,", "", " text")) + ")");
    }

    @Override
    /*TODO find a way to fetch column names when result set is empty */
    public Data find(String tableName) {
       return find(() -> "SELECT * FROM " + tableName);
    }

    private Data find(Supplier<String> supplier) {
        List<String> columns;
        try (Statement statement = jdbcTemplate.getDataSource().getConnection().createStatement()) {
            columns = getColumns(statement.executeQuery(supplier.get()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        List<List<String>> rows = new ArrayList<>();
        rows.addAll(jdbcTemplate.query(supplier.get(), new RowMapper<List<String>>() {
            List<String> row = new ArrayList<>();
            @Override
            public List<String> mapRow(ResultSet rs, int rowNum) throws SQLException {
                for (int i = 1; i <= columns.size() ; i++) {
                    row.add(rs.getString(i));
                }
                return row;
            }
        }));

        return new SqlTable(columns, rows);
    }

    private List<String> getColumns(ResultSet resultSet) {
        List<String> columns = new ArrayList<>();
        try {
            int columnsCount = resultSet.getMetaData().getColumnCount();
            for (int i = 1; i <= columnsCount; i++) {
                columns.add(resultSet.getMetaData().getColumnLabel(i));
            }

        } catch (SQLException e) {
            throw new MyDbException("message", e);
        }
        return columns;
    }

    @Override
    public void insert(String tableName, Data data) {
        final String sqlTemplateString = "INSERT INTO " + tableName +
            "(" + data.getNames().stream().collect(joining(",")) + ")" +
            " VALUES ( %s )";

        jdbcTemplate.batchUpdate(data.getValues()
            .stream()
            .map(row -> format(sqlTemplateString, row.getValuesInAllColumns()
                .stream()
                .map(s -> String.format("'%s'", s))
                .collect(joining(","))))
            .toArray(String[]::new));
    }

    @Override
    public void update(String tableName, String column, String value, Data data) {
        final String valuesToUpdate = data.getNames().stream().collect(joining("=?,", "", "=?"));
        final String sqlTemplate = "UPDATE " + tableName + " SET " + valuesToUpdate +
            " WHERE " + column + " = " + "'" + value + "'";

        List<Object[]> rows = data.getValues()
            .stream()
            .map(Row::getValuesInAllColumns)
            .map(List::toArray)
            .collect(toList());

        jdbcTemplate.batchUpdate(sqlTemplate, rows);

    }

    @Override
    public Data delete(String tableName, String column, String value) {
        Data selected = find(() -> format("SELECT * FROM %s WHERE %s = '%s'", tableName, column, value));
        final String deleteQuery = format("DELETE FROM %s WHERE %s = '%s'", tableName, column, value);
        jdbcTemplate.execute(deleteQuery);
        return selected;
    }
}
