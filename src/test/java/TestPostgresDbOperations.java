import database.dao.DbOperations;
import org.junit.Before;
import org.junit.Ignore;

import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Created by stas on 10/29/17.
 */
@Ignore
public class TestPostgresDbOperations extends TestDbOperations {

    @Before
    public void initConnection() {
        setInternalState(dbOperations, "connection", connection);
    }

    @Override
    public DbOperations getDatabaseManager() {
//        return new PostrgreDbOPerations();
        return null;
    }
}
