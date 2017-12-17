package main;

;import controller.Controller;
import model.PostrgreDbOPerations;
import view.view.ConsoleView;

/**
 * Created by stas on 10/21/17.
 */
public class Main {

    public static void main(String[] args) {
        Controller controller = new Controller(new PostrgreDbOPerations(), new ConsoleView());
        controller.run();
    }
}
