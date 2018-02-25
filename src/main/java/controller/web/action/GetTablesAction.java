package controller.web.action;

import controller.web.action.AbstractAction;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
public class GetTablesAction extends AbstractAction {

    private Service service;

    public GetTablesAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (!isDbInitialized(req)) {
            redirectToJsp("connect", resp);
            return;
        }
        req.setAttribute("tables", service.getAllTables(getSessionDbOperations(req)));
        forwardToJsp("tablesData", req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/getTables");
    }
}
