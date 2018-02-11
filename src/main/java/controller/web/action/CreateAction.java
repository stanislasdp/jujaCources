package controller.web.action;

import controller.web.action.AbstractAction;
import lombok.SneakyThrows;
import model.DbOperations;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class CreateAction extends AbstractAction {

    private Service service;

    public CreateAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (!isDbInitialized(req)) {
            redirectToJsp("connect", resp);
            return;
        }
        forwardToJsp("createTable", req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doCreate(req, resp);
        forwardToJsp("menu", req, resp);
    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/createTable");
    }


    @SneakyThrows
    private void doCreate(HttpServletRequest req, HttpServletResponse resp) {
        Map<String, String[]> map = req.getParameterMap();
        AtomicReference<String> tableName = new AtomicReference<>();
        List<String> columns = new ArrayList<>();
        map.forEach((key, value) ->
                {
                    if (key.contains("Column")) {
                        columns.add(value[0]);
                    } else if (key.contains("tableName")) {
                        tableName.set(value[0]);
                    }
                }
        );
        Objects.requireNonNull(tableName);
        service.createTable(tableName.get(), columns,
                (DbOperations) req.getSession().getAttribute("dboperations"));
    }
}
