package database.model;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Created by stas on 10/19/17.
 */
public class SqlTable implements Data {

    private List<String> columnNames;
    private List<Row> rows;

    public SqlTable(List<String> columnNames, List<List<String>> tableRows) {
        this.columnNames = columnNames;
        rows = tableRows.stream().map(row -> new Row(this, row)).collect(toList());
    }

    @Override
    public List<Row> getValues() {
        return rows;
    }

    public int getRowsCount() {
        return rows.size();
    }

    @Override
    public List<String> getNames() {
        return columnNames;
    }

    public List<String> getColumnValues(String columnName) {
        int index = columnNames.indexOf(columnName);
        return rows.stream().map(row -> row.getValueInColumn(index)).collect(toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlTable sqlTable = (SqlTable) o;
        return Objects.equals(columnNames, sqlTable.columnNames) &&
                Objects.equals(rows, sqlTable.rows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnNames, rows);
    }
}
