package controller.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.ImmutableList;
import model.Data;
import model.DbOperations;
import model.SqlTable;
import view.view.View;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 * Created by stas on 10/29/17.
 */
public class UpdateCommand implements Command<String> {

    private DbOperations dbOperations;
    private View view;

    public UpdateCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        final String tableName = parameters.get(0);
        final String columnToUpdate = parameters.get(1);
        final String valueToUpdate = parameters.get(2);
        final List<String> columns =
                range(1, parameters.size())
                        .filter(value -> value % 2 != 0)
                        .mapToObj(parameters::get)
                        .collect(toList());

        List<List<String>> rows = new ArrayList<List<String>>() {
            {
                add(range(1, parameters.size())
                        .filter(value -> value % 2 == 0)
                        .mapToObj(parameters::get)
                        .collect(toList()));
            }
        };
        dbOperations.update(tableName, columnToUpdate, valueToUpdate
                , new SqlTable(columns.subList(1, columns.size())
                        , new ArrayList<List<String>>() {{
                    add(rows.get(0).subList(1, columns.size()));
                }}));
        view.write(columns.stream().collect(joining(" ")));
        view.write(rows.get(0).stream().collect(joining(" ")));
    }
}
