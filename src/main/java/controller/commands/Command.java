package controller.commands;

import java.util.List;

/**
 * Created by stas on 10/21/17.
 */
public interface Command <T> {


    void execute(List<T> parameters);


}

