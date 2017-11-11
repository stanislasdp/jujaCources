package controller.commands;

import controller.exceptions.ControllerException;
import model.Data;
import model.DbOperations;
import view.view.View;

import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Created by stas on 10/29/17.
 */
public class GetTableColumnsCommand implements Command<String> {

    private static final int ALLOWED_PARAMS_SIZE = 1;

    private DbOperations dbOperations;
    private View view;

    public GetTableColumnsCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() != ALLOWED_PARAMS_SIZE) {
            throw new ControllerException("Incorrect parameters size, should be only tableName");
        }
       Data data =  dbOperations.find(parameters.get(0));
       view.write(data.getNames().stream().collect(joining(" ")));
       data.getValues().forEach(row -> {
           view.write(row.getValuesInAllColumns().stream().collect(joining(" ")));
       });
    }
}
