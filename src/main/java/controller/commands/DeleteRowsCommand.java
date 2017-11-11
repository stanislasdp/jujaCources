package controller.commands;

import controller.exceptions.ControllerException;
import model.Data;
import model.DbOperations;
import view.view.View;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Created by stas on 11/11/17.
 */
public class DeleteRowsCommand implements Command<String> {
    private static final int ALLOWED_PARAMETERS_SIZE = 3;

    private DbOperations dbOperations;
    private View view;

    public DeleteRowsCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() != ALLOWED_PARAMETERS_SIZE) {
            throw new ControllerException("Incorect paramters size, should be table, column, value");
        }
        final String table = parameters.get(0);
        final String column = parameters.get(1);
        final String value  = parameters.get(2);
        Data deletedData = dbOperations.delete(table, column, value);
        view.write(deletedData.getNames().stream().collect(joining(" ")));
        if (deletedData.getValues().isEmpty()) {
            view.write(String.format("No value %s in table %s that is present in column %s", value, table, column));
        } else {
        deletedData.getValues().forEach(row -> {
            view.write(row.getValuesInAllColumns().stream().collect(joining(" ")));
        });
    }
}
}
