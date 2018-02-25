package controller.web.action;

import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
public class ErrorAction extends AbstractAction {

    private Service service;

    public ErrorAction() {
    }

    public ErrorAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        forwardToJsp("error", req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        forwardToJsp("error", req, resp);
    }

    @Override
    public boolean canProcess(String req) {
        return Boolean.FALSE;
    }
}
