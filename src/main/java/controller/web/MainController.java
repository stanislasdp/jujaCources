package controller.web;

import model.DbOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.Service;

import java.util.List;

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
        model.addAttribute("items", service.getCommandList());
        return "menu";
    }

    @RequestMapping(path = "/connect", method = RequestMethod.GET)
    public String connect(Model model) {
        model.addAttribute("connect", new Connect());
        return "connect";
    }

    @RequestMapping(path = "/connect", method = RequestMethod.POST)
    public String connecting(@ModelAttribute("connect") Connect connect, Model model) {
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
        model.addAttribute("table", new Table());
        model.addAttribute("toUrl", "createTable");
        return "getTableData";
    }

    @RequestMapping(path = "/dataToCreate", method = RequestMethod.POST)
    public String createTableWithColumnsNames(@ModelAttribute("table") Table table) {
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

    @RequestMapping(path = "/getTableByName", method = RequestMethod.GET)
    public String getTableByName(Model model, @ModelAttribute("db_operations") DbOperations dbOperations) {
        model.addAttribute("toUrl", "");
        return "getTable";
    }

    @RequestMapping(path = "/tableData/{table:.+}", method = RequestMethod.GET)
    public String getTableContent(Model model,
                                  @PathVariable("table") String table,
                                  @ModelAttribute("db_operations") DbOperations dbOperations) {
        List<List<String>> tableContent = service.getTable(table, dbOperations);
        model.addAttribute("tableContent", tableContent);
        return "tableData";
    }

    @RequestMapping(path = "/insertToTable/{table:.+}")
    public String getTableToInsert(Model model,
                                   @PathVariable("table") String tableToInsert,
                                   @ModelAttribute("db_operations") DbOperations dbOperations) {
        if (dbOperations == null) {
            return "redirect:/connect";
        }
        Table table = new Table();
        table.setName(tableToInsert);
        table.setColumns(service.getTable(tableToInsert, dbOperations).iterator().next());
        model.addAttribute("table", table);

        return "insertRow";
    }


}
