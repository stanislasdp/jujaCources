package controller.commands;

import model.Data;
import model.DbOperations;
import view.view.View;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by stas on 10/29/17.
 */
public class GetTableColumnsCommand implements Command<String> {
    private DbOperations dbOperations;
    private View view;

    public GetTableColumnsCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
       Data data =  dbOperations.find(parameters.get(0));
       view.write(data.getNames().stream().collect(Collectors.joining(" ")));
       data.getValues().forEach(row -> {
           view.write(row.getValuesInAllColumns().stream().collect(Collectors.joining(" ")));
       });
    }
}
