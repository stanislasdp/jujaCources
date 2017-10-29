package controller.commands;

import model.DbOperations;
import view.*;
import view.view.View;

import java.util.List;

/**
 * Created by stas on 10/29/17.
 */
public class ClearTableCommand implements Command<String> {

    private DbOperations dbOperations;
    private View view;

    public ClearTableCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        final String table = parameters.get(0);
        dbOperations.clearTable(table);
        view.write(String.format("Table %s is cleared", table));
    }
}
