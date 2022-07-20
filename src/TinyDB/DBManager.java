package TinyDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DBManager {
    private static Map<String, Database> databases = new HashMap<String, Database>();

    public static void createDatabase(String name) {
        databases.put(name, new Database(name));
        File dir = new File("./databases/" + name + "/");
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static Database getDatabase(String name) {
        return databases.get(name);
    }

    public static void load() {
        File dbinfo = new File("./dbinfo/dbinfo.txt");
        Database db = null;
        Table tbl = null;
        try {
            Scanner scanner = new Scanner(dbinfo);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int cnt = 0;
                while (line.charAt(cnt) == '\t')
                    cnt++;
                if (cnt == 0) {
                    db = new Database(line.trim());
                    databases.put(line.trim(), db);
                    tbl = null;
                }
                else if (cnt == 1) {
                    if (tbl != null)
                        db.addTable(tbl);
                    tbl = new Table(line.trim(), db.getName());
                    db.addTable(tbl);
                } else if (cnt == 2) {
                    String[] tmp = line.split(" ");
                    String name = tmp[0].trim();
                    String type = tmp[1].trim();
                    Column col = null;
                    if (type.equals("INTEGER"))
                        col = new ColumnInteger(name);
                    else if (type.equals("REAL"))
                        col = new ColumnReal(name);
                    else if (type.equals("BOOL"))
                        col = new ColumnBool(name);
                    else if (type.equals("DATE"))
                        col = new ColumnDate(name);
                    else if (type.equals("VARCHAR")) {
                        int len = Integer.valueOf(tmp[2].trim());
                        col = new ColumnVarchar(name, len);
                    }
                    tbl.addColumn(col);
                }
            }
            if (db != null && tbl != null)
                db.addTable(tbl);
        } catch (FileNotFoundException ex) {
        }
    }

    public static void save() {
        try {
            FileWriter fw = new FileWriter("./dbinfo/dbinfo.txt");
            for (String dbName : databases.keySet()) {
                Database db = databases.get(dbName);
                fw.write(db.getName() + "\n");
                Collection<Table> tables = db.getTables();
                for (Table table : tables) {
                    fw.write("\t" + table.getName() + "\n");
                    ArrayList<Column> columnList = table.getColumnList();
                    for (int i = 0; i < columnList.size(); i++) {
                        Column column = columnList.get(i);
                        fw.write("\t\t" + column.getName() + " ");
                        switch (column.getType()) {
                            case INTEGER:
                                fw.write("INTEGER");
                                break;
                            case REAL:
                                fw.write("REAL");
                                break;
                            case BOOL:
                                fw.write("BOOL");
                                break;
                            case VARCHAR:
                                fw.write("VARCHAR " + ((ColumnVarchar) column).getLength());
                                break;
                            case DATE:
                                fw.write("DATE");
                                break;
                            default:
                                break;
                        }
                        fw.write("\n");
                    }
                }
            }
            fw.close();
        } catch (IOException ex) {
        }
    }
}
