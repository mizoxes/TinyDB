package TinyDB;

import java.util.*;

public class SQLExecutor {

    public static SQLExecutor instance = new SQLExecutor();

    public void Qcreate(TinyDBConnection connection, ArrayList<SQLToken> tokens) throws Exception {
        int currToken = 1;
        if (tokens.get(currToken++).getType() != SQLTokenType.TABLE) {
            throw new Exception("Expected 'TABLE'");
        }

        SQLToken TtableName = tokens.get(currToken++);
        if (TtableName.getType() != SQLTokenType.VARIABLE) {
            throw new Exception("Invalid table name");
        }

        String tableName = TtableName.getName();

        Database db = DBManager.getDatabase(connection.getDbName());
        Table tbl = new Table(tableName, connection.getDbName());

        SQLToken tmp = tokens.get(currToken++);
        if (tmp.getType() != (int) '(') {
            throw new Exception("Expected '('");
        }

        tmp = tokens.get(currToken++);
        while (tmp.getType() != (int) ')') {
            if (tmp.getType() != SQLTokenType.VARIABLE) {
                throw new Exception("Invalid column name");
            }

            String columnName = tmp.getName();

            SQLToken Ttype = tokens.get(currToken++);
            if (Ttype.getType() < 15) {
                throw new Exception("invalid type");
            }

            switch (Ttype.getType()) {
                case SQLTokenType.INTEGER:
                    tbl.addColumn(new ColumnInteger(columnName));
                    break;
                case SQLTokenType.REAL:
                    tbl.addColumn(new ColumnReal(columnName));
                    break;
                case SQLTokenType.BOOL:
                    tbl.addColumn(new ColumnBool(columnName));
                    break;
                case SQLTokenType.VARCHAR:
                    tmp = tokens.get(currToken++);
                    if (tmp.getType() != SQLTokenType.NUMBER) {
                        throw new Exception("Expected varchar length");
                    }
                    tbl.addColumn(new ColumnVarchar(columnName, (int) tmp.getValue()));
                    break;
                case SQLTokenType.DATE:
                    tbl.addColumn(new ColumnDate(columnName));
                    break;
                default:
                    break;
            }
            tmp = tokens.get(currToken++);
            if (tmp.getType() != (int) ',' && tmp.getType() != (int) ')')
                throw new Exception("Expected ','");

            if (tmp.getType() == (int) ')') {
                System.out.println("done");
                break;
            }

            tmp = tokens.get(currToken++);
        }

        db.addTable(tbl);
    }

    public void Qdelete(TinyDBConnection connection, ArrayList<SQLToken> tokens) throws Exception {
        int currToken = 1;
        SQLToken tmp = tokens.get(currToken++);
        if (tmp.getType() != SQLTokenType.FROM) {
            throw new Exception("Expected 'FROM'");
        }

        SQLToken TtableName = tokens.get(currToken++);
        if (TtableName.getType() != SQLTokenType.VARIABLE)
            throw new Exception("Invalid table name");

        Database db = DBManager.getDatabase(connection.getDbName());
        Table table = db.getTable(TtableName.getName());
        if (table == null)
            throw new Exception("table does not exist");

        ArrayList<Criterion> criteria = new ArrayList<Criterion>();

        if (currToken < tokens.size()) {
            tmp = tokens.get(currToken++);

            if (tmp.getType() != SQLTokenType.WHERE) {
                throw new Exception("Expected 'WHERE'");
            }

            while (currToken < tokens.size()) {
                SQLToken left = tokens.get(currToken++);
                if (left.getType() != SQLTokenType.VARIABLE)
                    throw new Exception("Invalid column name");
                SQLToken op = tokens.get(currToken++);
                if (op.getType() != (char) '>' && op.getType() != (char) '<' && op.getType() != (char) '=')
                    throw new Exception("Invalid operation");
                SQLToken right = tokens.get(currToken++);
                String sop = "";
                sop += (char) op.getType();
                Column col = table.getColumn(left.getName());
                if (col == null) {
                    throw new Exception("column does not exist");
                }
                String val = "";
                if (right.getType() == SQLTokenType.STRING) val = right.getName();
                else if (right.getType() == SQLTokenType.NUMBER) val += right.getValue();
                else throw new Exception("Expected a value");
                criteria.add(new Criterion(col, val, sop));
                //System.out.println("adding criteria on column " + col.getName() + " " + sop + " " + right.getName());
                if (currToken < tokens.size()) {
                    tmp = tokens.get(currToken++);
                    if (tmp.getType() != SQLTokenType.AND) {
                        throw new Exception("only AND is supported for now");
                    }
                }
            }
        }

        table.deleteRows(criteria);
    }

    public void Qinsert(TinyDBConnection connection, ArrayList<SQLToken> tokens) throws Exception {
        ArrayList<String> columnNames = new ArrayList<String>();
        ArrayList<Object> values = new ArrayList<Object>();

        int currToken = 1;
        SQLToken tmp = tokens.get(currToken++);
        if (tmp.getType() != SQLTokenType.INTO)
            throw new Exception("Expected 'INTO'");

        SQLToken TtableName = tokens.get(currToken++);
        if (TtableName.getType() != SQLTokenType.VARIABLE)
            throw new Exception("Invalid table name");

        Database db = DBManager.getDatabase(connection.getDbName());
        Table table = db.getTable(TtableName.getName());
        if (table == null)
            throw new Exception("table does not exist");

        tmp = tokens.get(currToken++);
        if (tmp.getType() == (char) '(') {
            while (tmp.getType() != (char) ')') {
                SQLToken TcolumnName = tokens.get(currToken++);
                if (TcolumnName.getType() != SQLTokenType.VARIABLE)
                    throw new Exception("Invalid column name");
                tmp = tokens.get(currToken++);
                if (tmp.getType() != (char) ',' && tmp.getType() != (char) ')')
                    throw new Exception("Expected ','");
                columnNames.add(TcolumnName.getName());
            }

            tmp = tokens.get(currToken++);
        }
        if (tmp.getType() != SQLTokenType.VALUES)
            throw new Exception("Expected 'VALUES'");

        tmp = tokens.get(currToken++);
        if (tmp.getType() != (char) '(')
            throw new Exception("Expected '('");
        while (tmp.getType() != (char) ')') {
            SQLToken Tvalue = tokens.get(currToken++);
            if (Tvalue.getType() != SQLTokenType.NUMBER && Tvalue.getType() != SQLTokenType.STRING)
                throw new Exception("Invalid column value");
            tmp = tokens.get(currToken++);

            if (tmp.getType() != (char) ',' && tmp.getType() != (char) ')')
                throw new Exception("Expected ','");
            values.add(Tvalue.getType() == SQLTokenType.NUMBER ? Tvalue.getValue() : Tvalue.getName());
        }

        HashMap<String, Object> hm = new HashMap<String, Object>();

        if (columnNames.isEmpty() && table.getColumnList().size() == values.size()) {
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) instanceof Double) {
                    Column col = table.getColumnList().get(i);
                    switch (col.getType()) {
                        case INTEGER:
                            values.set(i, (int) (double) values.get(i));
                            break;
                        case BOOL:
                            values.set(i, (double) values.get(i) == 1 ? true : false);
                            break;
                        default:
                            break;
                    }
                }
                hm.put(table.getColumnList().get(i).getName(), values.get(i));
            }
        }
        else if (columnNames.size() == values.size()) {
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) instanceof Double) {
                    Pair<Column, Integer> p = table.getColumns().get(columnNames.get(i));
                    if (p == null)
                        throw new Exception("Column does not exist");
                    Column col = p.first;
                    switch (col.getType()) {
                        case INTEGER:
                            values.set(i, (int) (double) values.get(i));
                            break;
                        case BOOL:
                            values.set(i, (double) values.get(i) == 1 ? true : false);
                            break;
                        default:
                            break;
                    }
                }
                hm.put(columnNames.get(i), values.get(i));
            }
        } else
            throw new Exception("The number of columns does not match the number of values");

        table.addRow(hm);
    }

    public List<Map<String, Object>> Qselect(TinyDBConnection connection, ArrayList<SQLToken> tokens) throws Exception {
        Set<String> selectColumns = new HashSet<String>();

        int currToken = 1;
        SQLToken tmp = tokens.get(currToken++);
        if (tmp.getType() != (char) '*') {
            while (tmp.getType() != SQLTokenType.FROM) {
                if (tmp.getType() != SQLTokenType.VARIABLE)
                    throw new Exception("Invalid column name");

                selectColumns.add(tmp.getName());

                tmp = tokens.get(currToken++);
                if (tmp.getType() == SQLTokenType.FROM)
                    break;
                if (tmp.getType() != (char) ',')
                    throw new Exception("Expected ','");

                tmp = tokens.get(currToken++);
            }
        } else {
            tmp = tokens.get(currToken++);
            if (tmp.getType() != SQLTokenType.FROM)
                throw new Exception("Expected 'FROM'");
        }

        SQLToken TtableName = tokens.get(currToken++);
        if (TtableName.getType() != SQLTokenType.VARIABLE)
            throw new Exception("Invalid table name");

        Database db = DBManager.getDatabase(connection.getDbName());
        Table table = db.getTable(TtableName.getName());
        if (table == null)
            throw new Exception("table does not exist");

        ArrayList<Criterion> criteria = new ArrayList<Criterion>();

        if (currToken < tokens.size()) {
            tmp = tokens.get(currToken++);

            if (tmp.getType() != SQLTokenType.WHERE) {
                throw new Exception("Expected 'WHERE'");
            }

            while (currToken < tokens.size()) {
                SQLToken left = tokens.get(currToken++);
                if (left.getType() != SQLTokenType.VARIABLE)
                    throw new Exception("Invalid column name");
                SQLToken op = tokens.get(currToken++);
                if (op.getType() != (char) '>' && op.getType() != (char) '<' && op.getType() != (char) '=')
                    throw new Exception("Invalid operation");
                SQLToken right = tokens.get(currToken++);
                String sop = "";
                sop += (char) op.getType();
                Column col = table.getColumn(left.getName());
                if (col == null) {
                    throw new Exception("column does not exist");
                }
                String val = "";
                if (right.getType() == SQLTokenType.STRING) val = right.getName();
                else if (right.getType() == SQLTokenType.NUMBER) val += right.getValue();
                else throw new Exception("Expected a value");
                criteria.add(new Criterion(col, val, sop));

                if (currToken < tokens.size()) {
                    tmp = tokens.get(currToken++);
                    if (tmp.getType() != SQLTokenType.AND) {
                        throw new Exception("only AND is supported for now");
                    }
                }
            }
        }

        return table.getRows(selectColumns, criteria);
    }

    public void Qupdate(TinyDBConnection connection, ArrayList<SQLToken> tokens) throws Exception {
        Map<String, Object> hm = new HashMap<String, Object>();

        int currToken = 1;
        SQLToken TtableName = tokens.get(currToken++);
        if (TtableName.getType() != SQLTokenType.VARIABLE)
            throw new Exception("Invalid table name");

        Database db = DBManager.getDatabase(connection.getDbName());
        Table table = db.getTable(TtableName.getName());
        if (table == null)
            throw new Exception("table does not exist");

        SQLToken tmp = tokens.get(currToken++);
        if (tmp.getType() != SQLTokenType.SET)
            throw new Exception("Expected 'SET'");

        while (currToken < tokens.size()) {
            SQLToken TcolumnName = tokens.get(currToken++);
            if (TcolumnName.getType() != SQLTokenType.VARIABLE)
                throw new Exception("Invalid column name");
            tmp = tokens.get(currToken++);
            if (tmp.getType() != (int) '=')
                throw new Exception("Expected '='");
            SQLToken Tvalue = tokens.get(currToken++);

            if (Tvalue.getType() == SQLTokenType.NUMBER) {
                Pair<Column, Integer> p = table.getColumns().get(TcolumnName.getName());
                if (p == null)
                    throw new Exception("Column does not exist");
                Column col = p.first;
                switch (col.getType()) {
                    case INTEGER:
                        hm.put(TcolumnName.getName(), (int) Tvalue.getValue());
                        break;
                    case BOOL:
                        hm.put(TcolumnName.getName(), (int) Tvalue.getValue() == 1 ? true : false);
                        break;
                    default:
                        break;
                }
            } else
                hm.put(TcolumnName.getName(), Tvalue.getName());

            if (currToken >= tokens.size())
                break;
            tmp = tokens.get(currToken++);
            if (tmp.getType() == SQLTokenType.WHERE) {
                currToken--;
                break;
            }
            if (tmp.getType() != (int) ',')
                throw new Exception("Expected ','");
        }

        ArrayList<Criterion> criteria = new ArrayList<Criterion>();

        if (currToken < tokens.size()) {
            tmp = tokens.get(currToken++);

            if (tmp.getType() != SQLTokenType.WHERE) {
                throw new Exception("Expected 'WHERE'");
            }

            while (currToken < tokens.size()) {
                SQLToken left = tokens.get(currToken++);
                if (left.getType() != SQLTokenType.VARIABLE)
                    throw new Exception("Invalid column name");
                SQLToken op = tokens.get(currToken++);
                if (op.getType() != (char) '>' && op.getType() != (char) '<' && op.getType() != (char) '=')
                    throw new Exception("Invalid operation");
                SQLToken right = tokens.get(currToken++);
                String sop = "";
                sop += (char) op.getType();
                Column col = table.getColumn(left.getName());
                if (col == null) {
                    throw new Exception("column does not exist");
                }
                String val = "";
                if (right.getType() == SQLTokenType.STRING) val = right.getName();
                else if (right.getType() == SQLTokenType.NUMBER) val += right.getValue();
                else throw new Exception("Expected a value");
                criteria.add(new Criterion(col, val, sop));

                if (currToken < tokens.size()) {
                    tmp = tokens.get(currToken++);
                    if (tmp.getType() != SQLTokenType.AND) {
                        throw new Exception("only AND is supported for now");
                    }
                }
            }
        }

        table.updateRows(hm, criteria);
    }

    public TinyDBResultSet execute(TinyDBConnection connection, String query) {
        TinyDBResultSet rs = new TinyDBResultSet();
        try {
            ArrayList<SQLToken> tokens = SQLLexer.lexString(query);
            if (tokens.size() > 0) {
                switch (tokens.get(0).getType()) {
                    case SQLTokenType.CREATE:
                        Qcreate(connection, tokens);
                        DBManager.save();
                        break;
                    case SQLTokenType.SELECT:
                        rs.setData(
                                Qselect(connection, tokens)
                        );
                        break;
                    case SQLTokenType.INSERT:
                        Qinsert(connection, tokens);
                        break;
                    case SQLTokenType.DELETE:
                        Qdelete(connection, tokens);
                        break;
                    case SQLTokenType.UPDATE:
                        Qupdate(connection, tokens);
                        break;
                    default:
                        rs.setSuccess(false);
                        rs.setError("Unknown query");
                        return rs;
                }
            }
        } catch (Exception ex) {
            rs.setSuccess(false);
            rs.setError(ex.getMessage());
            return rs;
        }
        rs.setSuccess(true);
        return rs;
    }

}
