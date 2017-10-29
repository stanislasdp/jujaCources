

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
import org.powermock.modules.junit4.PowerMockRunner;


import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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

    @BeforeClass
    public static void prepare() throws SQLException {

        dbOperations = new PostrgreDbOPerations();
    }

    @Before
    public void test() throws SQLException {
        statement = mock(Statement.class);
        when(statement.getConnection()).thenReturn(connection);
        when(connection.createStatement()).thenReturn(statement);
    }


    @Test
    public void getTablesTest() throws SQLException {
        final String tableFirst = "Person1";
        final String tableSecond = "Person2";
        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(resultSet.getString(1)).thenReturn(tableFirst).thenReturn(tableSecond);
        when(statement.executeQuery("SELECT table_schema || '.' || table_name FROM information_schema.tables " +
                "WHERE table_type = 'BASE TABLE' " +
                "AND table_schema NOT IN ('pg_catalog', 'information_schema')")).thenReturn(resultSet);
        List<String> tables = dbOperations.getTables();
        assertThat("list ", tables, containsInAnyOrder(tableFirst, tableSecond));
    }

    @Test
    public void clearTableTestWhenDataExists() throws SQLException {
        final String tableToDelete = "Person3";
        final List<Object>[] arguments = (ArrayList<Object>[]) new ArrayList<?>[1];
        arguments[0] = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            arguments[0].add(Arrays.asList(invocationOnMock.getArguments()));
            return true;
        }).when(statement).execute(anyString());
        final String expectedSql = String.format("[DELETE FROM %s]", tableToDelete);
        dbOperations.clearTable(tableToDelete);
        assertEquals(expectedSql, arguments[0].get(0).toString());

    }

    @Test(expected = MyDbException.class)
    public void clearTableTestWhenNoDataExists() throws SQLException {
        final String tableToDelete = "Person3";
        when(statement.execute("DELETE FROM " + tableToDelete)).thenReturn(false);
        dbOperations.clearTable(tableToDelete);
    }


    @Test
    public void dropTableWhenTableExistsTest() throws SQLException {
        final String tableToDrop = "Person3";
        final List<Object>[] arguments = (ArrayList<Object>[]) new ArrayList<?>[1];
        arguments[0] = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            arguments[0].add(Arrays.asList(invocationOnMock.getArguments()));
            return true;
        }).when(statement).execute(anyString());
        dbOperations.dropTable(tableToDrop);
        final String expectedSql = String.format("[DROP TABLE %s]", tableToDrop);
        assertEquals(expectedSql, arguments[0].get(0).toString());
    }

    @Test(expected = MyDbException.class)
    public void dropTableWhenTableNotExistsTest() throws SQLException {
        final String tableToDrop = "Person3";
        when(statement.execute(anyString())).thenReturn(false);
        dbOperations.clearTable(tableToDrop);
    }


    @Test
    public void createTableTest() throws SQLException {
        final String tableToCreate = "Person4";
        List<String> tableColumns = new ArrayList<String>() {{
            add("id");
            add("firstname");
            add("lastname");
            add("age");
        }};

        final List<Object>[] arguments = (ArrayList<Object>[]) new ArrayList<?>[1];
        arguments[0] = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            arguments[0].add(Arrays.asList(invocationOnMock.getArguments()));
            return null;
        }).when(statement).executeUpdate(anyString());
        dbOperations.create(tableToCreate, tableColumns);
        String expectedSql = "[CREATE TABLE Person4 ( id text,firstname text,lastname text,age text)]";
        assertEquals(expectedSql, arguments[0].get(0).toString());
    }

    @Test
    public void findTableTest() throws SQLException {
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
        when(statement.executeQuery("SELECT * FROM " + tableToFind)).thenReturn(resultSet);
        Data data = dbOperations.find(tableToFind);
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
        final List<Object>[] arguments = (ArrayList<Object>[]) new ArrayList<?>[2];
        arguments[0] = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            arguments[0].add(Arrays.asList(invocationOnMock.getArguments()));
            return null;
        }).when(statement).addBatch(anyString());

        //WHEN
        dbOperations.insert(tableToInsert, dataToInsert);

        //THEN
        String expectedFirstInsert = "[INSERT INTO Person6(id,firstName,lastName,id) " +
                "VALUES ( 'testId1','testFirstName1','testLastName1','testAge1' )]";
        String expectedSecondInsert = "[INSERT INTO Person6(id,firstName,lastName,id) " +
                "VALUES ( 'testId2','testFirstName2','testLastName2','testAge2' )]";
        assertEquals(expectedFirstInsert, arguments[0].get(0).toString());
        assertEquals(expectedSecondInsert, arguments[0].get(1).toString());
    }


    @Test
    public void updateRowTest() throws SQLException {

        final String table = "Person7";
        final String column = "id";
        final String value = "7";
        Data dataToUpdate = new SqlTable(ImmutableList.of("id", "firstName", "lastName", "id"),
                ImmutableList.of(ImmutableList.of("testId1", "testFirstName1", "testLastName1", "testAge1")));;
        final List<Object>[] arguments = (ArrayList<Object>[]) new ArrayList<?>[2];
        arguments[0] = new ArrayList<>();
        doAnswer(invocationOnMock -> {
            arguments[0].add(Arrays.asList(invocationOnMock.getArguments()));
            return null;
        }).when(statement).addBatch(anyString());
        dbOperations.update(table, column, value ,dataToUpdate);
        String expectedSql = "[UPDATE Person7 SET id='testId1',firstName='testFirstName1'," +
                "lastName='testLastName1',id='testAge1' WHERE id = '7']";
        assertEquals(expectedSql, arguments[0].get(0).toString());
    }

    public abstract DbOperations getDatabaseManager();


}
