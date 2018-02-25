package controller.web.action;

import com.google.common.collect.Iterables;
import controller.web.action.AbstractAction;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Deprecated
public class GetTableToDeleteAction extends AbstractAction {

    private Service service;

    public GetTableToDeleteAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (!isDbInitialized(req)) {
            forwardToJsp("connect", req, resp);
            return;
        }
        redirectToJsp("getTableForDelete", resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        final String tableName = req.getParameter("tableName");
        List<String> columnNames = Iterables.getFirst(service.getTable(tableName,
                getSessionDbOperations(req)),
                Collections.emptyList());
        req.setAttribute("tableName", tableName);
        req.setAttribute("columnNames", columnNames);
        forwardToJsp("deleteColumns",req, resp);
    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/deleteFromTable");
    }
}
