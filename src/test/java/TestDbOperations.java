

import com.google.common.collect.ImmutableList;
import model.*;
import model.exceptions.MyDbException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import java.sql.*;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Created by stas on 10/29/17.
 */
@RunWith(PowerMockRunner.class)
public abstract class TestDbOperations {
    protected static DbOperations dbOperations;
    protected static Connection connection;
    private Statement statement;
    private ArgumentCaptor<String> argumentCaptor;
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @BeforeClass
    public static void prepare() {
        dbOperations = new PostrgreDbOPerations();
    }

    @Before
    public void mockData() throws SQLException {
        connection = Mockito.mock(Connection.class);
        statement = Mockito.mock(Statement.class);
        argumentCaptor = ArgumentCaptor.forClass(String.class);
        when(statement.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
    }


    @Test
    public void getTablesTestWhenPresent() throws SQLException {
        //GIVEN
        final String tableFirst = "Person1";
        final String tableSecond = "Person2";
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn(tableFirst).thenReturn(tableSecond);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        //WHEN
        List<String> tables = dbOperations.getTables();
        String sql = "SELECT table_schema || '.' || table_name FROM information_schema.tables " +
                "WHERE table_type = 'BASE TABLE' " +
                "AND table_schema NOT IN ('pg_catalog', 'information_schema')";
        //THEN;
        verify(statement).executeQuery(eq(sql));
        assertThat("list should contain al tables", tables, containsInAnyOrder(tableFirst, tableSecond));
    }

    @Test
    public void clearTableTestWhenDataExists() throws SQLException {
        //GIVEN
        final String tableToDelete = "Person3";
        when(statement.execute(anyString())).thenReturn(false);
        //WHEN

        dbOperations.clearTable(tableToDelete);
        //THEN
        verify(statement).execute(eq(String.format("DELETE FROM %s", tableToDelete)));
    }

    @Test(expected = MyDbException.class)
    public void clearTableTestWhenNoDataExists() throws SQLException {
        final String tableToDelete = "Person3";
        when(statement.execute(anyString())).thenReturn(true);
        dbOperations.clearTable(tableToDelete);
    }


    @Test
    public void dropTableWhenTableExistsTest() throws SQLException {
        //GIVEN
        final String tableToDrop = "Person3";
        when(statement.execute(anyString())).thenReturn(false);
        makeTableExists(tableToDrop, true);

        //WHEN
        dbOperations.dropTable(tableToDrop);

        //THEN
        verify(statement).execute(eq(String.format("DROP TABLE %s", tableToDrop)));
    }

    @Test(expected = MyDbException.class)
    public void dropTableWhenTableNotExistsTest() throws SQLException {
        final String tableToDrop = "Person3";
        when(statement.execute(anyString())).thenReturn(true);
        dbOperations.clearTable(tableToDrop);
    }


    @Test
    public void createTableTestWhenTableNotExists() throws SQLException {
        //GIVEN
        final List<String> tableColumns = ImmutableList.of("id", "firstname", "lastname", "age");
        final String tableToCreate = "Person4";
        when(statement.executeUpdate(argumentCaptor.capture())).thenReturn(0);

        //WHEN
        dbOperations.create(tableToCreate, tableColumns);

        //THEN
        verify(statement).executeUpdate(eq("CREATE TABLE Person4 ( id text,firstname text,lastname text,age text)"));
    }

    @Test(expected = MyDbException.class)
    public void createTableTestWhenTableExists() throws SQLException {
        //GIVEN
        final List<String> tableColumns = ImmutableList.of("id", "firstname", "lastname", "age");
        final String tableToCreate = "Person4";
        //WHEN
        when(statement.executeUpdate(anyString())).thenReturn(1);
        //THEN
        dbOperations.create(tableToCreate, tableColumns);
    }

    @Test
    public void findTableTest() throws SQLException {
        //GIVEN
        final String tableToFind = "Person5";

        Data expectedData = new SqlTable(ImmutableList.of("id", "firstname", "lastname", "age")
                , ImmutableList.of(ImmutableList.of("testId", "testFirstName", "testLastName", "testAge")));
        ResultSet resultSet = mockResultSetForData(expectedData);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);
        makeTableExists(tableToFind, true);

        //WHEN
        Data data = dbOperations.find(tableToFind);
        //THEN
        verify(statement).executeQuery(eq("SELECT * FROM " + tableToFind));
        assertThat("data object contains columns", data.getNames(),
                allOf(hasItem("id"), hasItem("firstname"), hasItem("lastname"), hasItem("age")));
        assertThat("columns contains data", data.getValues().stream()
                .flatMap(row -> row.getValuesInAllColumns().stream())
                .collect(toList()), allOf(hasItem("testId"), hasItem("testFirstName")
                , hasItem("testLastName"), hasItem("testAge")));
    }


    @Test
    public void insertRowTest() throws SQLException {
        //GIVEN
        String tableToInsert = "Person6";
        Data dataToInsert = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "age"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")
                        , ImmutableList.of("testId2", "testFirstName2", "testLastName2", "testAge2")));
        when(statement.executeBatch()).thenReturn(new int[]{1, 1});
        makeTableExists(tableToInsert, true);

        //WHEN
        dbOperations.insert(tableToInsert, dataToInsert);

        //THEN
        verify(statement).addBatch(eq("INSERT INTO Person6(id,firstName,lastName,age)" +
                " VALUES ( 'testId1','testFirstName1','testLastName1','testAge1' )"));
        verify(statement).addBatch(eq("INSERT INTO Person6(id,firstName,lastName,age)" +
                " VALUES ( 'testId2','testFirstName2','testLastName2','testAge2' )"));

    }

    @Test
    public void insertRowNegativeTest() throws SQLException {
        //GIVEN
        String tableToInsert = "Person6";
        Data dataToInsert = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")
                        , ImmutableList.of("testId2", "testFirstName2", "testLastName2", "testAge2")));
        makeTableExists(tableToInsert, true);
        when(statement.executeBatch()).thenReturn(new int[]{1, 0});
        expectedEx.expect(MyDbException.class);
        expectedEx.expectMessage("Some columns may not be inserted");

        //WHEN
        dbOperations.insert(tableToInsert, dataToInsert);
    }

    @Test
    public void insertRowWhenTableDoesNotExistTest() throws SQLException {
        //GIVEN
        String tableToInsert = "Person6";
        Data dataToInsert = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")
                        , ImmutableList.of("testId2", "testFirstName2", "testLastName2", "testAge2")));
        expectedEx.expect(MyDbException.class);
        expectedEx.expectMessage("Table does not exist");
        makeTableExists(tableToInsert, false);

        //WHEN
        dbOperations.insert(tableToInsert, dataToInsert);

    }


    @Test
    public void updateRowTest() throws SQLException {
        //GIVEN
        final String table = "Person7";
        final String column = "id";
        final String value = "7";
        Data dataToUpdate = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")));
        when(statement.executeBatch()).thenReturn(new int[]{1});
        makeTableExists(table, true);

        //WHEN
        dbOperations.update(table, column, value, dataToUpdate);

        //THEN
        verify(statement).addBatch(eq("UPDATE Person7 SET id='testId1',firstName='testFirstName1'," +
                "lastName='testLastName1',id='testAge1' WHERE id = '7'"));
    }

    @Test
    public void updateRowNegativeTest() throws SQLException {
        //GIVEN
        final String tableToUpdate = "Person7";
        final String column = "id";
        final String value = "7";
        Data dataToUpdate = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")));
        makeTableExists(tableToUpdate, true);
        when(statement.executeBatch()).thenReturn(new int[]{0}).thenReturn(new int[]{0, 1});
        expectedEx.expect(MyDbException.class);
        expectedEx.expectMessage("No columns has been updated");

        //WHEN
        dbOperations.update(tableToUpdate, column, value, dataToUpdate);
        expectedEx.expectMessage("Some columns has not been updated");
        dbOperations.update(tableToUpdate, column, value, dataToUpdate);

    }

    @Test
    public void updateTableWhenItNotExists() throws SQLException {
        //GIVEN
        final String tableToUpdate = "Person7";
        final String column = "id";
        final String value = "7";
        Data dataToUpdate = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")));
        makeTableExists(tableToUpdate, false);
        expectedEx.expect(MyDbException.class);
        expectedEx.expectMessage("Table does not exist");

        //WHEN
        dbOperations.update(tableToUpdate, column, value, dataToUpdate);

    }

    @Test
    public void deleteTestWhenTableExists() throws SQLException {
        //GIVEN
        Data expectedData = new SqlTable(ImmutableList.of("id", "column")
                , ImmutableList.of(ImmutableList.of("1", "value")));
        ResultSet resultSet = mockResultSetForData(expectedData);
        final String table = "Person8";
        final String column = "column";
        final String value = "value";
        makeTableExists(table, true);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        //WHEN
        Data actualData = dbOperations.delete(table, column, value);
        verify(statement).executeQuery(eq("SELECT * FROM Person8 WHERE column = 'value'"));
        verify(statement).executeUpdate(eq("DELETE FROM Person8 WHERE column = 'value'"));

        //THEN;
        assertThat("sql results should be the same", expectedData, is(equalTo(actualData)));
    }

    @Test
    public void deleteTestWhenTableNotExists() throws SQLException {
        //GIVEN
        Data expectedData = new SqlTable(ImmutableList.of("id", "column")
                , ImmutableList.of(ImmutableList.of("1", "value")));
        ResultSet resultSet = mockResultSetForData(expectedData);
        final String table = "Person8";
        final String column = "column";
        final String value = "value";
        makeTableExists(table, false);
        expectedEx.expect(MyDbException.class);
        expectedEx.expectMessage("Table does not exist");
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        //WHEN
        dbOperations.delete(table, column, value);

    }


    private ResultSet mockResultSetForData(Data data) throws SQLException {
        ResultSet resultSet = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(resultSet.getMetaData()).thenReturn(metaData);
        int columnsSize = data.getNames().size();
        when(metaData.getColumnCount()).thenReturn(columnsSize);

        List<String> columns = (List<String>) data.getNames();
        for (int i = 1; i <= columnsSize; i++) {
            when(metaData.getColumnLabel(i)).thenReturn(columns.get(i - 1));
        }

        int rowsSize = data.getValues().size();
        List<Row> rows = (List<Row>) data.getValues();
        for (int i = 1; i <= rowsSize; i++) {
            int[] finalI = new int[]{i};
            when(resultSet.next()).thenAnswer(invocation -> {
                if (finalI[0] <= rowsSize) {
                    finalI[0] = ++finalI[0];
                    return true;
                } else {
                    return false;
                }
            });
            int tempCount = 1;
            while (tempCount <= columnsSize) {
                when(resultSet
                        .getString(tempCount))
                        .thenReturn(rows.get(i - 1).getValueInColumn(tempCount - 1));
                tempCount++;
            }
        }
        return resultSet;
    }


    private void makeTableExists(String table, boolean isTableShouldExist) throws SQLException {
        ResultSet resultSetTable = mock(ResultSet.class);
        when(resultSetTable.next()).thenReturn(isTableShouldExist).thenReturn(false);
        DatabaseMetaData databaseMetaData = mock(DatabaseMetaData.class);
        when(connection.getMetaData()).thenReturn(databaseMetaData);
        when(databaseMetaData.getTables(isNull(), isNull(), eq(table.toLowerCase()), any())).thenReturn(resultSetTable);
    }


    public abstract DbOperations getDatabaseManager();


}
