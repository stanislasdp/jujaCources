package model;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Created by stas on 10/16/17.
 */
public interface DbOperations {

    void connect(Properties connectionProperties);

    void exit();

    List<String> getTables();

    void clearTable(String tableName);

    void dropTable(String tableName);

    void create(String tableName, List<String> columns);

    Data find(String tableName);

    void insert(String tableName, Data data);

    void update(String tableName, String column, String value, Data data);

    Data delete(String tableName, String column, String value);





}
