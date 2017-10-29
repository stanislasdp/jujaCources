package controller.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import model.Data;
import model.DbOperations;
import model.SqlTable;
import view.*;
import view.view.View;

import static java.util.stream.Collectors.toList;

/**
 * Created by stas on 10/29/17.
 */
public class InsertCommand implements Command<String> {

    private DbOperations dbOperations;
    private View view;

    public InsertCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        final List<String> columns = IntStream.range(1, parameters.size())
                .filter(value -> value%2 != 0)
                .mapToObj(parameters::get)
                .collect(toList());
        List<List<String>> rows = new ArrayList<>();
        rows.add(IntStream.range(1, parameters.size())
                .filter(value -> value%2 == 0)
                .mapToObj(parameters::get)
                .collect(toList()));
        Data data = new SqlTable(columns, rows);
        dbOperations.insert(parameters.get(0), data);
    }
}
