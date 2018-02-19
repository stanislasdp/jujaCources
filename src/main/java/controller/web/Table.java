package controller.web;

import lombok.Data;

import java.util.List;

@Data
public class Table {

    private String name;

    private int columnsAmount;

    private List<String> columns;

    private List<String> row;

}
