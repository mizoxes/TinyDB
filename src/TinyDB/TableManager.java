package TinyDB;

import java.io.*;
import java.util.*;

public class TableManager {

    public static TableManager instance = new TableManager();

    public synchronized void writeRow(Table table, Map<String, Object> values) throws TinyDBException {
        ArrayList<Object> row = new ArrayList<Object>();

        for (Pair<Column, Integer> p : table.getColumns().values())
            row.add(p.first.getDefault());

        for (String columnName : values.keySet()) {
            Pair<Column, Integer> p = table.getColumns().get(columnName);
            if (p == null)
                throw new TinyDBException("column " + columnName + " does not exist");

            row.set(p.second, values.get(columnName));
        }

        File file = new File("./databases/" + table.getDBName() + "/" + table.getName() + ".table");
        long fileLength = file.length();
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
        ) {
            raf.seek(fileLength);
            for (int i = 0; i < row.size(); i++) {
                writeValue(row.get(i), table.getColumnList().get(i), raf);
            }
        }
        catch (IOException ex) {
            throw new TinyDBException("failed to add row");
        }
    }

    public synchronized void updateRows(Table table, Map<String, Object> values, ArrayList<Criterion> criteria) throws TinyDBException {
        File file = new File("./databases/" + table.getDBName() + "/" + table.getName() + ".table");
        long fileLength = file.length();
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
        ) {
            ArrayList<Object> row = new ArrayList<Object>();
            for (int i = 0; i < table.getColumnList().size(); i++) {
                row.add(null);
            }

            long rowPos = raf.getFilePointer();
            while (rowPos < fileLength) {
                for (int i = 0; i < table.getColumnList().size(); i++) {
                    row.set(i, readValue(table.getColumnList().get(i), raf));
                }

                // check criteria
                boolean skip = false;
                for (int i = 0; i < criteria.size(); i++) {
                    // this criterion is on which column?
                    Column column = criteria.get(i).getColumn();
                    // get the index of that column
                    int index = table.getColumns().get(column.getName()).second;
                    // get the value of the row we just read using the index
                    Object value = row.get(index);
                    // evaluate the criterion
                    if (!criteria.get(i).evaluate(value)) {
                        skip = true;
                        break;
                    }
                }
                if (!skip) {
                    for (String columnName : values.keySet()) {
                        Pair<Column, Integer> p = table.getColumns().get(columnName);
                        // update the column value
                        row.set(p.second, Helper.stringToByteArray(values.get(columnName), ((ColumnVarchar) p.first).getLength()));
                    }

                    // now rewrite the row
                    raf.seek(rowPos);
                    for (int i = 0; i < row.size(); i++) {
                        writeValue(row.get(i), table.getColumnList().get(i), raf);
                    }
                }

                rowPos = raf.getFilePointer();
            }
        }
        catch (IOException ex) {
            throw new TinyDBException("failed to update rows");
        }
    }

    public List<Map<String, Object>> readRows(Table table, Set<String> selectColumns, ArrayList<Criterion> criteria) throws TinyDBException {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();

        File file = new File("./databases/" + table.getDBName() + "/" + table.getName() + ".table");
        long fileLength = file.length();
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
        ) {
            ArrayList<Object> row = new ArrayList<Object>();
            for (int i = 0; i < table.getColumnList().size(); i++) {
                row.add(null);
            }

            while (raf.getFilePointer() < fileLength) {
                for (int i = 0; i < table.getColumnList().size(); i++) {
                    row.set(i, readValue(table.getColumnList().get(i), raf));
                }

                // check criteria
                boolean skip = false;
                for (int i = 0; i < criteria.size(); i++) {
                    // this criterion is on which column?
                    Column column = criteria.get(i).getColumn();
                    // get the index of that column
                    int index = table.getColumns().get(column.getName()).second;
                    // get the value of the row we just read using the index
                    Object value = row.get(index);
                    // evaluate the criterion
                    if (!criteria.get(i).evaluate(value)) {
                        skip = true;
                        break;
                    }
                }
                if (skip)
                    continue;

                Map<String, Object> betterRow = new HashMap<String, Object>();
                for (int i = 0; i < table.getColumnList().size(); i++) {
                    if (selectColumns.isEmpty() || selectColumns.contains(table.getColumnList().get(i).getName()))
                        betterRow.put(table.getColumnList().get(i).getName(), row.get(i));
                }

                ret.add(betterRow);
            }
        }
        catch (IOException ex) {
            throw new TinyDBException("failed to read rows");
        }

        return ret;
    }

    public void deleteRows(Table table, ArrayList<Criterion> criteria) throws TinyDBException {
        File tmp = new File("./databases/" + table.getDBName() + "/tmp");
        File file = new File("./databases/" + table.getDBName() + "/" + table.getName() + ".table");
        try (
                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                RandomAccessFile traf = new RandomAccessFile(tmp, "rw");
                ) {
            long fileLength = file.length();
            ArrayList<Object> row = new ArrayList<Object>();
            for (int i = 0; i < table.getColumnList().size(); i++) {
                row.add(null);
            }

            while (raf.getFilePointer() < fileLength) {
                for (int i = 0; i < table.getColumnList().size(); i++) {
                    row.set(i, readValue(table.getColumnList().get(i), raf));
                }

                // check criteria
                boolean skip = true;
                for (int i = 0; i < criteria.size(); i++) {
                    // this criterion is on which column?
                    Column column = criteria.get(i).getColumn();
                    // get the index of that column
                    int index = table.getColumns().get(column.getName()).second;
                    // get the value of the row we just read using the index
                    Object value = row.get(index);
                    // evaluate the criterion
                    if (!criteria.get(i).evaluate(value)) {
                        skip = false;
                        break;
                    }
                }
                if (skip)
                    continue;

                for (int i = 0; i < table.getColumnList().size(); i++) {
                    writeValue(row.get(i), table.getColumnList().get(i), traf);
                }
            }
            raf.close();
            traf.close();
            file.delete();
            tmp.renameTo(new File("./databases/" + table.getDBName() + "/" + table.getName() + ".table"));

        } catch (IOException ex) {
            throw new TinyDBException("failed to delete rows");
        }
    }

    private Object readValue(Column column, RandomAccessFile raf) throws IOException {
        byte[] arr = new byte[255];

        switch (column.getType()) {
            case INTEGER:
                return raf.readInt();
            case REAL:
                return raf.readDouble();
            case BOOL:
                return raf.readBoolean();
            case VARCHAR:
                raf.read(arr, 0, ((ColumnVarchar) column).getLength());
                return Helper.byteArrayToString(arr);
            case DATE:
                raf.read(arr, 0, 10);
                return new String(arr);
        }

        return null;
    }

    private void writeValue(Object value, Column column, RandomAccessFile raf) throws IOException {
        switch (column.getType()) {
            case INTEGER:
                raf.writeInt((int) value);
                break;
            case REAL:
                raf.writeDouble((double) value);
                break;
            case BOOL:
                raf.writeBoolean((boolean) value);
                break;
            case VARCHAR: {
                raf.write(Helper.stringToByteArray(value, ((ColumnVarchar) column).getLength()));
                break;
            }
            case DATE:
                raf.writeChars((String) value);
            default:
                break;
        }
    }

}
