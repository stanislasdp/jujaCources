package controller.web.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Table {

    private String name;

    @NotNull
    @Min(value = 1, message = "at least one column is needed")
    private Integer columnsAmount;

    private List<String> columns;

    private List<String> row;

    private String selectedColumn;

}
