package controller.web.action;

import controller.web.action.AbstractAction;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Deprecated
public class GetTableDataAction extends AbstractAction {

    private Service service;

    public GetTableDataAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (!isDbInitialized(req)) {
            redirectToJsp("connect", resp);
            return;
        }
        redirectToJsp("getTableByName", resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String tableName = req.getParameter("tableName");
                List<List<String>> tableData = service.getTable(tableName,
                        getSessionDbOperations(req));
                req.setAttribute("table", tableData);
                forwardToJsp("tableData",req, resp);
    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/getTableByName");
    }
}
