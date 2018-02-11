package controller.web.action;

import controller.web.action.AbstractAction;
import org.apache.commons.lang3.StringUtils;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class DeleteAction extends AbstractAction {

    private Service service;

    public DeleteAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doDelete(req);
        redirectToJsp("menu", resp);

    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/deleteColumns");
    }

    private void doDelete(HttpServletRequest req) {
        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        parameterMap.putAll(req.getParameterMap());
        String column = null;
        for (Iterator<Map.Entry<String, String[]>> it = parameterMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String[]> entry = it.next();
            String candidate = StringUtils.removeEnd(entry.getKey(), "_checkbox");
            if (!candidate.equals(entry.getKey())) {
                column = candidate;
                parameterMap.remove(entry.getKey());
                break;
            }
        }
        Objects.requireNonNull(column);
        service.deleteRows(req.getParameter("tableName"), column,
                parameterMap.get(column)[0], getSessionDbOperations(req));
    }
}
