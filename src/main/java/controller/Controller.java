package controller;

import com.google.common.collect.ImmutableMap;
import controller.commands.*;
import controller.exceptions.ControllerException;
import model.DbOperations;
import model.exceptions.MyDbException;
import view.view.View;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Created by stas on 10/21/17.
 */
public class Controller {

    private DbOperations dbOperations;
    private View view;
    private Map<String, Command> commandMap;

    public Controller(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
        initCommands();

    }

    private void initCommands() {
        commandMap = new ImmutableMap.Builder<String, Command>()
                .put("connect", new ConnectCommand(dbOperations, view))
                .put("tables", new GetTablesCommand(dbOperations, view))
                .put("clear", new ClearTableCommand(dbOperations, view))
                .put("drop", new DropTableCommand(dbOperations, view))
                .put("create", new CreateTableCommand(dbOperations, view))
                .put("help", new HelpCommand(view))
                .put("exit", new ExitCommand(dbOperations, view))
                .put("insert", new InsertCommand(dbOperations, view))
                .put("update", new UpdateCommand(dbOperations, view))
                .put("delete", new DeleteRowsCommand(dbOperations, view))
                .put("find", new GetTableColumnsCommand(dbOperations, view))
                .build();
    }


    public void run() {
        boolean isDbConnected = false;

        while (true) {
            view.write("Enter the command or type help");
            String[] inputArray = view.read().split("\\|");
            String commandString = inputArray[0].trim();
            Optional<Command> commandOpt = Optional.ofNullable(commandMap.get(commandString));
            List<String> parameters = getParamWithStrippedCommand(inputArray);
            if (!commandOpt.isPresent()) {
                view.write("Invalid command");
                continue;
            }
            Command command = commandOpt.get();
            try {
                if ("connect".equalsIgnoreCase(commandString)) {
                        command.execute(parameters);
                        isDbConnected = true;
                        continue;
                } else if ("help".equalsIgnoreCase(commandString)) {
                    command.execute(parameters);
                    continue;
                } else if ("exit".equalsIgnoreCase(commandString)) {
                    command.execute(parameters);
                    break;
                }

                if (!isDbConnected) {
                    view.write("Connection to DB must be initialized first");
                    continue;
                }
                command.execute(parameters);
            } catch (MyDbException | ControllerException e) {
                printException(e);
            }
        }

    }

    private void printException(Exception e) {
        view.write("Error has been occurred: " + e.getMessage());
        if (!Objects.isNull(e.getCause())) {
            view.write(e.getCause().getMessage());
        }
        view.write("Please try again one more time");
    }


    private List<String> getParamWithStrippedCommand(String[] inputArray) {
        return Stream.of(inputArray).skip(1).map(String::trim).collect(toList());
    }
}
