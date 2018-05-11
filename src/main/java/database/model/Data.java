package database.model;

import java.util.Collection;

/**
 * Created by stas on 10/19/17.
 */
public interface Data {

    Collection<Row> getValues();

    Collection<String> getNames();


}
