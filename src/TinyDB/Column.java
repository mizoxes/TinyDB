package TinyDB;

public abstract class Column {

    private String name;

    public Column(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract ColumnType getType();

    public abstract Object getDefault();

    public abstract int compare(String v1, Object v2);

}