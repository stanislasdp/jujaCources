import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import configuration.TestConfiguration;
import model.Data;
import model.DbOperations;
import model.Row;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import service.ServiceImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TestConfiguration.class, loader = AnnotationConfigContextLoader.class)
public class ServiceTest {

    @Mock
    private static DbOperations dbOperations;

    @Autowired
    @InjectMocks
    private static ServiceImpl service;

    @Before
    public void init() {
        when(dbOperations.connect(any())).thenReturn(dbOperations);
    }

    @Test
    public void commandListTest() {
        assertThat(service.getCommandList(),
                hasItems("menu", "createTable", "getTables"));
    }

    @Test
    public void connectTest() {
        assertThat(service.connect("anydb", "anyUser", "anyPassword"),
                Matchers.any(DbOperations.class));
    }

    @Test
    public void createTableTest() {
        String tableName = "table";
        List<String> columns = ImmutableList.of("column1", "column2");
        willDoNothing().given(dbOperations).create(eq(tableName), eq(columns));
        service.createTable(tableName, columns, dbOperations);
    }

    @Test
    public void getTableTest() {
        final String tableName = "table";
        final List<String> columns = ImmutableList.of("name1", "name2");
        final List<String> names = ImmutableList.of("column1", "column2");
        given(dbOperations.find(tableName)).willReturn(getStubData(tableName, names, columns));
        assertThat(service.getTable(tableName, dbOperations), hasItems(columns, names));
    }


    @Test
    public void getAllTablesTest() {
        List<String> tables = ImmutableList.of("table1", "table2");
        given(dbOperations.getTables()).willReturn(tables);

        assertThat(service.getAllTables(dbOperations), is(tables));
    }

    @Test
    public void insertRowTest() {
        final String tableName = "table";
        service.insertRow(tableName, ImmutableMap.of(), dbOperations);

        then(dbOperations).should(times(1)).insert(eq(tableName),
                isA(Data.class));
    }

    @Test
    public void updateRowsTest() {
        final String tableName = "table";
        final String column = "column";
        final String value = "value";

        service.updateRows(tableName, Pair.of(column, value), ImmutableMap.of(), dbOperations);

        then(dbOperations).should(times(1))
                .update(eq(tableName), eq(column), eq(value), isA(Data.class));
    }


    @Test
    public void deleteRowsTest() {
        final String tableName = "table";
        final String column = "column";
        final String value = "value";

        service.deleteRows(tableName, column, value, dbOperations);

        then(dbOperations).should(times(1)).delete(eq(tableName),
                eq(column), eq(value));
    }

    @Test
    public void dropTableTest() {
        final String tableName = "table";

        service.dropTable(tableName, dbOperations);

        then(dbOperations).should(times(1)).dropTable(eq(tableName));
    }


    private Data getStubData(String tableName, List<String> columns, List<String> names) {
        return new Data() {
            @Override
            public Collection<Row> getValues() {
                return ImmutableList.of(new Row(this, columns));
            }

            @Override
            public Collection<String> getNames() {
                return names;
            }
        };
    }


}
