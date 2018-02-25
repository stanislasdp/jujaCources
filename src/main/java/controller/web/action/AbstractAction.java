package controller.web.action;

import model.DbOperations;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Deprecated
public abstract class AbstractAction implements Action {

    protected void forwardToJsp(String fileName, HttpServletRequest request,
                                HttpServletResponse response) {
        try {
            request.getRequestDispatcher(fileName + ".jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void redirectToJsp(String fileName, HttpServletResponse resp) {
        try {
            resp.sendRedirect(fileName + ".jsp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean isDbInitialized(HttpServletRequest req) {
        final String dbAttributeName = "dboperations";
        return Objects.nonNull(req.getSession().getAttribute(dbAttributeName));
    }

    protected DbOperations getSessionDbOperations(HttpServletRequest req) {
        return (DbOperations) req.getSession().getAttribute("dboperations");
    }

//    @SneakyThrows
//    protected void redirectToConnectIfNotInitialized(HttpServletRequest req, HttpServletResponse resp) {
//        if (!isDbInitialized(req)) {
//            redirectToJsp("connect", resp);
//        }
//    }

}
