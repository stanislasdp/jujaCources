package controller.web.action;

import controller.web.action.AbstractAction;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DropAction extends AbstractAction {

    private Service service;

    public DropAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        if (!isDbInitialized(req)) {
            redirectToJsp("connect", resp);
            return;
        }
        redirectToJsp("getTableForDrop", resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        service.dropTable(req.getParameter("tableName"), getSessionDbOperations(req));
    }

    @Override
    public boolean canProcess(String req) {
        return req.startsWith("/dropTable");
    }
}
