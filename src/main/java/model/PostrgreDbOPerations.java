package model;

import model.exceptions.MyDbException;
import model.utils.DbUtils;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.sql.DriverManager.getConnection;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;


/**
 * Created by stas on 10/16/17.
 */
public class PostrgreDbOPerations implements DbOperations {

    private Connection connection;
    private static final String DEFAULT_PROP_RESOURCE = "postGreConnection.properties";

    @Override
    public void connect(Properties connectionProperties) {
        try {
            Properties urlProp = new Properties();
            urlProp.load(DbUtils.getResourceAsInputStream(DEFAULT_PROP_RESOURCE));
            connection = getConnection(format(urlProp.getProperty("url") + "%s",
                    connectionProperties.getProperty("database")), connectionProperties);
        } catch (SQLException | IOException e) {
            throw new MyDbException("Problems with Connection", e);
        }
    }


    @Override
    public void exit() {
        try {
            if (Objects.nonNull(connection)) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new MyDbException("Connection cannot be closed", e);
        }

    }

    @Override
    public List<String> getTables() {
        final String sql = "SELECT table_schema || '.' || table_name FROM information_schema.tables " +
                "WHERE table_type = 'BASE TABLE' " +
                "AND table_schema NOT IN ('pg_catalog', 'information_schema')";
        final List<String> result = new ArrayList<>();
        try (Statement statement = getConnect().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)
        ) {
            while (resultSet.next()) {
                result.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public void clearTable(String tableName) {
        try (Statement statement = getConnect().createStatement()) {
            boolean isEmpty = statement.execute("DELETE FROM " + tableName);
            if (isEmpty) {
                throw new MyDbException(String.format("table %s is already empty or not exist", tableName));
            }
        } catch (SQLException e) {
            throw new MyDbException(format("%s table cannot be cleared", tableName), e);
        }
    }

    @Override
    public void dropTable(String tableName) {
        if(!isTableExists(tableName)) {
            throw new MyDbException("Table does not exist");
        }
        try (Statement statement = getConnect().createStatement()) {
            statement.execute("DROP TABLE " + tableName);
        } catch (SQLException e) {
            throw new MyDbException(format("%s table cannot be dropped", tableName), e);
        }
    }

    @Override
    public void create(String tableName, List<String> columns) {
        String createTable = format("CREATE TABLE %s", tableName) + " ( " + columns.stream().collect(joining(" text,", "", " text")) + ")";
        try (Statement statement = getConnect().createStatement()) {
            int result = statement.executeUpdate(createTable);
            if (result != 0) {
                throw new MyDbException(format("Table %s cannot is not created", tableName));
            }
        } catch (SQLException e) {
            throw new MyDbException(format("Table %s cannot be created", tableName));
        }
    }

    @Override
    public Data find(String tableName) {
        return isTableExists(tableName) ? find(() -> "SELECT * FROM " + tableName)
                : new SqlTable(emptyList(), emptyList());
    }

    private Data find(Supplier<String> stringSupplier) {
        final String sql = stringSupplier.get();
        List<String> columns = new ArrayList<>();
        List<List<String>> values = new ArrayList<>();
        try (Statement statement = getConnect().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            List<String> columnsToIterate = getColumns(resultSet);
            while (resultSet.next()) {
                List<String> row = new ArrayList<>();
                for (int i = 1; i <= columnsToIterate.size(); i++) {
                    row.add(resultSet.getString(i));
                }
                values.add(row);
            }
            if (!values.isEmpty()) {
                columns = columnsToIterate;
            }
        } catch (SQLException e) {
            throw new MyDbException("Cannot select from table rows");
        }
        return new SqlTable(columns, values);
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
        if (!isTableExists(tableName)) {
            throw new MyDbException("Table does not exist");
        }
        final String sqlTemplateString = "INSERT INTO " + tableName + "(" + data.getNames().stream().collect(joining(",")) + ")" +
                " VALUES ( %s )";
        try (Statement statement = getConnect().createStatement()) {
            for (Row row : data.getValues()) {
                statement.addBatch(format(sqlTemplateString, row.getValuesInAllColumns()
                        .stream().map(s -> String.format("'%s'", s)).collect(joining(","))));
            }
            int[] result = statement.executeBatch();
            if (!stream(result).allMatch(value -> value == 1)) {
                throw new MyDbException("Some columns may not be inserted");
            }
        } catch (SQLException e) {
            throw new MyDbException("Cannot insert to table " + tableName);
        }
    }

    @Override
    public void update(String tableName, String column, String value, Data data) {
        if (!isTableExists(tableName)) {
            throw new MyDbException("Table does not exist");
        }
        final String valuesToUpdate = data.getNames().stream().collect(joining("='%s',", "", "='%s'"));
        final String sqlTemplate = "UPDATE " + tableName + " SET " + valuesToUpdate +
                " WHERE " + column + " = " + "'" + value + "'";

        try (Statement statement = getConnect().createStatement()) {
            for (Row row : data.getValues()) {
                statement.addBatch(format(sqlTemplate, row.getValuesInAllColumns().toArray()));
            }
            int[] result = statement.executeBatch();
            if (stream(result).noneMatch(val -> val == 1)) {
                throw new MyDbException("No columns has been updated");
            } else if (!stream(result).allMatch(val -> val == 1)) {
                throw new MyDbException("Some columns has not been updated");
            }

        } catch (SQLException e) {
            throw new MyDbException("Cannot update DB", e);
        }
    }

    @Override
    public Data delete(String tableName, String column, String value) {
        if (!isTableExists(tableName)) {
            throw new MyDbException("Table does not exist");
        }
        Data selected = find(() -> format("SELECT * FROM %s WHERE %s = '%s'", tableName, column, value));
        final String deleteQuery = format("DELETE FROM %s WHERE %s = '%s'", tableName, column, value);
        try (Statement statement = getConnect().createStatement()) {
            statement.executeUpdate(deleteQuery);
        } catch (SQLException e) {
            throw new MyDbException("Cannot delete from DB", e);
        }
        return selected;

    }

    private Connection getConnect() {
        if (connection == null) {
            throw new RuntimeException("Connection must be  initialized");
        }
        return connection;
    }

    private boolean isTableExists(String tableName) {
        try {
            return getConnect()
                    .getMetaData()
                    .getTables(null, null, tableName.toLowerCase(),
                            new String[]{"TABLE"}).next();
        } catch (SQLException e) {
            throw new MyDbException("error has been occured", e);
        }

    }


}
