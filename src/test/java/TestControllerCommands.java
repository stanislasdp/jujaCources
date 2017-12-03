import com.google.common.collect.ImmutableList;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import controller.commands.*;
import controller.exceptions.ControllerException;
import model.Data;
import model.DbOperations;
import model.SqlTable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.modules.junit4.PowerMockRunner;
import view.view.View;
import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by stas on 11/5/17.
 */
@RunWith(PowerMockRunner.class)
public class TestControllerCommands {

    private DbOperations dbOperations;
    private View view;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void prepareArgumentCaptor() {
        dbOperations = mock(DbOperations.class);
        view = mock(View.class);
        doNothing().when(view).write(anyString());
    }


    @Test
    public void connectCommandTest() {
        //GIVEN
        Command<String> command = new ConnectCommand(dbOperations, view);
        ArgumentCaptor<Properties> captorProp = ArgumentCaptor.forClass(Properties.class);
        doNothing().when(dbOperations).connect(any());

        //WHEN
        command.execute(ImmutableList.of(
                "Person", "Stas", "Password"));

        //THEN
        verify(dbOperations).connect(captorProp.capture());
        assertEquals(new Properties() {{
            setProperty("database", "Person");
            setProperty("user", "Stas");
            setProperty("password", "Password");
        }}, captorProp.getValue());

        verify(view).write(eq("Successful connect to db"));
    }

    @Test
    public void invalidArgCountConnectTest() {
        //GIVEN
        Command<String> command = new ConnectCommand(dbOperations, view);
        expectedEx.expect(ControllerException.class);
        expectedEx.expectMessage("Should be three parameters(database, login, password)");

        //WHEN
        command.execute(ImmutableList.of("Person", "Stas"));
    }


    @Test
    public void createTableCommandTest() {
        //GIVEN
        Command<String> command = new CreateTableCommand(dbOperations, view);
        List<String> parameters = ImmutableList.of("Person1", "firstColumn", "firstValue",
                "secondColumn", "secondValue");
        //WHEN
        command.execute(parameters);

        //THEN
        verify(dbOperations).create(eq("Person1"), eq(parameters.subList(1, parameters.size())));
        verify(view).write(eq("table Person1 has been created"));
    }

    @Test
    public void invalidArgCountCreateCommandTest() {
        //GIVEN
        Command<String> command = new CreateTableCommand(dbOperations, view);
        expectedEx.expect(ControllerException.class);
        expectedEx.expectMessage("Create table command needs at least onr column for table");

        //WHEN
        command.execute(ImmutableList.of("Person"));
    }


    @Test
    public void getTablesCommandWhenPresentTest() {
        //GIVEN
        Command<String> command = new GetTablesCommand(dbOperations, view);
        when(dbOperations.getTables()).thenReturn(ImmutableList.of("Person2",  "Person3"));
        //WHEN
        command.execute(emptyList());
        //THEN
        verify(view).write(eq("Person2, Person3"));
    }

    @Test
    public void getTablesCommandWhenNotPresent() {
        //GIVEN
        Command<String> command = new GetTablesCommand(dbOperations, view);
        when(dbOperations.getTables()).thenReturn(emptyList());

        //WHEN
        command.execute(emptyList());

        //THEN
        verify(view).write(eq("No tables present in selected DB"));
    }


    @Test
    public void getTableColumnsCommandTest() {
        //GIVEN
        Command<String> command = new GetTableColumnsCommand(dbOperations, view);
        Data data = new SqlTable(ImmutableList.of("id", "value"),
                ImmutableList.of(ImmutableList.of("1", "value1"), ImmutableList.of("2", "value2")));
        when(dbOperations.find(anyString())).thenReturn(data);

        //WHEN
        command.execute(ImmutableList.of("Person4"));

        //THEN
        verify(dbOperations).find(eq("Person4"));
        verify(view).write((eq("id value")));
        verify(view).write((eq("1 value1")));
        verify(view).write((eq("2 value2")));
    }

    @Test
    public void getTableColumnsCommandTestWhenEmpty() {
        //GIVEN
        Command<String> command = new GetTableColumnsCommand(dbOperations, view);
        Data data = new SqlTable(emptyList(), ImmutableList.of());
        when(dbOperations.find(anyString())).thenReturn(data);

        //WHEN
        command.execute(ImmutableList.of("Person4"));

        //THEN
        verify(view).write(eq("This table is empty"));
    }

    @Test
    public void invalidArgCountGetTableColTest() {
        //GIVEN
        Command<String> command = new GetTableColumnsCommand(dbOperations, view);
        expectedEx.expect(ControllerException.class);
        expectedEx.expectMessage("Incorrect parameters size, should be only tableName");

        //WHEN
        command.execute(ImmutableList.of());
    }

    @Test
    public void clearTableCommandTest() {
        //GIVEN
        Command<String> command = new ClearTableCommand(dbOperations, view);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);

        //WHEN
        command.execute(ImmutableList.of("Person6"));

        //THEN
        verify(dbOperations).clearTable(eq("Person6"));
        verify(view).write(eq("Table Person6 is cleared"));
    }

    @Test
    public void dropTableCommandTest() {
        Command<String> command = new DropTableCommand(dbOperations, view);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);

        //WHEN
        command.execute(ImmutableList.of("Person7"));

        verify(dbOperations).dropTable(captorString.capture());
        verify(view).write(captorString.capture());

        //THEN
        assertEquals("Person7", captorString.getAllValues().get(0));
        assertEquals("Table Person7 is dropped", captorString.getAllValues().get(1));
    }


    @Test
    public void invAgrCountDropTableTest() {
        //GIVEN
        expectedEx.expect(ControllerException.class);
        expectedEx.expectMessage("Should be one parameter tableName");
        Command<String> command = new DropTableCommand(dbOperations, view);

        //WHEN
        command.execute(ImmutableList.of());
    }

    @Test
    public void insertRowCommandTest() {
        //GIVEN
        Command<String> command = new InsertCommand(dbOperations, view);

        //WHEN
        command.execute(ImmutableList.of("Person5", "id", "6", "firstname", "stas", "secondname", "kiryan"));

        //THEN
        verify(dbOperations).insert(eq("Person5"),
                eq(new SqlTable(ImmutableList.of("id", "firstname", "secondname"),
                        ImmutableList.of(ImmutableList.of("6", "stas", "kiryan")))));
        verify(view).write(eq("new row is inserted to Person5"));
    }

    @Test
    public void updateCommandTest() {
        //GIVEN
        Command<String> command = new UpdateCommand(dbOperations, view);

        //WHEN
        command.execute(ImmutableList.of("Person6", "column1", "value1", "column2", "value2", "column3", "value3"));

        //THEN
        verify(dbOperations).update(eq("Person6"), eq("column1"), eq("value1"),
                eq(new SqlTable(ImmutableList.of("column2", "column3")
                        , ImmutableList.of(ImmutableList.of("value2", "value3")))));
        verify(view).write(eq("column1 column2 column3"));
        verify(view).write(eq("value1 value2 value3"));
    }

    @Test
    public void deleteCommandTest() {
        //GIVEN
        Data data = new SqlTable(ImmutableList.of("id", "value"),
                ImmutableList.of(ImmutableList.of("1", "value1"), ImmutableList.of("2", "value2")));
        when(dbOperations.delete("Person8", "column", "value")).thenReturn(data);
        Command<String> command = new DeleteRowsCommand(dbOperations, view);

        //WHEN
        command.execute(ImmutableList.of("Person8", "column", "value"));

        //THEN
        verify(view).write((eq("id value")));
        verify(view).write((eq("1 value1")));
        verify(view).write((eq("2 value2")));

    }


    @Test
    public void exitCommandTest() {
        //GIVEN
        Command<String> command = new ExitCommand(dbOperations, view);

        //WHEN
        command.execute(emptyList());

        //THEN
        view.write("Connection to db has been closed");
    }


    @Test
    public void helpCommandTest() {
        //GIVEN
        Command<String> command = new HelpCommand(view);

        //WHEN
        command.execute(emptyList());

        //THEN;
        verify(view).write("Available commands:");
        verify(view).write("connect | database | username | password - connect to DB");
        verify(view).write("tables - retrieve all table names from DB");
        verify(view).write("clear | tablename - clear all data from <tablename>");
        verify(view).write("drop | tableName - drop <tablename> from DB");
        verify(view).write("create | tableName | column1 | column2 | ... | columnN - create table <tableName> with columns 1 -N");
        verify(view).write("find | tableName - retreive all all columns and rows data from <tableName>");
        verify(view).write("insert | tableName | column1 | value1 | column2 | value2 | ... | columnN |" +
                "insert rows with specified columns 1 -N to <tablename>");
        verify(view).write("update | tableName | column1 | value1 | column2 | value2 |columnN |  valueN"
                + "update row that has column1 and value1 with column2=value2...columnN=valueN in <tableName>");
        verify(view).write("delete | tableName | column | value - delete rows from <tableName> which correspond to columnn=value from <tableName>");
        verify(view).write("exit - disconnects from DB");
    }

}
