import com.google.common.collect.ImmutableList;
import controller.commands.*;
import model.Data;
import model.DbOperations;
import model.SqlTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import view.view.View;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import static java.util.stream.Collectors.joining;
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
    private  View view;

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
        command.execute(new ArrayList<String>() {{
            add("Person");
            add("Stas");
            add("Password");
        }});

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
    public void createTableCommandTest() {
        Command<String> command = new CreateTableCommand(dbOperations, view);
        List<String> parameters = new ArrayList<String>() {{
            add("Person1");
            add("firstColumn");
            add("firstValue");
            add("secondColumn");
            add("secondValue");
        }};
        command.execute(parameters);
        verify(dbOperations).create(eq("Person1"), eq(parameters.subList(1, parameters.size())));
        verify(view).write(eq("table Person1 has been created"));
    }

    @Test
    public void getTablesCommandTest() {
        //GIVEN
        Command<String> command = new GetTablesCommand(dbOperations, view);
        when(dbOperations.getTables()).thenReturn(new ArrayList<String>(){{
            add("Person2");
            add("Person3");
        }});
        //WHEN
        command.execute(new ArrayList<>());
        //THEN
        verify(view).write(eq("Person2, Person3"));
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
    public void insertRowCommandTest() {
        //GIVEN
        Command<String> command = new InsertCommand(dbOperations, view);

        //WHEN
        command.execute(ImmutableList.of("Person5", "id","6", "firstname", "stas", "secondname" ,"kiryan"));

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
    public void exitCommandTest() {
        //GIVEN
        Command<String> command = new ExitCommand(dbOperations ,view);

        //WHEN
        command.execute(Collections.emptyList());

        //THEN
        view.write("Connection to db has been closed");
    }

}
