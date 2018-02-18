package service;


import model.DbOperations;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface Service {

    DbOperations connect(String database, String userName, String password);

    List<String> getCommandList();

    void createTable(String tableName, List<String> columns, DbOperations dbOperations);

    List<String> getAllTables(DbOperations dbOperations);

    List<List<String>> getTable(String tableName, DbOperations dbOperations);

    void insertRow(String tableName, Map<String, String> insertData, DbOperations dbOperations);

    void updateRows(String tableName, Pair<String, String> columnToFindWithValue , Map<String, String> updateData,
                    DbOperations dbOperations);

    void deleteRows(String tableName, String column, String value, DbOperations dbOperations);

    void dropTable(String tableName, DbOperations dbOperations);
}
