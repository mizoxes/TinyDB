package TinyDB;

public class ColumnInteger extends Column {

    public ColumnInteger(String name) {
        super(name);
    }

    public ColumnType getType() {
        return ColumnType.INTEGER;
    }

    public Object getDefault() {
        return 0;
    }

    public int compare(String v1, Object v2) {
        int rs = Integer.compare((int) Double.parseDouble(v1), (int) v2);
        return rs;
    }

}
