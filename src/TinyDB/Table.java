package TinyDB;

import java.util.*;

public class Table {

    private String name;
    private Map<String, Pair<Column, Integer>> columns;
    Column primaryKey;
    private ArrayList<Column> columnList;
    private int lastColumnIndex;

    private String dbName;

    public Table(String name, String dbName) {
        this.name = name;
        this.columns = new LinkedHashMap<String, Pair<Column, Integer>>();
        this.columnList = new ArrayList<Column>();
        this.primaryKey = null;
        this.lastColumnIndex = 0;
        this.dbName = dbName;
    }

    public String getDBName() {
        return this.dbName;
    }

    public void setPrimaryKey(Column column) {
        this.primaryKey = column;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Column getPrimaryKey() {
        return primaryKey;
    }

    public String getName() {
        return name;
    }


    public ArrayList<Column> getColumnList() {
        return columnList;
    }

    public Column getColumn(String columnName) {
        return columns.get(columnName) != null ? columns.get(columnName).first : null;
    }

    public Map<String, Pair<Column, Integer>> getColumns() {
        return columns;
    }

    public void addColumn(Column column) {
        columns.put(column.getName(), new Pair(column, lastColumnIndex++));
        columnList.add(column);
    }

    public void removeColumn(String columnName) throws TinyDBException {
        Pair<Column, Integer> toRemove = columns.get(columnName);
        if (toRemove == null)
            throw new TinyDBException("column " + columnName + " does not exist");

        for (Pair<Column, Integer> x : columns.values())
            if (x.second > toRemove.second)
                --x.second;
        --lastColumnIndex;

        columns.remove(toRemove);
        columnList.remove(toRemove.first);
    }

    public void addRow(Map<String, Object> values) throws TinyDBException {
        TableManager.instance.writeRow(this, values);
    }

    public void updateRows(Map<String, Object> values, ArrayList<Criterion> criteria) throws TinyDBException {
        TableManager.instance.updateRows(this, values, criteria);
    }

    public List<Map<String, Object>> getRows(Set<String> selectColumns, ArrayList<Criterion> criteria) throws TinyDBException {
        return TableManager.instance.readRows(this, selectColumns, criteria);
    }

    public void deleteRows(ArrayList<Criterion> criteria) throws TinyDBException {
        TableManager.instance.deleteRows(this, criteria);
    }

}