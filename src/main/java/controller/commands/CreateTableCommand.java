package controller.commands;

import java.util.List;

import model.DbOperations;
import view.*;
import view.view.View;

/**
 * Created by stas on 10/29/17.
 */
public class CreateTableCommand implements Command<String> {

    private DbOperations dbOperations;
    private View view;


    public CreateTableCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        String tableName = parameters.get(0);
        dbOperations.create(tableName, parameters.subList(1, parameters.size()));
    }
}
