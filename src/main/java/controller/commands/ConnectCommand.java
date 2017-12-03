package controller.commands;

import java.util.List;
import java.util.Properties;

import controller.exceptions.ControllerException;
import model.DbOperations;
import view.view.View;

/**
 * Created by stas on 10/29/17.
 */
public class ConnectCommand implements Command<String> {

    private static int ALLOWED_PARAMETERS_SIZE = 3;

    private DbOperations dbOperations;
    private View view;

    public ConnectCommand(DbOperations dbOperations, View view) {
        this.dbOperations = dbOperations;
        this.view = view;
    }

    @Override
    public void execute(List<String> parameters) {
        if (parameters.size() != ALLOWED_PARAMETERS_SIZE) {
            throw new ControllerException("Should be three parameters(database, login, password)");
        }
            Properties properties = new Properties();
            properties.setProperty("database", parameters.get(0));
            properties.setProperty("user", parameters.get(1));
            properties.setProperty("password", parameters.get(2));
            dbOperations.connect(properties);
            view.write("Successful connect to db");
    }



}
