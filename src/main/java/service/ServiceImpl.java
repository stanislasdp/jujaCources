package service;

import com.google.common.collect.ImmutableList;
import model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class ServiceImpl implements Service {

    @Autowired
    private DbOperations dbOperations;

    @Lookup
    private DbOperations getDbOperations() {
        return dbOperations;
    }

    @Override
    public DbOperations connect(String database, String userName, String password) {
        Properties properties = new Properties();
        properties.setProperty("database", database);
        properties.setProperty("user", userName);
        properties.setProperty("password", password);
        return getDbOperations().connect(properties);
    }

    @Override
    public List<String> getCommandList() {
        return ImmutableList.of("menu", "createTable", "getTables");
    }

    @Override
    public void createTable(String tableName, List<String> columns, DbOperations dbOperations) {
        dbOperations.create(tableName, columns);
    }

    @Override
    public List<List<String>> getTable(String tableName, DbOperations dbOperations) {
        Data data = dbOperations.find(tableName);
        List<List<String>> result = new ArrayList<>();
        result.add(new ArrayList<>(data.getNames()));
        data.getValues().stream()
                .map(Row::getValuesInAllColumns)
                .forEachOrdered(result::add);
        return result;
    }

    @Override
    public List<String> getAllTables(DbOperations dbOperations) {
        return dbOperations.getTables();
    }

    public void insertRow(String tableName, Map<String, String> insertData, DbOperations dbOperations) {

        dbOperations.insert(tableName, mapToData(insertData));
    }


    @Override
    public void updateRows(String tableName, Pair<String, String> columnToFindWithValue,
                           Map<String, String> updateData, DbOperations dbOperations) {
        dbOperations.update(tableName, columnToFindWithValue.getKey(),
                columnToFindWithValue.getValue(), mapToData(updateData));
    }

    private Data mapToData(Map<String, String> columnWithValuesMap) {
        return new Data() {
            @Override
            public Collection<Row> getValues() {
                return ImmutableList.of(new Row(this,
                        new ArrayList<>(columnWithValuesMap.values())));
            }

            @Override
            public Collection<String> getNames() {
                return columnWithValuesMap.keySet();
            }
        };
    }

    @Override
    public void deleteRows(String tableName, String column, String value, DbOperations dbOperations) {
        dbOperations.delete(tableName, column, value);
    }

    @Override
    public void dropTable(String tableName, DbOperations dbOperations) {
        dbOperations.dropTable(tableName);
    }
}
