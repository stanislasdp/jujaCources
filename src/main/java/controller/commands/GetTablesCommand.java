package controller.commands;

import java.util.List;
import java.util.stream.Collectors;

import model.DbOperations;
import view.view.View;

import static java.util.stream.Collectors.joining;

/**
 * Created by stas on 10/29/17.
 */
public class GetTablesCommand implements Command<String> {

    private DbOperations dbOperations;
    private View view;

    public GetTablesCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        List <String> tables = dbOperations.getTables();
        view.write(!tables.isEmpty() ? tables.stream().collect(joining(", "))
                : "No tables present in selected DB");
    }
}
