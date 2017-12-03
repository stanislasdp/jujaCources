package integration;

import com.google.common.collect.ImmutableList;
import main.Main;
import model.DbOperations;
import model.PostrgreDbOPerations;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;

import static main.Main.main;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private static ConfigurableInputStream configurableInputStream;
    private static ByteArrayOutputStream byteArrayOutputStream;

    @BeforeClass
    public static void initStreams() {
        configurableInputStream = new ConfigurableInputStream();
        byteArrayOutputStream = new ByteArrayOutputStream();
        System.setIn(configurableInputStream);
        System.setOut(new PrintStream(byteArrayOutputStream));
    }


    @Before
    public void resetStreams() {
        configurableInputStream.reset();
        byteArrayOutputStream.reset();
    }

    @Test
    public void connectAndExitTest() {
        configurableInputStream
                .add("connect|Testing|java|111111")
                .add("exit\n");
        start();
        assertThat(getOutPutStreamAsString(), allOf(
                containsString("Enter the command or type help"),
                containsString("Successful connect to db")
                , containsString("Connection to db has been closed, now exit")));
    }

    @Test
    public void invalidConnectionTest() {
        configurableInputStream
                .add("connect|NotExists|java|111111")
                .add("exit\n");
        start();
        assertThat(getOutPutStreamAsString(), allOf(
                containsString("Error has been occurred: Problems with Connection")
                , containsString("Please try again one more time")
                , containsString("database \"NotExists\" does not exist")));
    }


    @Test
    public void createCommandBeforeConnectTest() {
        configurableInputStream
                .add("create | tableName | column1 | column2")
                .add("exit\n");
        start();
        assertThat(getOutPutStreamAsString(), allOf(
                containsString("Connection to DB must be initialized first")
                , containsString("Enter the command or type help")));
    }


    @Test
    public void tableWorkflowTest() {
        configurableInputStream
                .add("connect | Testing | java |111111")
                .add("create | Person5| id | fname | lname| age")
                .add("tables")
                .add("insert | Person5 | id | 1 |fname | testFname | lname | testLname | age |18")
                .add("find | Person5")
                .add("update | Person5 | id | 1 | fname | changedF | lname | changedL | age | 19")
                .add("insert | Person5 | id | 2 | fname | testF2  | lname | testL2 | age | 19")
                .add("find | Person5")
                .add("delete | Person5 | age | 19")
                .add("clear | Person5")
                .add("drop | Person5")
                .add("exit\n");
        start();
        assertThat(getOutPutStreamAsString(), allOf(
                containsString("Successful connect to db")
                , containsString("table Person5 has been created")
                , containsString("public.person5")
                , containsString("new row is inserted to Person5")
                , containsString("id fname lname age\n1 testFname testLname 18")
                , containsString("id fname lname age\n1 changedF changedL 19")
                , containsString("id fname lname age\n1 changedF changedL 19" +
                        "\n2 testF2 testL2 19")
                , containsString("Table Person5 is cleared")
                , containsString("Table Person5 is dropped")
                , containsString("Connection to db has been closed, now exit")
        ));
    }


    private void start() {
        main(null);
    }

    private String getOutPutStreamAsString() {
        try {
            return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
