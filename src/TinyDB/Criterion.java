package TinyDB;

public class Criterion {

    private Column column;
    private String value;
    private String op;

    public Criterion(Column column, String value, String op) {
        this.column = column;
        this.value = value;
        this.op = op;
    }

    public Column getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

    public String getOp() {
        return op;
    }

    public boolean evaluate(Object columnValue) {
        switch (op) {
            case "=":
                return column.compare(value, columnValue) == 0;
            case "<":
                return column.compare(value, columnValue) == -1;
            case ">":
                return column.compare(value, columnValue) == 1;
            default:
                break;
        }
        return false;
    }

}
