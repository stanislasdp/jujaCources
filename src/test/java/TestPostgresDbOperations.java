import model.DbOperations;
import model.PostrgreDbOPerations;
import org.junit.Before;

import static org.powermock.reflect.Whitebox.setInternalState;


/**
 * Created by stas on 10/29/17.
 */
public class TestPostgresDbOperations extends TestDbOperations {

    @Before
    public void initConnection() {
        setInternalState(dbOperations, "connection", connection);
    }

    @Override
    public DbOperations getDatabaseManager() {
        return new PostrgreDbOPerations();
    }
}
