package controller.web;

import controller.web.entity.Connect;
import controller.web.entity.Table;
import model.DbOperations;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import service.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

@Controller
@SessionAttributes("db_operations")
public class MainController {

    private Service service;

    @Autowired
    public void setService(Service service) {
        this.service = service;
    }

    @RequestMapping(path = "/menu", method = RequestMethod.GET)
    public String menu(Model model) {
        if (!model.asMap().containsKey("db_operations")) {
            return "redirect:/connect";
        }
        model.addAttribute("items", service.getCommandList());
        return "menu";
    }

    @RequestMapping(path = "/connect", method = RequestMethod.GET)
    public String connect(Model model) {
        model.addAttribute("connect", new Connect());
        return "connect";
    }

    @RequestMapping(path = "/connect", method = RequestMethod.POST)
    public String connecting(@Valid @ModelAttribute("connect") Connect connect, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "connect";
        }
        DbOperations dbOperations = service.connect(connect.getDatabase(),
            connect.getUser(), connect.getPassword());
        model.addAttribute("db_operations", dbOperations);
        return "redirect:/menu";
    }

    @RequestMapping(path = "/createTable", method = RequestMethod.GET)
    public String getTableToCreate(Model model, @ModelAttribute("db_operations") DbOperations dbOperations) {
        if (dbOperations == null) {
            return "redirect:/connect";
        }
        model.addAttribute("table", Table.builder().build());
        model.addAttribute("toUrl", "dataToCreate");
        return "getTableData";
    }

    @RequestMapping(path = "/dataToCreate", method = RequestMethod.POST)
    public String createTableWithColumnsNames(@Valid @ModelAttribute("table") Table table, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "getTableData";
        }
        return "createTable";
    }

    @RequestMapping(path = "/createTable", method = RequestMethod.POST)
    public String createTable(@ModelAttribute("table") Table table,
                              @ModelAttribute("db_operations") DbOperations dbOperations) {
        service.createTable(table.getName(), table.getColumns(), dbOperations);
        return "redirect:/menu";
    }

    @RequestMapping(path = "/getTables", method = RequestMethod.GET)
    public String getTables(Model model, @ModelAttribute("db_operations") DbOperations dbOperations) {
        service.getAllTables(dbOperations);
        model.addAttribute("tables", service.getAllTables(dbOperations));
        return "tablesData";
    }

    @RequestMapping(path = "/tableData/{table:.+}", method = RequestMethod.GET)
    public String getTableContent(Model model,
                                  @PathVariable("table") String table,
                                  @ModelAttribute("db_operations") DbOperations dbOperations) {
        List<List<String>> tableContent = service.getTable(table, dbOperations);
        model.addAttribute("tableContent", tableContent);
        return "tableData";
    }

    @RequestMapping(path = "/insertToTable/{tableName:.+}", method = RequestMethod.GET)
    public String insertToTable(Model model,
                                @PathVariable("tableName") String tableToInsert,
                                @ModelAttribute("db_operations") DbOperations dbOperations) {
        if (dbOperations == null) {
            return "redirect:/connect";
        }
        Table table = Table.builder()
            .name(tableToInsert)
            .columns(service.getTable(tableToInsert, dbOperations).iterator().next())
            .build();
        model.addAttribute(table);
        return "insertRow";
    }

    @RequestMapping(path = "/insertRowToTable/{tableName:.+}", method = RequestMethod.POST)
    public String insertToTable(@PathVariable("tableName") String tableName,
                                @ModelAttribute("table") Table table,
                                @ModelAttribute("db_operations") DbOperations dbOperations) {
        if (dbOperations == null) {
            return "redirect:connect";
        }
        Map<String, String> insertData = range(0, table.getColumns().size())
            .boxed()
            .collect(toMap(i -> table.getColumns().get(i), i -> table.getRow().get(i)));
        service.insertRow(tableName, insertData, dbOperations);
        return "redirect:/menu";
    }

    @RequestMapping(path = "/updateTable/{tableName:.+}", method = RequestMethod.GET)
    public String getTableToUpdate(Model model, @PathVariable("tableName") String tableToUpdate,
                                   @ModelAttribute("db_operations") DbOperations dbOperations) {
        if (dbOperations == null) {
            return "redirect:/connect";
        }
        Table table = Table.builder()
            .name(tableToUpdate)
            .columns(service.getTable(tableToUpdate, dbOperations).iterator().next())
            .build();
        model.addAttribute("table", table);
        return "updateColumns";
    }

    @RequestMapping(path = "/updateRowInTable/{tableName:.+}", method = RequestMethod.POST)
    public String updateTable(@PathVariable("tableName") String tableName,
                              @ModelAttribute("table") Table table,
                              @ModelAttribute("db_operations") DbOperations dbOperations) {

        if (dbOperations == null) {
            return "redirect:connect";
        }
        String selectedColumn = table.getSelectedColumn();
        int updateColumnIndex = table.getColumns().indexOf(selectedColumn);
        Map<String, String> updateData = range(0, table.getColumns().size())
            .boxed()
            .collect(toMap(i -> table.getColumns().get(i), i -> table.getRow().get(i)));
        service.updateRows(tableName, Pair.of(selectedColumn,
            table.getRow().get(updateColumnIndex)), updateData, dbOperations);
        return "redirect:/menu";
    }

    @RequestMapping(path = "/deleteFromTable/{tableName:.+}", method = RequestMethod.GET)
    public String getTableToDeleteFrom(Model model, @PathVariable("tableName") String tableToDelete,
                                       @ModelAttribute("db_operations") DbOperations dbOperations) {

        if (dbOperations == null) {
            return "redirect:/connect";
        }
        Table table = Table.builder()
            .name(tableToDelete)
            .columns(service.getTable(tableToDelete, dbOperations).iterator().next())
            .build();
        model.addAttribute(table);
        return "deleteColumns";
    }

    @RequestMapping(path = "deleteRowsFromTable/{tableName:.+}", method = RequestMethod.POST)
    public String deleteRowsFromTable(@PathVariable("tableName") String tableName,
                                      @ModelAttribute("table") Table table,
                                      @ModelAttribute("db_operations") DbOperations dbOperations) {
        if (dbOperations == null) {
            return "redirect:connect";
        }

        String selectedColumn = table.getSelectedColumn();
        int columnIndex = table.getColumns().indexOf(selectedColumn);
        service.deleteRows(tableName, selectedColumn, table.getRow().get(columnIndex), dbOperations);
        return "redirect:/menu";
    }

    @RequestMapping(path = "dropTable/{tableName:.+}", method = RequestMethod.GET)
    public String dropTable(@PathVariable("tableName") String tableName,
                            @ModelAttribute("db_operations") DbOperations dbOperations) {
        if (dbOperations == null) {
            return "redirect:/connect";
        }
        service.dropTable(tableName, dbOperations);
        return "redirect:/menu";
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        /*dela with the case when only space is entered to text fields > null*/
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

}
