package controller.web.action;

import controller.web.action.AbstractAction;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

public class InsertAction extends AbstractAction {

    private Service service;

    public InsertAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        if (!isDbInitialized(req)) {
            redirectToJsp("connect", resp);
            return;
        }
        doInsert(req);
        forwardToJsp("menu", req, resp);
    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/insertRow");
    }


    private void doInsert(HttpServletRequest req) {
        Map<String, String> mapToInsert = new LinkedHashMap<>();
        req.getParameterMap()
                .forEach((key, value) -> mapToInsert.put(((String) key), ((String[]) value)[0]));
        mapToInsert.remove("Insert");
        mapToInsert.remove("tableName");
        service.insertRow(req.getParameter("tableName"), mapToInsert,
                getSessionDbOperations(req));
    }

}
