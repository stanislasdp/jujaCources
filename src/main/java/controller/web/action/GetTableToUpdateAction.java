package controller.web.action;

import com.google.common.collect.Iterables;
import controller.web.action.AbstractAction;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Deprecated
public class GetTableToUpdateAction extends AbstractAction {

    private Service service;

    public GetTableToUpdateAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (!isDbInitialized(req)) {
            redirectToJsp("connect", resp);
            return;
        }
        redirectToJsp("getTableForUpdate", resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String tableName = req.getParameter("tableName");
        List<String> columnNames = Iterables.getFirst(service.getTable(tableName,
                getSessionDbOperations(req)),
                Collections.emptyList());
        req.setAttribute("tableName", tableName);
        req.setAttribute("columnNames", columnNames);
        forwardToJsp("updateColumns",req, resp);
    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/updateTable");
    }
}


