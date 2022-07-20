package TinyDB;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private String name;
    private Map<String, Table> tables = new HashMap<String, Table>();

    public Database(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void addTable(Table table) {
        this.tables.put(table.getName(), table);
    }

    public Table getTable(String name) {
        return this.tables.get(name);
    }

    public Collection<Table> getTables() {
        return this.tables.values();
    }

}
