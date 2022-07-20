package TinyDB;

public class ColumnReal extends Column {

    public ColumnReal(String name) {
        super(name);
    }

    public ColumnType getType() {
        return ColumnType.REAL;
    }

    public int getLength() {
        return 4;
    }

    public Object getDefault() {
        return 0;
    }

    public int compare(String v1, Object v2) {
        return Double.compare(Double.parseDouble(v1), (double) v2);
    }

}
