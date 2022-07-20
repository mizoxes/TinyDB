package TinyDB;

import java.util.ArrayList;

public class SQLLexer {
    private static String currentString = "";
    private static int currentPos = 0;

    private static String nextChar() {
        String toRet = "";
        toRet += (char) currentString.charAt(currentPos++);
        return toRet;
    }

    private static double nextDouble() {
        String s2 = ".0123456789";
        String c = nextChar();
        String nb = "";
        while (s2.contains(c)) {
            nb += c;
            if (!thereIsMore())
                break;
            c = nextChar();
        }
        if (!s2.contains(c) || thereIsMore())
            ungetc();
        return Double.parseDouble(nb);
    }

    private static void ungetc() {
        --currentPos;
    }

    private static boolean thereIsMore() {
        return currentPos < currentString.length();
    }

    private static SQLToken next() throws Exception {
        if (!thereIsMore()) {
            return new SQLToken(SQLTokenType.EOF);
        }
        String c = nextChar();
        while (thereIsMore() && c.equals(" ") || c.equals("\n"))
            c = nextChar();
        if (c.equals(" ") || c.equals("\n"))
            return new SQLToken(SQLTokenType.EOF);

        String s1 = "=*+/-();,><";
        String s2 = ".0123456789";

        if (s1.contains(c)) {
            return new SQLToken((int) c.charAt(0));
        }

        if (s2.contains(c)) {
            ungetc();
            double val = nextDouble();
            return new SQLToken(SQLTokenType.NUMBER, val);
        }

        if (c.charAt(0) == '\'') {
            String str = "";
            c = nextChar();
            while (c.charAt(0) != '\'') {
                str += c;
                if (!thereIsMore())
                    break;
                c = nextChar();
            }
            return new SQLToken(SQLTokenType.STRING, str);
        }

        if (Character.isLetter(c.charAt(0))) {
            String str = c;
            c = nextChar();
            while (Character.isLetter(c.charAt(0)) || Character.isDigit(c.charAt(0))) {
                str += c;
                if (!thereIsMore())
                    break;
                c = nextChar();
            }
            if (thereIsMore())
                ungetc();


            switch (str) {
                case "CREATE":   return new SQLToken(SQLTokenType.CREATE);
                case "SELECT":   return new SQLToken(SQLTokenType.SELECT);
                case "INTO":     return new SQLToken(SQLTokenType.INTO);
                case "FROM":     return new SQLToken(SQLTokenType.FROM);
                case "DATABASE": return new SQLToken(SQLTokenType.DATABASE);
                case "INSERT":   return new SQLToken(SQLTokenType.INSERT);
                case "DELETE":   return new SQLToken(SQLTokenType.DELETE);
                case "UPDATE":   return new SQLToken(SQLTokenType.UPDATE);
                case "DROP":     return new SQLToken(SQLTokenType.DROP);
                case "TABLE":    return new SQLToken(SQLTokenType.TABLE);
                case "WHERE":    return new SQLToken(SQLTokenType.WHERE);
                case "OR":       return new SQLToken(SQLTokenType.OR);
                case "AND":      return new SQLToken(SQLTokenType.AND);
                case "INTEGER":  return new SQLToken(SQLTokenType.INTEGER);
                case "REAL":     return new SQLToken(SQLTokenType.REAL);
                case "BOOL":     return new SQLToken(SQLTokenType.BOOL);
                case "VARCHAR":  return new SQLToken(SQLTokenType.VARCHAR);
                case "DATE":     return new SQLToken(SQLTokenType.DATE);
                case "VALUES":   return new SQLToken(SQLTokenType.VALUES);
                case "SET":      return new SQLToken(SQLTokenType.SET);
                default:         return new SQLToken(SQLTokenType.VARIABLE, str);
            }
        }

        throw new Exception("unknown token");
    }

    public static ArrayList<SQLToken> lexString(String string) throws Exception {
        currentString = string;
        currentPos = 0;
        ArrayList<SQLToken> tokens = new ArrayList<SQLToken>();
        SQLToken tok = next();
        while (tok.getType() != SQLTokenType.EOF) {
            tokens.add(tok);
            tok = next();
        }
        return tokens;
    }
}
