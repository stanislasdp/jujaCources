package controller.commands;

import java.util.List;

import controller.exceptions.ControllerException;
import model.DbOperations;
import view.view.View;

/**
 * Created by stas on 10/29/17.
 */
public class CreateTableCommand implements Command<String> {

    private static int MIN_ALLOWED_ARG_COUNT = 2;

    private DbOperations dbOperations;
    private View view;


    public CreateTableCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() < MIN_ALLOWED_ARG_COUNT) {
            throw new ControllerException("Create table command needs at least onr column for table");
        }
        String tableName = parameters.get(0);
        dbOperations.create(tableName, parameters.subList(1, parameters.size()));
        view.write(String.format("table %s has been created", tableName));
    }
}
