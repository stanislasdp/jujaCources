package controller.web.action;

import controller.web.action.AbstractAction;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import service.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Deprecated
public class UpdateAction extends AbstractAction {

    private Service service;

    public UpdateAction(Service service) {
        this.service = service;
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        doUpdate(req);
        redirectToJsp("menu", resp);
    }

    @Override
    public boolean canProcess(String action) {
        return action.startsWith("/updateRowsTable");
    }

    private void doUpdate(HttpServletRequest req) {
        Map<String, String> mapToUpdate = new LinkedHashMap<>();
        req.getParameterMap()
                .forEach((key, value) -> mapToUpdate.put(((String) key), ((String[]) value)[0]));
        mapToUpdate.remove("updateTable");
        mapToUpdate.remove("Update");
        mapToUpdate.remove("tableName");
        String criteriaToFound = null;
        for (Iterator<Map.Entry<String, String>> it = mapToUpdate.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, String> entry = it.next();
            String candidate = StringUtils.removeEnd(entry.getKey(), "_checkbox");
            if (!candidate.equals(entry.getKey())) {
                criteriaToFound = candidate;
                mapToUpdate.remove(entry.getKey());
                break;
            }
        }
        service.updateRows(req.getParameter("tableName"),
                Pair.of(criteriaToFound, mapToUpdate.get(criteriaToFound)),
                mapToUpdate, getSessionDbOperations(req));
    }
}
