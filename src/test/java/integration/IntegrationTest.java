package integration;

import model.utils.DbUtils;
import org.junit.*;
import java.io.*;
import java.util.Properties;

import static main.Main.main;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

public class IntegrationTest {

    private static final String DEFAULT_PROP_RESOURCE = "postGreConnection.properties";
    private static Properties dbProperties = new Properties();

    private static ConfigurableInputStream configurableInputStream;
    private static ByteArrayOutputStream byteArrayOutputStream;

    @BeforeClass
    public static void initProperties() throws IOException {
        dbProperties.load(DbUtils.getResourceAsInputStream(DEFAULT_PROP_RESOURCE));
    }

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
                .add(String.format("connect|%s|%s|%s",
                        dbProperties.getProperty("database"),
                        dbProperties.getProperty("user"),
                        dbProperties.getProperty("password")))
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
                .add(String.format("connect|NotExists|%s|%s",
                        dbProperties.getProperty("user"),
                        dbProperties.getProperty("password")))
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
                .add(String.format("connect | %s | %s | %s",
                        dbProperties.getProperty("database"),
                        dbProperties.getProperty("user"),
                        dbProperties.getProperty("password")))
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
