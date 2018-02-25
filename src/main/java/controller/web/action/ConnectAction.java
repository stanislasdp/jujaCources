package controller.web.action;

import controller.web.action.AbstractAction;
import lombok.SneakyThrows;
import model.DbOperations;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
public class ConnectAction extends AbstractAction {

    private Service service;

    public ConnectAction(Service service) {
        this.service = service;
    }

    @Override
    @SneakyThrows
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
       forwardToJsp("connect", req, resp);
    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/connect");
    }


    @Override
    @SneakyThrows
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        final String dbAttributeName = "dboperations";
        req.getSession().setAttribute(dbAttributeName, doConnect(req));
        resp.sendRedirect(resp.encodeRedirectURL("menu"));
    }

    private DbOperations doConnect(HttpServletRequest req) {
        String databaseName = req.getParameter("dbname");
        String userName = req.getParameter("username");
        String password = req.getParameter("password");
        return service.connect(databaseName, userName, password);
    }
}
