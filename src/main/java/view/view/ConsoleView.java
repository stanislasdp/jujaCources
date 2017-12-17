package view.view;
import java.util.Scanner;

/**
 * Created by stas on 10/21/17.
 */
public class ConsoleView implements View {


    @Override
    public void write(String string) {
        System.out.println(string);
    }

    @Override
    public String read() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
