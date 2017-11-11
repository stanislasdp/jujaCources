package controller.commands;

import view.view.View;

import java.util.List;

/**
 * Created by stas on 11/11/17.
 */
public class HelpCommand implements Command<String> {

    private View view;

    public HelpCommand(View view) {
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        view.write("Available commands:");
        view.write("connect | database | username | password - connect to DB");
        view.write("tables - retrieve all table names from DB");
        view.write("clear | tablename - clear all data from <tablename>");
        view.write("drop | tableName - drop <tablename> from DB" );
        view.write("create | tableName | column1 | column2 | ... | columnN - create table <tableName> with columns 1 -N");
        view.write("find | tableName - retreive all all columns and rows data from <tableName>");
        view.write("insert | tableName | column1 | value1 | column2 | value2 | ... | columnN |" +
        "insert rows with specified columns 1 -N to <tablename>");
        view.write("update | tableName | column1 | value1 | column2 | value2 |columnN |  valueN"
                + "update row that has column1 and value1 with column2=value2...columnN=valueN in <tableName>");
        view.write("delete | tableName | column | value - delete rows from <tableName> which correspond to columnn=value from <tableName>");
        view.write("exit - disconnects from DB");
    }
}
