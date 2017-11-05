import com.google.common.collect.ImmutableList;
import controller.commands.*;
import model.Data;
import model.DbOperations;
import model.SqlTable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.modules.junit4.PowerMockRunner;
import view.view.View;
import java.util.ArrayList;
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
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);
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

        verify(view).write(captorString.capture());
        assertEquals("Successful connect to db", captorString.getValue());
    }


    @Test
    public void createTableCommandTest() {
        Command<String> command = new CreateTableCommand(dbOperations, view);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> captorList = ArgumentCaptor.forClass(List.class);
        List<String> parameters = new ArrayList<String>() {{
            add("Person1");
            add("firstColumn");
            add("firstValue");
            add("secondColumn");
            add("secondValue");
        }};

        command.execute(parameters);
        verify(dbOperations).create(captorString.capture(), captorList.capture());
        verify(view).write(captorString.capture());
        assertEquals("Person1", captorString.getAllValues().get(0));
        assertEquals(parameters.subList(1, parameters.size()), captorList.getValue());
        assertEquals("table Person1 has been created", captorString.getAllValues().get(1));
    }

    @Test
    public void getTablesCommandTest() {
        //GIVEN
        Command<String> command = new GetTablesCommand(dbOperations, view);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);
        when(dbOperations.getTables()).thenReturn(new ArrayList<String>(){{
            add("Person2");
            add("Person3");
        }});
        //WHEN
        command.execute(new ArrayList<>());
        //THEN
        verify(view).write(captorString.capture());
        assertEquals("Person2, Person3", captorString.getValue());
    }

    @Test
    public void getTableColumnsCommandTest() {
        //GIVEN
        Command<String> command = new GetTableColumnsCommand(dbOperations, view);
        ArgumentCaptor<String> captorStringDb = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> captorStringView = ArgumentCaptor.forClass(String.class);
        Data data = new SqlTable(ImmutableList.of("id", "value"),
                ImmutableList.of(ImmutableList.of("1", "value1"), ImmutableList.of("2", "value2")));
        when(dbOperations.find(anyString())).thenReturn(data);

        //WHEN
        command.execute(ImmutableList.of("Person4"));
        verify(dbOperations).find(captorStringDb.capture());
        verify(view, times(3)).write(captorStringView.capture());

        assertEquals("Person4", captorStringDb.getValue());
        assertEquals("id value\n" + "1 value1\n" +"2 value2",
                captorStringView.getAllValues().stream().collect(joining("\n")));
    }

    @Test
    public void clearTableCommandTest() {
        //GIVEN
        Command<String> command = new ClearTableCommand(dbOperations, view);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);

        //WHEN
        command.execute(ImmutableList.of("Person6"));

        verify(dbOperations).clearTable(captorString.capture());
        verify(view).write(captorString.capture());

        //THEN
        assertEquals("Person6", captorString.getAllValues().get(0));
        assertEquals("Table Person6 is cleared", captorString.getAllValues().get(1));
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
        Command command = new InsertCommand(dbOperations, view);
        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Data> captorData = ArgumentCaptor.forClass(Data.class);

        Data expectedData = new SqlTable(ImmutableList.of("id", "firstname", "secondname"),
                ImmutableList.of(ImmutableList.of("6", "stas", "kiryan")));

        //WHEN
        command.execute(ImmutableList.of("Person5", "id","6", "firstname", "stas", "secondname" ,"kiryan"));

        //THEN
        verify(dbOperations).insert(captorString.capture(), captorData.capture());
        verify(view).write(captorString.capture());
        assertEquals("Person5", captorString.getAllValues().get(0));
        assertEquals(expectedData, captorData.getValue());
        assertEquals("new row is inserted to Person5", captorString.getAllValues().get(1));
    }

}
