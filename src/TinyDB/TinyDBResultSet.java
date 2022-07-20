package TinyDB;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TinyDBResultSet {

    private boolean success;
    private int numRows;

    private String error;
    private List<Map<String, Object>> data;

    public TinyDBResultSet() {
        success = false;
        numRows = 0;
        data = null;
        error = "";
    }

    public boolean getSuccess() {
        return success;
    }

    public int getNumRows() {
        return numRows;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
   }

    @Override
    public String toString() {
        String ret = "";

        ret += (this.success ? "1" : "0") + "\n";

        if (error == null) ret += "\n";
        else ret += error + "\n";

        if (data != null && data.size() > 0) {
            ret += data.size() + "\n";
            for (Iterator<Map<String, Object>> it = data.iterator(); it.hasNext(); ) {
                Map<String, Object> row = it.next();
                for (String columnName : row.keySet()) {
                    ret += columnName + " " + row.get(columnName) + "~";
                }
                ret += "\n";
            }
        } else ret += "0\n";

        return ret;
    }
}
