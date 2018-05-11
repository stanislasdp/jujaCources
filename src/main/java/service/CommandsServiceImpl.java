package service;

import com.google.common.collect.ImmutableList;
import database.dao.DbOperations;
import database.model.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import repository.UserActionRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class CommandsServiceImpl implements CommandsService {

    @Autowired
    private DbOperations dbOperations;

    @Autowired
    private UserActionRepository userActionRepository;

    @Lookup
    public DbOperations getDbOperations() {
        return dbOperations;
    }

    @Override
    public List<String> getCommandList() {
        return ImmutableList.of("menu", "createTable", "getTables");
    }

    @Override
    public void createTable(String tableName, List<String> columns, DbOperations dbOperations) {
        dbOperations.create(tableName, columns);
        User user = getCurrentUser(dbOperations);
        addLogAction(user, ActionName.CREATE);

        userActionRepository.save(user);
    }

    @Override
    public List<List<String>> getTable(String tableName, DbOperations dbOperations) {
        Data data = dbOperations.find(tableName);
        List<List<String>> result = new ArrayList<>();
        result.add(new ArrayList<>(data.getNames()));
        data.getValues().stream()
                .map(Row::getValuesInAllColumns)
                .forEachOrdered(result::add);
        User user = getCurrentUser(dbOperations);
        addLogAction(user, ActionName.GET);
        userActionRepository.save(user);
        return result;
    }

    @Override
    public List<String> getAllTables(DbOperations dbOperations) {
        List<String> tables = dbOperations.getTables();
        User user = getCurrentUser(dbOperations);
        addLogAction(user, ActionName.GETALL);
        userActionRepository.save(user);
        return tables;
    }


    @Override
    public void insertRow(String tableName, Map<String, String> insertData, DbOperations dbOperations) {
        dbOperations.insert(tableName, mapToData(insertData));
        User user = getCurrentUser(dbOperations);
        addLogAction(user, ActionName.INSERT);
        userActionRepository.save(user);
    }


    @Override
    public void updateRows(String tableName, Pair<String, String> columnToFindWithValue,
                           Map<String, String> updateData, DbOperations dbOperations) {
        dbOperations.update(tableName, columnToFindWithValue.getKey(),
                columnToFindWithValue.getValue(), mapToData(updateData));
        User user = getCurrentUser(dbOperations);
        addLogAction(user, ActionName.UPDATE);
        userActionRepository.save(user);
    }


    @Override
    public void deleteRows(String tableName, String column, String value, DbOperations dbOperations) {
        dbOperations.delete(tableName, column, value);
        User user = getCurrentUser(dbOperations);
        addLogAction(user, ActionName.DELETE);
    }


    @Override
    public void dropTable(String tableName, DbOperations dbOperations) {
        dbOperations.dropTable(tableName);
        User currentUser = getCurrentUser(dbOperations);
        addLogAction(currentUser, ActionName.DROP);
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


    private User getCurrentUser(DbOperations dbOperations) {
        User user = new User();
        user.setName(dbOperations.getUserName());
        return user;
    }

    private void addLogAction(User user, ActionName actionName) {
        Action action = new Action();
        action.setActionName(actionName);
        user.addAction(action);
    }
}
