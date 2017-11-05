package model;

import java.util.List;
import java.util.Objects;

/**
 * Created by stas on 10/19/17.
 */
public class Row {

    private SqlTable table;
    private List<String> data;

    public Row(SqlTable table, List<String> data) {
        this.table = table;
        this.data = data;
    }

    public int getRowNumber() {
          return table.getValues().indexOf(this);
    }

    public String getValueInColumn(int columnIndex) {
        return data.get(columnIndex);
    }

    public List<String> getValuesInAllColumns() {
        return data;
    }

    public List<String> getColumnsNames() {
        return table.getNames();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Row row = (Row) o;
        return Objects.equals(data, row.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, data);
    }
}
