package TinyDB;

public class SQLTokenType {
    public static final int SET = -4;
    public static final int SELECT = -3;
    public static final int CREATE = -2;
    public static final int INTO = -1;
    public static final int FROM = 0;
    public static final int DATABASE = 1;
    public static final int INSERT = 2;
    public static final int DELETE = 3;

    public static final int UPDATE = 4;
    public static final int DROP = 5;
    public static final int TABLE = 6;
    public static final int EOF = 7;

    public static final int NUMBER = 8;
    public static final int VARIABLE = 9;

    public static final int WHERE = 10;
    public static final int OR = 11;
    public static final int AND = 12;

    public static final int STRING = 13;
    public static final int VALUES = 14;

    public static final int INTEGER = 15;
    public static final int REAL = 16;
    public static final int BOOL = 17;
    public static final int VARCHAR = 18;
    public static final int DATE = 19;
}
