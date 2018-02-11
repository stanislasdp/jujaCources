package controller.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Action {

    void doGet(HttpServletRequest req, HttpServletResponse resp);

    void doPost(HttpServletRequest req, HttpServletResponse resp);

    boolean canProcess(String req);

}
