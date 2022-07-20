package TinyDB;

public class SQLToken {
    private int type;
    private String name;
    private double value;

    SQLToken(int type) {
        this.type = type;
    }

    SQLToken(int type, String name) {
        this.type = type;
        this.name = name;
    }

    SQLToken(int type, double value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
