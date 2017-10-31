

import com.google.common.collect.ImmutableList;
import model.Data;
import model.DbOperations;
import model.PostrgreDbOPerations;
import model.SqlTable;
import model.exceptions.MyDbException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.modules.junit4.PowerMockRunner;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


/**
 * Created by stas on 10/29/17.
 */
@RunWith(PowerMockRunner.class)
public abstract class TestDbOperations {
    protected static DbOperations dbOperations;


    protected static Connection connection = mock(Connection.class);
    private Statement statement;


    private ArgumentCaptor<String> argumentCaptor;

    @BeforeClass
    public static void prepare() throws SQLException {

        dbOperations = new PostrgreDbOPerations();
    }

    @Before
    public void test() throws SQLException {
        statement = mock(Statement.class);
        when(statement.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
        argumentCaptor = ArgumentCaptor.forClass(String.class);
    }


    @Test
    public void getTablesTest() throws SQLException {
        //GIVEN
        final String tableFirst = "Person1";
        final String tableSecond = "Person2";
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn(tableFirst).thenReturn(tableSecond);
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        //WHEN
        List<String> tables = dbOperations.getTables();
        String sql = "SELECT table_schema || '.' || table_name FROM information_schema.tables " +
                "WHERE table_type = 'BASE TABLE' " +
                "AND table_schema NOT IN ('pg_catalog', 'information_schema')";
        verify(statement).executeQuery(argumentCaptor.capture());

        //THEN
        assertEquals(sql, argumentCaptor.getValue());
        assertThat("list should contain al tables", tables, containsInAnyOrder(tableFirst, tableSecond));
    }


    @Test
    public void clearTableTestWhenDataExists() throws SQLException {
        //GIVEN
        final String tableToDelete = "Person3";
        when(statement.execute(anyString())).thenReturn(true);

        //WHEN
        dbOperations.clearTable(tableToDelete);
        //THEN
        verify(statement).execute(argumentCaptor.capture());
        final String expectedSql = String.format("DELETE FROM %s", tableToDelete);
        assertEquals(expectedSql, argumentCaptor.getValue());

    }

    @Test(expected = MyDbException.class)
    public void clearTableTestWhenNoDataExists() throws SQLException {
        final String tableToDelete = "Person3";
        when(statement.execute(anyString())).thenReturn(false);
        dbOperations.clearTable(tableToDelete);
    }


    @Test
    public void dropTableWhenTableExistsTest() throws SQLException {
        //GIVEN
        final String tableToDrop = "Person3";
        when(statement.execute(anyString())).thenReturn(true);

        //WHEN
        dbOperations.dropTable(tableToDrop);

        //THEN
        final String expectedSql = String.format("DROP TABLE %s", tableToDrop);
        verify(statement).execute(argumentCaptor.capture());
        assertEquals(expectedSql, argumentCaptor.getValue());
    }

    @Test(expected = MyDbException.class)
    public void dropTableWhenTableNotExistsTest() throws SQLException {
        final String tableToDrop = "Person3";
        when(statement.execute(anyString())).thenReturn(false);
        dbOperations.clearTable(tableToDrop);
    }


    @Test
    public void createTableTestWhenTableNotExists() throws SQLException {
        //GIVEN
        final List<String> tableColumns = new ArrayList<String>() {{
            add("id");
            add("firstname");
            add("lastname");
            add("age");
        }};
        final String tableToCreate = "Person4";
        when(statement.executeUpdate(argumentCaptor.capture())).thenReturn(0);

        //WHEN
        dbOperations.create(tableToCreate, tableColumns);

        //THEN
        verify(statement).executeUpdate(argumentCaptor.capture());
        String expectedSql = "CREATE TABLE Person4 ( id text,firstname text,lastname text,age text)";
        assertEquals(expectedSql, argumentCaptor.getValue());
    }

    @Test(expected = MyDbException.class)
    public void createTableTestWhenTableExists() throws SQLException {
        //GIVEN
        final List<String> tableColumns = new ArrayList<String>() {{
            add("id");
            add("firstname");
            add("lastname");
            add("age");
        }};
        final String tableToCreate = "Person4";
        when(statement.executeUpdate(anyString())).thenReturn(1);

        //WHEN
        dbOperations.create(tableToCreate, tableColumns);
    }


    @Test
    public void findTableTest() throws SQLException {
        //GIVEN
        final String tableToFind = "Person5";
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(false);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(4);
        when(metaData.getColumnLabel(1)).thenReturn("id");
        when(metaData.getColumnLabel(2)).thenReturn("firstname");
        when(metaData.getColumnLabel(3)).thenReturn("lastname");
        when(metaData.getColumnLabel(4)).thenReturn("age");
        when(resultSet.getString(1)).thenReturn("testId");
        when(resultSet.getString(2)).thenReturn("testFirstName");
        when(resultSet.getString(3)).thenReturn("testLastName");
        when(resultSet.getString(4)).thenReturn("testId");
        when(statement.executeQuery(anyString())).thenReturn(resultSet);

        //WHEN
        Data data = dbOperations.find(tableToFind);
        //THEN
        verify(statement).executeQuery(argumentCaptor.capture());
        final String expectedSql = "SELECT * FROM " + tableToFind;
        assertEquals(expectedSql, argumentCaptor.getValue());
        assertThat("data object contains columns", data.getNames(),
                allOf(hasItem("id"), hasItem("firstname"), hasItem("lastname"), hasItem("age")));
        assertThat("columns contains data", data.getValues().stream()
                .flatMap(row -> row.getValuesInAllColumns().stream())
                .collect(toList()), allOf(hasItem("testId"), hasItem("testFirstName")
                , hasItem("testLastName"), hasItem("testId")));
    }


    @Test
    public void insertRowTest() throws SQLException {
        //GIVEN
        String tableToInsert = "Person6";
        Data dataToInsert = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")
                        , ImmutableList.of("testId2", "testFirstName2", "testLastName2", "testAge2")));
        when(statement.executeBatch()).thenReturn(new int[]{1, 1});

        //WHEN
        dbOperations.insert(tableToInsert, dataToInsert);

        //THEN
        final String expectedFirstInsertSql = "INSERT INTO Person6(id,firstName,lastName,id) " +
                "VALUES ( 'testId1','testFirstName1','testLastName1','testAge1' )";
        final String expectedSecondInsertSql = "INSERT INTO Person6(id,firstName,lastName,id) " +
                "VALUES ( 'testId2','testFirstName2','testLastName2','testAge2' )";
        verify(statement, times(2)).addBatch(argumentCaptor.capture());
        assertEquals(expectedFirstInsertSql, argumentCaptor.getAllValues().get(0));
        assertEquals(expectedSecondInsertSql, argumentCaptor.getAllValues().get(1));
    }

    @Test(expected = MyDbException.class)
    public void insertRowNegativeTest() throws SQLException {
        //GIVEN
        String tableToInsert = "Person6";
        Data dataToInsert = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")
                        , ImmutableList.of("testId2", "testFirstName2", "testLastName2", "testAge2")));
        when(statement.executeBatch()).thenReturn(new int[]{1, 0});
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

        //WHEN
        dbOperations.update(table, column, value, dataToUpdate);

        //THEN
        verify(statement).addBatch(argumentCaptor.capture());

        final String expectedSql = "UPDATE Person7 SET id='testId1',firstName='testFirstName1'," +
                "lastName='testLastName1',id='testAge1' WHERE id = '7'";
        assertEquals(expectedSql, argumentCaptor.getValue());
    }

    @Test(expected = MyDbException.class)
    public void updateRowNegativeTest() throws SQLException {
        //GIVEN
        final String table = "Person7";
        final String column = "id";
        final String value = "7";
        Data dataToUpdate = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")));
        when(statement.executeBatch()).thenReturn(new int[]{0});

        //WHEN
        dbOperations.update(table, column, value, dataToUpdate);

        //THEN
        verify(statement).addBatch(argumentCaptor.capture());
    }

    @Test
    public void deleteTest() throws SQLException {
        final String table = "Person8";
        final String column = "column";
        final String value = "value";
        Data expectedData = new SqlTable(ImmutableList.of("id","column")
                , ImmutableList.of(ImmutableList.of("1", "value")));

        DbOperations dbOperations = spy(DbOperations.class);
        when(dbOperations.find(anyString())).thenReturn(expectedData);
        dbOperations.delete(table, column, value);

       /* when(statement.executeQuery(argumentCaptor.capture()));*/
        when(statement.executeUpdate(argumentCaptor.capture()));
        final String expectedSelectSql = "SELECT FROM Person8 WHERE column = value";
        final String expectedDeleteSql = "DELETE FROM Person8 WHERE column = value";
        assertEquals(expectedSelectSql, argumentCaptor.getAllValues().get(0));
        assertEquals(expectedDeleteSql, argumentCaptor.getAllValues().get(1));


       /* @Override
        public Data delete(String tableName, String column, String value) {
            Data selected = find(() -> format("SELECT FROM %s WHERE %s = %s", tableName, column, value));
            final String deleteQuery = format("DELETE FROM %s WHERE %s = %s", tableName, column, value);
            try (Statement statement = getConnect().createStatement()) {
                statement.executeUpdate(deleteQuery);
            } catch (SQLException e) {
                throw new MyDbException("Cannot delete from  DB",e);
            }
            return selected;

        }*/
    }

    public abstract DbOperations getDatabaseManager();


}
