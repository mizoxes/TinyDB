package TinyDB;

public class ColumnDate extends Column {

    public ColumnDate(String name) {
        super(name);
    }

    public ColumnType getType() {
        return ColumnType.DATE;
    }

    public Object getDefault() {
        return "00/00/0000";
    }

    public int compare(String v1, Object v2) {
        String s1 = v1;
        String s2 = (String) v2;
        return s1.compareTo(s2);
    }

}
