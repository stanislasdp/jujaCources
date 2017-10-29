package controller.commands;

import java.util.List;

import model.DbOperations;
import view.*;
import view.view.View;

/**
 * Created by stas on 10/29/17.
 */
public class DropTableCommand implements Command<String> {

    private DbOperations dbOperations;
    private View view;

    public DropTableCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        final String tableName = parameters.get(0);
        dbOperations.dropTable(tableName);
        view.write(String.format("Table %s is dropped", tableName));
    }
}
