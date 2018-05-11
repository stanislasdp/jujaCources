package database.model;

import java.util.List;
import java.util.Objects;

/**
 * Created by stas on 10/19/17.
 */
public class Row {

    private Data table;
    private List<String> data;

    public Row(Data table, List<String> data) {
        this.table = table;
        this.data = data;
    }

    public String getValueInColumn(int columnIndex) {
        return data.get(columnIndex);
    }

    public List<String> getValuesInAllColumns() {
        return data;
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
