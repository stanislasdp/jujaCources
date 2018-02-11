package model;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by stas on 10/19/17.
 */
public interface Data {

    Collection<Row> getValues();

    Collection<String> getNames();


}
