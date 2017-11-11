package controller.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import controller.exceptions.ControllerException;
import model.Data;
import model.DbOperations;
import model.SqlTable;
import view.view.View;

import static java.util.stream.Collectors.toList;

/**
 * Created by stas on 10/29/17.
 */
public class InsertCommand implements Command<String> {
    private static int MIN_ALLOWED_PARAMS_SIZE = 5;

    private DbOperations dbOperations;
    private View view;

    public InsertCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    private void validateParams(List<String> parameters) {
        if (parameters.size() < MIN_ALLOWED_PARAMS_SIZE) {
            throw new ControllerException("columns are not specified in parameters");
        } else if (parameters.subList(2 ,parameters.size()).size() % 2 == 0) {
            throw new ControllerException(" some column or value is not specified");
        }
    }

    @Override
    public void execute(List<String> parameters) {
        validateParams(parameters);
        final String table = parameters.get(0);
        final List<String> columns = IntStream.range(1, parameters.size())
                .filter(value -> value % 2 != 0)
                .mapToObj(parameters::get)
                .collect(toList());
        List<List<String>> rows = new ArrayList<>();
        rows.add(IntStream.range(1, parameters.size())
                .filter(value -> value % 2 == 0)
                .mapToObj(parameters::get)
                .collect(toList()));
        Data data = new SqlTable(columns, rows);
        dbOperations.insert(table, data);
        view.write(String.format("new row is inserted to %s", table));
    }
}
