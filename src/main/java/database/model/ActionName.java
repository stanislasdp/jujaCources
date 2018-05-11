package database.model;

public enum ActionName {
    CREATE("create"), GET("get"), GETALL("getAll"), INSERT("insert"), UPDATE("update"), DELETE("delete"), DROP("drop");

    private String name;

    ActionName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
