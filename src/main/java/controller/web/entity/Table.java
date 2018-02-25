package controller.web.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Table {

    private String name;

    private int columnsAmount;

    private List<String> columns;

    private List<String> row;

    private String selectedColumn;

}
