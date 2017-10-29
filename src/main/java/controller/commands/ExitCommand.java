package controller.commands;

import java.util.List;

import model.DbOperations;
import view.*;
import view.view.View;

/**
 * Created by stas on 10/29/17.
 */
public class ExitCommand implements Command<String> {

    private DbOperations dbOperations;
    private View view;

    public ExitCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        dbOperations.exit();
        view.write("Connection to db has been closed");
    }
}
