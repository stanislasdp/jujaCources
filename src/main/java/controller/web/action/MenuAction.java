package controller.web.action;

import controller.web.action.AbstractAction;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
public class MenuAction extends AbstractAction {


    private Service service;

    public MenuAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        req.setAttribute("items", service.getCommandList());
        forwardToJsp("menu", req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {

    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/menu");
    }
}
