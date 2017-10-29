package controller.commands;

import java.util.List;

import model.DbOperations;
import view.*;
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
        view.write(dbOperations.getTables().stream().collect(joining(", ")));
    }
}
