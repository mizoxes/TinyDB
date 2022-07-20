package TinyDB;

public class ColumnBool extends Column {

    public ColumnBool(String name) {
        super(name);
    }

    public ColumnType getType() {
        return ColumnType.BOOL;
    }

    public Object getDefault() {
        return false;
    }

    public int compare(String v1, Object v2) {
        boolean b1 = Boolean.parseBoolean(v1);
        boolean b2 = (boolean) v2;
        return b1 == b2 ? 0 : 1;
    }

}