package TinyDB;

public class ColumnVarchar extends Column {
    private int length;

    public ColumnVarchar(String name, int length) {
        super(name);
        this.length = length;
    }

    public ColumnType getType() {
        return ColumnType.VARCHAR;
    }

    public Object getDefault() {
        return new byte[length];
    }

    public int getLength() {
        return length;
    }

    public int compare(String v1, Object v2) {
        String s1 = v1;
        String s2 = (String) v2;
        System.out.println("comparing " + s1 + " and " + s2);
        return s1.compareTo(s2);
    }

}