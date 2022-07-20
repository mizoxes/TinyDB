package TinyDB;

import java.net.Socket;

public class TinyDBConnection {

    private Socket clientSocket;
    private String dbName;

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

}
