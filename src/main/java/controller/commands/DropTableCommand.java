package controller.commands;

import java.util.List;

import controller.exceptions.ControllerException;
import model.DbOperations;
import view.*;
import view.view.View;

/**
 * Created by stas on 10/29/17.
 */
public class DropTableCommand implements Command<String> {

    private static final int ALLOWED_PARAMS_SIZE = 1;

    private DbOperations dbOperations;
    private View view;

    public DropTableCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() != ALLOWED_PARAMS_SIZE) {
            throw new ControllerException("Should be one parameter tableName");
        }
        final String tableName = parameters.get(0);
        dbOperations.dropTable(tableName);
        view.write(String.format("Table %s is dropped", tableName));
    }
}
