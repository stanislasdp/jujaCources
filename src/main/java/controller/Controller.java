package controller;

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
        commandMap = new HashMap<>();
        commandMap.put("connect", new ConnectCommand(dbOperations, view));
        commandMap.put("tables", new GetTablesCommand(dbOperations, view));
        commandMap.put("clear", new ClearTableCommand(dbOperations, view));
        commandMap.put("drop", new DropTableCommand(dbOperations, view));
        commandMap.put("create", new CreateTableCommand(dbOperations, view));
        commandMap.put("help", new HelpCommand(view));
        commandMap.put("exit", new ExitCommand(dbOperations, view));
        commandMap.put("insert", new InsertCommand(dbOperations, view));
        commandMap.put("update", new UpdateCommand(dbOperations, view));
        commandMap.put("delete", new DeleteRowsCommand(dbOperations, view));
        commandMap.put("find", new GetTableColumnsCommand(dbOperations, view));
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
                if ("connect".equals(commandString) && !isDbConnected) {
                        command.execute(parameters);
                        isDbConnected = true;
                        continue;
                }
                if (!isDbConnected && !"exit".equals(commandString)) {
                    view.write("Connection to DB must be initialized first");
                    continue;
                } else if ("exit".equals(commandString)) {
                    command.execute(parameters);
                    break;
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
